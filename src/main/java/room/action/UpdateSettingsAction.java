package room.action;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;

import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import room.model.RoomDao;
import room.model.RoomRequestDto;

public class UpdateSettingsAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		if (authorization == null || !isValidAuthorization(authorization)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패했습니다.");
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

			String title = reqData.optString("title", null);
			String password = reqData.optString("password", null);
			String isPrivate = !password.equals("") ? "on" : "off";
			String maxPlayers = reqData.optString("max_players", null);
			String roundCount = reqData.optString("round", null);
			String roomCode = reqData.optString("room_code", null);

			if (title == null || password == null || maxPlayers == null || roundCount == null || roomCode == null) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "필수 요청 데이터가 누락되었습니다.");
				return;
			}

			RoomDao roomDao = RoomDao.getInstance();
			RoomRequestDto roomDto = new RoomRequestDto(title, isPrivate, password, maxPlayers, roundCount, roomCode);

			try {
				roomDao.updateRoomSettings(roomDto);
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "방 설정이 정상적으로 업데이트되었습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"방 설정 업데이트 중 오류가 발생했습니다.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
		}

	}

	private boolean isValidAuthorization(String authorization) {
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
