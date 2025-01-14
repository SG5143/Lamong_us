package room.action;

import java.io.IOException;

import controller.Action;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import room.model.RoomDao;
import room.model.RoomRequestDto;

public class CreateFormAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String hostUser = request.getParameter("hostUser");
		String title = request.getParameter("title");
		String isPrivate = request.getParameter("isPrivate");
		String password = request.getParameter("password");
		String maxPlayers = request.getParameter("maxPlayers");
		String roundCount = request.getParameter("roundCount");

		RoomRequestDto roomDto = new RoomRequestDto(hostUser, title, isPrivate, password, maxPlayers, roundCount);

		RoomDao roomDao = RoomDao.getInstance();

		try {
			roomDao.createRoom(roomDto);

			response.sendRedirect("/");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("게임방 생성 실패..");
		}
	}
}
