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

public class CreateFormAction implements Action {

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

			String hostUser = reqData.optString("host_user", null);
			String title = reqData.optString("title", null);
			String isPrivate = reqData.optString("is_private", null);
			String password = reqData.optString("password", null);
			String maxPlayers = reqData.optString("max_players", null);
			String roundCount = reqData.optString("round", null);

			if (hostUser == null || title == null || maxPlayers == null || roundCount == null) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "필수 요청 데이터가 누락되었습니다.");
				return;
			}

			RoomDao roomDao = RoomDao.getInstance();

			int roomNumber = roomDao.getAvailableRoomNumber();
			RoomRequestDto roomDto = new RoomRequestDto(roomNumber, hostUser, title, isPrivate, password, maxPlayers,
					roundCount);

			try {
				roomDao.createRoom(roomDto);
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_CREATED, "게임방이 생성되었습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"게임방 생성 중 오류가 발생했습니다.");
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
