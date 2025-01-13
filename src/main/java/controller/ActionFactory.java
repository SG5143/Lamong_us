package controller;

import util.HttpMethod;

public class ActionFactory {

	private ActionFactory() {}

	private static ActionFactory instance = new ActionFactory();

	public static ActionFactory getInstance() {
		return instance;
	}

	public Action getAction(String path, String command, HttpMethod method) {
		Action action = null;

		if (path == null || command == null)
			return action;
		else if (path.equals("user"))
			return action;

		return action;
	}

	private Action getUserAction(String command, HttpMethod method) {
		Action action = null;

		return action;
	}

	private Action getApiAction(String command, HttpMethod method) {
		Action action = null;

		return action;
	}

}