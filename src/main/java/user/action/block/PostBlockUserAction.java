package user.action.block;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.block.*;
import user.model.user.*;

public class PostBlockUserAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		System.out.println("Authorization Header: " + authorization); // 디버깅용 로그

		if (!isValidAuthorization(authorization)) {
			System.out.println("Invalid Authorization"); // 디버깅용 로그
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, null);
			return;
		}

		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		String requestBodyString = sb.toString();
		System.out.println("Request Body: " + requestBodyString); // 디버깅용 로그

		JSONObject requestBody = new JSONObject(sb.toString());
		String blockingUserNickname = requestBody.optString("blocking_user");
		String blockedUserNickname = requestBody.optString("blocked_user");

		System.out.println("Blocking User: " + blockingUserNickname); // 디버깅용 로그
		System.out.println("Blocked User: " + blockedUserNickname); // 디버깅용 로그

		if (blockingUserNickname == null || blockedUserNickname == null) {
			System.out.println("Invalid User Information"); // 디버깅용 로그
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 사용자 정보입니다.");
			return;
		}
		UserDao userDao = UserDao.getInstance();
		String blockingUserUuid = userDao.getUuidByNickname(blockingUserNickname);
		String blockedUserUuid = userDao.getUuidByNickname(blockedUserNickname);

		System.out.println("Blocking User UUID: " + blockingUserUuid); // 디버깅용 로그
		System.out.println("Blocked User UUID: " + blockedUserUuid); // 디버깅용 로그

		BlockDao blockDao = BlockDao.getInstance();
		if (blockDao.isBlocked(blockingUserUuid, blockedUserUuid)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "이미 차단된 사용자입니다.");
			System.out.println("Already Blocked"); // 디버깅용 로그
			return;
		}

		boolean result = blockDao.blockUser(blockingUserUuid, blockedUserUuid);
		System.out.println("Block Result: " + result); // 디버깅용 로그

		sendResponse(response, result ? "차단 성공" : "차단 싫패",
				result ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	// Authorization 유효성 검사
	private boolean isValidAuthorization(String authorization) {
		return authorization != null && !authorization.trim().isEmpty();
	}

	private void sendResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
		JSONObject result = new JSONObject();
		result.put("status", statusCode);
		result.put("message", message);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result.toString());
	}

	private void sendResponseStatusAndMessage(HttpServletResponse response, int statusCode, String message)
			throws IOException {
		sendResponse(response, message, statusCode);
	}
}
