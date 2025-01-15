package chat.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import util.DBManager;

public class ChatDao {
	private static final String LOCATION_WAITING = "waiting";
	private static final String LOCATION_PLAYING = "playing";
	
	private static final String COL_MESSAGE_CODE = "message_code";
	private static final String COL_LOCATION_TYPE = "location_type";
	private static final String COL_ROOM_CODE = "room_code";
	private static final String COL_INGAME_CODE = "ingame_code";
	private static final String COL_CHAT_ROOM_CODE = "chat_room_code";
	private static final String COL_WRITER = "writer";
	private static final String COL_MESSAGE = "message";
	private static final String COL_REG_DATE = "reg_date";

	private static final String INSERT_MESSAGE_SQL = "INSERT INTO Message(chat_room_code, writer, message) VALUES(?, ?, ?)";
	private static final String FETCH_USER_CHAT_FOR_ADMIN_SQL = "SELECT CR.chat_room_code, CR.location_type, CR.room_code, CR.ingame_code, M.message, M.reg_date "
			+ "FROM Message M JOIN ChatRoom CR ON M.chat_room_code = CR.chat_room_code " + "WHERE writer=? "
			+ "ORDER BY M.reg_date DESC " + "LIMIT 20 OFFSET ?";
	private static final String FETCH_USER_CHAT_COUNT_SQL = "SELECT COUNT(*) FROM Message WHERE writer=? ";
	private static final String FETCH_CHATS_BY_CHAT_ROOM_CODE_SQL= "SELECT * FROM Message where chat_room_code = ? ORDER BY reg_date DESC ";
	private static final String FETCH_WAITING_CHAT_ROOM_CODE_SQL ="SELECT chat_room_code FROM ChatRoom WHERE room_code =?";
	private static final String FETCH_PLAYING_CHAT_ROOM_CODE_SQL= "SELECT chat_room_code FROM ChatRoom WHERE ingame_code =?";
	private static final String FETCH_CREATE_WAITING_CHAT_ROOM_SQL="INSERT INTO ChatRoom (location_type, room_code) VALUES(?, ?)";
	private static final String FETCH_CREATE_PLAYING_CHAT_ROOM_SQL="INSERT INTO ChatRoom (location_type, ingame_code) VALUES(?, ?)";

	private ChatDao() {};

	private static ChatDao instance = new ChatDao();

	public static ChatDao getInstance() {
		return instance;
	}

	public boolean insertChat(ChatRequestDto dto) {

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(INSERT_MESSAGE_SQL)) {

			pstmt.setString(1, dto.getChatRoomCode());
			pstmt.setString(2, dto.getWriter());
			pstmt.setString(3, dto.getMessage());

			return pstmt.executeUpdate() > 0; // 성공 여부 알려주기

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Chat> getUserChatsForPage(String userUUID, int page) {
		List<Chat> list = new ArrayList<>();

		int offset = (page - 1) * 20;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FETCH_USER_CHAT_FOR_ADMIN_SQL);) {

			pstmt.setString(1, userUUID);
			pstmt.setInt(2, offset);

			list = mapChatsForUserChats(pstmt);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<Chat> mapChatsForUserChats(PreparedStatement pstmt) throws SQLException {
		List<Chat> list = new ArrayList<>();

		try (ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				String location = rs.getString(COL_LOCATION_TYPE);
				String locationCode = rs.getString(COL_ROOM_CODE) == null ? rs.getString(COL_INGAME_CODE)
						: rs.getString(COL_ROOM_CODE);
				String chatRoomCode = rs.getString(COL_CHAT_ROOM_CODE);
				String message = rs.getString(COL_MESSAGE);
				Timestamp regDate = rs.getTimestamp(COL_REG_DATE);

				list.add(new Chat(location, locationCode, chatRoomCode, message, regDate));
			}
		}
		return list;
	}

	public int getUserChatCount(String userUUID) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FETCH_USER_CHAT_COUNT_SQL);) {

			pstmt.setString(1, userUUID);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public List<Chat> getChatsByRoomCode(String roomCode){
		List<Chat> list = new ArrayList<>();
		
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FETCH_CHATS_BY_CHAT_ROOM_CODE_SQL);) {
			pstmt.setString(1, roomCode);

			list = mapChatsForRoomCode(pstmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
		
	private List<Chat> mapChatsForRoomCode(PreparedStatement pstmt) throws SQLException {
		List<Chat> list = new ArrayList<>();

		try (ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				int messageCode = rs.getInt(COL_MESSAGE_CODE);
				String writer = rs.getString(COL_WRITER);
				String message = rs.getString(COL_MESSAGE);
				Timestamp regDate = rs.getTimestamp(COL_REG_DATE);

				list.add(new Chat(messageCode, writer, message, regDate));
			}
		}
		return list;
	}

	public String getChatRoomCodeBylocationCode(String location, String locationCode) {
		String roomCode = null;
		String sql = null;

		switch (location) {
		case LOCATION_WAITING -> sql = FETCH_WAITING_CHAT_ROOM_CODE_SQL;
		case LOCATION_PLAYING -> sql = FETCH_PLAYING_CHAT_ROOM_CODE_SQL;
		default -> sql = null;
		}

		if (sql == null) return null;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, locationCode);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					roomCode = rs.getString(COL_CHAT_ROOM_CODE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return roomCode;
	}
		
	
	public boolean ensureChatRoomExists(String location, String locationCode) {
	    String chatRoomCode = getChatRoomCodeBylocationCode(location, locationCode);
	    if (chatRoomCode != null) return true;
	    return createChatRoomByLocation(location, locationCode);
	}
	
	private boolean createChatRoomByLocation(String location, String locationCode) {
		String sql = null;

		switch (location) {
		case LOCATION_WAITING -> sql = FETCH_CREATE_WAITING_CHAT_ROOM_SQL;
		case LOCATION_PLAYING -> sql = FETCH_CREATE_PLAYING_CHAT_ROOM_SQL;
		default -> sql = null;
		}

		if (sql == null) return false;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, location);
			pstmt.setString(2, locationCode);

			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
		
}
