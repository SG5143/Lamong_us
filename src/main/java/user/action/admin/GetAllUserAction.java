package user.action.admin;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import user.model.user.*;

public class GetAllUserAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (!isValidAuthorization(authorization)) {
            sendResponseStatusAndResult(response, HttpServletResponse.SC_UNAUTHORIZED, null);
            return;
        }

        int totalCount = getUserCount();
        String pageParam = request.getParameter("page");
        int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        boolean isEnd = (page >= totalPages);

        JSONObject apiResponse = new JSONObject();
        apiResponse.put("status", 200);
        JSONObject meta = createMeta(totalCount, totalPages, isEnd);
        apiResponse.put("Meta", meta);

        sendResponseStatusAndResult(response, apiResponse.getInt("status"), apiResponse.toString());
    }

    private JSONObject createMeta(int totalCount, int totalPages, boolean isEnd) {
        JSONObject meta = new JSONObject();

        meta.put("total_count", totalCount);
        meta.put("pageable_count", totalPages);
        meta.put("is_end", isEnd);
        return meta;
    }

    private int getUserCount() {
        UserDao userDao = UserDao.getInstance();
        return userDao.countAllUsers();
    }

    private boolean isValidAuthorization(String authorization) {
        return authorization != null;
    }

    private void sendResponseStatusAndResult(HttpServletResponse response, int status, String result) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        responseJson.put("status", status);
        responseJson.put("result", result);

        out.print(responseJson.toString());
        out.flush();
    }
}
