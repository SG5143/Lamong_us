package websoket;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import jakarta.websocket.Session;

public class LiarGameManager {
    private static final String TYPE_MESSAGE = "MESSAGE";
    private static final String TYPE_SET_TURN = "SET_TURN";
    private static final String TYPE_GAME_START = "GAME_START";
    private static final String TYPE_CUR_TURN = "CUR_TURN";

    private static final String TURN = "turn";
    private static final String IS_END = "isEnd";
    private static final String CURRENT_TURN = "currentTurn";

    private final Logger LOGGER = Logger.getLogger(LiarGameManager.class.getName());

    private Map<String, Map<String, Object>> gameSessions = new HashMap<>();
    private Map<String, String> playerAssignments = new HashMap<>();

    private LiarGameManager() {
    }

    private static final LiarGameManager INSTANCE = new LiarGameManager();

    public static LiarGameManager getInstance() {
        return INSTANCE;
    }

    public void startGame(final GameStartDto gameStartDto) {
        initializeGame(gameStartDto);

        String roomKey = gameStartDto.getRoomKey();
        Map<String, Object> gameSession = gameSessions.get(roomKey);
        boolean isGameEnded = (boolean) gameSession.get(IS_END);
        if (isGameEnded) {
            endGame(gameStartDto.getRoomKey());
        }
    }

    // 게임 메시지 처리
    public boolean processGameMessage(String roomKey, Session session, String message) {
        Map<String, Object> gameSession = gameSessions.get(roomKey);
        int currentTurn = (int) gameSession.get(CURRENT_TURN);

        int rounds = (int) gameSession.get("rounds");
        int round = (int) gameSession.get("round");

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

    // 게임 턴 정보 업데이트
    private void updateGameTurn(Map<String, Object> gameSession, String roomKey) {
        @SuppressWarnings("unchecked")
        List<Session> clients = (List<Session>) gameSession.get("clients");
        int currentTurn = (int) gameSession.get(CURRENT_TURN);
        currentTurn = (currentTurn % clients.size()) + 1;

        if (currentTurn == 1) {
            if (!sendRoundInfo(gameSession, roomKey, currentTurn)) {
                return;
            }
        }

        gameSession.put(CURRENT_TURN, currentTurn);
        sendTurnInfo(clients, currentTurn);
    }

    // 현재 클라이언트 차례인지 확인
    private boolean isCurrentPlayerTurn(Session session, int currentTurn) {
        return session.getUserProperties().get(TURN) != null &&
                (Integer) session.getUserProperties().get(TURN) == currentTurn;
    }

    // 현재 턴 정보를 클라이언트로 전송하는 메서드
    private void sendTurnInfo(List<Session> clients, int currentTurn) {
        for (Session client : clients) {
            try {
                sendJsonMessage(client, createJsonMessage(TYPE_CUR_TURN, CURRENT_TURN, currentTurn));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    // 데이터 정리 및 게임 종료
    private void endGame(final String ROOM_KEY) {
        gameSessions.remove(ROOM_KEY);
        playerAssignments.remove(ROOM_KEY);
    }

    // 게임 초기화
    private void initializeGame(GameStartDto gameStartDto) {
        String roomKey = gameStartDto.getRoomKey();
        List<Session> clients = new ArrayList<>(gameStartDto.getClients());
        Session liar = assignLiar(roomKey, clients);

        gameSessions.put(roomKey, createGameSession(liar.getId(), gameStartDto.getTopic(), gameStartDto.getKeyword(),
                gameStartDto.getRounds(), clients));

        assignTurns(clients);
        distributeWords(clients, liar, gameStartDto.getTopic(), gameStartDto.getKeyword());
        sendTurnInfo(clients, 1);
        sendChangeRound(clients, 1);
    }

    // 라운드 정보 전송
    private boolean sendRoundInfo(Map<String, Object> gameSession, String roomKey, int currentTurn) {
        @SuppressWarnings("unchecked")
        List<Session> clients = (List<Session>) gameSession.get("clients");

        int rounds = (int) gameSession.get("rounds");
        int round = (int) gameSession.get("round");
        round++;
        gameSession.put("round", round);

        if (round > rounds) {
            sendNewRound(clients);
            return false;
        }

        sendChangeRound(clients, round);
        return true;
    }

    private void sendChangeRound(List<Session> clients, int round){
        for (Session client : clients) {
            try {
                JSONObject jsonObject = createJsonMessage("ROUND_INFO", "round", round);
                sendJsonMessage(client, jsonObject);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    private void sendNewRound(List<Session> clients) {
        for (Session client : clients) {
            try {
                JSONObject jsonObject = createJsonMessage("ROUND_END", "message", "라운드 종료");
                sendJsonMessage(client, jsonObject);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
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
        playerAssignments.put(roomKey, liar.getId());
        return liar;
    }

    // 클라이언트 그룹 순서 배정하기
    private void assignTurns(List<Session> clients) {
        List<Session> clientList = new ArrayList<>(clients);
        for (int i = 0; i < clients.size(); i++) {
            assignTurnToClient(clientList.get(i), i + 1);
        }
    }

    // 순서 기록 및 전달
    private void assignTurnToClient(Session client, int turn) {
        try {
            client.getUserProperties().put(TURN, turn);
            sendJsonMessage(client, createJsonMessage(TYPE_SET_TURN, TURN, turn));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    // 단어 분배
    private void distributeWords(List<Session> clients, Session liar, String topic, String keyword) {
        for (Session client : clients) {
            try {
                JSONObject wordMessage = createJsonMessage(TYPE_GAME_START, "topic",
                        topic, "keyword", client.equals(liar) ? null : keyword); // 라이어는 키워드 제외
                sendJsonMessage(client, wordMessage);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    // 메시지 브로드캐스트
    private void broadcastMessage(String roomKey, String senderId, String message) {
        Map<String, Object> gameSession = gameSessions.get(roomKey);
        @SuppressWarnings("unchecked")
        List<Session> clients = (List<Session>) gameSession.get("clients");

        JSONObject chatMessage = createJsonMessage(TYPE_MESSAGE, "sessionId", senderId, "message", message);
        clients.forEach(client -> {
            try {
                sendJsonMessage(client, chatMessage);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        });
    }

    // JSON 오브젝트 생성
    private JSONObject createJsonMessage(String type, Object... keyValuePairs) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            jsonObject.put(String.valueOf(keyValuePairs[i]), keyValuePairs[i + 1]);
        }
        return jsonObject;
    }

    // JSON 메시지 전송
    private void sendJsonMessage(Session client, JSONObject message) throws IOException {
        client.getBasicRemote().sendText(message.toString());
    }
}