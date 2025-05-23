package bookitem;

import java.util.ArrayList; // List로 변경하는 것이 좋음
import java.util.List;

public class BookInIt { // 클래스명을 BookService 등으로 변경 고려
	private static List<Book> mBookList; // ArrayList -> List
	private static int mTotalBook = 0;
	private static BookDAO bookDAO = new BookDAO(); // BookDAO 인스턴스 생성

	public static void init() {
		// DB에서 책 목록을 가져오도록 변경
		mBookList = bookDAO.getAllBooks();
		mTotalBook = mBookList.size();
	}

	// totalFileToBookList() 와 setFileToBookList() 메소드는 더 이상 필요 없음 (파일 기반이 아니므로)
	// 필요하다면 삭제하거나 주석 처리

	public static List<Book> getmBookList() { // 반환 타입 List로 변경
		if (mBookList == null) { // 최초 호출 시 DB에서 로드
			init();
		}
		return mBookList;
	}




	// 새로운 책을 DB와 mBookList 모두에 추가하는 메소드 (관리자 페이지에서 사용)
	public static boolean addBook(Book newBook) {
		if (bookDAO.addBook(newBook)) {
			if (mBookList != null) { // 이미 리스트가 로드된 경우
				mBookList.add(newBook);
				mTotalBook = mBookList.size();
			} else { // 아직 리스트가 로드되지 않은 경우, 다음 init() 호출 시 반영됨
				init(); // 또는 mBookList를 여기서 다시 로드
			}
			return true;
		}
		return false;
	}
}