package user.action;

import java.io.*;

import org.apache.catalina.User;
import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.*;

public class DeleteFormAction implements Action {
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 삭제 상태 활성화
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		if (user == null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "로그인 상태가 아닙니다.");
			return;
		}

		try {
			UserDao userDao = UserDao.getInstance();
			boolean deactivated = userDao.deactivateUser(user.getUsername());

			if (deactivated) {
				session.removeAttribute("user");
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
