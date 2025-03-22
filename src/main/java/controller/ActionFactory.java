package controller;

import user.action.admin.*;
import user.action.block.*;
import user.action.history.*;
import user.action.user.*;
import room.action.*;
import chat.action.*;
import ingame.action.*;

import util.HttpMethod;

public class ActionFactory {

	private ActionFactory() {
	}

	private static ActionFactory instance = new ActionFactory();

	public static ActionFactory getInstance() {
		return instance;
	}

	public Action getAction(String path, String command, HttpMethod method) {
		Action action = null;

		if (path == null && command == null)
			return action;
		else if (path.equals("members"))
			return getMemberAction(command, method);
		else if (path.equals("game-room"))
			return getGameRoomAction(command, method);
		else if (path.equals("chat"))
			return getChatAction(command, method);
		else if (path.equals("chat-room"))
			return getChatRoomAction(command, method);
		else if (path.equals("in-game"))
			return getInGameAction(command, method);
		else if (path.equals("record-game"))
			return getRecordGameAction(command, method);

		return action;
	}

	private Action getMemberAction(String command, HttpMethod method) {

		return switch (method) {
		case POST -> switch (command) {
		case "login" -> new LoginFormAction();
		case "logout" -> new LogoutFormAction();
		case "join" -> new JoinFormAction(); 
		case "block" -> new PostBlockUserAction();
		case "check-password" -> new CheckUserPassword();
		case "search-username" -> new SearchUsernameAction();
		case "search-email" -> new SearchUserEmailAction(); 
		case "search-phone" -> new SearchUserPhoneAction();
		case "search-nickname" -> new SearchUserNicknameAction();
		default -> null;
		};
		case GET -> switch (command) {
		case "all_users_info" -> new GetAllUserAction();
		case "history" -> new GetGameHistoryAction();
		case "user_info" -> new GetUserPublicInfoAction();
		case "blocked_list" -> new GetBlockUserListAction();
		case "get_nickname" -> new GetUserNicknameByUuid();
		default -> null;
		};
		case PATCH -> switch (command) {
		case "update" -> new UpdateFormAction();
		default -> null;
		};
		case DELETE -> switch (command) {
		case "delete" -> new DeleteFormAction();
		case "cancelAllBlock" -> new DeleteBlockedAllUserAction();
		case "deleteALlInactive" -> new DeleteInactiveUserAction();
		default -> null;
		};
		default -> null;
		};
	}

	private Action getGameRoomAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST && command != null && command.equals("attend-log"))
			return new CreateAttendanceLog();
		else if (method == HttpMethod.POST)
			return new CreateFormAction();
		else if (method == HttpMethod.GET && command != null && command.equals("all"))
			return new RoomListAction();
		else if (method == HttpMethod.GET)
			return new GetRoomAction();
		else if (method == HttpMethod.PATCH && command != null && command.equals("update-leave"))
			return new LeaveRoomAction();
		else if (method == HttpMethod.PATCH && command != null && command.equals("update-join"))
			return new JoinRoomAction();
		else if (method == HttpMethod.PATCH)
			return new UpdateSettingsAction();
		else if (method == HttpMethod.DELETE)
			return new DeleteRoomAction();

		return action;
	}

	private Action getChatAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return new PostChat();
		else if (method == HttpMethod.GET)
			return new AdminChatHistory();

		return action;
	}

	private Action getChatRoomAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return new CreateChatRoom();
		else if (method == HttpMethod.GET)
			return new ChatRoomMessages();

		return action;
	}

	private Action getInGameAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return new CreateGameAction();
		else if (method == HttpMethod.GET)
			return new GetGameAction();
		else if (method == HttpMethod.PATCH)
			return new UpdateGameAction();

		return action;
	}

	private Action getRecordGameAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return new InsertUserRecordAction();
		else if (method == HttpMethod.GET)
			return new GetGameRecordAction();

		return action;
	}

}