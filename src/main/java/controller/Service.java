package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpMethod;

import java.io.IOException;
import java.sql.Timestamp;

import org.json.JSONObject;

@MultipartConfig(maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5)
public class Service extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String path = request.getPathInfo();
		path = path != null ? path.substring(1) : path;

		String command = request.getParameter("command");
		String method = request.getMethod();

		ActionFactory actionFactory = ActionFactory.getInstance();
		Action action = actionFactory.getAction(path, command, HttpMethod.valueOf(method));

		if (action != null) {
			action.execute(request, response);
		} else {
			JSONObject resData = new JSONObject();
			resData.put("status", HttpServletResponse.SC_BAD_REQUEST);
			resData.put("error", "BAD REQUEST");
			resData.put("message", "잘못된 요청입니다. 필수 키 값이 누락되었습니다.");
			resData.put("timestamp", new Timestamp(System.currentTimeMillis()));

			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().append(resData.toString());
			response.getWriter().flush();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if ("PATCH".equalsIgnoreCase(request.getMethod()))
			doGet(request, response);
		else
			super.service(request, response);
	}

}
