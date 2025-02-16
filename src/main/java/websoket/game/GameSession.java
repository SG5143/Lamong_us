package websoket.game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.websocket.Session;

public class GameSession {
	
    public enum GameState {
        INIT, ROUND_START, ROUND_END, TURN_PROGRESS, VOTE_PHASE, FINAL_CHANCE, GAME_END
    }
    
    public static class VoteResult {
    	private final String NICKNAME;
        private final String MOST_VOTED_PLAYER_UUID; // 가장 많은 표를 받은 플레이어 ID
        private final int MAX_VOTES; // 가장 많은 표 수
        private final boolean IS_LIAR_CORRECT; // 라이어가 정답을 맞췄는지 여부

        public VoteResult(String nickname, String mostVotedPlayerUUID, int maxVotes, boolean isLiarCorrect) {
            this.NICKNAME = nickname;
        	this.MOST_VOTED_PLAYER_UUID = mostVotedPlayerUUID;
            this.MAX_VOTES = maxVotes;
            this.IS_LIAR_CORRECT = isLiarCorrect;
        }
        
        public String getNICKNAME() {
			return NICKNAME;
		}

        public String getMostVotedPlayerUUID() {
            return MOST_VOTED_PLAYER_UUID;
        }

        public int getMaxVotes() {
            return MAX_VOTES;
        }

        public boolean isLiarCorrect() {
            return IS_LIAR_CORRECT;
        }
    }

    private final String ROOM_ID;
    private final String LIAR_ID;
    private final String TOPIC;
    private final String KEYWORD;
    private final int MAX_ROUNDS;
    private final List<Session> CLIENTS;

    private GameState state;
    private int currentRound;
    private int currentTurn;
    private final Map<String, String> votes = new ConcurrentHashMap<>();
    private boolean isVoteInProgress;

    public GameSession(String roomId, String liarId, String topic, String keyword, int maxRounds, List<Session> clients) {
        this.ROOM_ID = Objects.requireNonNull(roomId, "roomId must not be null");
        this.LIAR_ID = Objects.requireNonNull(liarId, "liarId must not be null");
        this.TOPIC = Objects.requireNonNull(topic, "topic must not be null");
        this.KEYWORD = Objects.requireNonNull(keyword, "keyword must not be null");
        this.MAX_ROUNDS = maxRounds;
        this.CLIENTS = new CopyOnWriteArrayList<>(clients);
        this.state = GameState.INIT;
        this.currentRound = 1;
        this.currentTurn = 1;
    }

    public synchronized void transitionState(GameState newState) {
        this.state = newState;
    }

    public synchronized void advanceTurn() {
        currentTurn = (currentTurn % CLIENTS.size()) + 1;
        if (currentTurn == 1) {
            currentRound++;
        }
    }

    public synchronized void addVote(String userUUID, String vote) {
        votes.put(userUUID, vote);
    }

    public synchronized boolean isAllVoted() {
        return votes.size() == CLIENTS.size();
    }

    public synchronized boolean shouldTerminate() {
        return CLIENTS.size() <= 2 || state == GameState.GAME_END;
    }

    public String getRoomId() {
        return ROOM_ID;
    }

    public String getLiarId() {
        return LIAR_ID;
    }

    public String getTopic() {
        return TOPIC;
    }

    public String getKeyword() {
        return KEYWORD;
    }

    public int getCurrentRound() {
        return currentRound;
    }
    
    public int getMaxRounds() {
    	return MAX_ROUNDS;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }
    
	public void setCurrentTurn(int currentTurn) {
		this.currentTurn = currentTurn;
	}

    public GameState getState() {
        return state;
    }

    public List<Session> getClients() {
        return Collections.unmodifiableList(CLIENTS); 
    }

    public Map<String, String> getVotes() {
        return Collections.unmodifiableMap(votes);
    }

    public boolean isVoteInProgress() {
        return isVoteInProgress;
    }

    public synchronized void setVoteInProgress(boolean voteInProgress) {
        isVoteInProgress = voteInProgress;
    }

    public synchronized void removeClient(Session client) {
        CLIENTS.remove(client);
    }

    public synchronized boolean isCurrentPlayer(Session client) {
        return CLIENTS.indexOf(client) == currentTurn - 1;
    }
    
    public synchronized boolean isNewRoundNeeded() {
        return currentTurn == 1;
    }
    
    public synchronized VoteResult calculateVoteResult() {
        Map<String, Integer> voteCounts = new HashMap<>();
        for (String votedPlayerId : votes.values()) {
            voteCounts.put(votedPlayerId, voteCounts.getOrDefault(votedPlayerId, 0) + 1);
        }

        String mostVotedPlayerId = null;
        int maxVotes = 0;
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() > maxVotes) {
                mostVotedPlayerId = entry.getKey();
                maxVotes = entry.getValue();
            }
        }
        
        String votedPlayerNickname = null;
        for(Session client: CLIENTS) {
        	@SuppressWarnings("unchecked")
			Map<String, Object> liarInfo = (Map<String, Object>) client.getUserProperties().get("userInfo");
        	String uuid = (String) liarInfo.get("uuid");
        	String nickname = (String) liarInfo.get("nickname");
        	
        	if(uuid.equals(mostVotedPlayerId)) {
        		votedPlayerNickname = nickname;
        	}
        }

        boolean isLiarCorrect = LIAR_ID.equals(mostVotedPlayerId);

        return new VoteResult(votedPlayerNickname, mostVotedPlayerId, maxVotes, isLiarCorrect);
    }
    
}

