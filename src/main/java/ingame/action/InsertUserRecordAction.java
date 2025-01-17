package ingame.action;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;

import controller.Action;
import ingame.model.InGameDao;
import ingame.model.UserGameRecordRequestDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InsertUserRecordAction implements Action {

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

            String playUser = reqData.optString("user", null);
            String gameCode = reqData.optString("game_code", null);
            String scoreChange = reqData.optString("score_change", null);
            String isVoteCorrect = reqData.optString("is_vote_correct", null);
            String isLying = reqData.optString("is_lying", null);
            String isQuit = reqData.optString("is_quit", null);
            String isWin = reqData.optString("is_win", null);

            if (playUser == null || gameCode == null || scoreChange == null || isVoteCorrect == null || isLying == null
                    || isQuit == null ) {
                sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "입력값이 잘못되었습니다");
                return;
            }

            InGameDao inGameDao = InGameDao.getInstance();

            UserGameRecordRequestDto dto = new UserGameRecordRequestDto(playUser, gameCode, isLying, isVoteCorrect,
                    scoreChange, isQuit, isWin);


            if (inGameDao.createInGameRecord(dto))
                sendResponseStatusAndMessage(response, HttpServletResponse.SC_OK, "게임 기록을 업데이트했습니다");
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
        jsonResponse.put("status", statusCode);
        jsonResponse.put("message", message);

        String json = jsonResponse.toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
