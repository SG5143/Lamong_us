package websoket.wait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;
import jakarta.websocket.Session;
import room.model.RoomDao;
import websoket.JsonUtil;

public class WaitRoomManager {
	private static final WaitRoomManager INSTANCE = new WaitRoomManager();
	private static final RoomDao roomDao = RoomDao.getInstance();
	private final Map<String, RoomSession> roomSessions = new ConcurrentHashMap<>();

	private WaitRoomManager() {};
	
	public static WaitRoomManager getInstance() {
		return INSTANCE;
	}
	
	public boolean findRoomByRoomKey(String RoomKey) {
		if(roomSessions.containsKey(RoomKey)) {
			return true;
		}
		return false;
	}
	
	public void initializeRoom(String roomKey, RoomSession roomSession) {
		this.roomSessions.put(roomKey, roomSession);
	}
	
	public void enterClient(String roomKey, Session client) {
		RoomSession session = roomSessions.get(roomKey);
		session.addClient(client);
	}
	
	@SuppressWarnings("unchecked")
	public void manageClientDisconnection(String roomKey, Session client) {
		RoomSession session = roomSessions.get(roomKey);
		if(session == null) {
			return;
		}
		
		session.removeClient(client);
		
		Map<String, Object> userInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
		String uuid = (String) userInfo.get("uuid");
		
		// 방장이 나갔을 경우
		if (uuid.equals(session.getHostUUID())) {
			Set<Session> clients = (Set<Session>) session.getClients();
			if (!clients.isEmpty()) {
				Session newHost = clients.iterator().next(); // 첫 번째 유저 선택
				Map<String, Object> newHostInfo = (Map<String, Object>) newHost.getUserProperties().get("userInfo");
				String newHostUUID = (String) newHostInfo.get("uuid");
				session.setHostUUID(newHostUUID); // 새로운 방장 설정
			}
		}
		
		// 인원이 없으면 방삭제
		if (session.getClients().isEmpty()) {
			roomSessions.remove(roomKey);
			roomDao.deleteRoomByCode(roomKey.split("/")[1]); 
			return;
		}

		broadcastPlayersInfo(roomKey);
	}
	
	public boolean handleRoomMessage(String roomKey, Session client, String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");
            
            switch (type) {
			case "PLAYER_INFO":
				return broadcastPlayersInfo(roomKey);
			case "MESSAGE":
				return handleChatMessage(roomKey, client, message);
			case "READY":
				return false;
			case "START":
				return false;
            };
            return false;
        } catch (Exception e) {
        	System.out.println("메시지 처리 에러: " + e.getMessage());
            return false;
        }
    }
	
	@SuppressWarnings("unchecked")
	private boolean handleChatMessage(String roomKey, Session sender, String message) {
		RoomSession session = roomSessions.get(roomKey);
		List<Session> clients = session.getClients();
		
		Map<String, Object> userInfo = (Map<String, Object>) sender.getUserProperties().get("userInfo");
		String uuid = (String) userInfo.get("uuid");
		String nickname = (String) userInfo.get("nickname");
		String image = (String) userInfo.get("profileImage");

		JSONObject jsonObject = new JSONObject(message);
		String extractedMsg = jsonObject.getString("message");

		JSONObject chatMessage = JsonUtil.createJsonMessage(
				"MESSAGE",
				"sender", uuid,
				"nickname", nickname,
				"profileImage", image,
				"message", extractedMsg);
		clients.forEach(client -> {
			try {
				client.getBasicRemote().sendText(chatMessage.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return true;
	}
	
	public boolean broadcastPlayersInfo(String roomKey) {
		RoomSession session = roomSessions.get(roomKey);
		List<Session> clients = session.getClients();
		
		List<Map<String, Object>> playersInfo = new ArrayList<>();
		
		for (Session client : clients) {
			@SuppressWarnings("unchecked")
			Map<String, Object> clientInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
			playersInfo.add(clientInfo);
		}
		
		for (Session client : clients) {
			JSONObject jsonObject = JsonUtil.createJsonMessage(
					"USER_UPDATE",
					"clientInfo", playersInfo);
			try {
				client.getBasicRemote().sendText(jsonObject.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    return false;
	}
}
