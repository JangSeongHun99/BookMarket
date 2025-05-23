package member;

// import com.market.util.DBUtil; // AuthService에서는 직접 DBUtil 사용 안 함 (UserDAO 통해)
// import java.sql.*;             // AuthService에서는 직접 DBUtil 사용 안 함 (UserDAO 통해)


public class AuthService {
    private static User currentUser;
    private static UserDAO userDAO = new UserDAO();

    public static boolean login(String userId, String password) {
        User user = userDAO.validateUser(userId, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean registerUser(User newUser) {
        // User 객체 생성 시 role이 이미 "USER"로 설정되어 있다고 가정
        // 또는 여기서 newUser.setRole("USER"); 명시적으로 설정 가능
        if (newUser.getRole() == null || newUser.getRole().isEmpty()) {
            newUser.setRole("USER"); // 기본값 설정
        }
        return userDAO.addUser(newUser);
    }

    public static boolean loginAdmin(String adminId, String password) {
        User adminUser = userDAO.validateUser(adminId, password); // UserDAO에서 role까지 포함된 User 반환
        if (adminUser != null && "ADMIN".equalsIgnoreCase(adminUser.getRole())) {
            currentUser = adminUser;
            return true;
        }
        return false;
    }

    // 테스트 목적으로 현재 사용자를 직접 설정하는 메소드
    public static void setCurrentUserForTest(User user) {
        currentUser = user;
    }

    /* // DB에서 직접 role 가져오는 임시 메소드 제거
    private static String getRoleFromDB(String userId) {
        // ...
    }
    */
}