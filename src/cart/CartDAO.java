package cart; // 또는 dao 패키지

import util.DBUtil; // DBUtil 경로에 맞게 수정
import bookitem.Book; // Book 클래스 import (책 정보를 함께 가져올 때 필요)
import bookitem.BookDAO; // BookDAO import (책 정보를 함께 가져올 때 필요)

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    private BookDAO bookDAO = new BookDAO(); // 책 정보를 가져오기 위해

    // 특정 사용자의 장바구니 모든 항목 조회
    public List<CartItem> getCartItemsByUserId(String userId) {
        List<CartItem> cartItems = new ArrayList<>();
        // 책 정보까지 함께 조회하기 위해 JOIN 사용 (선택 사항)
        // String sql = "SELECT ci.*, b.name as book_name, b.unit_price as book_price " +
        //              "FROM cart_items_tb ci " +
        //              "JOIN books_tb b ON ci.book_id = b.book_id " +
        //              "WHERE ci.user_id = ?";
        String sql = "SELECT * FROM cart_items_tb WHERE user_id = ?"; // CartItem만 가져오는 경우

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = bookDAO.getBookById(rs.getString("book_id")); // CartItem에 Book 객체가 필요하므로 조회
                if (book != null) {
                    CartItem item = new CartItem(book); // 생성자에서 bookID, quantity, totalPrice 자동 설정
                    item.setQuantity(rs.getInt("quantity")); // DB에서 가져온 수량으로 업데이트
                    // CartItem 생성자에서 updateTotalPrice()가 호출되므로, 수량 변경 후 다시 호출할 필요는 없음.
                    // 만약 수동으로 가격을 설정해야 한다면 여기서 item.updateTotalPrice() 호출.
                    cartItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return cartItems;
    }

    // 장바구니에 상품 추가 (또는 수량 업데이트)
    public boolean addBookToCart(String userId, String bookId, int quantityToAdd) {
        // 1. 해당 사용자의 장바구니에 이미 책이 있는지 확인
        String selectSql = "SELECT quantity FROM cart_items_tb WHERE user_id = ? AND book_id = ?";
        String upsertSql; // INSERT 또는 UPDATE SQL
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = DBUtil.getConnection();
            // 이미 있는지 확인
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setString(1, userId);
            pstmt.setString(2, bookId);
            rs = pstmt.executeQuery();

            if (rs.next()) { // 이미 장바구니에 있는 경우: 수량 업데이트
                int currentQuantity = rs.getInt("quantity");
                int newQuantity = currentQuantity + quantityToAdd;
                upsertSql = "UPDATE cart_items_tb SET quantity = ? WHERE user_id = ? AND book_id = ?";
                DBUtil.close(pstmt, rs); // 이전 PreparedStatement와 ResultSet 닫기
                pstmt = conn.prepareStatement(upsertSql);
                pstmt.setInt(1, newQuantity);
                pstmt.setString(2, userId);
                pstmt.setString(3, bookId);
            } else { // 장바구니에 없는 경우: 새로 추가
                upsertSql = "INSERT INTO cart_items_tb (user_id, book_id, quantity) VALUES (?, ?, ?)";
                DBUtil.close(pstmt, rs); // 이전 PreparedStatement와 ResultSet 닫기
                pstmt = conn.prepareStatement(upsertSql);
                pstmt.setString(1, userId);
                pstmt.setString(2, bookId);
                pstmt.setInt(3, quantityToAdd);
            }
            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn); // 최종적으로 사용된 pstmt, rs, conn 닫기
        }
        return success;
    }

    // 장바구니 항목 수량 직접 변경 (예: +, - 버튼 또는 직접 입력)
    public boolean updateCartItemQuantity(String userId, String bookId, int newQuantity) {
        if (newQuantity <= 0) { // 수량이 0 이하이면 삭제 처리
            return removeBookFromCart(userId, bookId);
        }
        String sql = "UPDATE cart_items_tb SET quantity = ? WHERE user_id = ? AND book_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, userId);
            pstmt.setString(3, bookId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }


    // 장바구니에서 특정 상품 제거
    public boolean removeBookFromCart(String userId, String bookId) {
        String sql = "DELETE FROM cart_items_tb WHERE user_id = ? AND book_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, bookId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    // 특정 사용자의 장바구니 전체 비우기
    public boolean clearCartByUserId(String userId) {
        String sql = "DELETE FROM cart_items_tb WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected >= 0; // 0개 삭제도 성공으로 간주
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }
}