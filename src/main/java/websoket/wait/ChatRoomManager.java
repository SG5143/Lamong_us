package websoket.wait;

import java.util.*;
import chat.model.ChatRequestDto;
import jakarta.websocket.Session;

public class ChatRoomManager {
	private static final int MAX_CLIENTS = 9;
	private static Map<String, Map<String, Set<Session>>> roomClients = new HashMap<>();
	private static Map<String, Map<String, List<ChatRequestDto>>> roomChatHistory = new HashMap<>();

	private ChatRoomManager() {
	}

	private static final ChatRoomManager INSTANCE = new ChatRoomManager();

	public static ChatRoomManager getInstance() {
		return INSTANCE;
	}

	public boolean addClientToRoom(String roomType, String roomCode, Session session) {
		roomClients.computeIfAbsent(roomType, k -> new HashMap<>()).computeIfAbsent(roomCode, k -> new HashSet<>());

		Set<Session> clients = roomClients.get(roomType).get(roomCode);
		if (clients.size() >= MAX_CLIENTS) {
			return false;
		}

		clients.add(session);
		return true;
	}

	public void removeClientFromRoom(String roomType, String roomCode, Session session) {
		Map<String, Set<Session>> rooms = roomClients.get(roomType);
		if (rooms != null) {
			Set<Session> sessions = rooms.get(roomCode);
			if (sessions != null) {
				sessions.remove(session);
				if (sessions.isEmpty()) {
					rooms.remove(roomCode);
				}
			}
			if (rooms.isEmpty()) {
				roomClients.remove(roomType);
			}
		}
	}

	public void addChatToRoomHistory(String roomType, String roomCode, ChatRequestDto chatRequest) {
		roomChatHistory.computeIfAbsent(roomType, k -> new HashMap<>())
				.computeIfAbsent(roomCode, k -> new ArrayList<>()).add(chatRequest);
	}

	public List<ChatRequestDto> getRoomChatHistory(String roomType, String roomCode) {
		return roomChatHistory.getOrDefault(roomType, Collections.emptyMap()).getOrDefault(roomCode,
				Collections.emptyList());
	}

	public Set<Session> getClientsInRoom(String roomType, String roomCode) {
		return roomClients.getOrDefault(roomType, Collections.emptyMap()).getOrDefault(roomCode,
				Collections.emptySet());
	}
}