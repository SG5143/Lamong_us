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

	private static final String CREATE_ROOM = "INSERT INTO GameWatingRoom (room_number, host_user, room_title, is_private, room_password, max_players, round_count) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

	private static final String FIND_ROOM_NUMBER = "SELECT room_number FROM GameWatingRoom WHERE room_state != 'delete'";
	private static final String FIND_ROOM_COUNT = "SELECT COUNT(*) FROM GameWatingRoom WHERE room_state != 'delete'";

	private static final String FIND_ALL_ROOM = "SELECT * FROM GameWatingRoom where room_state != 'delete' ORDER BY room_number ASC LIMIT 10 OFFSET ?";

	private RoomDao() {};

	private static RoomDao instance = new RoomDao();

	public static RoomDao getInstance() {
		return instance;
	};

	public void createRoom(RoomRequestDto roomDto) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(CREATE_ROOM)) {

			pstmt.setInt(1, roomDto.getRoomNumber());
			pstmt.setString(2, roomDto.getHost());
			pstmt.setString(3, roomDto.getTitle());
			pstmt.setBoolean(4, roomDto.isPrivate());
			pstmt.setString(5, roomDto.getPassword());
			pstmt.setInt(6, roomDto.getMaxPlayers());
			pstmt.setInt(7, roomDto.getRoundCount());

			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getAvailableRoomNumber() {
		List<Integer> usedRoomNumbers = new ArrayList<>();

		while (true) {
			try (Connection conn = DBManager.getConnection();
					PreparedStatement pstmt = conn.prepareStatement(FIND_ROOM_NUMBER);
					ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					usedRoomNumbers.add(rs.getInt("room_number"));
				}

				int roomNumber = 1;

				while (usedRoomNumbers.contains(roomNumber)) {
					roomNumber++;
				}

				return roomNumber;

			} catch (SQLException e) {
				e.printStackTrace();
				usedRoomNumbers.clear();
			}
		}
	}

	public int getRoomCount() {
		int roomCount = 0;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_ROOM_COUNT);
				ResultSet rs = pstmt.executeQuery()) {

			if (rs.next()) {
				roomCount = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return roomCount;
	}

	public List<Room> fetchRoomsForPage(int page) {
		List<Room> list = new ArrayList<>();

		int offset = (page - 1) * 10;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_ROOM)) {

			pstmt.setInt(1, offset);

			list = fetchRoomsByPage(pstmt);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<Room> fetchRoomsByPage(PreparedStatement pstmt) throws SQLException {
		List<Room> list = new ArrayList<>();

		try (ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				String code = rs.getString(COL_ROOM_CODE);
				String host = rs.getString(COL_HOST);
				int roomNumber = rs.getInt(COL_ROOM_NUMBER);
				String roomTitle = rs.getString(COL_ROOM_TITLE);
				boolean isPrivate = rs.getBoolean(COL_ISPRIVATE);
				String password = rs.getString(COL_ROOM_PASSWORD);
				int maxPlayers = rs.getInt(COL_MAXPLAYERS);
				int roundCount = rs.getInt(COL_ROUNDCOUNT);

				list.add(new Room(code, host, roomNumber, roomTitle, isPrivate, password, maxPlayers, roundCount));

			}
		}
		return list;
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
