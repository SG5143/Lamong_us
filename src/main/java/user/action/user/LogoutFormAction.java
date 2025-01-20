package user.action.user;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class LogoutFormAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		session.removeAttribute("user");
		session.invalidate();

		sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "로그아웃 성공");
	}

	private void sendResponseStatusAndMessage(HttpServletResponse response, int statusCode, String message) throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("status", statusCode);
		jsonResponse.put("message", message);

		String json = jsonResponse.toString();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
}
