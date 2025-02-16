package chat.action;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import chat.model.Chat;
import chat.model.ChatDao;
import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ChatRoomMessages implements Action {

	private final ChatDao CHAT_DAO = ChatDao.getInstance();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendResponseStatusAndResult(response, HttpServletResponse.SC_UNAUTHORIZED, null);
			return;
		}

		String location = request.getParameter("location");
		String locationCode = request.getParameter("location-code");

		String chatRoomCode = CHAT_DAO.getChatRoomCodeBylocationCode(location, locationCode);

		if (chatRoomCode == null) {
			sendResponseStatusAndResult(response, HttpServletResponse.SC_BAD_REQUEST, null);
			return;
		}

		try {
			JSONObject resChatRoomCode = new JSONObject();
			resChatRoomCode.put("chat_room", chatRoomCode);
			JSONArray resChats = createChats(chatRoomCode);

			JSONArray resData = new JSONArray();
			resData.put(resChatRoomCode);
			resData.put(resChats);

			sendResponseStatusAndResult(response, HttpServletResponse.SC_OK, resData);
		} catch (Exception e) {
			e.printStackTrace();
			sendResponseStatusAndResult(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
		}

	}

	// Response-chats
	private JSONArray createChats(String roomCode) {
		JSONArray resChatInfo = new JSONArray();

		List<Chat> chatList = CHAT_DAO.getChatsByRoomCode(roomCode);

		int cnt = 1;
		for (Chat chat : chatList) {
			JSONObject chatJson = new JSONObject();
			chatJson.put("seq", cnt++);
			chatJson.put("writer", chat.getWriter());
			chatJson.put("message", chat.getMessage());
			chatJson.put("reg_date", chat.getRegDate().toString());
			resChatInfo.put(chatJson);
		}

		return resChatInfo;
	}

	// 인증 검증 로직 구현 임시로 항상 true 반환
	private boolean isValidAuthorization(String authorization) {

		return authorization != null;
	}

	private void sendResponseStatusAndResult(HttpServletResponse response, int statuscode, JSONArray resData)
			throws IOException {
		JSONObject jsonResponse = new JSONObject();

		if (resData == null)
			resData = new JSONArray();

		jsonResponse.put("status_code", statuscode);
		jsonResponse.put("chat_room", resData.length() > 0 ? resData.get(0) : new JSONObject());
		jsonResponse.put("chats", resData.length() > 1 ? resData.get(1) : new JSONObject());

		String json = jsonResponse.toString();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
}
