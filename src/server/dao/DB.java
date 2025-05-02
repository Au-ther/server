package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/transmission?useUnicode=true&characterEncoding=UTF-8&useOldAliasMetadataBehavior=true&useSLL=false&serverTimezone=UTC&zeroDateTimeBehavior=CONVERT_TO_NULL&nullCatalogMeansCurrent=true";
			conn = DriverManager.getConnection(url, "root", "root");
			System.out.println(conn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
