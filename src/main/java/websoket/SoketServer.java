package websoket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import chat.model.ChatRequestDto;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

@ServerEndpoint(value = "/ws/{roomType}/{roomUUID}", configurator = CustomConfigurator.class)
public class SoketServer {
	private final Logger LOGGER = Logger.getLogger(SoketServer.class.getName());

	private LiarGameManager liarGameManager = LiarGameManager.getInstance();
	private ChatRoomManager chatRoomManager = ChatRoomManager.getInstance();

	@OnOpen
	public void onOpen(Session session, @PathParam("roomType") String roomType, @PathParam("roomUUID") String roomUUID, EndpointConfig config) {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");

		if (httpSession != null) {
			String uuid = (String) httpSession.getAttribute("uuid");
			String nickname = (String) httpSession.getAttribute("nickname");
			String profileImage = (String) httpSession.getAttribute("profileImage");
			Integer score = (Integer) httpSession.getAttribute("score");

			Map<String, Object> userInfo = new HashMap<>();
			userInfo.put("uuid", uuid);
			userInfo.put("nickname", nickname);
			userInfo.put("profileImage", profileImage);
			userInfo.put("score", score);

			session.getUserProperties().put("userInfo", userInfo);
		}

		if (!chatRoomManager.addClientToRoom(roomType, roomUUID, session)) {
			closeSession(session);
			return;
		}

		if ("playing".equals(roomType)) {
			Set<Session> clients = chatRoomManager.getClientsInRoom(roomType, roomUUID);
			if (clients.size() == 2)
				startGameForAllClients(roomType, roomUUID, clients);
		}
	}

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("roomType") String roomType, @PathParam("roomUUID") String roomUUID) throws IOException {
		String roomKey = roomType + "/" + roomUUID;
		
		System.out.println(message);
		// 로그인 세션 연결 이전 테스트로 세션 구분하기 위한 코드
		if ("TEST_SESSION_ID".equals(message)) {
			JSONObject sessionIdMessage = new JSONObject();
			sessionIdMessage.put("type", "SESSION_ID");
			sessionIdMessage.put("uuid", session.getId());
			session.getBasicRemote().sendText(sessionIdMessage.toString());
			return;
		}

		// if ("GET_CHAT_HISTORY".equals(message)) {
		// sendChatHistory(session, roomType, roomUUID);
		// return;
		// }

		if ("playing".equals(roomType)) {
			if (liarGameManager.handleGameMessage(roomKey, session, message)) {
				JSONObject jsonObject = new JSONObject(message);
				String extractedMessage = jsonObject.getString("message");
				ChatRequestDto chatRequest = new ChatRequestDto(session.getId(), extractedMessage);
				chatRoomManager.addChatToRoomHistory(roomType, roomUUID, chatRequest);
			}
		}
	}

	@OnClose
	public void onClose(Session session, @PathParam("roomType") String roomType, @PathParam("roomUUID") String roomUUID) {
		chatRoomManager.removeClientFromRoom(roomType, roomUUID, session);
	    
		if ("playing".equals(roomType)) {
	        liarGameManager.manageClientDisconnection(roomType + "/" + roomUUID, session);
	    }
	}

	@OnError
	public void onError(Throwable e) {
		LOGGER.log(Level.SEVERE, e.getMessage());
	}

	private void startGameForAllClients(String roomType, String roomUUID, Set<Session> clients) {
		String roomKey = roomType + "/" + roomUUID;
		int rounds = 3;
		GameStartDto gameStartDto = new GameStartDto(roomKey, rounds, clients);
		liarGameManager.initializeNewGame(gameStartDto);
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
