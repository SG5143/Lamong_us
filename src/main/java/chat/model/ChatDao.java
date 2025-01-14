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
	private static final String COL_LOCATION_TYPE = "location_type";
	private static final String COL_ROOM_CODE = "room_code";
	private static final String COL_INGAME_CODE = "ingame_code";
	private static final String COL_CHAT_ROOM_CODE = "chat_room_code";
	private static final String COL_MESSAGE = "message";
	private static final String COL_REG_DATE = "reg_date";
	
	private static final String INSERT_MESSAGE_SQL = "INSERT INTO Message(chat_room_code, writer, message) VALUES(?, ?, ?)";
	private static final String FETCH_USER_CHAT_FOR_ADMIN_SQL = "SELECT CR.chat_room_code, CR.location_type, CR.room_code, CR.ingame_code, M.message, M.reg_date "
			+ "FROM Message M JOIN ChatRoom CR ON M.chat_room_code = CR.chat_room_code " + "WHERE writer=? "
			+ "ORDER BY M.reg_date DESC " + "LIMIT 20 OFFSET ?";

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
			System.err.println("ChatDao: insertChat(ChatRequestDto dto) Error");
			e.printStackTrace();
			return false;
		}
	}

	public List<Chat> fetchUserChatsForPage(String userUUID, int page) {
		List<Chat> list = new ArrayList<>();;

		try (Connection conn = DBManager.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(FETCH_USER_CHAT_FOR_ADMIN_SQL);) {
			
			pstmt.setString(1, userUUID);
			pstmt.setInt(2, page * 20);
			
			list = fetchChatByUserUUID(pstmt);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<Chat> fetchChatByUserUUID(PreparedStatement pstmt) throws SQLException {
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
}
