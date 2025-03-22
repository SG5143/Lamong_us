package user.action.user;

import java.io.*;
import java.sql.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;
import user.model.user.User;

public class SearchUsernameAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder builder = new StringBuilder();

		BufferedReader reader = request.getReader();
		while (reader.ready()) {
			builder.append(reader.readLine());
		}

		JSONObject reqData = new JSONObject(builder.toString());
		JSONObject resData = new JSONObject();

		if (!reqData.has("username")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resData.put("status", HttpServletResponse.SC_BAD_REQUEST);
			resData.put("error", "BAD REQUEST");
			resData.put("message", "잘못된 요청입니다. 필수 키 값이 누락되었습니다.");
			resData.put("timestamp", new Timestamp(System.currentTimeMillis()));
		} else {
			String username = reqData.getString("username");

			UserDao userDao = UserDao.getInstance();
			User user = userDao.findUserByUsername(username);

			boolean isValid = user == null;

			resData.put("isValid", isValid);
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		PrintWriter out = response.getWriter();
		out.append(resData.toString());
		out.flush();

	}

}