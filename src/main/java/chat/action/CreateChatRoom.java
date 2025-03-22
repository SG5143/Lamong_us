package chat.action;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import chat.model.ChatDao;
import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CreateChatRoom implements Action{

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, null);
			return;
		}
		
		try {
			BufferedReader reader = request.getReader();
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}
			String requestBody = jsonBuilder.toString();

			JSONObject reqData = new JSONObject(requestBody);

			String location = reqData.optString("location", null);
			String locationCode = reqData.optString("location_code", null);

			// 입력값 검증
			if (location == null || location.isEmpty() || locationCode == null || locationCode.isEmpty()) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "필수 데이터가 누락되었습니다.");
				return;
			}

			ChatDao chatDao = ChatDao.getInstance();

			if (chatDao.ensureChatRoomExists(location, locationCode))
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_CREATED, "채팅방을 생성했습니다");
			else
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "채팅방 생성에 실패했습니다");

		} catch (JSONException e) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 JSON 형식입니다.");
		} catch (IOException e) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "입력 데이터 처리 중 오류가 발생했습니다.");
		}
	}
	
	// 인증 검증 로직 구현 임시로 항상 true 반환
	private boolean isValidAuthorization(String authorization) {

		return authorization != null;
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
