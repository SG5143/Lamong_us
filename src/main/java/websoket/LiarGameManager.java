package websoket;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import jakarta.websocket.Session;

public class LiarGameManager {
	private static final LiarGameManager INSTANCE = new LiarGameManager();
	private final Logger LOGGER = Logger.getLogger(LiarGameManager.class.getName());

	private Map<String, GameSession> gameSessions = new HashMap<>();

	private LiarGameManager() {}

	public static LiarGameManager getInstance() {
		return INSTANCE;
	}

	/*
	 * 인원수가 모이면 게임 초기화 및 시작 
	 * 1. 클라이언트 모두 중 라이어를 임의 배정 
	 * 2. 현재 클라이언트들의 정보를 모두에게 건내줌
	 * 3.현재 턴 순서를 배정하고 각자의 순서 정보를 넘겨줌 
	 * 4. 주제와 제시어를 응답 -> 라이어의 경우 제시어는 응답하지 않음 
	 * 5. 시작을 알리기 위해 현재 라운드(1)와 턴정보(1)를 보내줌
	 */
	public void startGame(GameStartDto gameStartDto) {
		String roomKey = gameStartDto.getRoomKey();
		int rounds = gameStartDto.getRounds();
		
		List<Session> clients = new ArrayList<>(gameStartDto.getClients());
		Session liar = assignLiar(clients);
		
		String topic = "음식";
		String keyword = "사과";

		GameSession gameDto = new GameSession(liar.getId(), topic, keyword, rounds, clients);
		gameSessions.put(roomKey, gameDto);

		sendClientInfo(clients);
		assignTurns(clients);
		distributeWords(clients, liar, topic, keyword);

		sendChangeRound(clients, 1);
		sendTurnInfo(clients, 1);
	}

	/*
	 * 게임 메시지 처리 
	 * 1. 라운드 전체 종료시 모두 채팅을 할 수 있음 
	 * 2. 라운드 진행 시 현재 턴인 플레이어만 채팅을 할 수 있음
	 */
	public boolean processGameMessage(String roomKey, Session session, String message) {
		GameSession gameSession = gameSessions.get(roomKey);
		int currentTurn = gameSession.getCurrentTurn();

		int rounds = gameSession.getRounds();
		int round = gameSession.getRound();
		
		if (round > rounds) {
			broadcastMessage(roomKey, session.getId(), message);
			return true;
		} else if (isCurrentPlayerTurn(session, currentTurn)) {
			broadcastMessage(roomKey, session.getId(), message);
			updateGameTurn(gameSession, roomKey);
			return true;
		}
		return false;
	}

	/*
	 * 게임 턴 정보 업데이트 후 > 현재 턴 정보 응답 
	 * 메서드 호출 턴을 증가시키고 라운드 정보 계산 및 응답 메서드 호출 
	 * 라운드가 종료되어 false를 반환할 경우 턴 정보 업데이트X 
	 * 아닐경우 계산된 턴 정보를 클라이언트 모두에게 전달
	 */
	private void updateGameTurn(GameSession gameSession, String roomKey) {
		List<Session> clients = gameSession.getClients();
		int currentTurn = gameSession.getCurrentTurn();
		currentTurn = (currentTurn % clients.size()) + 1;

		if (currentTurn == 1) {
			if (!sendRoundInfo(gameSession, roomKey, currentTurn)) {
				return;
			}
		}

		gameSession.setCurrentTurn(currentTurn);
		sendTurnInfo(clients, currentTurn);
	}

	// 현재 클라이언트 차례인지 확인하여 true false 반환
	private boolean isCurrentPlayerTurn(Session session, int currentTurn) {
		return session.getUserProperties().get(GameConstants.TURN) != null
				&& (Integer) session.getUserProperties().get(GameConstants.TURN) == currentTurn;
	}

	// 현재 턴 정보를 클라이언트로 전송하는 메서드
	private void sendTurnInfo(List<Session> clients, int currentTurn) {
		for (Session client : clients) {
				sendJsonMessage(client, JsonUtil.createJsonMessage(
						GameConstants.TYPE_CUR_TURN,
						GameConstants.CURRENT_TURN, currentTurn));
		}
	}

	// 데이터 정리 및 게임 종료
	private void endGame(String roomKey) {
		gameSessions.remove(roomKey);
	}

