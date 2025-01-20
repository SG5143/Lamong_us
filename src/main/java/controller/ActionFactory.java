package controller;

import user.action.*;
import user.action.block.*;
import user.action.history.*;
import user.action.user.*;
import room.action.*;
import chat.action.*;
import ingame.action.*;

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
		Action action = null;

		if (method == HttpMethod.POST)
			return new PostBlockUserAction();
		else if (method == HttpMethod.GET)
			return new GetGameHistoryAction();
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
		else if (method == HttpMethod.GET && command != null && command.equals("all"))
			return new RoomListAction();
		else if (method == HttpMethod.GET)
			return new GetRoomAction();
		else if (method == HttpMethod.PATCH)
			return new UpdateSettingsAction();
		else if (method == HttpMethod.DELETE)
			return new DeleteRoomAction();

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