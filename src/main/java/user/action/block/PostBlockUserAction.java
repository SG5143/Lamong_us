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

		if (!isValidAuthorization(authorization)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, null);
			return;
		}

		String blockingUserNickname = request.getParameter("blocking_user");
		String blockedUserNickname = request.getParameter("blocked_user");

		if (blockingUserNickname == null || blockedUserNickname == null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 사용자 정보입니다.");
			return;
		}
		UserDao userDao = UserDao.getInstance();
		String blockingUserUuid = userDao.getUuidByNickname(blockingUserNickname);
		String blockedUserUuid = userDao.getUuidByNickname(blockedUserNickname);

		BlockDao blockDao = BlockDao.getInstance();
		if (blockDao.isBlocked(blockingUserUuid, blockedUserUuid)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "이미 차단된 사용자입니다.");
			return;
		}

		boolean result = blockDao.blockUser(blockingUserUuid, blockedUserUuid);
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
