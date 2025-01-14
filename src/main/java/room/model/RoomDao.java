package room.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBManager;

public class RoomDao {

	private static final String COL_ROOM_CODE = "code";
	private static final String COL_HOST = "host";
	private static final String COL_ROOM_NUMBER = "room_number";
	private static final String COL_ROOM_TITLE = "room_title";
	private static final String COL_ISPRIVATE = "is_private";
	private static final String COL_ROOM_PASSWORD = "room_password";
	private static final String COL_MAXPLAYERS = "max_players";
	private static final String COL_ROUNDCOUNT = "round_count";

	private static final String CREATE_ROOM = "INSERT INTO GameWatingRoom (host_user, room_title, is_private, "
			+ "room_password, max_players, round_count) " + "VALUES (?, ?, ?, ?, ?, ?)";

	private static final String FIND_ALL_ROOM = "SELECT * FROM room_view LIMIT 10 OFFSET ?";

	private RoomDao() {};

	private static RoomDao instance = new RoomDao();

	public static RoomDao getInstance() {
		return instance;
	};

	public void createRoom(RoomRequestDto roomDto) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(CREATE_ROOM)) {

			pstmt.setString(1, roomDto.getHost());
			pstmt.setString(2, roomDto.getTitle());
			pstmt.setBoolean(3, roomDto.isPrivate());
			pstmt.setString(4, roomDto.getPassword());
			pstmt.setInt(5, roomDto.getMaxPlayers());
			pstmt.setInt(6, roomDto.getRoundCount());

			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RoomResponseDto> findAllRoom(int page) {

		List<RoomResponseDto> list = new ArrayList<>();

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_ROOM);
				ResultSet rs = pstmt.executeQuery()) {

			pstmt.setInt(1, (page - 1) * 10);

			while (rs.next()) {
				String code = rs.getString(COL_ROOM_CODE);
				String host = rs.getString(COL_HOST);
				int roomNumber = rs.getInt(COL_ROOM_NUMBER);
				String title = rs.getString(COL_ROOM_TITLE);
				boolean isPrivate = rs.getBoolean(COL_ISPRIVATE);
				String password = rs.getString(COL_ROOM_PASSWORD);
				int maxPlayers = rs.getInt(COL_MAXPLAYERS);
				int roundCount = rs.getInt(COL_ROUNDCOUNT);

				RoomResponseDto roomDto = new RoomResponseDto(code, host, roomNumber, title, isPrivate, password,
						maxPlayers, roundCount);

				list.add(roomDto);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

}
