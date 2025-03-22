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

public class AdminChatHistory implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendResponseStatusAndResult(response, HttpServletResponse.SC_UNAUTHORIZED, null);
			return;
		}

		String resUser = request.getParameter("user-uuid");
		int page = parsePage(request.getParameter("page"));
		int chatCount = getChatCount(resUser);
		int maxPage = calculateMaxPage(chatCount);

		if (page < 1 || page > maxPage) {
			sendResponseStatusAndResult(response, HttpServletResponse.SC_BAD_REQUEST, null);
			return;
		}

		try {
			JSONObject resMeta = createMeta(chatCount, maxPage, page);
			JSONArray resChatInfo = createChatInfo(resUser, page);

			JSONArray resData = new JSONArray();
			resData.put(resMeta);
			resData.put(resChatInfo);

			sendResponseStatusAndResult(response, HttpServletResponse.SC_OK, resData);
		} catch (Exception e) {
			e.printStackTrace();
			sendResponseStatusAndResult(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
		}
	}

	// 페이지 형변환 (기본값 1)
	private int parsePage(String pageParam) {
		try {
			return Integer.parseInt(pageParam);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	// 채팅 개수 가져오기
	private int getChatCount(String resUser) {
		ChatDao chatDao = ChatDao.getInstance();
		return chatDao.getUserChatCount(resUser);
	}

	// 최대 페이지 계산
	private int calculateMaxPage(int chatCount) {
		return (int) Math.ceil((double) chatCount / 20);
	}

	// Response-Meta
	private JSONObject createMeta(int chatCount, int maxPage, int currentPage) {
		JSONObject resMeta = new JSONObject();
		resMeta.put("total_count", chatCount);
		resMeta.put("pageable_count", maxPage);
		resMeta.put("is_end", currentPage == maxPage);
		return resMeta;
	}

	// Response-ChatInfo
	private JSONArray createChatInfo(String resUser, int page) {
		JSONArray resChatInfo = new JSONArray();

		ChatDao chatDao = ChatDao.getInstance();
		List<Chat> chatList = chatDao.getUserChatsForPage(resUser, page);
		
		for (Chat chat : chatList) {
			JSONObject chatJson = new JSONObject();
			chatJson.put("room", chat.getChatRoomCode());
			chatJson.put("location", chat.getLocation());
			chatJson.put("location_code", chat.getLocationCode());
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
		jsonResponse.put("Meta", resData.length() > 0 ? resData.get(0) : new JSONObject());
		jsonResponse.put("Chat_Info", resData.length() > 1 ? resData.get(1) : new JSONObject());

		String json = jsonResponse.toString();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
