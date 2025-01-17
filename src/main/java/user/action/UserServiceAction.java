package user.action;

import java.io.*;
import java.sql.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.*;
import util.*;

public class UserServiceAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		if (sb.length() > 0) {
			try {
				JSONObject jsonObject = new JSONObject(sb.toString());
				String userNickname = jsonObject.getString("userNickname");

				String userUuid = UserDao.getUuidByNickname(userNickname);

				if (userUuid == null) {
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "존재하지 않는 사용자입니다.");
					return;
				}

				String publicInfo = getUserPublicInfo(userUuid);

				if (publicInfo == null) {
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_NOT_FOUND, "사용자 정보를 찾을 수 없습니다.");
					return;
				}

				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(publicInfo);
			} catch (JSONException e) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 JSON 형식입니다.");
			}
		} else {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "요청 본문이 비어 있습니다.");
		}
	}

	private void sendResponseStatusAndMessage(HttpServletResponse response, int statusCode, String userInfo)
			throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("status", statusCode);

		if (userInfo != null) {
			JSONObject memberInfo = new JSONObject(userInfo);
			jsonResponse.put("Members", memberInfo);
		}

		response.setStatus(statusCode);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonResponse.toString());
	}

	private String getUserPublicInfo(String uuid) {
		if (uuid == null || uuid.isEmpty()) {
			return null;
		}

		JSONObject userInfo = new JSONObject();

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Users WHERE uuid = ?")) {

			pstmt.setString(1, uuid);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				userInfo.put("reg_date", rs.getTimestamp("reg_date").toInstant().toString());
				userInfo.put("score", rs.getInt("score"));
				userInfo.put("profile_image", rs.getString("profile_image"));
				userInfo.put("profile_info", rs.getString("profile_info"));
				userInfo.put("nickname", rs.getString("nickname"));
				userInfo.put("uuid", rs.getString("uuid"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return userInfo.toString();
	}
}