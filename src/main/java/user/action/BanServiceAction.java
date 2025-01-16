package user.action;

import java.io.*;
import java.util.logging.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.*;

public class BanServiceAction implements Action {

	private static final Logger logger = Logger.getLogger(BanServiceAction.class.getName());
	private UserDao userDao;

	public BanServiceAction() {
		this.userDao = UserDao.getInstance();
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		String jsonRequest = sb.toString();
		JSONObject jsonObject = new JSONObject(jsonRequest);

		String actionType = jsonObject.getString("action");
		String nickname = jsonObject.getString("nickname");

		if (actionType.equals("ban")) {
			boolean result = banUser(nickname);
			sendResponse(response, result ? "차단 성공" : "차단 실패");
		} else if (actionType.equals("check")) {
			boolean isBanned = isUserBanned(nickname);
			sendResponse(response, "차단 상태: " + isBanned);
		} else {
			sendResponse(response, "잘못된 요청입니다.");
		}
	}

	private boolean banUser(String nickname) {
		String currentUser = "현재 로그인한 유저 UUID";
		if (userDao.isUserBlocked(currentUser, nickname)) {
			logger.info("이미 차단한 유저입니다.");
			return false;
		}
		return userDao.banUser(currentUser, nickname);
	}

	private boolean isUserBanned(String nickname) {
		String currentUser = "현재 로그인한 유저 UUID";
		return userDao.isUserBlocked(currentUser, nickname);
	}

	private void sendResponse(HttpServletResponse response, String message) throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("message", message);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonResponse.toString());
	}
}
