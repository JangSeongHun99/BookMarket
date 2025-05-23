package member; // 또는 dao 패키지

import util.DBUtil; // DBUtil 경로에 맞게 수정
import java.sql.*;

public class UserDAO {

    public boolean addUser(User user) {
        String sql = "INSERT INTO users_tb (user_id, password, name, phone, address, role) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, String.valueOf(user.getPhone()));
            pstmt.setString(5, user.getAddress());
            pstmt.setString(6, user.getRole());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    public User validateUser(String userId, String plainPassword) {
        String sql = "SELECT * FROM users_tb WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // 비밀번호 비교 로직 (예: BCrypt.checkpw(plainPassword, storedPassword))
                if (storedPassword.equals(plainPassword)) { // 현재는 평문 비교 (보안 취약)
                    String role = rs.getString("role");
                    // User 객체 생성 시 role 정보도 함께 전달
                    user = new User(
                            rs.getString("user_id"),
                            null, // 비밀번호는 다시 설정하지 않음
                            rs.getString("name"),
                            (rs.getString("phone") != null && !rs.getString("phone").isEmpty()) ? Integer.parseInt(rs.getString("phone")) : 0,
                            rs.getString("address"),
                            role // DB에서 가져온 role 설정
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return user;
    }

    public User getUserById(String userId) {
        String sql = "SELECT * FROM users_tb WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                user = new User(
                        rs.getString("user_id"),
                        null,
                        rs.getString("name"),
                        (rs.getString("phone") != null && !rs.getString("phone").isEmpty()) ? Integer.parseInt(rs.getString("phone")) : 0,
                        rs.getString("address"),
                        role
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return user;
    }
}