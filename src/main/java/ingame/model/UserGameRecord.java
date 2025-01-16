package ingame.model;

public class UserGameRecord {
	private String playUser;
	private String gameCode;
	private boolean isLying;
	private boolean isVoteCorrect;
	private int scoreChange;
	private boolean isQuit;
	private Boolean isWin;

	public UserGameRecord(String playUser, String gameCode, boolean isLying, boolean isVoteCorrect, int scoreChange,
			boolean isQuit, Boolean isWin) {
		this.playUser = playUser;
		this.gameCode = gameCode;
		this.isLying = isLying;
		this.isVoteCorrect = isVoteCorrect;
		this.scoreChange = scoreChange;
		this.isQuit = isQuit;
		this.isWin = isWin;
	}

	public String getPlayUser() {
		return playUser;
	}

	public String getGameCode() {
		return gameCode;
	}

	public boolean isLying() {
		return isLying;
	}

	public boolean isVoteCorrect() {
		return isVoteCorrect;
	}

	public int getScoreChange() {
		return scoreChange;
	}

	public boolean isQuit() {
		return isQuit;
	}

	public Boolean getIsWin() {
		return isWin;
	}

}