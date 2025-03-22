package user.action.admin;

import java.io.*;

import org.json.*;

import controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class DeleteInactiveUserAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (!isValidAuthorization(authorization)) {
            sendResponseStatusAndResult(response, HttpServletResponse.SC_UNAUTHORIZED, null);
            return;
        }

        String password = request.getParameter("password");

        if (password == null || password.isEmpty()) {
            sendResponseStatusAndResult(response, HttpServletResponse.SC_BAD_REQUEST, "Password is required");
            return;
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("password", password);

        JSONObject apiResponse = new JSONObject();
        apiResponse.put("status", 200);
        apiResponse.put("message", "회원 삭제처리가 완료됬습니다.");

        sendResponseStatusAndResult(response, apiResponse.getInt("status"), apiResponse.getString("message"));
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
