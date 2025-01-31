package websoket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import jakarta.websocket.Session;

public class WaitRoomManager {
	private static final WaitRoomManager INSTANCE = new WaitRoomManager();
	
	private WaitRoomManager() {};
	
	public static WaitRoomManager getInstance() {
		return INSTANCE;
	}
	
	public boolean handleRoomMessage(String roomKey, Session client, Set<Session> clients, String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");
            
            switch (type) {
                case "MESSAGE" : return handleChatMessage(client, clients, message);
            };
            return false;
        } catch (Exception e) {
        	System.out.println("메시지 처리 에러: " + e.getMessage());
            return false;
        }
    }
	
	@SuppressWarnings("unchecked")
	private boolean handleChatMessage(Session sender, Set<Session> clients, String message){
		Map<String, Object> userInfo = (Map<String, Object>) sender.getUserProperties().get("userInfo");
		String uuid = (String) userInfo.get("uuid");
		String nickname = (String) userInfo.get("nickname");
		
		JSONObject jsonObject = new JSONObject(message);
		String extractedMessage = jsonObject.getString("message");

		JSONObject chatMessage = JsonUtil.createJsonMessage(
				MessageConstants.TYPE_MESSAGE,
				MessageConstants.SENDER_UUID, uuid,
				MessageConstants.SENDER_NICKNAME, nickname,
				MessageConstants.MESSAGE, extractedMessage);
		clients.forEach(client -> {
			try {
				client.getBasicRemote().sendText(chatMessage.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return true;
	}
	
}
