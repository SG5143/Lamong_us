package room.action;

import java.io.IOException;
import org.json.JSONObject;

import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import room.model.Room;
import room.model.RoomDao;

public class GetRoomAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int roomNum = parseRoomNumber(request.getParameter("room_number"));

		if (roomNum <= 0) {
			sendResponseStatusAndResult(response, HttpServletResponse.SC_BAD_REQUEST, null);
			return;
		}

		try {
			RoomDao roomDao = RoomDao.getInstance();
			Room room = roomDao.getByRoomNumber(roomNum);

			if (room == null) {
				sendResponseStatusAndResult(response, HttpServletResponse.SC_NOT_FOUND, null);
				return;
			}

			JSONObject resData = createGameRoom(room);
			sendResponseStatusAndResult(response, HttpServletResponse.SC_OK, resData);

		} catch (Exception e) {
			e.printStackTrace();
			sendResponseStatusAndResult(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
		}

	}

	private int parseRoomNumber(String roomNumber) {
		try {
			return Integer.parseInt(roomNumber);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private JSONObject createGameRoom(Room room) {
		JSONObject roomJson = new JSONObject();

		if (room != null) {
			roomJson.put("room_code", room.getCode());
			roomJson.put("host_user", room.getHost());
			roomJson.put("room_number", room.getRoomNumber());
			roomJson.put("room_title", room.getTitle());
			roomJson.put("is_private", room.isPrivate());
			roomJson.put("password", room.getPassword());
			roomJson.put("max_players", room.getMaxPlayers());
			roomJson.put("round_count", room.getRoundCount());
		}

		return roomJson;
	}

	private void sendResponseStatusAndResult(HttpServletResponse response, int statusCode, JSONObject resData)
			throws IOException {
		JSONObject jsonResponse = new JSONObject();

		jsonResponse.put("status", statusCode);
		jsonResponse.put("GameRoom", resData != null ? resData : new JSONObject());

		String json = jsonResponse.toString();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
