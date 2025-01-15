package user.action;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.*;

public class UpdateFormAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

			UserRequestDto userDto = new UserRequestDto();
			userDto.setUuid(uuid);
			userDto.setPassword(password);
			userDto.setNickname(nickname);
			userDto.setEmail(email);
			userDto.setPhone(phone);
			userDto.setProfileInfo(profileInfo);
			userDto.setProfileImage(profileImage != null ? profileImage.getBytes() : null);

			boolean isUpdated = updateUserInfo(userDto);

			if (isUpdated) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "회원 정보가 성공적으로 업데이트되었습니다.");
			} else {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "회원 정보 업데이트에 실패했습니다.");
			}

		} catch (JSONException e) {
			e.printStackTrace();
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 JSON 형식입니다.");
		}
	}

	private boolean updateUserInfo(UserRequestDto userDto) {
		System.out.println("사용자 정보 업데이트: " + userDto.getUuid());
		return true;
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
