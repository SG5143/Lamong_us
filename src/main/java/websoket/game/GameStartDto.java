package websoket.game;

import java.util.Set;
import jakarta.websocket.Session;
import room.model.Room;

public class GameStartDto {
	private String roomKey;
	private String waitRoomUUID;
	private String title;
	private int roomNumber;
	private int rounds;
	private Set<Session> clients;

	public GameStartDto(String roomUUID, Set<Session> clients, Room room) {
		this.roomKey = "play/" + roomUUID;
		this.waitRoomUUID = room.getCode();
		this.title = room.getTitle();
		this.roomNumber = room.getRoomNumber();
		this.rounds = room.getRoundCount();
		this.clients = clients;
	}

	public String getRoomKey() {
		return roomKey;
	}

	public Set<Session> getClients() {
		return clients;
	}

	public int getRounds() {
		return rounds;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public String getTitle() {
		return title;
	}
	
	public String getWaitRoomUUID() {
		return waitRoomUUID;
	}

}