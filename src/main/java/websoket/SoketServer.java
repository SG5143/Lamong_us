package websoket;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import chat.model.ChatRequestDto;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

@ServerEndpoint("/ws/{roomType}/{roomUUID}")
public class SoketServer {
	private final Logger LOGGER = Logger.getLogger(SoketServer.class.getName());

	private LiarGameManager liarGameManager = LiarGameManager.getInstance();
	private ChatRoomManager chatRoomManager = ChatRoomManager.getInstance();

	@OnOpen
	public void onOpen(Session session, @PathParam("roomType") String roomType,
			@PathParam("roomUUID") String roomUUID) {
		if (!chatRoomManager.addClientToRoom(roomType, roomUUID, session)) {
			closeSession(session);
			return;
		}

		if ("playing".equals(roomType)) {
			Set<Session> clients = chatRoomManager.getClientsInRoom(roomType, roomUUID);
			if (clients.size() == 2) {
				startGameForAllClients(roomType, roomUUID, clients);
			}
		}
	}

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("roomType") String roomType,
			@PathParam("roomUUID") String roomUUID) throws IOException {
		String roomKey = roomType + "/" + roomUUID;

		// 로그인 세션 연결 이전 테스트로 세션 구분하기 위한 코드
		if ("TEST_SESSION_ID".equals(message)) {
			JSONObject sessionIdMessage = new JSONObject();
			sessionIdMessage.put("type", "SESSION_ID");
			sessionIdMessage.put("sessionId", session.getId());
			session.getBasicRemote().sendText(sessionIdMessage.toString());
			return;
		}

		// if ("GET_CHAT_HISTORY".equals(message)) {
		// sendChatHistory(session, roomType, roomUUID);
		// return;
		// }

		JSONObject jsonObject = new JSONObject(message);
		String extractedMessage = jsonObject.getString("message");

		if ("playing".equals(roomType)) {
			if (liarGameManager.processGameMessage(roomKey, session, extractedMessage)) {
				ChatRequestDto chatRequest = new ChatRequestDto(session.getId(), extractedMessage);
				chatRoomManager.addChatToRoomHistory(roomType, roomUUID, chatRequest);
			}
		}
	}

	@OnClose
	public void onClose(Session session, @PathParam("roomType") String roomType,
			@PathParam("roomUUID") String roomUUID) {
		chatRoomManager.removeClientFromRoom(roomType, roomUUID, session);
	}

	@OnError
	public void onError(Throwable e) {
		LOGGER.log(Level.SEVERE, e.getMessage());
	}

	private void startGameForAllClients(String roomType, String roomUUID, Set<Session> clients) {
		String roomKey = roomType + "/" + roomUUID;
		String topic = "과일";
		String keyword = "사과";
		int rounds = 3;
		GameStartDto gameStartDto = new GameStartDto(roomKey, clients, topic, keyword, rounds);
		liarGameManager.startGame(gameStartDto);
	}

	private void closeSession(Session session) {
		try {
			session.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

//    private void sendChatHistory(Session session, String roomType, String roomUUID) throws IOException {
//        List<ChatRequestDto> chatHistory = chatRoomManager.getRoomChatHistory(roomType, roomUUID);
//        JSONArray chatHistoryJson = new JSONArray(chatHistory);
//        JSONObject chatHistoryMessage = new JSONObject();
//        chatHistoryMessage.put("type", "CHAT_HISTORY");
//        chatHistoryMessage.put("chatHistory", chatHistoryJson);
//        session.getBasicRemote().sendText(chatHistoryMessage.toString());
//    }
}
