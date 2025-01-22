package user.model.user;

import java.sql.*;
import java.util.*;

import org.mindrot.jbcrypt.*;

import jakarta.servlet.http.*;
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

	private static final String COUNT_USERS = "SELECT COUNT(*) FROM users";
	private static final String CREATE_USER = "INSERT INTO Users (username, password, nickname, phone, email, login_type) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String FIND_ALL_USERS = "SELECT * FROM Users";
	private static final String FIND_USER_BY_UUID = "SELECT * FROM Users WHERE uuid=?";
	private static final String FIND_USER_BY_USERNAME = "SELECT * FROM Users WHERE username=?";
	private static final String FIND_USER_BY_EMAIL = "SELECT * FROM Users WHERE email=?";
	private static final String FIND_USER_BY_NICKNAME = "SELECT * FROM Users WHERE nickname=?";
	private static final String FIND_USER_BY_PHONE = "SELECT * FROM Users WHERE phone=?";
	private static final String FIND_USER_BY_USERKEY = "SELECT * FROM Users WHERE phone=?";

	private static final String UPDATE_USER_INFO = "UPDATE Users SET password = ?, nickname = ?, email = ?, phone = ?, profile_info = ?, profile_image = ?, login_type = ? WHERE uuid = ?";
	private static final String UPDATE_DELETE_STATUS = "UPDATE Users SET delete_status = TRUE WHERE username = ?";
	private static final String FIND_USER_PUBLIC_INFO = "SELECT uuid, nickname, profile_info, profile_image, score, reg_date FROM Users WHERE uuid = ?";

	public UserDao() {
	}

	private static UserDao instance = new UserDao();

	public static UserDao getInstance() {
		return instance;
	}

	public int countAllUsers() {
		int totalCount = 0;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(COUNT_USERS);
				ResultSet rs = pstmt.executeQuery()) {

			if (rs.next())
				totalCount = rs.getInt(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalCount;
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

			if (userDto.getLoginType() == null || "null".equalsIgnoreCase(userDto.getLoginType().trim())) {
				pstmt.setNull(6, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(6, userDto.getLoginType().trim());
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

	private static User mapResultSetToUser(ResultSet rs) throws SQLException {
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
		if (blob != null)
			profileImage = blob.getBytes(1, (int) blob.length());

		int score = rs.getInt(COL_SCORE);
		String apiKey = rs.getString(COL_API_KEY);
		Timestamp regDate = rs.getTimestamp(COL_REG_DATE);
		Timestamp modDate = rs.getTimestamp(COL_MOD_DATE);

		return new User(uuid, username, password, nickname, phone, email, deleteStatus, loginType, banStatus,
				profileInfo, profileImage, score, apiKey, regDate, modDate);
	}

	public String getCurrentUserUUID(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			String currentUserUUID = (String) session.getAttribute("userUUID");
			if (currentUserUUID != null)
				return currentUserUUID;
		}
		return null;
	}

	public User findUserBy(String query, String parameter) {
		User user = null;
		try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, parameter);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					user = mapResultSetToUser(rs);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public String getUuidByNickname(String nickname) {
		User user = findUserBy(FIND_USER_BY_NICKNAME, nickname);
		return user != null ? user.getUuid() : null;
	}

	public User findUserByUserkey(String apiKey) {
		return findUserBy(FIND_USER_BY_USERKEY, apiKey);
	}

	public User findUserByUuid(String uuid) {
		return findUserBy(FIND_USER_BY_UUID, uuid);
	}

	public User findUserByUsername(String username) {
		return findUserBy(FIND_USER_BY_USERNAME, username);
	}

	public User findUserByUserEmail(String email) {
		return findUserBy(FIND_USER_BY_EMAIL, email);
	}

	public User findUserByUserNickname(String nickname) {
		return findUserBy(FIND_USER_BY_NICKNAME, nickname);
	}

	public User findUserByUserPhone(String phone) {
		return findUserBy(FIND_USER_BY_PHONE, phone);
	}

	public User updateUserInfo(UserRequestDto userDto) {
		User updatedUser = null;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(UPDATE_USER_INFO)) {

			User existingUser = findUserByUuid(userDto.getUuid());

			if (existingUser == null) {
				System.out.println("업데이트 실패: 유저 정보가 존재하지 않습니다. UUID: " + userDto.getUuid());
				return null;
			}

			int cnt = 0;

			if (userDto.getPassword() != null) {
				String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
				pstmt.setString(++cnt, hashedPassword);
			} else {
				pstmt.setString(++cnt, existingUser.getPassword());
			}

			if (userDto.getNickname() != null)
				pstmt.setString(++cnt, userDto.getNickname());
			else
				pstmt.setString(++cnt, existingUser.getNickname());

			if (userDto.getEmail() != null)
				pstmt.setString(++cnt, userDto.getEmail());
			else
				pstmt.setString(++cnt, existingUser.getEmail());

			if (userDto.getPhone() != null)
				pstmt.setString(++cnt, userDto.getPhone());
			else
				pstmt.setString(++cnt, existingUser.getPhone());

			if (userDto.getProfileInfo() != null)
				pstmt.setString(++cnt, userDto.getProfileInfo());
			else
				pstmt.setString(++cnt, existingUser.getProfileInfo());

			if (userDto.getProfileImage() != null)
				pstmt.setBytes(++cnt, userDto.getProfileImage());
			else
				pstmt.setBytes(++cnt, existingUser.getProfileImage());

			if (userDto.getLoginType() != null)
				pstmt.setString(++cnt, userDto.getLoginType());
			else
				pstmt.setString(++cnt, existingUser.getLoginType());

			pstmt.setString(++cnt, userDto.getUuid());

			conn.setAutoCommit(false);

			int rowsUpdated = pstmt.executeUpdate();

			if (rowsUpdated > 0) {
				conn.commit();
				updatedUser = findUserByUuid(userDto.getUuid());
			} else {
				System.out.println("업데이트 실패: 유저 정보가 존재하지 않습니다.");
				conn.rollback();
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

			if (rowsAffected > 0)
				isDeactivated = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isDeactivated;
	}

	public String getUserPublicInfo(String uuid) {
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

					if (blob != null)
						profileImage = blob.getBytes(1, (int) blob.length());

					int score = rs.getInt("score");
					Timestamp regDate = rs.getTimestamp("reg_date");

					userPublicInfo = new User(userUuid, nickname, profileInfo, profileImage, score, regDate);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userPublicInfo.toString();
	}

}
