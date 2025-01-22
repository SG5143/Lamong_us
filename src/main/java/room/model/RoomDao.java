package room.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import util.DBManager;

public class RoomDao {
	private static final String COL_ROOM_CODE = "room_code";
	private static final String COL_HOST = "host_user";
	private static final String COL_ROOM_NUMBER = "room_number";
	private static final String COL_ROOM_TITLE = "room_title";
	private static final String COL_IS_PRIVATE = "is_private";
	private static final String COL_ROOM_PASSWORD = "room_password";
	private static final String COL_MAX_PLAYERS = "max_players";
	private static final String COL_ROUND_COUNT = "round_count";
	private static final String COL_ROOM_STATE = "room_state";
	private static final String COL_REG_DATE = "reg_date";
	private static final String COL_MOD_DATE = "mod_date";
	
	private static final String CREATE_ROOM_SQL = "INSERT INTO GameWatingRoom (room_number, host_user, room_title, is_private, room_password, max_players, round_count) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String FIND_ROOM_NUMBER_SQL = "SELECT room_number FROM GameWatingRoom WHERE room_state != 'delete'";
	private static final String FIND_ROOM_COUNT_SQL = "SELECT COUNT(*) FROM GameWatingRoom WHERE room_state != 'delete'";
	private static final String FIND_ALL_ROOM_SQL = "SELECT * FROM GameWatingRoom where room_state != 'delete' ORDER BY room_number DESC LIMIT 10 OFFSET ?";
	private static final String FIND_ONE_ROOM_SQL = "SELECT * FROM GameWatingRoom WHERE room_number = ? AND room_state != 'delete'";
	private static final String DELETE_ROOM_SQL= "UPDATE GameWatingRoom SET room_state = 'delete' WHERE room_code = ?";
	private static final String UPDATE_ROOM_SQL = "UPDATE GameWatingRoom SET room_title = ?, is_private = ?, room_password = ?, max_players = ?, round_count = ? WHERE room_code = ? AND room_state != 'delete'";
	private static final String INSERT_GAME_ROOM_ATTENDANCE_SQL = "INSERT INTO GameRoomAttendance (user_code, room_code) VALUES (?, ?)";
	private static final String UPDATE_LEAVE_TIME_SQL = "UPDATE GameRoomAttendance SET leave_time = CURRENT_TIMESTAMP WHERE user_code = ? AND room_code = ?";
	private static final String UPDATE_JOIN_TIME_SQL = "UPDATE GameRoomAttendance SET join_time = CURRENT_TIMESTAMP, leave_time = NULL WHERE user_code = ? AND room_code = ?";
	private static final String PLAYERS_COUNT_SQL = "SELECT COUNT(*) AS current_players FROM GameRoomAttendance WHERE room_code = ? AND leave_time IS NULL";



	private RoomDao() {};

	private static RoomDao instance = new RoomDao();

	public static RoomDao getInstance() {
		return instance;
	};

