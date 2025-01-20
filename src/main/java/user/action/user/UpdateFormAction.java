package user.action.user;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;

public class UpdateFormAction implements Action {

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

		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String jsonRequest = sb.toString();

		try {
			JSONObject jsonObject = new JSONObject(jsonRequest);

			String uuid = jsonObject.optString("uuid");
			String password = jsonObject.optString("password", null);
			String nickname = jsonObject.optString("nickname", null);
			String email = jsonObject.optString("email", null);
			String phone = jsonObject.optString("phone", null);
			String profileInfo = jsonObject.optString("profile_info", null);
			String profileImage = jsonObject.optString("profile_image", null);
			String loginType = jsonObject.optString("loginType", null);
			

			UserRequestDto userDto = new UserRequestDto();
			userDto.setUuid(uuid);
			userDto.setPassword(password);
			userDto.setNickname(nickname);
			userDto.setEmail(email);
			userDto.setPhone(phone);
			userDto.setProfileInfo(profileInfo);
			userDto.setProfileImage(profileImage != null ? profileImage.getBytes() : null);
			userDto.setLoginType(loginType);

			boolean isUpdated = updateUserInfo(userDto);

			if (isUpdated) 
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "회원 정보가 성공적으로 업데이트되었습니다.");
			else 
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "회원 정보 업데이트에 실패했습니다.");
		} catch (JSONException e) {
			e.printStackTrace();
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 JSON 형식입니다.");
		}
	}

	private boolean updateUserInfo(UserRequestDto userDto) {
		UserDao userDao = UserDao.getInstance();
		User updatedUser = userDao.updateUserInfo(userDto);
		if (updatedUser != null) {
			System.out.println("사용자 정보 업데이트 성공: " + updatedUser.getUuid());
			return true;
		} else {
			System.out.println("사용자 정보 업데이트 실패: " + userDto.getUuid());
			return false;
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
