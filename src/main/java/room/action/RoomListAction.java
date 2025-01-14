package room.action;

import java.io.IOException;
import java.util.List;

import controller.Action;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import room.model.RoomDao;
import room.model.RoomResponseDto;

public class RoomListAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int page = 1;

		try {
			page = Integer.parseInt(request.getParameter("page"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		RoomDao roomDao = RoomDao.getInstance();

		List<RoomResponseDto> room = roomDao.findAllRoom(page);

		if (page != 1 && room.size() == 0) {
			String referer = request.getHeader("Referer");

			if (referer != null)
				response.sendRedirect(referer);
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/game-room");
		request.setAttribute("room", room);
		dispatcher.forward(request, response);

	}

}
