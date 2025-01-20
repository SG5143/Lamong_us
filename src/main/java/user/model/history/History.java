package user.model.history;

import java.sql.Timestamp;

public class History {
    private String gameUuid;
    private Timestamp playDate;
    private boolean isLying;
    private boolean isWin;
    private int score;
    
    public History(String gameCode, boolean isLying, int scoreChange, Boolean isWin) {
		this.gameUuid = gameCode;
		this.isLying = isLying;
		this.score = scoreChange;
		this.isWin = isWin;
	}

    public String getGameUuid() {
        return gameUuid;
    }

    public void setGameUuid(String gameUuid) {
        this.gameUuid = gameUuid;
    }

    public Timestamp getPlayDate() {
        return playDate;
    }

    public void setPlayDate(Timestamp playDate) {
        this.playDate = playDate;
    }

    public boolean isLying() {
        return isLying;
    }

    public void setIsLying(boolean isLying) {
        this.isLying = isLying;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setIsWin(boolean isWin) {
        this.isWin = isWin;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
