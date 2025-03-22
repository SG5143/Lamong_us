package websoket.game;

import java.util.*;
import java.util.concurrent.*;
import org.json.JSONObject;
import jakarta.websocket.Session;

@SuppressWarnings("unchecked")
public class LiarGameManager {
	private static final LiarGameManager instance = new LiarGameManager();
	private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();
	private final SandGameMessage sgm = SandGameMessage.getInstance();

	private LiarGameManager() {}

	public static LiarGameManager getInstance() {
		return instance;
	}

	// ================== 게임 초기화 ================== //
	public void initializeNewGame(GameStartDto gameStartDto) {
		sgm.sendRoomInfo(gameStartDto);
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

		sgm.clientInfoMessage(session);
		distributeTurns(session);
		sgm.distributeWords(session);
		session.transitionState(GameSession.GameState.ROUND_START);
		sgm.sendChangeRound(session);
		sgm.sendTurnInfo(session);
	}

	// ================== 메시지 처리 ================== //
	public boolean handleGameMessage(String roomKey, Session client, String message) {
		try {
			JSONObject json = new JSONObject(message);
			String type = json.getString("type");

			switch (type) {
			case "VOTE":
				return handleVoteAction(roomKey, message);
			case "MESSAGE":
				return handleChatMessage(roomKey, client, message);
			case "FINAL_CHANCE":
				return handleFinalChance(roomKey, client, message);
			}
			
		} catch (Exception e) {
			System.out.println("메시지 처리 에러: " + e.getMessage());
		}
		
		return false;
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

	private boolean handleChatMessage(String roomKey, Session client, String message) {
		GameSession session = gameSessions.get(roomKey);

		if (session.getState().equals(GameSession.GameState.ROUND_END)) {
			sgm.broadcastMessage(session, client, message);
			return true;
		} else if (session.isCurrentPlayer(client)) {
			sgm.broadcastMessage(session, client, message);
			advanceGameTurn(session);
			return true;
		}
		return false;
	}

	private boolean handleFinalChance(String roomKey, Session client, String message) {
		GameSession session = gameSessions.get(roomKey);

		JSONObject json = new JSONObject(message);
		String keyword = json.getString("keyword");

		Map<String, Object> userInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
		String liarUUID = session.getLiarId();
		String senderUUID = (String) userInfo.get("uuid");

		if (liarUUID.equals(senderUUID)) {
			if (keyword.equals(session.getKeyword())) {
				sgm.endGameMessage(session, "liarVictory");
			} else {
				sgm.endGameMessage(session, "liarDefeat");
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
				}, 31, TimeUnit.SECONDS); // 30초후 투표시작
				return;
			}
		}
		sgm.sendTurnInfo(session);
	}

	private boolean startNewRound(GameSession session) {
		if (session.getCurrentRound() > session.getMaxRounds()) {
			session.transitionState(GameSession.GameState.ROUND_END);
			sgm.broadcastStateChange(session);
			return false;
		}
		sgm.sendChangeRound(session);
		return true;
	}

	// ================== 투표 관리 ================== //
	private void initiateVoting(GameSession session) {
		session.transitionState(GameSession.GameState.VOTE_PHASE);
		sgm.broadcastStateChange(session);
		session.setVoteInProgress(true);

		Executors.newSingleThreadScheduledExecutor().schedule(() -> {
			if (session.getState() == GameSession.GameState.VOTE_PHASE) {
				session.setVoteInProgress(false);
				processVoteResults(session);
			}
		}, 15, TimeUnit.SECONDS);
	}

	private void processVoteResults(GameSession session) {
		GameSession.VoteResult result = session.calculateVoteResult();
		sgm.voteResult(session, result);

		if (result.isLiarCorrect()) {
			session.transitionState(GameSession.GameState.FINAL_CHANCE);
			sgm.broadcastStateChange(session);
		} else {
			sgm.endGameMessage(session, "liarVictory");
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
		sgm.clientInfoMessage(session);
	}

	// ================== 보조 메서드 ================== //
	private void terminateGame(String roomKey) {
		GameSession session = gameSessions.get(roomKey);
		session.getClients().forEach(this::safeCloseSession);
		sgm.endGameMessage(session, "userLeave");
	}

	private void safeCloseSession(Session session) {
		try {
			if (session.isOpen())
				session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Session selectLiar(List<Session> clients) {
		Collections.shuffle(clients);
		return clients.get(0);
	}

	private void distributeTurns(GameSession session) {
		List<Session> clients = session.getClients();
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).getUserProperties().put("turn", i + 1);
			sgm.assignTurnToClient(clients.get(i), i + 1);
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