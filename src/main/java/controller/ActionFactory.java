package controller;

import chat.action.AdminGetChatMessagesAction;
import chat.action.PostChatAction;
import util.HttpMethod;

public class ActionFactory {

	private ActionFactory() {}

	private static ActionFactory instance = new ActionFactory();

	public static ActionFactory getInstance() {
		return instance;
	}

	public Action getAction(String path, String command, HttpMethod method) {
		Action action = null;

		if (path == null && command == null)
			return action;
		else if (path.equals("members"))
			return getUserAction(command, method);
		else if (path.equals("game-room"))
			return getGameRoomAction(command, method);
		else if (path.equals("chat"))
			return getChatAction(command, method);

		return action;
	}

	private Action getUserAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.GET)
			return action;
		else if (method == HttpMethod.DELETE)
			return action;

		return action;
	}

	private Action getGameRoomAction(String command, HttpMethod method) {
		Action action = null;

		if (method == HttpMethod.POST)
			return action;
		else if (method == HttpMethod.GET)
			return action;
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

}