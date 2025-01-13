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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}
	
}
