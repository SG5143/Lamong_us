package websoket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
import user.model.user.User;

import org.json.JSONObject;

@ServerEndpoint(value = "/ws/{roomType}/{roomUUID}", configurator = CustomConfigurator.class)
public class SoketServer {
	private final Logger LOGGER = Logger.getLogger(SoketServer.class.getName());

	private LiarGameManager liarGameManager = LiarGameManager.getInstance();
	private WaitRoomManager waitRoomManager = WaitRoomManager.getInstance();
	private ChatRoomManager chatRoomManager = ChatRoomManager.getInstance();

	@OnOpen
	public void onOpen(Session session, @PathParam("roomType") String roomType, @PathParam("roomUUID") String roomUUID, EndpointConfig config) {
		
		if (roomType == null && roomUUID == null) {
			return;
		}
		
		HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
		
		if (httpSession.getAttribute("log") != null) {
			User user = (User) httpSession.getAttribute("log");
			String uuid = user.getUuid();
			String nickname = user.getNickname();
			byte[] profileImage = user.getProfileImage();
			Integer score = user.getScore();

			Map<String, Object> userInfo = new HashMap<>();
			userInfo.put("uuid", uuid);
			userInfo.put("nickname", nickname);
			userInfo.put("profileImage", profileImage);
			userInfo.put("score", score);
			session.getUserProperties().put("userInfo", userInfo);
		} else {
			String uuid = session.getId();
			String nickname = "임시닉" + uuid;
			String profileImage = "resources/images/Default" + String.format("%02d", new Random().nextInt(11) + 1) + ".jpg";
			Integer score = 1000;

			Map<String, Object> userInfo = new HashMap<>();
			userInfo.put("uuid", uuid);
			userInfo.put("nickname", nickname);
			userInfo.put("profileImage", profileImage);
			userInfo.put("score", score);

			System.out.println("uuid:" + uuid);
			session.getUserProperties().put("userInfo", userInfo);
		}

		if (!chatRoomManager.addClientToRoom(roomType, roomUUID, session)) {
			closeSession(session);
			return;
		}

		if ("play".equals(roomType)) {
			Set<Session> clients = chatRoomManager.getClientsInRoom(roomType, roomUUID);
			if (clients.size() == 2)
				startGameForAllClients(roomType, roomUUID, clients);
		}
	}

	@OnMessage
	@SuppressWarnings("unchecked")
	public void onMessage(String message, Session session, @PathParam("roomType") String roomType, @PathParam("roomUUID") String roomUUID) throws IOException {
		String roomKey = roomType + "/" + roomUUID;

		// if ("GET_CHAT_HISTORY".equals(message)) {
		// sendChatHistory(session, roomType, roomUUID);
		// return;
		// }

		if ("TEST_SESSION_ID".equals(message)) {
			JSONObject sessionIdMessage = new JSONObject();
			sessionIdMessage.put("type", "SESSION_ID");
			Map<String, Object> info = (Map<String, Object>) session.getUserProperties().get("userInfo");
			String uuid = (String) info.get("uuid");
			sessionIdMessage.put("uuid", uuid);
			session.getBasicRemote().sendText(sessionIdMessage.toString());
			return;
		}

		System.out.println(message);
		
		if ("play".equals(roomType)) {
			if (liarGameManager.handleGameMessage(roomKey, session, message)) {
				JSONObject jsonObject = new JSONObject(message);
				String extractedMessage = jsonObject.getString("message");
				Map<String, Object> userInfo = (Map<String, Object>) session.getUserProperties().get("userInfo");
				ChatRequestDto chatRequest = new ChatRequestDto((String) userInfo.get("uuid"), extractedMessage);
				chatRoomManager.addChatToRoomHistory(roomType, roomUUID, chatRequest);
			}
		}else if("wait".equals(roomType)) {
			Set<Session> clients = chatRoomManager.getClientsInRoom(roomType, roomUUID);
			if(waitRoomManager.handleRoomMessage(roomKey, session, clients, message)) {
				JSONObject jsonObject = new JSONObject(message);
				String extractedMessage = jsonObject.getString("message");
				Map<String, Object> userInfo = (Map<String, Object>) session.getUserProperties().get("userInfo");
				ChatRequestDto chatRequest = new ChatRequestDto((String) userInfo.get("uuid"), extractedMessage);
				chatRoomManager.addChatToRoomHistory(roomType, roomUUID, chatRequest);
			}
		}
	}

	@OnClose
	public void onClose(Session session, @PathParam("roomType") String roomType, @PathParam("roomUUID") String roomUUID) {	    
		if ("playing".equals(roomType)) {
	        liarGameManager.manageClientDisconnection(roomType + "/" + roomUUID, session);
	    }
		chatRoomManager.removeClientFromRoom(roomType, roomUUID, session);
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
