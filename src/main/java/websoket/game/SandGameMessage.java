package websoket.game;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import org.json.JSONObject;
import jakarta.websocket.Session;
import websoket.JsonUtil;

@SuppressWarnings("unchecked")
public class SandGameMessage {
	private static final SandGameMessage instance = new SandGameMessage();
	private final Logger logger = Logger.getLogger(LiarGameManager.class.getName());

	private SandGameMessage() {}

	public static SandGameMessage getInstance() {
		return instance;
	}
	
	public void sendRoomInfo(GameStartDto gameStartDto) {
		gameStartDto.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"ROOM_INFO",
				"waitRoomUUID", gameStartDto.getWaitRoomUUID(),
				"waitRoomTitle", gameStartDto.getTitle(),
				"waitRoomNumber", gameStartDto.getRoomNumber())));
	}
	
	public void broadcastKeywordReveal(GameSession session) {
		session.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"KEYWORD_REVEAL",
				"keyword", session.getKeyword())));
	}

	public void voteResult(GameSession session, GameSession.VoteResult result) {
		session.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"VOTE_RESULT",
				"votedPlayerNickname", result.getNICKNAME(),
				"voteResultMostVoted", result.getMostVotedPlayerUUID(),
				"voteResultMaxVotes", result.getMaxVotes(),
				"isLiarCorrect", result.isLiarCorrect())));
	}
	
	public void broadcastStateChange(GameSession session) {
		session.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"STATE_CHANGE", 
				"gamsState", session.getState().toString())));
	}

	public void sendTurnInfo(GameSession session) {
		session.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"CUR_TURN",
				"currentTurn", session.getCurrentTurn())));
	}

	public void clientInfoMessage(GameSession session) {
		List<Map<String, Object>> playersInfo = new ArrayList<>();
		session.getClients().forEach(client -> 
		playersInfo.add((Map<String, Object>) client.getUserProperties().get("userInfo")));
		
		session.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"CLIENT_INFO",
				"clientInfo", playersInfo)));
	}

	public void endGameMessage(GameSession session, String endStatus) {
		session.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"GAME_END",
				"gameEnd", endStatus)));
	}

	public void sendChangeRound(GameSession session) {
		session.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"ROUND_INFO",
				"round", session.getCurrentRound())));
	}

	public void assignTurnToClient(Session client, int turn) {
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"SET_TURN",
				"turn", turn));
	}

	public void distributeWords(GameSession session) {
		for (Session client :session.getClients()) {
			Map<String, Object> clientInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
			String uuid = (String) clientInfo.get("uuid");

			sendJsonMessage(client, JsonUtil.createJsonMessage(
					"GAME_START",
					"topic", session.getTopic(),
					"keyword", uuid.equals(session.getLiarId()) ? null : session.getKeyword()));
		}
	}

	public void broadcastSystemMessage(List<Session> clients, String message) {
		clients.forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"SYSTEM_MESSAGE",
				"message", message)));
	}

	public void broadcastMessage(GameSession session, Session sender, String message) {
		Map<String, Object> userInfo = (Map<String, Object>) sender.getUserProperties().get("userInfo");
		String uuid = (String) userInfo.get("uuid");
		String nickname = (String) userInfo.get("nickname");
		
		JSONObject jsonObject = new JSONObject(message);
		String extractedMessage = jsonObject.getString("message");

		session.getClients().forEach(client -> 
		sendJsonMessage(client, JsonUtil.createJsonMessage(
				"MESSAGE",
				"senderUUID", uuid,
				"senderNickname", nickname,
				"message", extractedMessage)));
	}

	public boolean sendJsonMessage(Session client, JSONObject message) {
		try {
			client.getBasicRemote().sendText(message.toString());
			return true;
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			return false;
		}
	}

}
