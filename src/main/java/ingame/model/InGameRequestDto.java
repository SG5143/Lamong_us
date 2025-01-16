package ingame.model;

public class InGameRequestDto {
	private String code;
	private String roomCode;
	private String topic;
	private String keyword;
	private int round;
	private String endType;
	private String winType;
	
	public InGameRequestDto() {}

	public InGameRequestDto(String code, String topic, String keyword, int round) {
		this.code = code;
		this.topic = topic;
		this.keyword = keyword;
		this.round = round;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
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

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getEndType() {
		return endType;
	}

	public void setEndType(String endType) {
		this.endType = endType;
	}

	public String getWinType() {
		return winType;
	}

	public void setWinType(String winType) {
		this.winType = winType;
	}
	
}
