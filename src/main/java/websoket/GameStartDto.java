package websoket;

import java.util.Set;
import jakarta.websocket.Session;

public class GameStartDto {
    private String roomKey;
    private Set<Session> clients;
    private String topic;
    private String keyword;
    private int rounds;

    public GameStartDto(String roomKey, Set<Session> clients, String topic, String keyword, int rounds) {
        this.roomKey = roomKey;
        this.clients = clients;
        this.topic = topic;
        this.keyword = keyword;
        this.rounds = rounds;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public Set<Session> getClients() {
        return clients;
    }

    public String getTopic() {
        return topic;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getRounds() {
        return rounds;
    }
    
}