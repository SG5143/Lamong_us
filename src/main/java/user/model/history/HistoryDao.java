package user.model.history;

import java.sql.*;
import java.util.*;

import util.*;

public class HistoryDao {

    private static final String COUNT_PLAYER_GAMES = "SELECT COUNT(DISTINCT game_code) FROM ActivePlayers WHERE play_user = ?";
    private static final String COUNT_GAMES = "SELECT COUNT(DISTINCT game_code) FROM ActivePlayers";
    private static final String IS_LAST_PAGE = "SELECT COUNT(DISTINCT game_code) FROM ActivePlayers WHERE play_user = ? AND game_code > ?";
    private static final String GAME_HISTORY = "SELECT game_code, is_lying, score_change, is_win FROM ActivePlayers WHERE play_user = ? ORDER BY game_code DESC LIMIT ?, 10";

    private static HistoryDao instance = new HistoryDao();

    public static HistoryDao getInstance() {
        return instance;
    }

    // 플레이어의 플레이 기록 수 조회
    public int getPlayerGameCount(String playerUUID) {
        int gameCount = 0;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(COUNT_PLAYER_GAMES)) {

            pstmt.setString(1, playerUUID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                gameCount = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameCount;
    }

    // 전체 게임 수 조회
    public int getTotalGamesCount() {
        int totalGames = 0;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(COUNT_GAMES);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                totalGames = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalGames;
    }

    // 현재 페이지가 마지막 페이지인지 확인
    public boolean isLastPage(String playerUUID, int lastGameCode) {
        boolean isLastPage = false;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(IS_LAST_PAGE)) {

            pstmt.setString(1, playerUUID);
            pstmt.setInt(2, lastGameCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                isLastPage = rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isLastPage;
    }

    // 플레이어 게임 기록 조회
    public List<History> getPlayerGames(String playerUUID, int pageNumber) {
        List<History> gameHistoryList = new ArrayList<>();
        int offset = pageNumber * 10;  

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GAME_HISTORY)) {

            pstmt.setString(1, playerUUID);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String gameCode = rs.getString("game_code");
                boolean isLying = rs.getBoolean("is_lying");
                int scoreChange = rs.getInt("score_change");
                Boolean isWin = rs.getBoolean("is_win");

                History history = new History(gameCode, isLying, scoreChange, isWin);
                gameHistoryList.add(history);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameHistoryList;
    }
}
