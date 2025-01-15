package room.action;

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

		if (authorization == null || !isValidAuthorization(authorization)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패했습니다.");
			return;
		}

		String hostUser = request.getParameter("hostUser");
		String title = request.getParameter("title");
		String isPrivate = request.getParameter("isPrivate");
		String password = request.getParameter("password");
		String maxPlayers = request.getParameter("maxPlayers");
		String roundCount = request.getParameter("roundCount");

		if (hostUser == null || title == null || maxPlayers == null || roundCount == null) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "필수 요청 데이터가 누락되었습니다.");
			return;
		}

		RoomRequestDto roomDto = new RoomRequestDto(hostUser, title, isPrivate, password, maxPlayers, roundCount);
		RoomDao roomDao = RoomDao.getInstance();

		try {
			roomDao.createRoom(roomDto);
			response.sendRedirect("/");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("게임방 생성 실패..");
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
