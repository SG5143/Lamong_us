package ingame.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import util.DBManager;

public class InGameDao {
	private static final Logger LOGGER = Logger.getLogger(InGameDao.class.getName());
	
	private static final String COL_INGAME_CODE = "in_game_code";
	private static final String COL_ROOM_CODE = "room_code";
	private static final String COL_TOPIC = "topic";
	private static final String COL_KEYWORD = "game_keyword";
	private static final String COL_ROUND = "round_count";
	private static final String COL_END_TYPE = "end_type";
	private static final String COL_WIN_TYPE = "win_type";
	private static final String COL_REG_DATE = "reg_date";

	private static final String COL_PLAY_USER = "play_user";
	private static final String COL_GAME_CODE = "game_code";
	private static final String COL_IS_LYING = "is_lying";
	private static final String COL_IS_VOTE_CORRECT = "is_vote_correct";
	private static final String COL_SCORE_CHANGE = "score_change";
	private static final String COL_IS_QUIT = "is_quit";
	private static final String COL_IS_WIN = "is_win";

	private static final String CREATE_INGAME_SQL="INSERT INTO InGame(room_code, topic, game_keyword, round_count) VALUES (?, ?, ?, ?)";
	private static final String GET_INGAME_SQL="SELECT * FROM InGame WHERE in_game_code = ?";
	private static final String UPDATE_INGAME_SQL="UPDATE InGame SET end_type = ?, win_type = ? WHERE in_game_code = ?";
	private static final String CREATE_INGAME_RECORD_SQL = "INSERT INTO ActivePlayers(play_user, game_code, is_lying, is_vote_correct, score_change, is_quit, is_win) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String GET_INGAME_RECORD_SQL = "SELECT * FROM ActivePlayers WHERE game_code = ?";

	private InGameDao() {}

	private static InGameDao instance = new InGameDao();

	public static InGameDao getInstance() {
		return instance;
	}

	public String createInGame(InGameRequestDto dto) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(
						CREATE_INGAME_SQL,
						PreparedStatement.RETURN_GENERATED_KEYS)) {
			// PreparedStatement.RETURN_GENERATED_KEYS 옵션을 사용하면 자동 생성 키를 반환 받을 수 있음

			pstmt.setString(1, dto.getRoomCode());
			pstmt.setString(2, dto.getTopic());
			pstmt.setString(3, dto.getKeyword());
			pstmt.setInt(4, dto.getRound());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next())
						return rs.getString(1);
				}
			}
		} catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException 에러 발생 : createInGame()", e);
		}
		return null;
	}
	
	public InGame getInGameByGameCode(String GameCode) {
		InGame game = null; 
		
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(GET_INGAME_SQL)) {
			pstmt.setString(1, GameCode);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String code = rs.getString(COL_INGAME_CODE);
					String roomCode = rs.getString(COL_ROOM_CODE);
					String topic = rs.getString(COL_TOPIC);
					String keyword = rs.getString(COL_KEYWORD);
					int round = rs.getInt(COL_ROUND);
					String endType = rs.getString(COL_END_TYPE);
					String winType = rs.getString(COL_WIN_TYPE);
					Timestamp regDate = rs.getTimestamp(COL_REG_DATE);

					game = new InGame(code, roomCode, topic, keyword, round, endType, winType, regDate);
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "SQLException 에러 발생 : getInGameByGameCode()", e);
		}
		return game;
	}

	public boolean updateInGameResult(InGameRequestDto dto) {		
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(UPDATE_INGAME_SQL)) {
			pstmt.setString(1, dto.getEndType());
			pstmt.setString(2, dto.getWinType());
			pstmt.setString(3, dto.getCode());
			
			pstmt.executeUpdate();

			return true;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "SQLException 에러 발생 : updateInGameResult()", e);
			return false;
		}
	}

	// 유저별 게임 기록 생성시 사용할 메소드
	public boolean createInGameRecord(UserGameRecordRequestDto dto) {
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(CREATE_INGAME_RECORD_SQL)) {
			pstmt.setString(1, dto.getPlayUser());
			pstmt.setString(2, dto.getGameCode());
			pstmt.setBoolean(3, dto.isLying());
			pstmt.setBoolean(4, dto.isVoteCorrect());
			pstmt.setInt(5, dto.getScoreChange());
			pstmt.setBoolean(6, dto.isQuit());
			pstmt.setBoolean(7, dto.getIsWin());

			int affectedRows = pstmt.executeUpdate();

			return affectedRows > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "SQLException 에러 발생 : createInGameRecord()", e);
			return false;
		}
	}

	// 게임 상세 기록 조회 시 사용할 메소드
	public List<UserGameRecord> getUserGameRecordByGameCode(String code) {
		List<UserGameRecord> records = new ArrayList<>();

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(GET_INGAME_RECORD_SQL)) {
			pstmt.setString(1, code);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					String playUser = rs.getString(COL_PLAY_USER);
					String gameCode = rs.getString(COL_GAME_CODE);
					boolean isLying = rs.getBoolean(COL_IS_LYING);
					boolean isVoteCorrect = rs.getBoolean(COL_IS_VOTE_CORRECT);
					int scoreChange = rs.getInt(COL_SCORE_CHANGE);
					boolean isQuit = rs.getBoolean(COL_IS_QUIT);
					Boolean isWin = rs.getBoolean(COL_IS_WIN);

					records.add(new UserGameRecord(playUser, gameCode, isLying, isVoteCorrect, scoreChange, isQuit, isWin));
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "SQLException 에러 발생 : getGameRecordByGameCode()", e);
		}
		return records;
	}
	
}
