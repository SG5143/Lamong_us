package websoket.wait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.websocket.Session;

public class RoomSession {
	private String roomUUID;
	private String hostUUID;
	private String title;
	private int rounds;
	private boolean isPrivate;
	private String password;
	private List<Session> clients;
	private Map<Session, Boolean> clientReadyStatus;

	public RoomSession(String roomUUID, String hostUUID, String title, boolean isPrivate, String password, int rounds) {
		this.roomUUID = roomUUID;
		this.title = title;
		this.isPrivate = isPrivate;
		this.password = password;
		this.rounds = rounds;
		this.clients = new ArrayList<>();
		this.clientReadyStatus = new HashMap<>();
	}
	
    public void addClient(Session session) {
        clients.add(session);
        clientReadyStatus.put(session, false); // 기본적으로 준비 상태는 false
    }
    
    public void removeClient(Session session) {
        clients.remove(session);
        clientReadyStatus.remove(session);
    }
    
    public void setClientReady(Session session, boolean isReady) {
        if (clientReadyStatus.containsKey(session)) {
            clientReadyStatus.put(session, isReady);
        }
    }
    
    // 모든 클라이언트가 준비 상태인지 확인
    public boolean areAllClientsReady() {
        for (Boolean ready : clientReadyStatus.values()) {
            if (!ready) {
                return false;
            }
        }
        return true;
    }
    
    // 방에 있는 모든 클라이언트의 준비 상태를 초기화
    public void resetAllClientsReadyStatus() {
        for (Session session : clientReadyStatus.keySet()) {
            clientReadyStatus.put(session, false);
        }
    }

	public String getRoomUUID() {
		return roomUUID;
	}

	public void setRoomUUID(String roomUUID) {
		this.roomUUID = roomUUID;
	}

	public String getHostUUID() {
		return hostUUID;
	}

	public void setHostUUID(String hostUUID) {
		this.hostUUID = hostUUID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRounds() {
		return rounds;
	}

	public void setRounds(int rounds) {
		this.rounds = rounds;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Session> getClients() {
		return clients;
	}

	public void setClients(List<Session> clients) {
		this.clients = clients;
	}
	
    public synchronized boolean shouldTerminate() {
        return clients.size() <= 0;
    }
    
}
