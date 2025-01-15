package room.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Room {

	private String code;
	private String host;
	private int roomNumber;
	private String title;
	private boolean isPrivate;
	private char[] password; // 4자리
	private int maxPlayers;
	private int roundCount;
	private String state;
	
	private Timestamp regDate;
	private Timestamp modDate;
	
	public Room(String code, String host, int roomNumber, String title, boolean isPrivate, char[] password,
			int maxPlayers, int roundCount, String state, Timestamp regDate, Timestamp modDate) {
		this.code = code;
		this.host = host;
		this.roomNumber = roomNumber;
		this.title = title;
		this.isPrivate = isPrivate;
		this.password = password;
		this.maxPlayers = maxPlayers;
		this.roundCount = roundCount;
		this.state = state; 
		this.regDate = regDate;
		this.modDate = modDate;
	}
	
	public Room(String code, String host, int roomNumber, String title, boolean isPrivate, String password,
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

	public String getHost() {
		return host;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public String getTitle() {
		return title;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public char[] getPassword() {
		return password;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getRoundCount() {
		return roundCount;
	}

	public String getState() {
		return state;
	}

	public Timestamp getRegDate() {
		return regDate;
	}

	public Timestamp getModDate() {
		return modDate;
	}

}
