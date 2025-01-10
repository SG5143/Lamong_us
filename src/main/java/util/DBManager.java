package util;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBManager {
	public static Connection getConnection() {

		Connection conn = null;

		try {
			Context init = new InitialContext();
			Context ctx = (Context) init.lookup("java:comp/env");

			DataSource dataSource = (DataSource) ctx.lookup("jdbc/LamongDB");
			conn = dataSource.getConnection();
			
			System.out.println("데이터베이스 연동 성공");
		} catch (Exception e) {
			System.out.println("데이터베이스 연동 실패");
			e.printStackTrace();
		}

		return conn;
	}
}
