package websoket;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;

import org.json.JSONObject;

import jakarta.websocket.Session;

public class SandGameMessage {
	private static final SandGameMessage INSTANCE = new SandGameMessage();
	private final Logger LOGGER = Logger.getLogger(LiarGameManager.class.getName());

	private SandGameMessage() {}

	public static SandGameMessage getInstance() {
		return INSTANCE;
	}
	
	public void broadcastKeywordReveal(GameSession session) {
	    JSONObject keywordMessage = JsonUtil.createJsonMessage(
	        GameConstants.TYPE_KEYWORD_REVEAL,
	        GameConstants.KEYWORD, session.getKeyword()
	    );
	    session.getClients().forEach(client -> sendJsonMessage(client, keywordMessage));
	}

	public void voteResult(List<Session> clients,  GameSession.VoteResult result) {
		for (Session client : clients) {
			sendJsonMessage(client, JsonUtil.createJsonMessage(
					GameConstants.TYPE_VOTE_RESULT,
					GameConstants.VOTED_PLAYER_NICKNAME, result.getNICKNAME(),
					GameConstants.VOTE_RESULT_MOST_VOTED, result.getMostVotedPlayerUUID(),
					GameConstants.VOTE_RESULT_MAX_VOTES, result.getMaxVotes(),
					GameConstants.IS_LIAR_CORRECT, result.isLiarCorrect()
					));
		}
	}
	
	public void broadcastStateChange(List<Session> clients, String state) {
		JSONObject stateMessage = JsonUtil.createJsonMessage(
				GameConstants.TYPE_STATE_CHANGE, 
				GameConstants.GAME_STATE, state);
		for (Session client : clients) {
			sendJsonMessage(client, stateMessage);
		}
	}

	public void sendTurnInfo(List<Session> clients, int currentTurn) {
		for (Session client : clients) {
			sendJsonMessage(client, JsonUtil.createJsonMessage(
					GameConstants.TYPE_CUR_TURN,
					GameConstants.CURRENT_TURN, currentTurn));
		}
	}

	public void clientInfoMessage(List<Session> clients, List<Map<String, Object>> playersInfo) {
		for (Session client : clients) {
			JSONObject jsonObject = JsonUtil.createJsonMessage(
					GameConstants.TYPE_CLIENT_INFO,
					GameConstants.CLIENT_INFO, playersInfo);
			sendJsonMessage(client, jsonObject);
		}
	}

	public void endGameMessage(List<Session> clients, String endStatus) {
		for (Session client : clients) {
			sendJsonMessage(client, JsonUtil.createJsonMessage(
					GameConstants.TYPE_GAME_END,
					GameConstants.GAME_END, endStatus));
		}
	}

	public void sendChangeRound(List<Session> clients, int round) {
		for (Session client : clients) {
			JSONObject jsonObject = JsonUtil.createJsonMessage(
					GameConstants.TYPE_ROUND_INFO,
					GameConstants.ROUND, round);
			sendJsonMessage(client, jsonObject);
		}
	}

	public void assignTurnToClient(Session client, int turn) {
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				GameConstants.TYPE_SET_TURN,
				GameConstants.TURN, turn));
	}

	public void distributeWords(GameSession session) {
		List<Session> clients = session.getClients();
		for (Session client : clients) {
			JSONObject wordMessage = JsonUtil.createJsonMessage(
					GameConstants.TYPE_GAME_START,
					GameConstants.TOPIC, session.getTopic(), 
					GameConstants.KEYWORD, client.getId().equals(session.getLiarId()) ? null : session.getKeyword()); // 라이어는 키워드 제외
			sendJsonMessage(client, wordMessage);
		}
	}

	public void broadcastSystemMessage(List<Session> clients, String message) {
		JSONObject systemMessage = JsonUtil.createJsonMessage(
				GameConstants.TYPE_SYSTEM_MESSAGE,
				GameConstants.MESSAGE, message);
		clients.forEach(client -> sendJsonMessage(client, systemMessage));
	}

	public void broadcastMessage(List<Session> clients , String senderUUID, String nickname, String message) {
		JSONObject jsonObject = new JSONObject(message);
		String extractedMessage = jsonObject.getString("message");

		JSONObject chatMessage = JsonUtil.createJsonMessage(
				GameConstants.TYPE_MESSAGE,
				GameConstants.SENDER_UUID, senderUUID,
				GameConstants.SENDER_NICKNAME, nickname,
				GameConstants.MESSAGE, extractedMessage);
		clients.forEach(client -> {
			sendJsonMessage(client, chatMessage);
		});
	}

	public boolean sendJsonMessage(Session client, JSONObject message) {
		try {
			client.getBasicRemote().sendText(message.toString());
			return true;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			return false;
		}
	}

}
