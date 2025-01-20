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

public class JoinRoomAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
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

			String userCode = reqData.optString("user_code", null);
			String roomCode = reqData.optString("room_code", null);

			if (roomCode == null) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "필수 요청 데이터가 누락되었습니다.!!");
				return;
			}

			RoomDao roomDao = RoomDao.getInstance();
			RoomRequestDto roomDto = new RoomRequestDto(userCode, roomCode);

			try {
				roomDao.userJoinRoom(roomDto);
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "방에 성공적으로 입장했습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"방 입장 중 오류가 발생했습니다.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
		}

	}

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
