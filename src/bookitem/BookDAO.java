package bookitem; // 또는 dao 패키지

import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // 책 추가 (관리자용)
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books_tb (book_id, name, unit_price, author, description, category, release_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, book.getBookId());
            pstmt.setString(2, book.getName());
            pstmt.setInt(3, book.getUnitPrice());
            pstmt.setString(4, book.getAuthor());
            pstmt.setString(5, book.getDescription());
            pstmt.setString(6, book.getCategory());
            pstmt.setString(7, book.getReleaseDate());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    // 모든 책 목록 조회
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT * FROM books_tb";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("book_id"),
                        rs.getString("name"),
                        rs.getInt("unit_price"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("release_date")
                );
                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return bookList;
    }

    // ID로 책 한 권 조회 (필요시)
    public Book getBookById(String bookId) {
        String sql = "SELECT * FROM books_tb WHERE book_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Book book = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                book = new Book(
                        rs.getString("book_id"),
                        rs.getString("name"),
                        rs.getInt("unit_price"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("release_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return book;
    }

    // (필요에 따라 updateBook, deleteBook 메소드도 추가)
}