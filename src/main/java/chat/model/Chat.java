package chat.model;

import java.sql.Timestamp;

// 채팅 메시지 VO
public class Chat {

	private int code;
	private String chatRoomCode;
	private String writer;
	private String message;
	private Timestamp regDate;

	private String location;
	private String locationCode;

	public Chat(int code, String writer, String message, Timestamp regDate) {
		this.code = code;
		this.writer = writer;
		this.message = message;
		this.regDate = regDate;
	}

	public Chat(String location, String locationCode, String chatRoomCode, String message, Timestamp regDate) {
		this.chatRoomCode = chatRoomCode;
		this.message = message;
		this.regDate = regDate;
		this.location = location;
		this.locationCode = locationCode;
	}

	public int getCode() {
		return code;
	}

	public String getChatRoomCode() {
		return chatRoomCode;
	}

	public String getWriter() {
		return writer;
	}

	public String getMessage() {
		return message;
	}

	public Timestamp getRegDate() {
		return regDate;
	}

	public String getLocation() {
		return location;
	}

	public String getLocationCode() {
		return locationCode;
	}

}
