package user.action.history;

import java.io.*;
import java.util.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.history.*;
import user.model.user.*;

public class GetGameHistoryAction implements Action {

	private static final int PAGE_SIZE = 10;
	private final HistoryDao historyDao = HistoryDao.getInstance();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, null, "인증되지 않은 사용자입니다.");
			return;
		}

		String userKey = authorization.substring(5).trim();

		User user = UserDao.findUserByUserkey(userKey);
		if (user == null) {
			sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, null, "유효하지 않은 userKey입니다.");
			return;
		}

		String playerUUID = user.getUuid();

		int pageNumber = parsePageParameter(request.getParameter("page"));

		List<History> gameHistoryList = historyDao.getPlayerGames(playerUUID, pageNumber);
		int totalGamesCount = historyDao.getPlayerGameCount(playerUUID);

		boolean isLastPage = gameHistoryList.size() < PAGE_SIZE || totalGamesCount == gameHistoryList.size();

		JSONObject responseJson = new JSONObject();
		responseJson.put("meta", createMeta(totalGamesCount, gameHistoryList.size(), isLastPage));
		responseJson.put("data", createPlayHistoryInfo(gameHistoryList));

		response.setContentType("application/json");
		response.getWriter().write(responseJson.toString());
	}

	private int parsePageParameter(String pageParam) {
		try {
			return Integer.parseInt(pageParam);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	private JSONObject createMeta(int totalCount, int gamesOnPage, boolean isEnd) {
		JSONObject resMeta = new JSONObject();
		resMeta.put("total_count", totalCount);
		resMeta.put("games_on_page", gamesOnPage);
		resMeta.put("is_end", isEnd);
		return resMeta;
	}

	private JSONArray createPlayHistoryInfo(List<History> gameHistoryList) {
		JSONArray resHistoryInfo = new JSONArray();

		for (History history : gameHistoryList) {
			JSONObject historyJson = new JSONObject();

			historyJson.put("game_uuid", history.getGameUuid());
			historyJson.put("play_date", history.getPlayDate().toString());
			historyJson.put("is_lying", history.isLying());
			historyJson.put("is_win", history.isWin());
			historyJson.put("score", history.getScore());

			resHistoryInfo.put(historyJson);
		}

		return resHistoryInfo;
	}

	private boolean isValidAuthorization(String authorization) {
		return authorization != null;
	}

	private void sendJsonResponse(HttpServletResponse response, int statusCode, JSONObject resData, String message)
			throws IOException {
		response.setStatus(statusCode);
		JSONObject responseJson = new JSONObject();
		responseJson.put("message", message);
		if (resData != null) {
			responseJson.put("data", resData);
		}
		response.getWriter().write(responseJson.toString());
	}
}
