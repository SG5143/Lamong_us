package websoket;

import java.util.*;
import java.util.concurrent.*;
import org.json.JSONObject;
import jakarta.websocket.Session;

public class LiarGameManager {
	private static final LiarGameManager INSTANCE = new LiarGameManager();
	private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();
	private final SandGameMessage SGM = SandGameMessage.getInstance();

	private LiarGameManager() {}

	public static LiarGameManager getInstance() {
		return INSTANCE;
	}

	@SuppressWarnings("unchecked")
	// ================== 게임 초기화 ================== //
	public void initializeNewGame(GameStartDto gameStartDto) {
		String roomKey = gameStartDto.getRoomKey();
		List<Session> clients = new CopyOnWriteArrayList<>(gameStartDto.getClients());

		Session liar = selectLiar(clients);
		Map<String, String> randomSelection = LiarGameTopic.getRandomTopicAndWord();
		Map<String, Object> liarInfo = (Map<String, Object>) liar.getUserProperties().get("userInfo");
		GameSession session = new GameSession(
				roomKey,
				(String) liarInfo.get("uuid"),
				randomSelection.get("topic"),
				randomSelection.get("keyword"),
				gameStartDto.getRounds(),
				clients);

		gameSessions.put(roomKey, session);

		distributeGameSetup(session);
		SGM.sendChangeRound(clients, 1);
		SGM.sendTurnInfo(clients, 1);
	}

	private void distributeGameSetup(GameSession session) {
		broadcastClientInfo(session);
		distributeTurns(session);
		SGM.distributeWords(session);
		session.transitionState(GameSession.GameState.ROUND_START);
	}

	// ================== 메시지 처리 ================== //
	public boolean handleGameMessage(String roomKey, Session client, String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");
            
            switch (type) {
                case MessageConstants.TYPE_VOTE : return handleVoteAction(roomKey, message);
                case MessageConstants.TYPE_MESSAGE: return handleChatMessage(roomKey, client, message);
                case MessageConstants.TYPE_FINAL_CHANCE: return handleFinalChance(roomKey, client, message);
            };
            return false;
        } catch (Exception e) {
        	System.out.println("메시지 처리 에러: " + e.getMessage());
            return false;
        }
    }

	private boolean handleVoteAction(String roomKey, String message) {
		GameSession session = gameSessions.get(roomKey);
		if (session.getState() != GameSession.GameState.VOTE_PHASE)
			return false;

		JSONObject json = new JSONObject(message);

		session.addVote(json.getString("userUUID"), json.getString("vote"));

		if (session.isAllVoted() || session.isVoteInProgress() == false) {
			processVoteResults(session);
			return false;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean handleChatMessage(String roomKey, Session client, String message) {
		GameSession session = gameSessions.get(roomKey);

		Map<String, Object> userInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
		String uuid = (String) userInfo.get("uuid");
		String nickname = (String) userInfo.get("nickname");

		if (session.getState().equals(GameSession.GameState.ROUND_END)) {
			SGM.broadcastMessage(session.getClients(), uuid, nickname, message);
			return true;
		} else if (session.isCurrentPlayer(client)) {
			SGM.broadcastMessage(session.getClients(), uuid, nickname, message);
			advanceGameTurn(session);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean handleFinalChance(String roomKey, Session client, String message) {
		GameSession session = gameSessions.get(roomKey);

		JSONObject json = new JSONObject(message);
		String keyword = json.getString("keyword");

		Map<String, Object> userInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
		String liarUUID = session.getLiarId();
		String senderUUID = (String) userInfo.get("uuid");

		if (liarUUID.equals(senderUUID)) {
			if (keyword.equals(session.getKeyword())) {
				SGM.endGameMessage(session.getClients(), "liarVictory");
			} else {
				SGM.endGameMessage(session.getClients(), "liarDefeat");
			}
		}
		return false;
	}

	// ================== 턴 관리 ================== //
	private void advanceGameTurn(GameSession session) {
		session.advanceTurn();
		if (session.isNewRoundNeeded()) {
			if (!startNewRound(session)) {
				Executors.newSingleThreadScheduledExecutor().schedule(() -> {
					initiateVoting(session);
				}, 11, TimeUnit.SECONDS); // 10초후 투표시작
				return;
			}
		}
		SGM.sendTurnInfo(session.getClients(), session.getCurrentTurn());
	}

	private boolean startNewRound(GameSession session) {
		if (session.getCurrentRound() > session.getMaxRounds()) {
			session.transitionState(GameSession.GameState.ROUND_END);
			SGM.broadcastStateChange(session.getClients(), session.getState().toString());
			return false;
		}
		SGM.sendChangeRound(session.getClients(), session.getCurrentRound());
		return true;
	}

	// ================== 투표 관리 ================== //
	private void initiateVoting(GameSession session) {
		session.transitionState(GameSession.GameState.VOTE_PHASE);
		SGM.broadcastStateChange(session.getClients(), session.getState().toString());
		session.setVoteInProgress(true);

		Executors.newSingleThreadScheduledExecutor().schedule(() -> {
			if (session.getState() == GameSession.GameState.VOTE_PHASE) {
				session.setVoteInProgress(false);
				processVoteResults(session);
			}
		}, 11, TimeUnit.SECONDS);
	}

	private void processVoteResults(GameSession session) {
		GameSession.VoteResult result = session.calculateVoteResult();
		SGM.voteResult(session.getClients(), result);

		if (result.isLiarCorrect()) {
			session.transitionState(GameSession.GameState.FINAL_CHANCE);
			SGM.broadcastStateChange(session.getClients(), session.getState().toString());
		} else {
			SGM.endGameMessage(session.getClients(), "liarVictory");
		}
	}

	// ================== 연결 종료 처리 ================== //
	public void manageClientDisconnection(String roomKey, Session client) {
		GameSession session = gameSessions.get(roomKey);
		if (session == null)
			return;

		session.removeClient(client);
		if (session.shouldTerminate()) {
			terminateGame(roomKey);
			return;
		}

		if (session.isCurrentPlayer(client)) {
			reassignTurn(session);
			advanceGameTurn(session);
		}
		broadcastClientInfo(session);
	}

	// ================== 보조 메서드 ================== //
	private void terminateGame(String roomKey) {
		GameSession session = gameSessions.get(roomKey);
		session.getClients().forEach(this::safeCloseSession);
		SGM.endGameMessage(session.getClients(), "userLeave");
	}

	private void safeCloseSession(Session session) {
		try {
			if (session.isOpen())
				session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void broadcastClientInfo(GameSession session) {
		List<Map<String, Object>> playersInfo = new ArrayList<>();
		for (Session client : session.getClients()) {

			@SuppressWarnings("unchecked")
			Map<String, Object> clientInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
			playersInfo.add(clientInfo);
		}
		SGM.clientInfoMessage(session.getClients(), playersInfo);
	}

	private Session selectLiar(List<Session> clients) {
		Collections.shuffle(clients);
		return clients.get(0);
	}

	private void distributeTurns(GameSession session) {
		List<Session> clients = session.getClients();
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).getUserProperties().put(MessageConstants.TURN, i + 1);
			SGM.assignTurnToClient(clients.get(i), i + 1);
		}
	}

	private void reassignTurn(GameSession session) {
		List<Session> clients = session.getClients();
		int currentTurn = session.getCurrentTurn();
		if (currentTurn > clients.size()) {
			session.setCurrentTurn(1);
		}
	}
}