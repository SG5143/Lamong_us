package user.action;

import java.io.*;
import org.json.*;
import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.*;

public class LoginFormAction implements Action {

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

		if (user.checkPassword(password)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "로그인 성공");
		} else {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
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
