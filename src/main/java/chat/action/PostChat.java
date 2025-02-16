package chat.action;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import chat.model.ChatDao;
import chat.model.ChatRequestDto;
import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PostChat implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패했습니다");
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

			String chatRoom = reqData.optString("chat_room", null);
			String user = reqData.optString("user_uuid", null);
			String message = reqData.optString("message", null);

			// 입력값 검증
			if (chatRoom == null || chatRoom.isEmpty() || user == null || user.isEmpty() || message == null) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "필수 데이터가 누락되었습니다.");
				return;
			}
			ChatRequestDto chatDto = new ChatRequestDto(chatRoom, user, message);

			ChatDao chatDao = ChatDao.getInstance();

			if (chatDao.insertChat(chatDto))
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_CREATED, "메시지를 등록했습니다");
			else
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "요청 데이터가 누락되었습니다.");

		} catch (JSONException e) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 JSON 형식입니다.");
		} catch (IOException e) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "입력 데이터 처리 중 오류가 발생했습니다.");
		}

	}

	// 인증 검증 로직 구현 임시로 항상 true 반환
	private boolean isValidAuthorization(String authorization) {

		return authorization != null ;
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
