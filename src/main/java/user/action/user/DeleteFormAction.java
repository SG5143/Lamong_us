package user.action.user;

import java.io.*;
import org.json.*;
import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;

public class DeleteFormAction implements Action {
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String apiKeyFromHeader = request.getHeader("Authorization");
		if (apiKeyFromHeader == null || !apiKeyFromHeader.startsWith("Bearer ")) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "API 키가 제공되지 않았습니다.");
			return;
		}

		String apiKey = apiKeyFromHeader.substring(7);
		HttpSession session = request.getSession();
		User loggedInUser = (User) session.getAttribute("log");

		if (loggedInUser == null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "로그인 상태가 아닙니다.");
			return;
		}

		if (!loggedInUser.getApiKey().equals(apiKey)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "API 키가 유효하지 않습니다.");
			return;
		}

		try {
			BufferedReader reader = request.getReader();
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}

			JSONObject jsonRequest = new JSONObject(jsonBuilder.toString());
			String password = jsonRequest.getString("password");

			if (!loggedInUser.checkPassword(password)) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
				return;
			}

			UserDao userDao = UserDao.getInstance();
			boolean deactivated = userDao.deactivateUser(loggedInUser.getUsername());

			if (deactivated) {
				session.removeAttribute("log"); 
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "회원 탈퇴가 완료되었습니다.");
			} else {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "회원 탈퇴에 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
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