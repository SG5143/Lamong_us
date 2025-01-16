package user.action;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.*;

public class JoinFormAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			BufferedReader reader = request.getReader();
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}

			String requestBody = jsonBuilder.toString();

			JSONObject reqData = new JSONObject(requestBody);

			String username = reqData.optString("username", null);
			String password = reqData.optString("password", null);
			String email = reqData.optString("email", null);
			String nickname = reqData.optString("nickname", null);
			String phone = reqData.optString("phone", null);
			String loginType = reqData.optString("login_type", null);

			if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null
					|| email.isEmpty() || nickname == null || nickname.isEmpty() || phone == null || phone.isEmpty()) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "필수 데이터가 누락되었습니다.");
				return;
			}
			
			if (loginType == null || loginType.isEmpty()) {
				loginType = "NULL"; 
			}

			UserRequestDto userDto = new UserRequestDto(username, password, email, nickname, phone, loginType);
			UserDao userDao = UserDao.getInstance();

			if (isDuplicate(userDao, username, email, phone, nickname, response)) {
				return;
			}

			userDao.createUser(userDto);
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_CREATED, "회원가입이 완료되었습니다.");

		} catch (JSONException e) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 JSON 형식입니다.");
		} catch (Exception e) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
		}
	}

	private boolean isDuplicate(UserDao userDao, String username, String email, String phone, String nickname,
			HttpServletResponse response) throws IOException {
		if (userDao.findUserByUsername(username) != null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_CONFLICT, "이미 사용 중인 아이디입니다.");
			return true;
		}
		if (userDao.findUserByUserEmail(email) != null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_CONFLICT, "이미 사용 중인 이메일입니다.");
			return true;
		}
		if (userDao.findUserByUserPhone(phone) != null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_CONFLICT, "이미 사용 중인 전화번호입니다.");
			return true;
		}
		if (userDao.findUserByUserNickname(nickname) != null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_CONFLICT, "이미 사용 중인 닉네임입니다.");
			return true;
		}
		return false;
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
