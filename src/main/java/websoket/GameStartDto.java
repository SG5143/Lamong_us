package websoket;

import java.util.Set;
import jakarta.websocket.Session;

public class GameStartDto {
	private String roomKey;
	private int rounds;
	private Set<Session> clients;

	public GameStartDto(String roomKey, int rounds,  Set<Session> clients) {
		this.roomKey = roomKey;
		this.rounds = rounds;
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

}