package user.action.user;

import java.io.*;
import java.sql.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;

public class CheckUserPassword implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder builder = new StringBuilder();

		BufferedReader reader = request.getReader();
		while (reader.ready()) {
			builder.append(reader.readLine());
		}

		JSONObject reqData = new JSONObject(builder.toString());
		JSONObject resData = new JSONObject();

		if (!reqData.has("uuid") || !reqData.has("password")) {
			resData.put("status", HttpServletResponse.SC_BAD_REQUEST);
			resData.put("error", "BAD REQUEST");
			resData.put("message", "잘못된 요청입니다. 필수 키 값이 누락되었습니다.");
			resData.put("timestamp", new Timestamp(System.currentTimeMillis()));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			String useruuid = reqData.getString("uuid");
			String password = reqData.getString("password");

			UserDao userDao = UserDao.getInstance();
			User user = userDao.findUserByUuid(useruuid);

			if (user == null) {
				resData.put("status", HttpServletResponse.SC_NOT_FOUND);
				resData.put("message", "사용자를 찾을 수 없습니다.");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				boolean isValid = user.checkPassword(password);

				resData.put("status", HttpServletResponse.SC_OK);
				resData.put("isValid", isValid);
				resData.put("message", isValid ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다.");
			}
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		PrintWriter out = response.getWriter();
		out.append(resData.toString());
		out.flush();
	}

}