	public void createRoom(RoomRequestDto roomDto) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(CREATE_ROOM_SQL)) {
			pstmt.setInt(1, roomDto.getRoomNumber());
			pstmt.setString(2, roomDto.getHost());
			pstmt.setString(3, roomDto.getTitle());
			pstmt.setBoolean(4, roomDto.isPrivate());
			if (roomDto.getPassword() == null) {
				pstmt.setNull(5, Types.VARCHAR);
			} else {
				pstmt.setString(5, roomDto.getPassword());
			}
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
					PreparedStatement pstmt = conn.prepareStatement(FIND_ROOM_NUMBER_SQL);
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
				PreparedStatement pstmt = conn.prepareStatement(FIND_ROOM_COUNT_SQL);
				ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) 
				roomCount = rs.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return roomCount;
	}

	public List<Room> fetchRoomsForPage(int page) {
		List<Room> list = new ArrayList<>();

		int offset = (page - 1) * 10;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_ROOM_SQL)) {
			pstmt.setInt(1, offset);

			list = fetchRoomsByPage(pstmt);
			
			for (Room room : list) {
				int currentPlayers = getCurrentPlayers(room.getCode());
				room.setCurPlayers(currentPlayers);
			}
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
				String title = rs.getString(COL_ROOM_TITLE);
				boolean isPrivate = rs.getBoolean(COL_IS_PRIVATE);
				String password = rs.getString(COL_ROOM_PASSWORD);
				int maxPlayers = rs.getInt(COL_MAX_PLAYERS);
				int roundCount = rs.getInt(COL_ROUND_COUNT);
				String state = rs.getString(COL_ROOM_STATE);
				Timestamp regDate = rs.getTimestamp(COL_REG_DATE);
				Timestamp modDate = rs.getTimestamp(COL_MOD_DATE);

				Room room = new Room(code, host, roomNumber, title, isPrivate, password,
						maxPlayers, roundCount, state, regDate, modDate);
				list.add(room);
			}
		}
		return list;
	}

	public List<Room> findAllRoom(int page) {
		List<Room> list = new ArrayList<>();

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_ROOM_SQL);
				ResultSet rs = pstmt.executeQuery()) {

			pstmt.setInt(1, (page - 1) * 10);

			while (rs.next()) {
				String code = rs.getString(COL_ROOM_CODE);
				String host = rs.getString(COL_HOST);
				int roomNumber = rs.getInt(COL_ROOM_NUMBER);
				String title = rs.getString(COL_ROOM_TITLE);
				boolean isPrivate = rs.getBoolean(COL_IS_PRIVATE);
				String password = rs.getString(COL_ROOM_PASSWORD);
				int maxPlayers = rs.getInt(COL_MAX_PLAYERS);
				int roundCount = rs.getInt(COL_ROUND_COUNT);
				String state = rs.getString(COL_ROOM_STATE);
				Timestamp regDate = rs.getTimestamp(COL_REG_DATE);
				Timestamp modDate = rs.getTimestamp(COL_MOD_DATE);

				Room room = new Room(code, host, roomNumber, title, isPrivate, password,
						maxPlayers, roundCount, state, regDate, modDate);
				list.add(room);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public Room getByRoomNumber(int num) {
		Room room = null;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_ONE_ROOM_SQL)) {
			pstmt.setInt(1, num);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String code = rs.getString(COL_ROOM_CODE);
					String host = rs.getString(COL_HOST);
					int roomNumber = rs.getInt(COL_ROOM_NUMBER);
					String title = rs.getString(COL_ROOM_TITLE);
					boolean isPrivate = rs.getBoolean(COL_IS_PRIVATE);
					String password = rs.getString(COL_ROOM_PASSWORD);
					int maxPlayers = rs.getInt(COL_MAX_PLAYERS);
					int roundCount = rs.getInt(COL_ROUND_COUNT);
					String state = rs.getString(COL_ROOM_STATE);
					Timestamp regDate = rs.getTimestamp(COL_REG_DATE);
					Timestamp modDate = rs.getTimestamp(COL_MOD_DATE);

					room = new Room(code, host, roomNumber, title, isPrivate, password, 
							maxPlayers, roundCount, state, regDate, modDate);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return room;
	}

	public void deleteRoomByCode(String roomCode) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(DELETE_ROOM_SQL)) {

			pstmt.setString(1, roomCode);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateRoomSettings(RoomRequestDto roomDto) {

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(UPDATE_ROOM_SQL)) {

			pstmt.setString(1, roomDto.getTitle());
			pstmt.setBoolean(2, roomDto.isPrivate());
			pstmt.setString(3, roomDto.getPassword());
			pstmt.setInt(4, roomDto.getMaxPlayers());
			pstmt.setInt(5, roomDto.getRoundCount());
			pstmt.setString(6, roomDto.getCode());
			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void userToGameRoomAttendance(String userCode, RoomRequestDto roomDto) {
		
		try(Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(INSERT_GAME_ROOM_ATTENDANCE_SQL)) {
			pstmt.setString(1, userCode);
			pstmt.setString(2, roomDto.getCode());
			
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void userLeaveRoom(RoomRequestDto roomDto) {

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(UPDATE_LEAVE_TIME_SQL)) {
			pstmt.setString(1, roomDto.getUserCode());
			pstmt.setString(2, roomDto.getCode());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void userJoinRoom(RoomRequestDto roomDto) {

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(UPDATE_JOIN_TIME_SQL)) {
			pstmt.setString(1, roomDto.getUserCode());
			pstmt.setString(2, roomDto.getCode());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getCurrentPlayers(String roomCode) {

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(PLAYERS_COUNT_SQL)) {
			pstmt.setString(1, roomCode);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("current_players");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	public Room getRoomByCode(String roomCode) {
	    Room room = null;

	    String query = "SELECT * FROM GameWatingRoom WHERE room_code = ? AND room_state != 'delete'";

	    try (Connection conn = DBManager.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, roomCode);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                String code = rs.getString(COL_ROOM_CODE);
	                String host = rs.getString(COL_HOST);
	                int roomNumber = rs.getInt(COL_ROOM_NUMBER);
	                String title = rs.getString(COL_ROOM_TITLE);
	                boolean isPrivate = rs.getBoolean(COL_IS_PRIVATE);
	                String password = rs.getString(COL_ROOM_PASSWORD);
	                int maxPlayers = rs.getInt(COL_MAX_PLAYERS);
	                int roundCount = rs.getInt(COL_ROUND_COUNT);
	                String state = rs.getString(COL_ROOM_STATE);
	                Timestamp regDate = rs.getTimestamp(COL_REG_DATE);
	                Timestamp modDate = rs.getTimestamp(COL_MOD_DATE);

	                room = new Room(code, host, roomNumber, title, isPrivate, password, 
	                        maxPlayers, roundCount, state, regDate, modDate);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return room;
	}
	
	

}
