package room.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DBManager;

public class RoomDao {

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	private RoomDao() {

	};

	private static RoomDao instance = new RoomDao();

	public static RoomDao getInstance() {
		return instance;
	};

	public void createRoom(RoomRequestDto roomDto) {
		conn = DBManager.getConnection();

		String sql = "INSERT INTO GameWatingRoom (host_user,room_title, is_private, "
				+ "room_password, max_players, round_count) VALUES(?, ?, ?, ?, ?, ?)";

		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, roomDto.getHostUser());
			pstmt.setString(2, roomDto.getRoomTitle());
			pstmt.setBoolean(3, roomDto.isPrivate());
			pstmt.setString(4, roomDto.getRoomPassword());
			pstmt.setInt(5, roomDto.getMaxPlayers());
			pstmt.setInt(6, roomDto.getRoundCount());

			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
