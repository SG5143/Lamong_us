package websoket;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import jakarta.websocket.Session;

public class LiarGameManager {
	private final Logger LOGGER = Logger.getLogger(LiarGameManager.class.getName());

	private Map<String, Map<String, Object>> liarGameSessions = new HashMap<>();
	private Map<String, String> liarAssignments = new HashMap<>();

	private LiarGameManager() {
	}

	private static final LiarGameManager INSTANCE = new LiarGameManager();

	public static LiarGameManager getInstance() {
		return INSTANCE;
	}

	public void runGame(final GameStartDto GAME_START_DTO) {
		initGame(GAME_START_DTO);

		String roomKey = GAME_START_DTO.getRoomKey();
		Map<String, Object> gameSession = liarGameSessions.get(roomKey);
		boolean isEnd = (boolean) gameSession.get("isEnd");
		if (isEnd) {
			endGame(GAME_START_DTO.getRoomKey());
		}
	}

	public boolean handleGameMessage(String roomKey, Session session, String message) {
		Map<String, Object> gameSession = liarGameSessions.get(roomKey);
		@SuppressWarnings("unchecked")
		List<Session> clients = (List<Session>) gameSession.get("clients");
		int currentTurn = (int) gameSession.get("currentTurn");

		String senderId = session.getId();

		if (clients.get(currentTurn - 1).equals(session)) {
			// 메시지 전송
			broadcastMessage(roomKey, senderId, message);
			
			// 다음 턴을 위해 +1
			currentTurn = (currentTurn % clients.size()) + 1;
			
			// 해당 게임세션 턴 정보 갱신
			gameSession.put("currentTurn", currentTurn);
			liarGameSessions.put(roomKey, gameSession);
			
			// 해당 게임세션 턴 정보 전달
			sendTurnInfo(clients, currentTurn);
			return true;
		}
		return false;
	}

	// 현재 턴 정보를 클라이언트로 전송하는 메서드
	private void sendTurnInfo(List<Session> clients, int currentTurn) {
		for (Session client : clients) {
			try {
				JSONObject turnMessage = new JSONObject();
				turnMessage.put("type", "CUR_TURN");
				turnMessage.put("currentTurn", currentTurn);
				client.getBasicRemote().sendText(turnMessage.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void endGame(final String ROOM_KEY) {
		liarGameSessions.remove(ROOM_KEY);
		liarAssignments.remove(ROOM_KEY);
	}

	private void initGame(GameStartDto gameStartDto) {
		String roomKey = gameStartDto.getRoomKey();
		Set<Session> clients = gameStartDto.getClients();
		String topic = gameStartDto.getTopic();
		String keyword = gameStartDto.getKeyword();
		int rounds = gameStartDto.getRounds();

		List<Session> clientList = new ArrayList<>(clients);
		Session liar = assignLiar(roomKey, clientList);

		Map<String, Object> gameSession = createGameSession(liar.getId(), topic, keyword, rounds, clientList);
		liarGameSessions.put(roomKey, gameSession);

		assignTurns(clients);
		distributeWords(clients, liar, topic, keyword);
	}

	// 게임 세션 정보 생성
	private Map<String, Object> createGameSession(String liarId, String topic, String keyword, int rounds,
			List<Session> clients) {
		Map<String, Object> gameSession = new HashMap<>();
		gameSession.put("liarId", liarId);
		gameSession.put("topic", topic);
		gameSession.put("keyword", keyword);
		gameSession.put("rounds", rounds);
		gameSession.put("clients", clients);
		gameSession.put("currentTurn", 1);
		gameSession.put("round", 1);
		gameSession.put("isEnd", false);
		return gameSession;
	}

	// 라이어 지정
	private Session assignLiar(String roomKey, List<Session> clientList) {
		Collections.shuffle(clientList);
		Session liar = clientList.get(0);
		liarAssignments.put(roomKey, liar.getId());
		return liar;
	}

	// 순서 지정 - error
	private void assignTurns(Set<Session> clients) {
		int turn = 1;
		for (Session client : clients) {
			try {
				JSONObject turnMessage = new JSONObject();
				turnMessage.put("type", "SET_TURN");
				turnMessage.put("turn", turn);
				client.getBasicRemote().sendText(turnMessage.toString());
				turn++;
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
		}
	}

	// 단어 분배
	private void distributeWords(Set<Session> clients, Session liar, String topic, String keyword) {
		for (Session client : clients) {
			try {
				JSONObject wordMessage = new JSONObject();
				wordMessage.put("type", "GAME_START");

				if (!client.equals(liar)) {
					wordMessage.put("topic", topic);
					wordMessage.put("keyword", keyword);
				}

				client.getBasicRemote().sendText(wordMessage.toString());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
		}
	}

	private void broadcastMessage(String roomKey, String senderId, String message) {
		Map<String, Object> gameSession = liarGameSessions.get(roomKey);
		@SuppressWarnings("unchecked")
		List<Session> clients = (List<Session>) gameSession.get("clients");

		for (Session client : clients) {
			try {
				JSONObject chatMessage = new JSONObject();
				chatMessage.put("type", "MESSAGE");
				chatMessage.put("sessionId", senderId);
				chatMessage.put("message", message);
				client.getBasicRemote().sendText(chatMessage.toString());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
		}

	}
}