package room.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import room.model.Room;
import room.model.RoomDao;

public class RoomListAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int page = parsePage(request.getParameter("page"));
		int roomCount = getRoomCount();
		int maxPage = calculateMaxPage(roomCount);

		if (page < 1 || page > maxPage) {
			sendResponseStatusAndResult(response, HttpServletResponse.SC_BAD_REQUEST, null);
			return;
		}

		try {
			JSONObject resMeta = createMeta(roomCount, maxPage, page);
			JSONArray resGameRoom = createGameRoom(page);

			JSONArray resData = new JSONArray();
			resData.put(resMeta);
			resData.put(resGameRoom);

			sendResponseStatusAndResult(response, HttpServletResponse.SC_OK, resData);
		} catch (Exception e) {
			e.printStackTrace();
			sendResponseStatusAndResult(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
		}

	}

	private int parsePage(String pageParam) {
		try {
			return Integer.parseInt(pageParam);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	private int getRoomCount() {
		RoomDao roomDao = RoomDao.getInstance();
		return roomDao.getRoomCount();
	}

	private int calculateMaxPage(int roomCount) {
		return (int) Math.ceil((double) roomCount / 10);
	}

	private JSONObject createMeta(int roomCount, int maxPage, int currentPage) {
		JSONObject resMeta = new JSONObject();
		resMeta.put("total_count", roomCount);
		resMeta.put("pageable_count", maxPage);
		resMeta.put("is_end", currentPage);
		return resMeta;
	}

	private JSONArray createGameRoom(int page) {
		JSONArray resGameRoom = new JSONArray();

		RoomDao roomDao = RoomDao.getInstance();
		List<Room> roomList = roomDao.fetchRoomsForPage(page);

		for (Room room : roomList) {
			JSONObject roomJson = new JSONObject();
			roomJson.put("room_code", room.getCode());
			roomJson.put("host_user", room.getHost());
			roomJson.put("room_number", room.getRoomNumber());
			roomJson.put("room_title", room.getTitle());
			roomJson.put("is_private", room.isPrivate());
			roomJson.put("password", room.getPassword());
			roomJson.put("max_players", room.getMaxPlayers());
			roomJson.put("round_count", room.getRoundCount());
			roomJson.put("state", room.getState());

			int curPlayers = roomDao.getCurrentPlayers(room.getCode());
			roomJson.put("current_players", curPlayers);
			resGameRoom.put(roomJson);
		}

		return resGameRoom;
	}

	private void sendResponseStatusAndResult(HttpServletResponse response, int statuscode, JSONArray resData)
			throws IOException {
		JSONObject jsonResponse = new JSONObject();

		if (resData == null)
			resData = new JSONArray();

		jsonResponse.put("status_code", statuscode);
		jsonResponse.put("Meta", resData.length() > 0 ? resData.get(0) : new JSONObject());
		jsonResponse.put("GameRoom", resData.length() > 1 ? resData.get(1) : new JSONObject());

		String json = jsonResponse.toString();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
