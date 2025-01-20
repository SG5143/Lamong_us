package user.action.block;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.block.*;

public class DeleteBlockedAllUserAction implements Action {

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
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters");
			return;
		}

		BlockDao blockDao = BlockDao.getInstance();
		boolean isSuccess = blockDao.cancelBlock(blockingUserNickname, blockedUserNickname);

		if (isSuccess) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "User unblocked successfully");
		} else {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_NOT_FOUND, "Block relationship not found");
		}

	}

	// 인증 검증 로직 구현 임시로 항상 true 반환
	private boolean isValidAuthorization(String authorization) {

		return authorization != null;
	}

	private void sendResponseStatusAndMessage(HttpServletResponse response, int statusCode, String message)
			throws IOException {
		JSONObject result = new JSONObject();
		result.put("status", statusCode);
		result.put("message", message);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result.toString());
	}

}
