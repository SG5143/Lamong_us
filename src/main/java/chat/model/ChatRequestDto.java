package chat.model;

public class ChatRequestDto {

	private String chatRoomCode;
	private String writer;
	private String message;

	public ChatRequestDto() {}

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

}
