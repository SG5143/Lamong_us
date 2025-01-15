package user.model;

import java.sql.*;
import java.util.*;

import org.mindrot.jbcrypt.*;

import util.*;

public class UserDao {

	private static final String COL_UUID = "uuid";
	private static final String COL_USERNAME = "username";
	private static final String COL_PASSWORD = "password";
	private static final String COL_NICKNAME = "nickname";
	private static final String COL_PHONE = "phone";
	private static final String COL_EMAIL = "email";
	private static final String COL_DELETE_STATUS = "delete_status";
	private static final String COL_LOGIN_TYPE = "login_type";
	private static final String COL_BAN_STATUS = "ban_status";
	private static final String COL_PROFILE_INFO = "profile_info";
	private static final String COL_PROFILE_IMAGE = "profile_image";
	private static final String COL_SCORE = "score";
	private static final String COL_API_KEY = "api_key";
	private static final String COL_REG_DATE = "reg_date";
	private static final String COL_MOD_DATE = "mod_date";

	private static final String CREATE_USER = "INSERT INTO Users (username, password, nickname, phone, email, login_type) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String FIND_ALL_USERS = "SELECT * FROM Users";
	private static final String FIND_USER_UUID = "SELECT * FROM Users WHERE uuid=?";
	private static final String FIND_USER_USERNAME = "SELECT * FROM Users WHERE username=?";
	private static final String FIND_USER_BY_EMAIL = "SELECT * FROM Users WHERE email=?";
	private static final String FIND_USER_BY_NICKNAME = "SELECT * FROM Users WHERE nickname=?";
	private static final String FIND_USER_BY_PHONE = "SELECT * FROM Users WHERE phone=?";

	private static final String UPDATE_USER_INFO = "UPDATE Users SET password = ?, nickname = ?, email = ?, phone = ?, profile_info = ?, profile_image = ? WHERE uuid = ?";
	private static final String UPDATE_DELETE_STATUS = "UPDATE Users SET delete_status = TRUE WHERE username = ?";
	private static final String FIND_USER_PUBLIC_INFO = "SELECT uuid, nickname, profile_info, profile_image, score, reg_date FROM Users WHERE uuid = ?";

	private UserDao() {
	}

	private static UserDao instance = new UserDao();

	public static UserDao getInstance() {
		return instance;
	}

