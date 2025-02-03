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
	        MessageConstants.TYPE_KEYWORD_REVEAL,
	        MessageConstants.KEYWORD, session.getKeyword()
	    );
	    session.getClients().forEach(client -> sendJsonMessage(client, keywordMessage));
	}

	public void voteResult(List<Session> clients,  GameSession.VoteResult result) {
		for (Session client : clients) {
			sendJsonMessage(client, JsonUtil.createJsonMessage(
					MessageConstants.TYPE_VOTE_RESULT,
					MessageConstants.VOTED_PLAYER_NICKNAME, result.getNICKNAME(),
					MessageConstants.VOTE_RESULT_MOST_VOTED, result.getMostVotedPlayerUUID(),
					MessageConstants.VOTE_RESULT_MAX_VOTES, result.getMaxVotes(),
					MessageConstants.IS_LIAR_CORRECT, result.isLiarCorrect()
					));
		}
	}
	
	public void broadcastStateChange(List<Session> clients, String state) {
		JSONObject stateMessage = JsonUtil.createJsonMessage(
				MessageConstants.TYPE_STATE_CHANGE, 
				MessageConstants.GAME_STATE, state);
		for (Session client : clients) {
			sendJsonMessage(client, stateMessage);
		}
	}

	public void sendTurnInfo(List<Session> clients, int currentTurn) {
		for (Session client : clients) {
			sendJsonMessage(client, JsonUtil.createJsonMessage(
					MessageConstants.TYPE_CUR_TURN,
					MessageConstants.CURRENT_TURN, currentTurn));
		}
	}

	public void clientInfoMessage(List<Session> clients, List<Map<String, Object>> playersInfo) {
		for (Session client : clients) {
			JSONObject jsonObject = JsonUtil.createJsonMessage(
					MessageConstants.TYPE_CLIENT_INFO,
					MessageConstants.CLIENT_INFO, playersInfo);
			sendJsonMessage(client, jsonObject);
		}
	}

	public void endGameMessage(List<Session> clients, String endStatus) {
		for (Session client : clients) {
			sendJsonMessage(client, JsonUtil.createJsonMessage(
					MessageConstants.TYPE_GAME_END,
					MessageConstants.GAME_END, endStatus));
		}
	}

	public void sendChangeRound(List<Session> clients, int round) {
		for (Session client : clients) {
			JSONObject jsonObject = JsonUtil.createJsonMessage(
					MessageConstants.TYPE_ROUND_INFO,
					MessageConstants.ROUND, round);
			sendJsonMessage(client, jsonObject);
		}
	}

	public void assignTurnToClient(Session client, int turn) {
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				MessageConstants.TYPE_SET_TURN,
				MessageConstants.TURN, turn));
	}

	public void distributeWords(GameSession session) {
		List<Session> clients = session.getClients();
		
		for (Session client : clients) {
			@SuppressWarnings("unchecked")
			Map<String, Object> clientInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
			String uuid = (String) clientInfo.get("uuid");
			JSONObject wordMessage = JsonUtil.createJsonMessage(
					MessageConstants.TYPE_GAME_START,
					MessageConstants.TOPIC, session.getTopic(), 
					MessageConstants.KEYWORD, uuid.equals(session.getLiarId()) ? null : session.getKeyword()); // 라이어는 키워드 제외
			sendJsonMessage(client, wordMessage);
		}
	}

	public void broadcastSystemMessage(List<Session> clients, String message) {
		JSONObject systemMessage = JsonUtil.createJsonMessage(
				MessageConstants.TYPE_SYSTEM_MESSAGE,
				MessageConstants.MESSAGE, message);
		clients.forEach(client -> sendJsonMessage(client, systemMessage));
	}

	public void broadcastMessage(List<Session> clients , String senderUUID, String nickname, String message) {
		JSONObject jsonObject = new JSONObject(message);
		String extractedMessage = jsonObject.getString("message");

		JSONObject chatMessage = JsonUtil.createJsonMessage(
				MessageConstants.TYPE_MESSAGE,
				MessageConstants.SENDER_UUID, senderUUID,
				MessageConstants.SENDER_NICKNAME, nickname,
				MessageConstants.MESSAGE, extractedMessage);
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
