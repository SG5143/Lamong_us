package controller;

import chat.action.*;
import user.action.*;
import room.action.*;

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

		return action;
	}

	private Action getMemberAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return new LoginFormAction();
		else if (method == HttpMethod.PATCH)
			return new UpdateFormAction();
		else if (method == HttpMethod.DELETE)
			return new DeleteFormAction();

		return action;
	}

	private Action getGameRoomAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return new CreateFormAction();
		else if (method == HttpMethod.GET)
			return new RoomListAction();
		else if (method == HttpMethod.PATCH)
			return action;
		else if (method == HttpMethod.DELETE)
			return action;

		return action;
	}

	private Action getChatAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return new PostChatAction();
		else if (method == HttpMethod.GET)
			return new AdminGetChatMessagesAction();

		return action;
	}

	private Action getChatRoomAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return new CreateChatRoomAction();
		else if (method == HttpMethod.GET)
			return new GetChatRoomMessagesAction();

		return action;
	}

}