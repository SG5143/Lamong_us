package websoket;

import java.util.List;
import jakarta.websocket.Session;

public class GameSession {
	private String liarId;
	private String topic;
	private String keyword;
	private int round;
	private int rounds;
	private int currentTurn;
	private boolean isEnd;
	private List<Session> clients;
	
	public GameSession(String liarId, String topic, String keyword, int rounds, List<Session> clients) {
		this.liarId = liarId;
		this.topic = topic;
		this.keyword = keyword;
		this.round = 1;
		this.rounds = rounds;
		this.currentTurn = 1;
		this.isEnd = false;
		this.clients = clients;
	}

	public String getLiarId() {
		return liarId;
	}

	public void setLiarId(String liarId) {
		this.liarId = liarId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getRounds() {
		return rounds;
	}

	public void setRounds(int rounds) {
		this.rounds = rounds;
	}

	public int getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(int currentTurn) {
		this.currentTurn = currentTurn;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public List<Session> getClients() {
		return clients;
	}

	public void setClients(List<Session> clients) {
		this.clients = clients;
	}

}
