package user.action.user;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;

public class GetUserPublicInfoAction implements Action {

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

				UserDao userDao = new UserDao(); 
				String userUuid = userDao.getUuidByNickname(userNickname);

				if (userUuid == null) {
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "존재하지 않는 사용자입니다.");
					return;
				}

				String publicInfo = UserDao.getUserPublicInfo(userUuid);

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

}