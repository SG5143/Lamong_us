package room.model;

public class RoomResponseDto {

	private String code;
	private String host;
	private int roomNumber;
	private String title;
	private boolean isPrivate;
	private char[] password; // 4자리
	private int maxPlayers;
	private int roundCount;

	public RoomResponseDto() {}

	public RoomResponseDto(String code, String host, int roomNumber, String title, boolean isPrivate, String password,
			int maxPlayers, int roundCount) {
		this.code = code;
		this.host = host;
		this.roomNumber = roomNumber;
		this.title = title;
		this.isPrivate = isPrivate;
		this.password = password.length() > 4 ? password.substring(0, 4).toCharArray() : password.toCharArray();
		this.maxPlayers = maxPlayers;
		this.roundCount = roundCount;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getPassword() {
		return new String(password);
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public int getRoundCount() {
		return roundCount;
	}

	public void setRoundCount(int roundCount) {
		this.roundCount = roundCount;
	};

}
