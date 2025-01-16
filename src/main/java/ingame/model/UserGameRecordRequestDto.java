package ingame.model;

public class UserGameRecordRequestDto {
	private String playUser;
	private String gameCode;
	private boolean isLying;
	private boolean isVoteCorrect;
	private int scoreChange;
	private boolean isQuit;
	private Boolean isWin;
    
    public UserGameRecordRequestDto(){}

    public String getPlayUser() {
        return playUser;
    }

    public void setPlayUser(String playUser) {
        this.playUser = playUser;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public boolean isLying() {
        return isLying;
    }

    public void setLying(boolean isLying) {
        this.isLying = isLying;
    }

    public boolean isVoteCorrect() {
        return isVoteCorrect;
    }

    public void setVoteCorrect(boolean isVoteCorrect) {
        this.isVoteCorrect = isVoteCorrect;
    }

    public int getScoreChange() {
        return scoreChange;
    }

    public void setScoreChange(int scoreChange) {
        this.scoreChange = scoreChange;
    }

    public boolean isQuit() {
        return isQuit;
    }

    public void setQuit(boolean isQuit) {
        this.isQuit = isQuit;
    }

    public Boolean getIsWin() {
        return isWin;
    }

    public void setIsWin(Boolean isWin) {
        this.isWin = isWin;
    }

}