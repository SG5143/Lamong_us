package ingame.model;

import java.sql.Timestamp;

public class InGame {
	private String inGameCode;
	private String roomCode;
	private String topic;
	private String keyword;
	private int round;
	private String endType;
	private String winType;
	private Timestamp regDate;
	
	public InGame(String inGameCode, String roomCode, String topic, 
			String keyword, int round, String endType, String winType, Timestamp regDate) {
		this.inGameCode = inGameCode;
		this.roomCode = roomCode;
		this.topic = topic;
		this.keyword = keyword;
		this.round = round;
		this.endType = endType;
		this.winType = winType;
		this.regDate = regDate;
	}

	public String getInGameCode() {
		return inGameCode;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public String getTopic() {
		return topic;
	}

	public String getKeyword() {
		return keyword;
	}

	public int getRound() {
		return round;
	}

	public String getEndType() {
		return endType;
	}

	public String getWinType() {
		return winType;
	}

	public Timestamp getRegDate() {
		return regDate;
	}
	
}
