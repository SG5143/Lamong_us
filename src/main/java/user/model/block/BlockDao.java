package user.model.block;

import java.sql.*;
import java.util.*;

import util.*;

public class BlockDao {
	private static final String GET_BLOCKED_USERS = "SELECT blocked_user, reg_date FROM BlockedUsers WHERE blocking_user = ? LIMIT ? OFFSET ?";
	private static final String CREATE_BLOCK = "INSERT INTO BlockedUsers (blocking_user, blocked_user) VALUES (?, ?)";
	private static final String GET_TOTAL_BLOCKED_USERS_COUNT = "SELECT COUNT(*) FROM BlockedUsers WHERE blocking_user = ?";
	private static final String FIND_BLOCKED_USER = "SELECT COUNT(*) FROM BlockedUsers WHERE blocking_user = ? AND blocked_user = ? LIMIT 1";
	private static final String CANCEL_BLOCKED_USER = "DELETE FROM BlockedUsers WHERE blocking_user = ? AND blocked_user = ?";

	private static final int PAGE_SIZE = 10;

	private static BlockDao instance = new BlockDao();

	public static BlockDao getInstance() {
		return instance;
	}

	private BlockDao() {
	}

	public boolean blockUser(String blockingUser, String blockedUser) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(CREATE_BLOCK)) {
			pstmt.setString(1, blockingUser);
			pstmt.setString(2, blockedUser);
			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean cancelBlock(String blockingUser, String blockedUser) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(CANCEL_BLOCKED_USER)) {
			pstmt.setString(1, blockingUser);
			pstmt.setString(2, blockedUser);
			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getTotalBlockedUsersCount(String blockingUser) {
		int count = 0;
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(GET_TOTAL_BLOCKED_USERS_COUNT)) {
			pstmt.setString(1, blockingUser);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				count = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	public List<Block> getBlockedUser(String blockingUser, int page) {
		List<Block> blockedUsers = new ArrayList<>();

		page = Math.max(1, page);
		int offset = (page - 1) * PAGE_SIZE;

		System.out.println("[DEBUG] getBlockedUser() - blockingUser: " + blockingUser);
		System.out.println("[DEBUG] getBlockedUser() - page: " + page);
		System.out.println("[DEBUG] getBlockedUser() - offset: " + offset);

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(GET_BLOCKED_USERS)) {

			pstmt.setString(1, blockingUser);
			pstmt.setInt(2, PAGE_SIZE);
			pstmt.setInt(3, offset);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Block block = new Block();
				block.setBlockingUser(blockingUser);
				block.setBlockedUser(rs.getString("blocked_user"));
				block.setRegDate(rs.getTimestamp("reg_date"));
				blockedUsers.add(block);
			}

			System.out.println("[DEBUG] getBlockedUser() - ResultSet size: " + blockedUsers.size());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return blockedUsers;
	}

	public boolean isBlocked(String blockingUser, String blockedUser) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(FIND_BLOCKED_USER)) {
			pstmt.setString(1, blockingUser);
			pstmt.setString(2, blockedUser);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next())
				return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
