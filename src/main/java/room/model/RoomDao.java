package room.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBManager;

public class RoomDao {
	
	private static final String CREATE_ROOM = 
		    "INSERT INTO GameWatingRoom (host_user, room_title, is_private, " +
		    "room_password, max_players, round_count) " +
		    "VALUES (?, ?, ?, ?, ?, ?)";

	private static final String FIND_ALL_ROOM = 
		    "SELECT code, host, roomNumber, title, isPrivate, password, maxPlayers, roundCount " +
		    "FROM GameWatingRoom " +
		    "WHERE room_state != 'delete' " +
		    "LIMIT 10 OFFSET ?";


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
				String code = rs.getString(1);
				String host = rs.getString(2);
				int roomNumber = rs.getInt(3);
				String title = rs.getString(4);
				boolean isPrivate = rs.getBoolean(5);
				String password = rs.getString(6);
				int maxPlayers = rs.getInt(7);
				int roundCount = rs.getInt(8);

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
