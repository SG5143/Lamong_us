package user.action;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Logger;

import org.json.JSONObject;

import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import user.model.UserDao;

public class BanServiceAction implements Action {
	private UserDao userDao;
	private Map<String, Map<String, Boolean>> userBlockStatus;

	private static final Logger logger = Logger.getLogger(BanServiceAction.class.getName());

	public BanServiceAction() {
		userDao = new UserDao();
		userBlockStatus = new HashMap<>();
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

		switch (actionType) {
		case "ban":
			boolean banResult = banUser(nickname, request);
			sendResponse(response, banResult ? "차단 성공" : "차단 실패");
			break;
		case "check":
			boolean isBanned = isUserBanned(nickname);
			sendResponse(response, "차단 상태: " + isBanned);
			break;
		case "unban":
			boolean unbanResult = unbanUser(nickname);
			sendResponse(response, unbanResult ? "차단 취소 성공" : "차단 취소 실패");
			break;
		default:
			sendResponse(response, "잘못된 요청입니다.");
			break;
		}
	}

	private boolean banUser(String nickname, HttpServletRequest request) {
		String currentUser = userDao.getCurrentUserUUID(request);

		if (currentUser == null) {
			logger.warning("로그인된 유저가 없습니다.");
			return false;
		}

		if (isUserBanned(nickname)) {
			logger.info("이미 차단한 유저입니다.");
			return false;
		}
		Map<String, Boolean> blockedUsers = userBlockStatus.getOrDefault(currentUser, new HashMap<>());

		blockedUsers.put(nickname, true);
		userBlockStatus.put(currentUser, blockedUsers);
		return true;
	}

	private boolean isUserBanned(String nickname) {
		String currentUser = "현재 로그인한 유저 UUID";
		Map<String, Boolean> blockedUsers = userBlockStatus.get(currentUser);
		if (blockedUsers != null) {
			return blockedUsers.getOrDefault(nickname, false);
		}
		return false;
	}

	private boolean unbanUser(String nickname) {
		String currentUser = "현재 로그인한 유저 UUID";
		if (!isUserBanned(nickname)) {
			logger.info("차단되지 않은 유저입니다.");
			return false;
		}

		Map<String, Boolean> blockedUsers = userBlockStatus.get(currentUser);
		blockedUsers.remove(nickname);
		userBlockStatus.put(currentUser, blockedUsers);
		return true;
	}

	private void sendResponse(HttpServletResponse response, String message) throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("message", message);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonResponse.toString());
	}
}
