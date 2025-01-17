package user.action;

import java.io.*;
import java.util.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.*;

public class BlockServiceAction implements Action {

	private static final String POST = "POST";
	private static final String GET = "GET";
	private static final String DELETE = "DELETE";

	private static final int PAGE_SIZE = 10;

	private BlockDao blockDao = new BlockDao();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, null);
			return;
		}
		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		if (sb.length() > 0) {
			try {
				JSONObject jsonObject = new JSONObject(sb.toString());
				String blockingUserNickname = jsonObject.getString("blocking_user");
				String blockedUserNickname = jsonObject.getString("blocked_user");

				String blockingUser = UserDao.getUuidByNickname(blockingUserNickname);
				String blockedUser = UserDao.getUuidByNickname(blockedUserNickname);

				if (blockingUser == null || blockedUser == null) {
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "존재하지 않는 사용자입니다.");
					return;
				}
				switch (request.getMethod().toUpperCase()) {
				case POST:
					handlePostRequest(response, blockingUser, blockedUser);
					break;

				case GET:
					handleGetRequest(request, response, blockingUser);
					break;

				case DELETE:
					handleDeleteRequest(response, blockingUser, blockedUser);
					break;

				default:
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
							"지원하지 않는 메서드입니다.");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류 발생");
			}
		}
	}

	private void handlePostRequest(HttpServletResponse response, String blockingUser, String blockedUser)
			throws IOException {
		if (blockDao.isBlocked(blockingUser, blockedUser)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "이미 차단된 사용자입니다.");
			return;
		}

		boolean resultFlag = blockDao.blockUser(blockingUser, blockedUser);
		sendResponse(response, resultFlag ? "차단 성공" : "차단 실패", blockDao.getTotalBlockedUsersCount(blockingUser),
				resultFlag);
	}

	private void handleGetRequest(HttpServletRequest request, HttpServletResponse response, String blockingUser)
			throws IOException {
		int page = parsePageParameter(request.getParameter("page"));
		List<String> blockedUsers = blockDao.getBlockedUser(blockingUser, page);
		int totalCount = blockDao.getTotalBlockedUsersCount(blockingUser);

		JSONObject result = new JSONObject();
		result.put("meta", new JSONObject()
			  .put("total_count", totalCount).put("pageable_count", totalCount)
			  .put("is_end", totalCount <= page * PAGE_SIZE));

		JSONArray resultArray = new JSONArray();
		for (String user : blockedUsers) {
			resultArray.put(new JSONObject()
					   .put("blocking_user", blockingUser).put("blocked_user", user)
					   .put("reg_date", java.time.Instant.now().toString()));
		}
		result.put("result", resultArray);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result.toString());
	}

	private void handleDeleteRequest(HttpServletResponse response, String blockingUser, String blockedUser)
			throws IOException {
		if (!blockDao.isBlocked(blockingUser, blockedUser)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "차단되지 않은 사용자입니다.");
			return;
		}

		boolean resultFlag = blockDao.cancelBlock(blockingUser, blockedUser);
		sendResponse(response, resultFlag ? "차단 취소 성공" : "차단 취소 실패", blockDao.getTotalBlockedUsersCount(blockingUser),
				resultFlag);
	}

	private boolean isValidAuthorization(String authorization) {
		return authorization != null;
	}

	private int parsePageParameter(String pageParam) {
		try {
			return Integer.parseInt(pageParam);
		} catch (NumberFormatException ignored) {
			return 1;
		}
	}

	private void sendResponseStatusAndMessage(HttpServletResponse response, int statusCode, String message)
			throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("status", statusCode);
		jsonResponse.put("message", message);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonResponse.toString());
	}

	private void sendResponse(HttpServletResponse response, String message, int totalCount, boolean isEnd)
			throws IOException {
		JSONObject result = new JSONObject();
		result.put("meta",
				new JSONObject()
				.put("total_count", totalCount)
				.put("pageable_count", totalCount)
				.put("is_end", isEnd));
		result.put("message", message);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result.toString());
	}
}