	/*
	 * 유저 기본 정보 전달 추후 로그인한 유저 uud, 닉네임, 프로필 이미지 정보로 변경
	 * 현재 자동생성 클라이언트 id, nickname, profileImage를 건내줌
	 */
	private boolean sendClientInfo(List<Session> clients) {
		List<Map<String, Object>> playersInfo = new ArrayList<>();

		for (Session client : clients) {
			Map<String, Object> clientInfo = new HashMap<>();
			clientInfo.put("uuid", client.getId());
			clientInfo.put("nickname", "nickname");
			clientInfo.put("image", "profileImage");

			playersInfo.add(clientInfo);
		}

		for (Session client : clients) {
				JSONObject jsonObject = JsonUtil.createJsonMessage(
						GameConstants.TYPE_CLIENT_INFO,
						GameConstants.CLIENT_INFO, playersInfo);
				sendJsonMessage(client, jsonObject);
		}
		return true;
	}

	/*
	 * 라운드 증가 처리 후 라운드 정보 전송
	 * 라운드 종료 시 종료 알림 메서드 호출
	 */
	private boolean sendRoundInfo(GameSession gameSession, String roomKey, int currentTurn) {
		List<Session> clients = (List<Session>) gameSession.getClients();

		int rounds = gameSession.getRounds();
		int round = gameSession.getRound();
		gameSession.setRound(round+1);

		if (round > rounds) {
			sendRoundEndMessage(clients);
			return false;
		}

		sendChangeRound(clients, round);
		return true;
	}

	// 라운드 종료 처리
	private void sendRoundEndMessage(List<Session> clients) {
		for (Session client : clients) {
				JSONObject jsonObject = JsonUtil.createJsonMessage(
						GameConstants.TYPE_ROUND_END,
						GameConstants.MESSAGE, "라운드 종료");
				sendJsonMessage(client, jsonObject);
		}
	}

	// 라운드 증가 메시지 응답
	private void sendChangeRound(List<Session> clients, int round) {
		for (Session client : clients) {
				JSONObject jsonObject = JsonUtil.createJsonMessage(
						GameConstants.TYPE_ROUND_INFO,
						GameConstants.ROUND, round);
				sendJsonMessage(client, jsonObject);
		}
	}

	// 클라이언트 셔플 후 0번 클라이언트에게 라이어 역할 배정
	private Session assignLiar(List<Session> clientList) {
		Collections.shuffle(clientList);
		Session liar = clientList.get(0);
		return liar;
	}

	// 클라이언트 모두를 받아 순서를 배정
	private void assignTurns(List<Session> clients) {
		List<Session> clientList = new ArrayList<>(clients);
		for (int i = 0; i < clients.size(); i++) {
			assignTurnToClient(clientList.get(i), i + 1);
		}
	}

	// 배정받은 순서 할당 및 메시지 전달
	private void assignTurnToClient(Session client, int turn) {
			client.getUserProperties().put(GameConstants.TURN, turn);
			sendJsonMessage(client, JsonUtil.createJsonMessage(
					GameConstants.TYPE_SET_TURN,
					GameConstants.TURN, turn));
	}

	/*
	 * 클라이언트 모두와 라이어, 주제, 키워드를 받아
	 *  라이어가 아닐 경우 주제와 키워드 
	 *  라이어일 경우 주제만 전달
	 */
	private void distributeWords(List<Session> clients, Session liar, String topic, String keyword) {
		for (Session client : clients) {
				JSONObject wordMessage = JsonUtil.createJsonMessage(
						GameConstants.TYPE_GAME_START, 
						GameConstants.TOPIC, topic,
						GameConstants.KEYWORD, client.equals(liar) ? null : keyword); // 라이어는 키워드 제외
				sendJsonMessage(client, wordMessage);
		}
	}
	
	private void voteMessage() {
		
	}
	

	// 채팅 메시지 전체 클라이언트에게 브로드캐스트
	private void broadcastMessage(String roomKey, String senderId, String message) {
		GameSession gameSession = gameSessions.get(roomKey);
		List<Session> clients = gameSession.getClients();

		JSONObject chatMessage = JsonUtil.createJsonMessage(
				GameConstants.TYPE_MESSAGE,
				GameConstants.SESSION_ID, senderId,
				GameConstants.MESSAGE, message);
		clients.forEach(client -> {
				sendJsonMessage(client, chatMessage);
		});
	}

	// JSON 메시지를 클라이언트에게 전송
	private boolean sendJsonMessage(Session client, JSONObject message){
		try {
			client.getBasicRemote().sendText(message.toString());
			return true;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			return false;
		}
	}
}