package websoket;

import java.util.*;

import org.json.JSONObject;
import jakarta.websocket.Session;

public class LiarGameManager {
	private static final LiarGameManager INSTANCE = new LiarGameManager();
	private final SandGameMessage SGM = SandGameMessage.getInstance();  

	private Map<String, GameSession> gameSessions = new HashMap<>();

	private LiarGameManager() {}

	public static LiarGameManager getInstance() {
		return INSTANCE;
	}

	/*
	 * 인원수가 모이면 게임 초기화 및 시작 
	 * 1. 클라이언트 모두 중 라이어를 임의 배정 
	 * 2. 현재 클라이언트들의 정보를 모두에게 건내줌
	 * 3. 현재 턴 순서를 배정하고 각자의 순서 정보를 넘겨줌 
	 * 4. 주제와 제시어를 응답 -> 라이어의 경우 제시어는 응답하지 않음 
	 * 5. 시작을 알리기 위해 현재 라운드(1)와 턴정보(1)를 보내줌
	 */
	public void initializeNewGame(GameStartDto gameStartDto) {
		String roomKey = gameStartDto.getRoomKey();
		int rounds = gameStartDto.getRounds();
		
		List<Session> clients = new ArrayList<>(gameStartDto.getClients());
		Session liar = selectLiar(clients);
		
		String topic = "음식";
		String keyword = "사과";

		GameSession gameDto = new GameSession("START", liar.getId(), topic, keyword, rounds, clients);
		gameSessions.put(roomKey, gameDto);

		broadcastClientInfo(clients);
		distributeTurns(clients);
		SGM.distributeWords(clients, liar, topic, keyword);

		SGM.sendChangeRound(clients, 1);
		SGM.sendTurnInfo(clients, 1);
	}

	public boolean handleGameMessage(String roomKey, Session client, String message) {
		JSONObject jsonObject = new JSONObject(message);
		String type = jsonObject.getString("type");

		switch (type) {
		case GameConstants.TYPE_VOTE:
			handleVote(roomKey, client, message);
			return false;
		case GameConstants.TYPE_MESSAGE:
			return handleGameChatMessage(roomKey, client, message);
		default:
			System.out.println("해당 타입은 정의되지 않았습니다. type: " + type);
		}
		return false;
	}
	
	private boolean handleGameChatMessage(String roomKey, Session client, String message) {
		GameSession gameSession = gameSessions.get(roomKey);
		int currentTurn = gameSession.getCurrentTurn();
		
		if (gameSession.getState().equals("ROUND_END")) { // 자유 채팅
			SGM.broadcastMessage(gameSession.getClients(), client.getId(), message);
			return true;
		} else if (isPlayerTurn(client, currentTurn)) { // 라운드 진행 중
			SGM.broadcastMessage(gameSession.getClients(), client.getId(), message);
			advanceGameTurn(gameSession);
			return true;
		}
		return false;
	}
	
	private boolean isPlayerTurn(Session session, int currentTurn) {
		return session.getUserProperties().get(GameConstants.TURN) != null
				&& (Integer) session.getUserProperties().get(GameConstants.TURN) == currentTurn;
	}
	
	private void advanceGameTurn(GameSession gameSession) {
		List<Session> clients = gameSession.getClients();
		int currentTurn = gameSession.getCurrentTurn();
		currentTurn = (currentTurn % clients.size()) + 1;

		if (currentTurn == 1) {
			if (!handleRoundTransition(gameSession)) {
				return;
			}
		}
		gameSession.setCurrentTurn(currentTurn);
		SGM.sendTurnInfo(clients, currentTurn);
	}

	private boolean handleRoundTransition(GameSession gameSession) {
		List<Session> clients = (List<Session>) gameSession.getClients();

		int rounds = gameSession.getRounds();
		int round = gameSession.getRound();
		
		gameSession.setRound(round+1);

		if (gameSession.getRound() > rounds) {
	        gameSession.setState("ROUND_END");
	        SGM.broadcastStateChange(clients, gameSession.getState());
			
	        beginVoteCountdown(clients, gameSession);
	        return false;
		}

		SGM.sendChangeRound(clients, gameSession.getRound());
		return true;
	}
	
