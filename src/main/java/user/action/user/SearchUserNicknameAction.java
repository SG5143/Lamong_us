package user.action.user;

import java.io.*;
import java.sql.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;

public class SearchUserNicknameAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject resData = new JSONObject();

		try (BufferedReader reader = request.getReader()) {
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			JSONObject reqData = new JSONObject(builder.toString());

			if (!reqData.has("nickname")) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resData.put("status", HttpServletResponse.SC_BAD_REQUEST);
				resData.put("error", "BAD REQUEST");
				resData.put("message", "잘못된 요청입니다. 필수 키 값이 누락되었습니다.");
				resData.put("timestamp", new Timestamp(System.currentTimeMillis()));
			} else {
				String nickname = reqData.getString("nickname");

				UserDao userDao = UserDao.getInstance();
				User user = userDao.findUserByUserNickname(nickname);

				boolean isValid = (user == null);
				resData.put("isValid", isValid);
				resData.put("status", HttpServletResponse.SC_OK);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resData.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resData.put("error", "SERVER ERROR");
			resData.put("message", "서버 처리 중 오류가 발생했습니다.");
			resData.put("timestamp", new Timestamp(System.currentTimeMillis()));
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(resData.toString());
	}
}
