package user.action.user;

import java.io.*;
import org.json.*;
import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;

import java.util.logging.*;

public class LoginFormAction implements Action {

	private static final Logger logger = Logger.getLogger(LoginFormAction.class.getName());

	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String jsonRequest = sb.toString();

		JSONObject jsonObject = new JSONObject(jsonRequest);
		String username = jsonObject.getString("username");
		String password = jsonObject.getString("password");

		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "아이디와 비밀번호를 모두 입력해주세요.");
			return;
		}

		UserDao userDao = UserDao.getInstance();
		User user = userDao.findUserByUsername(username);

		if (user == null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_NOT_FOUND, "사용자를 찾을 수 없습니다.");
			return;
		}

		if (user.getDeleteStatus()) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_FORBIDDEN, "비활성화된 유저입니다.");
			return;
		}

		if (user.checkPassword(password)) {

			HttpSession session = request.getSession();
			session.setAttribute("log", user);

			logger.info("로그인 성공: " + username);

			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("status", HttpServletResponse.SC_OK);
			jsonResponse.put("message", "로그인 성공");
			jsonResponse.put("nickname", user.getNickname());
			jsonResponse.put("uuid", user.getUuid());
			jsonResponse.put("apiKey", user.getApiKey());

			String json = jsonResponse.toString();

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);

			return;
		} else {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
			return;

		}
	}

	private void sendResponseStatusAndMessage(HttpServletResponse response, int statusCode, String message)
			throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("status", statusCode);
		jsonResponse.put("message", message);

		String json = jsonResponse.toString();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}
