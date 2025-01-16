package user.action;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import util.*;

public class BanServiceAction implements Action {

	private static final String GET_BLOCKED_USERS = "SELECT blocked_user FROM BlockedUsers WHERE blocking_user = ? LIMIT ? OFFSET ?";
	private static final String CREATE_BLOCK = "INSERT INTO BlockedUsers (blocking_user, blocked_user) VALUES (?, ?)";
	private static final String DELETE_BLOCK = "DELETE FROM BlockedUsers WHERE blocking_user = ? AND blocked_user = ?";
	private static final String GET_TOTAL_BLOCKED_USERS_COUNT = "SELECT COUNT(*) FROM BlockedUsers WHERE blocking_user = ?";

	private static final int PAGE_SIZE = 10;

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String blockingUser = request.getParameter("blocking_user");
		String blockedUser = request.getParameter("blocked_user");

		String method = request.getMethod();

		if ("POST".equalsIgnoreCase(method)) {
			boolean resultFlag = banUser(blockingUser, blockedUser);
			sendResponse(response, resultFlag ? "차단 성공" : "차단 실패", getTotalBlockedUsersCount(blockingUser), resultFlag);
		} else if ("GET".equalsIgnoreCase(method)) {
			int page = 1;
			try {
				page = Integer.parseInt(request.getParameter("page"));
			} catch (NumberFormatException e) {
			}

			List<String> blockedUsers = getBlockedUsers(blockingUser, page);
			int totalCount = getTotalBlockedUsersCount(blockingUser);

			JSONObject result = new JSONObject();
			result.put("meta", new JSONObject().put("total_count", totalCount).put("pageable_count", totalCount)
					.put("is_end", totalCount <= page * PAGE_SIZE));
			JSONArray resultArray = new JSONArray();
			for (String user : blockedUsers) {
				resultArray.put(new JSONObject().put("blocking_user", blockingUser).put("blocked_user", user)
						.put("reg_date", java.time.Instant.now().toString()));
			}
			result.put("result", resultArray);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		} else if ("DELETE".equalsIgnoreCase(method)) {
			boolean resultFlag = unbanUser(blockingUser, blockedUser);
			sendResponse(response, resultFlag ? "차단 취소 성공" : "차단 취소 실패", getTotalBlockedUsersCount(blockingUser),
					resultFlag);
		}
	}

	private List<String> getBlockedUsers(String blockingUser, int page) {
		List<String> blockedUsers = new ArrayList<>();
		int offset = (page - 1) * PAGE_SIZE;

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(GET_BLOCKED_USERS)) {
			pstmt.setString(1, blockingUser);
			pstmt.setInt(2, PAGE_SIZE);
			pstmt.setInt(3, offset);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				blockedUsers.add(rs.getString("blocked_user"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return blockedUsers;
	}

	private boolean banUser(String blockingUser, String blockedUser) {
		if (blockingUser == null || blockedUser == null) {
			return false;
		}

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

	private boolean unbanUser(String blockingUser, String blockedUser) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(DELETE_BLOCK)) {
			pstmt.setString(1, blockingUser);
			pstmt.setString(2, blockedUser);
			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private int getTotalBlockedUsersCount(String blockingUser) {
		int count = 0;
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(GET_TOTAL_BLOCKED_USERS_COUNT)) {
			pstmt.setString(1, blockingUser);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	private void sendResponse(HttpServletResponse response, String message, int totalCount, boolean isEnd)
			throws IOException {

		JSONObject result = new JSONObject();
		result.put("meta",
				new JSONObject().put("total_count", totalCount).put("pageable_count", totalCount).put("is_end", isEnd));
		result.put("message", message);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result.toString());
	}
}
