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

public class CreateGameAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (!isValidAuthorization(authorization)) {
            sendResponseStatusAndMessage(response, HttpServletResponse.SC_UNAUTHORIZED, null);
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

            String reqRoomCode = reqData.optString("room", null);
            String reqRound = reqData.optString("round_count", null);
            String reqTopic = reqData.optString("topic", null);
            String reqKeyword = reqData.optString("game_keyword", null);
            
            int round = 0;
            try{
                round = Integer.parseInt(reqRound);
            } catch (NumberFormatException e) {
                sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "round 입력값이 잘못되었습니다");
            }

            // 입력값 검증
            if (reqRoomCode == null || reqTopic == null || reqKeyword == null) {
                sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "입력값이 잘못되었습니다");
            }

            InGameDao inGameDao = InGameDao.getInstance();
            InGameRequestDto dto = new InGameRequestDto(reqRoomCode, reqTopic, reqKeyword, round);

            String gameCode = inGameDao.createInGame(dto);
            if (gameCode != null)
                sendResponseStatusAndMessage(response, HttpServletResponse.SC_CREATED, "게임방을 생성했습니다", gameCode);
            else
                sendResponseStatusAndMessage(response, HttpServletResponse.SC_BAD_REQUEST, "게임방 생성에 실패했습니다");

        } catch (Exception e) {
            sendResponseStatusAndMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // 인증 검증 로직 구현 임시로 항상 true 반환
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

    private void sendResponseStatusAndMessage(HttpServletResponse response, int statusCode, String message,
            String gameCode) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", statusCode);
        jsonResponse.put("message", message);
        jsonResponse.put("game-uuid", gameCode);

        String json = jsonResponse.toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
