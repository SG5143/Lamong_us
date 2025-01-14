package room.model;

import java.sql.Timestamp;

public class Room {

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

	public Room(String roomCode, String hostUser, int roomNumber, String roomTitle, boolean isPrivate,
			String roomPassword, int maxPlayers, int roundCount, String roomState, Timestamp regDate,
			Timestamp modDate) {
		super();
		this.roomCode = roomCode;
		this.hostUser = hostUser;
		this.roomNumber = roomNumber;
		this.roomTitle = roomTitle;
		this.isPrivate = isPrivate;
		this.roomPassword = roomPassword;
		this.maxPlayers = maxPlayers;
		this.roundCount = roundCount;
		this.roomState = roomState;
		this.regDate = regDate;
		this.modDate = modDate;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public String getHostUser() {
		return hostUser;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public String getRoomTitle() {
		return roomTitle;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public String getRoomPassword() {
		return roomPassword;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getRoundCount() {
		return roundCount;
	}

	public String getRoomState() {
		return roomState;
	}

	public Timestamp getRegDate() {
		return regDate;
	}

	public Timestamp getModDate() {
		return modDate;
	}

}
