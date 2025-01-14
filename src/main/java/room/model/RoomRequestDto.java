package room.model;

import java.sql.Timestamp;

public class RoomRequestDto {
	private String roomCode;
	private String hostUser;
	private int roomNumber;
	private String roomTitle;
	private boolean isPrivate;
	private String roomPassword;
	private int maxPlayers;
	private int roundCount;
	private String roomState;
	private Timestamp regDate;
	private Timestamp modDate;

	public RoomRequestDto() {

	}

	public RoomRequestDto(String hostUser, String roomTitle, boolean isPrivate, String roomPassword, int maxPlayers,
			int roundCount) {

		this.hostUser = hostUser;
		this.roomTitle = roomTitle;
		this.isPrivate = isPrivate;
		this.roomPassword = roomPassword;
		this.maxPlayers = maxPlayers;
		this.roundCount = roundCount;

	}

	public RoomRequestDto(String hostUser, String roomTitle, String isPrivate, String roomPassword, String maxPlayers,
			String roundCount) {

		this.hostUser = hostUser;
		this.roomTitle = roomTitle;
		this.isPrivate = isPrivate.equals("on");
		this.roomPassword = roomPassword;
		this.maxPlayers = Integer.parseInt(maxPlayers);
		this.roundCount = Integer.parseInt(roundCount);

	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public String getHostUser() {
		return hostUser;
	}

	public void setHostUser(String hostUser) {
		this.hostUser = hostUser;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getRoomTitle() {
		return roomTitle;
	}

	public void setRoomTitle(String roomTitle) {
		this.roomTitle = roomTitle;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getRoomPassword() {
		return roomPassword;
	}

	public void setRoomPassword(String roomPassword) {
		this.roomPassword = roomPassword;
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
	}

	public String getRoomState() {
		return roomState;
	}

	public void setRoomState(String roomState) {
		this.roomState = roomState;
	}

	public Timestamp getRegDate() {
		return regDate;
	}

	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}

	public Timestamp getModDate() {
		return modDate;
	}

	public void setModDate(Timestamp modDate) {
		this.modDate = modDate;
	};

}
