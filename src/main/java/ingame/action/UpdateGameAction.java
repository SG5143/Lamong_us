package ingame.action;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;

import controller.Action;
import ingame.model.InGameDao;
import ingame.model.InGameRequestDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UpdateGameAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		if (!isValidAuthorization(authorization)) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패했습니다");
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

			String inGameCode = reqData.optString("room", null);
			String endType = reqData.optString("end_type", null);
			String winType = reqData.optString("win_type", null);

			if (inGameCode == null) {
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "입력값이 잘못되었습니다");
				return;
			}

			InGameDao inGameDao = InGameDao.getInstance();
			InGameRequestDto dto = new InGameRequestDto(inGameCode, endType, winType);

			if (inGameDao.updateInGameResult(dto))
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "게임방을 업데이트했습니다");
			else
				sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "업데이트 실패했습니다.");
		} catch (Exception e) {
			sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 에러 발생");
		}
	}

	private boolean isValidAuthorization(String authorization) {
		return authorization != null;
	}

	private void sendResponseStatusAndMessage(HttpServletResponse response, int statusCode, String message)
			throws IOException {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("status_code", statusCode);
		jsonResponse.put("message", message);

		String json = jsonResponse.toString();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}
