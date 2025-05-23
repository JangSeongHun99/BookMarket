package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/bookmark_db?serverTimezone=UTC"; // bookmark_db로 변경
    private static final String USERNAME = "root"; // 실제 DB 사용자명으로 변경
    private static final String PASSWORD = "153123"; // 실제 DB 비밀번호로 변경

    static {
        try {
            // MySQL JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC 드라이버를 로드하는데 실패했습니다.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // 필요에 따라 close 유틸리티 메소드 추가 (Statement, PreparedStatement, ResultSet, Connection)
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}