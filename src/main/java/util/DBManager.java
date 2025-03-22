package util;

import java.sql.*;

import javax.naming.*;
import javax.sql.*;

public class DBManager {

	public static Connection getConnection() {
		Connection conn = null;

		try {
			Context init = new InitialContext();
			Context ctx = (Context) init.lookup("java:comp/env");
			DataSource dataSource = (DataSource) ctx.lookup("jdbc/LamongDB");

			conn = dataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}

	public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
