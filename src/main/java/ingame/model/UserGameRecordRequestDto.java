package ingame.model;

public class UserGameRecordRequestDto {
    private String playUser;
    private String gameCode;
    private boolean isLying;
    private boolean isVoteCorrect;
    private int scoreChange;
    private boolean isQuit;
    private Boolean isWin;

    public UserGameRecordRequestDto() {}

    public UserGameRecordRequestDto(String playUser, String gameCode, String isLying, String isVoteCorrect,
            String scoreChange, String isQuit, String isWin) {
        this.playUser = playUser;
        this.gameCode = gameCode;
        this.isLying = Boolean.parseBoolean(isLying);
        this.isVoteCorrect = Boolean.parseBoolean(isVoteCorrect);
        try {
            this.scoreChange = Integer.parseInt(scoreChange);
        } catch (NumberFormatException e) {
            this.scoreChange = 0;
        }
        this.isQuit = Boolean.parseBoolean(isQuit);
        this.isWin = Boolean.parseBoolean(isWin);
    }

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

    public Boolean isWin() {
        return isWin;
    }

    public void setWin(Boolean isWin) {
        this.isWin = isWin;
    }

}