	public void createUser(UserRequestDto userDto) {

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(CREATE_USER)) {

			pstmt.setString(1, userDto.getUsername());

			String rawPassword = userDto.getPassword();
			String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
			pstmt.setString(2, hashedPassword);

			pstmt.setString(3, userDto.getNickname());
			pstmt.setString(4, userDto.getPhone());
			pstmt.setString(5, userDto.getEmail());

			if (userDto.getLoginType() != null) {
				if ("kakao".equals(userDto.getLoginType()) || "google".equals(userDto.getLoginType())) {
					pstmt.setString(6, userDto.getLoginType());
				}
			} else {
				pstmt.setNull(6, java.sql.Types.VARCHAR);
			}

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<User> findUserAll() {
		List<User> list = new ArrayList<>();

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_USERS);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToUser(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private User mapResultSetToUser(ResultSet rs) throws SQLException {
		String uuid = rs.getString(COL_UUID);
		String username = rs.getString(COL_USERNAME);
		String password = rs.getString(COL_PASSWORD);
		String nickname = rs.getString(COL_NICKNAME);
		String phone = rs.getString(COL_PHONE);
		String email = rs.getString(COL_EMAIL);
		boolean deleteStatus = rs.getBoolean(COL_DELETE_STATUS);
		String loginType = rs.getString(COL_LOGIN_TYPE);
		boolean banStatus = rs.getBoolean(COL_BAN_STATUS);
		String profileInfo = rs.getString(COL_PROFILE_INFO);

		byte[] profileImage = null;
		Blob blob = rs.getBlob(COL_PROFILE_IMAGE);
		if (blob != null) {
			profileImage = blob.getBytes(1, (int) blob.length());
		}

		int score = rs.getInt(COL_SCORE);
		String apiKey = rs.getString(COL_API_KEY);
		Timestamp regDate = rs.getTimestamp(COL_REG_DATE);
		Timestamp modDate = rs.getTimestamp(COL_MOD_DATE);

		return new User(uuid, username, password, nickname, phone, email, deleteStatus, loginType, banStatus,
				profileInfo, profileImage, score, apiKey, regDate, modDate);
	}

	public User findUserByUuid(String uuid) {
		User user = null;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_USER_UUID)) {

			pstmt.setString(1, uuid);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					user = mapResultSetToUser(rs);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	public User findUserByUsername(String username) {
		User user = null;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_USER_USERNAME)) {

			pstmt.setString(1, username);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					user = mapResultSetToUser(rs);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	public User findUserByEmail(String email) {
		User user = null;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_USER_BY_EMAIL)) {

			pstmt.setString(1, email);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					user = mapResultSetToUser(rs);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public User findUserByNickname(String nickname) {
		User user = null;
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_USER_BY_NICKNAME)) {

			pstmt.setString(1, nickname);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					user = mapResultSetToUser(rs);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public User findUserByPhone(String phone) {
		User user = null;
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_USER_BY_PHONE)) {

			pstmt.setString(1, phone);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					user = mapResultSetToUser(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public User updateUserInfo(UserRequestDto userDto) {
		User updatedUser = null;

		String sql = UPDATE_USER_INFO;

		try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			int cnt = 0;

			if (userDto.getPassword() != null) {
				String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
				pstmt.setString(++cnt, hashedPassword);
			} else {
				pstmt.setNull(++cnt, Types.VARCHAR);
			}

			if (userDto.getNickname() != null) {
				pstmt.setString(++cnt, userDto.getNickname());
			} else {
				pstmt.setNull(++cnt, Types.VARCHAR);
			}

			if (userDto.getEmail() != null) {
				pstmt.setString(++cnt, userDto.getEmail());
			} else {
				pstmt.setNull(++cnt, Types.VARCHAR);
			}

			if (userDto.getPhone() != null) {
				pstmt.setString(++cnt, userDto.getPhone());
			} else {
				pstmt.setNull(++cnt, Types.VARCHAR);
			}

			if (userDto.getProfileInfo() != null) {
				pstmt.setString(++cnt, userDto.getProfileInfo());
			} else {
				pstmt.setNull(++cnt, Types.VARCHAR);
			}

			if (userDto.getProfileImage() != null) {
				pstmt.setBytes(++cnt, userDto.getProfileImage());
			} else {
				pstmt.setNull(++cnt, Types.BLOB);
			}

			pstmt.setString(++cnt, userDto.getUuid());

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				updatedUser = findUserByUuid(userDto.getUuid());
			} else {
				System.out.println("업데이트 실패: 유저 정보가 존재하지 않습니다.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return updatedUser;
	}

	public boolean deactivateUser(String username) {
		boolean isDeactivated = false;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(UPDATE_DELETE_STATUS)) {
			pstmt.setString(1, username);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				isDeactivated = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isDeactivated;
	}

	public User getUserPublicInfo(String uuid) {
		User userPublicInfo = null;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_USER_PUBLIC_INFO)) {

			pstmt.setString(1, uuid);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String userUuid = rs.getString("uuid");
					String nickname = rs.getString("nickname");
					String profileInfo = rs.getString("profile_info");

					byte[] profileImage = null;
					Blob blob = rs.getBlob("profile_image");
					if (blob != null) {
						profileImage = blob.getBytes(1, (int) blob.length());
					}

					int score = rs.getInt("score");
					Timestamp regDate = rs.getTimestamp("reg_date");

					userPublicInfo = new User(userUuid, nickname, profileInfo, profileImage, score, regDate);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userPublicInfo;
	}

}