	private void beginVoteCountdown(List<Session> clients, GameSession gameSession) {
		gameSession.setVoteInProgress(true);

	    new Thread(() -> {
	        try {
	            Thread.sleep(11000);  //  10+1초 대기

	            // 상태 변경
	            gameSession.setState("VOTE_START"); 
	            SGM.broadcastStateChange(clients, gameSession.getState());

	            // 10초 후 투표 집계 실행 > 이미 모두 제출해 집계 끝나면 리턴
	            if (!gameSession.isVoteInProgress()) return;  
	            
	            tallyVotes(gameSession); 
	            gameSession.setVoteInProgress(false);  
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }).start();
	}

	private void terminateGame(String roomKey) {
		GameSession gameSession = gameSessions.get(roomKey);
		SGM.endGameMessage(gameSession.getClients());
		gameSessions.remove(roomKey);
	}

	private boolean broadcastClientInfo(List<Session> clients) {
		List<Map<String, Object>> playersInfo = new ArrayList<>();

		for (Session client : clients) {
			Map<String, Object> clientInfo = new HashMap<>();
			clientInfo.put("uuid", client.getId());
			clientInfo.put("nickname", "nickname");
			clientInfo.put("image", "profileImage");

			playersInfo.add(clientInfo);
		}
		
		SGM.clientInfoMessage(clients, playersInfo);
		return true;
	}

	private Session selectLiar(List<Session> clientList) {
		Collections.shuffle(clientList);
		Session liar = clientList.get(0);
		return liar;
	}

	private void distributeTurns(List<Session> clients) {
		List<Session> clientList = new ArrayList<>(clients);
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).getUserProperties().put(GameConstants.TURN, i+1);
			SGM.assignTurnToClient(clientList.get(i), i + 1);
		}
	}
	
	private void handleVote(String roomKey, Session client, String message) {
	    GameSession gameSession = gameSessions.get(roomKey);

	    JSONObject jsonObject = new JSONObject(message);
	    String userUUID = jsonObject.getString("userUUID");
	    String vote = jsonObject.getString("vote");

	    gameSession.addVote(userUUID, vote);

	    if (gameSession.getVotes().size() == gameSession.getClients().size()) {
	        tallyVotes(gameSession);
	        gameSession.setVoteInProgress(false);  // 투표 완료 후 상태 업데이트
	    }
	}
	
	private void tallyVotes(GameSession gameSession) {
	    Map<String, String> votes = gameSession.getVotes();
	    Map<String, Integer> voteCount = new HashMap<>();

	    // 투표 집계
	    for (String vote : votes.values()) {
	        voteCount.put(vote, voteCount.getOrDefault(vote, 0) + 1);
	    }

	    // 최대 투표수와 대상자 찾기
	    String mostVoted = null;
	    int maxVotes = 0;
	    for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
	        if (entry.getValue() > maxVotes) {
	            maxVotes = entry.getValue();
	            mostVoted = entry.getKey();
	        }
	    }

	    // 투표 결과 처리
	    if (mostVoted != null) {
	        broadcastVoteResult(gameSession, mostVoted, maxVotes);
	    }
	}
	
	private void broadcastVoteResult(GameSession gameSession, String mostVoted, int maxVotes) {
		List<Session> clients = gameSession.getClients();
		int totalVotes = clients.size();
		boolean isMajority = maxVotes > totalVotes / 2;

		SGM.voteResult(gameSession.getClients(), mostVoted, maxVotes);
		if (isMajority && mostVoted.equals(gameSession.getLiarId())) {
			// 과반수로 라이어를 찾음 -> 최후의 제시어 제시
			gameSession.setState("FINAL_CHANCE");
			SGM.broadcastStateChange(clients, gameSession.getState());
		} else {
			// 과반수로 라이어를 찾지 못함 또는 라이어가 아님 -> 게임 종료
			gameSession.setState("GAME_END");
			SGM.broadcastStateChange(clients, gameSession.getState());
		}
	}
	
	public void manageClientDisconnection(String roomKey, Session disconnectedClient) {
	    GameSession gameSession = gameSessions.get(roomKey);
	    if (gameSession == null) {
	        return;
	    }

	    List<Session> clients = gameSession.getClients();
	    clients.remove(disconnectedClient);

	    // 최소 인원 플레이어를 충족하지 못했을 경우
	    if (clients.size() <= 2) {
	        terminateGame(roomKey);
	        return;
	    }

	    // 라이어가 나갔을 경우
		if (gameSession.getLiarId().equals(disconnectedClient.getId())) {
			SGM.broadcastSystemMessage(clients, "라이어가 나갔습니다. 게임이 종료됩니다.");
			terminateGame(roomKey);
			return;
		}

	    // 현재 턴인 플레이어가 나갔을 경우
	    if (isPlayerTurn(disconnectedClient, gameSession.getCurrentTurn())) {
	        reassignTurnAfterDisconnection(gameSession);
	        advanceGameTurn(gameSession); // 다음 턴으로 넘김
	    }
	    
	    // 턴 정보 갱신
	    updateTurns(gameSession);

	    // 남은 클라이언트에게 변경 사항 알림
	    broadcastClientInfo(clients);
	}
	
	private void reassignTurnAfterDisconnection(GameSession gameSession) {
	    List<Session> clients = gameSession.getClients();
	    int currentTurn = gameSession.getCurrentTurn();

	    // 나간 클라이언트의 턴이 마지막 순서일 경우, 첫 번째 클라이언트로 턴 이동
	    if (currentTurn > clients.size()) {
	        currentTurn = 1;
	    }

	    gameSession.setCurrentTurn(currentTurn);
	}
	
	private void updateTurns(GameSession gameSession) {
	    List<Session> clients = gameSession.getClients();
	    int turn = 1;

	    for (Session client : clients) {
	        client.getUserProperties().put(GameConstants.TURN, turn);
	        SGM.assignTurnToClient(client, turn);
	        turn++;
	    }

	    // 갱신된 첫 번째 턴 정보를 설정
	    gameSession.setCurrentTurn(1);
	}
}