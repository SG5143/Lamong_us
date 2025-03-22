package room.action;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;

import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import room.model.RoomDao;
import room.model.RoomRequestDto;
import user.model.user.User;

public class CreateAttendanceLog implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패했습니다.");
			return;
		}

		HttpSession session = request.getSession();
		User log = (User) session.getAttribute("log");

		if (log == null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "세션이 만료되었거나 인증되지 않았습니다.");
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

			String roomCode = reqData.optString("room_code", null);

			if (roomCode == null) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "필수 요청 데이터가 누락되었습니다.");
				return;
			}

			RoomDao roomDao = RoomDao.getInstance();

			String userCode = log.getUuid();

			String existingStatus = roomDao.checkUserRoomStatus(userCode, roomCode);
			RoomRequestDto roomDto = new RoomRequestDto(userCode, roomCode);

			if ("exists".equals(existingStatus) || "in_progress".equals(existingStatus)) {
				try {
					roomDao.updateJoinTime(userCode, roomDto);
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "참가 시간이 성공적으로 업데이트되었습니다.");
				} catch (Exception e) {
					e.printStackTrace();
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"참가 시간 업데이트 중 오류가 발생했습니다.");
				}
			} else {
				try {
					roomDao.userToGameRoomAttendance(userCode, roomDto);
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "참가 기록이 성공적으로 저장되었습니다.");
				} catch (Exception e) {
					e.printStackTrace();
					sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"참가 기록 저장 중 오류가 발생했습니다.");
				}
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
