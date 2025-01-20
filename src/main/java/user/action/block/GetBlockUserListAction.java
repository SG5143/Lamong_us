package user.action.block;

import java.io.*;
import java.util.*;
import org.json.*;
import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.block.*;

public class GetBlockUserListAction implements Action {

	private static final int PAGE_SIZE = 10;
	private final BlockDao blockDao = BlockDao.getInstance();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, null, "인증되지 않은 사용자입니다.");
			return;
		}

		String blockingUser = request.getParameter("blocking_user");

		if (blockingUser == null || blockingUser.isEmpty() || blockingUser.length() > 100) {
			sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, null, "잘못된 차단 사용자 파라미터입니다.");
			return;
		}

		int page = parsePageParameter(request.getParameter("page"));
		try {
			int totalCount = blockDao.getTotalBlockedUsersCount(blockingUser);
			List<Block> blockList = blockDao.getBlockedUser(blockingUser, page);

			JSONObject meta = createMeta(totalCount, (int) Math.ceil((double) totalCount / PAGE_SIZE), page);

			JSONArray resBlockInfo = new JSONArray();
			String message = null;
			if (blockList.isEmpty()) {
				message = "차단한 유저가 없습니다.";
			} else {
				resBlockInfo = createBlockInfo(blockList);
			}

			JSONArray resData = new JSONArray();
			resData.put(meta);
			resData.put(resBlockInfo);

			sendJsonResponse(response, HttpServletResponse.SC_OK, resData, message);
		} catch (Exception e) {
			e.printStackTrace();
			sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null, "서버 내부 오류가 발생했습니다.");
		}
	}

	// 페이지 형변환 (기본값 1)
	private int parsePageParameter(String pageParam) {
		try {
			return Integer.parseInt(pageParam);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	// Meta 정보 생성
	private JSONObject createMeta(int blockCount, int maxPage, int currentPage) {
		JSONObject resMeta = new JSONObject();
		resMeta.put("total_count", blockCount);
		resMeta.put("pageable_count", maxPage);
		resMeta.put("is_end", currentPage == maxPage);
		return resMeta;
	}

	// Block 정보 생성
	private JSONArray createBlockInfo(List<Block> blockList) {
		JSONArray resBlockInfo = new JSONArray();

		for (Block block : blockList) {
			JSONObject blockJson = new JSONObject();

			blockJson.put("blocking_user", block.getBlockingUser());
			blockJson.put("blocked_user", block.getBlockedUser());
			blockJson.put("reg_date", block.getRegDate().toString());

			resBlockInfo.put(blockJson);
		}
		return resBlockInfo;
	}

	// Authorization 유효성 검사
	private boolean isValidAuthorization(String authorization) {
		return authorization != null;
	}

	// 응답 전송
	private void sendJsonResponse(HttpServletResponse response, int statusCode, JSONArray resData, String message)
			throws IOException {
		JSONObject jsonResponse = new JSONObject();

		jsonResponse.put("status_code", statusCode);

		if (resData == null) {
			resData = new JSONArray();
		}

		jsonResponse.put("Meta", resData.length() > 0 ? resData.get(0) : new JSONObject());
		jsonResponse.put("Block_info", resData.length() > 1 ? resData.get(1) : new JSONArray());

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonResponse.toString());
	}
}
