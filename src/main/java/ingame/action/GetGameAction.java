package ingame.action;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import controller.Action;
import ingame.model.InGame;
import ingame.model.InGameDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetGameAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (!isValidAuthorization(authorization)) {
            sendResponseStatusAndResult(response, HttpServletResponse.SC_UNAUTHORIZED, null);
            return;
        }

        String gameCode = request.getParameter("game-uuid");

        if (gameCode == null) {
            sendResponseStatusAndResult(response, HttpServletResponse.SC_BAD_REQUEST, null);
            return;
        }

        InGameDao inGameDao = InGameDao.getInstance();
        InGame game = inGameDao.getInGameByGameCode(gameCode);

        if (game == null) {
            sendResponseStatusAndResult(response, HttpServletResponse.SC_BAD_REQUEST, null);
            return;
        }

        try {
            JSONArray resData = new JSONArray();
            JSONObject resGame = buildGameJson(game);
            resData.put(resGame);

            sendResponseStatusAndResult(response, HttpServletResponse.SC_OK, resData);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseStatusAndResult(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
        }
    }

    private JSONObject buildGameJson(InGame game) {
        JSONObject resGame = new JSONObject();
        resGame.put("game_code", game.getInGameCode());
        resGame.put("topic", game.getTopic());
        resGame.put("keyword", game.getKeyword());
        resGame.put("round", game.getRound());
        resGame.put("time", game.getRegDate());
        return resGame;
    }

    private boolean isValidAuthorization(String authorization) {
        return authorization != null;
    }

    private void sendResponseStatusAndResult(HttpServletResponse response, int statuscode, JSONArray resData)
            throws IOException {
        JSONObject jsonResponse = new JSONObject();

        if (resData == null)
            resData = new JSONArray();

        jsonResponse.put("status_code", statuscode);
        jsonResponse.put("Game_Info", resData.length() > 0 ? resData.get(0) : new JSONObject());

        String json = jsonResponse.toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

}
