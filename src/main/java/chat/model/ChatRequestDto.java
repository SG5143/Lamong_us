package chat.model;

import java.sql.Timestamp;

public class ChatRequestDto {
	private String chatRoomCode;
	private String writer;
	private String message;
	private Timestamp sentTime;

	public ChatRequestDto(String writer, String message) {
        this.writer = writer;
        this.message = message;
        this.sentTime = new Timestamp(System.currentTimeMillis());
	}

	public ChatRequestDto(String chatRoomCode, String writer, String message) {
		this.chatRoomCode = chatRoomCode;
		this.writer = writer;
		this.message = message;
	}

	public String getChatRoomCode() {
		return chatRoomCode;
	}

	public void setChatRoomCode(String chatRoomCode) {
		this.chatRoomCode = chatRoomCode;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Timestamp getSentTime() {
		return sentTime;
	}
	
    @Override
    public String toString() {
    	return String.format("Message{Writer=%s, Message='%s', sentTime=%s}\n", writer, message, sentTime);
    }
}
