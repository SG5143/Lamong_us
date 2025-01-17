package ingame.action;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import controller.Action;
import ingame.model.InGame;
import ingame.model.InGameDao;
import ingame.model.UserGameRecord;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetGameRecordAction implements Action {
    private final InGameDao inGameDao = InGameDao.getInstance();

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

        try {
            JSONArray resData = new JSONArray();
            JSONObject resGameInfo = buildGameJson(gameCode);
            JSONArray resMembers = buildMembersJson(gameCode);
            resData.put(resGameInfo);
            resData.put(resMembers);

            sendResponseStatusAndResult(response, HttpServletResponse.SC_OK, resData);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseStatusAndResult(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
        }
    }

    private JSONArray buildMembersJson(String gameCode) {
        JSONArray resMembers = new JSONArray();
        List<UserGameRecord> members = inGameDao.getUserGameRecordByGameCode(gameCode);

        for (UserGameRecord member : members) {
            JSONObject resMember = new JSONObject();
            resMember.put("user_id", member.getPlayUser());
            resMember.put("before_score", member.getBeforeScore());
            resMember.put("score_change", member.getScoreChange());
            resMember.put("is_lying", member.isLying());
            resMember.put("is_vote_correct", member.isVoteCorrect());
            resMember.put("is_quit", member.isQuit());
            resMember.put("is_win", member.isWin());

            resMembers.put(resMember);
        }

        return resMembers;
    }

    private JSONObject buildGameJson(String gameCode) {
        InGame game = inGameDao.getInGameByGameCode(gameCode);
        JSONObject resGame = new JSONObject();
        resGame.put("game_code", game.getInGameCode());
        resGame.put("time",game.getRegDate());
        resGame.put("topic", game.getTopic());
        resGame.put("keyword", game.getKeyword());
        resGame.put("round", game.getRound());
        resGame.put("win_type", game.getWinType());
        resGame.put("end_type", game.getEndType());

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
        jsonResponse.put("Members", resData.length() > 1 ? resData.get(1) : new JSONArray());

        String json = jsonResponse.toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

}
