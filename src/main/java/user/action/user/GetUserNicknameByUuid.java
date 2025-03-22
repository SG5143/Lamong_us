package user.action.user;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;

public class GetUserNicknameByUuid implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject resData = new JSONObject();
		String userUuid = request.getParameter("user_uuid");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		if (userUuid == null || userUuid.isEmpty()) {
			try {
				System.out.println("user_uuid 파라미터가 없습니다.");
				resData.put("error", "user_uuid 파라미터가 필요합니다.");
				response.getWriter().write(resData.toString());
				return;
			} catch (JSONException e) {
				throw new ServletException("JSON 생성 에러", e);
			}
		}

		UserDao userDao = UserDao.getInstance();
		User user = userDao.findUserByUuid(userUuid);

		if (user != null)
			resData.put("nickname", user.getNickname());
		else
			resData.put("error", "해당 UUID의 사용자를 찾을 수 없습니다.");

		try {
			response.getWriter().write(resData.toString());
		} catch (JSONException e) {
			throw new ServletException("JSON 생성 에러", e);
		}
	}
}