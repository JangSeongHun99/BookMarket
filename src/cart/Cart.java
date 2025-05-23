package cart;

import java.util.ArrayList;
import java.util.List; // ArrayList 대신 List 인터페이스 사용 권장
import bookitem.Book;
import member.User; // 현재 사용자 정보를 위해 필요

public class Cart implements CartInterface { // CartInterface 구현 유지

	private User currentUser; // 이 장바구니의 주인
	public List<CartItem> mCartItem; // public보다는 private 후 getter로 접근 권장
	// public static int mCartCount = 0; // 더 이상 static일 필요 없음, mCartItem.size()로 대체

	private CartDAO cartDAO = new CartDAO(); // DB 접근용 DAO

	// 생성자: 사용자 정보를 받아 해당 사용자의 장바구니를 로드
	public Cart(User user) {
		this.currentUser = user;
		this.mCartItem = new ArrayList<>(); // 빈 리스트로 초기화
		if (user != null) { // 로그인된 사용자가 있을 경우에만 DB에서 로드
			loadCartFromDB();
		}
	}

	// DB에서 현재 사용자의 장바구니 정보를 가져와 mCartItem에 채움
	private void loadCartFromDB() {
		if (this.currentUser == null || this.currentUser.getUserId() == null) {
			System.err.println("장바구니 로드 실패: 사용자 정보가 없습니다.");
			this.mCartItem.clear(); // 사용자 정보 없으면 장바구니 비움
			return;
		}
		this.mCartItem = cartDAO.getCartItemsByUserId(this.currentUser.getUserId());
	}

	// CartInterface 메소드 구현 (DB 연동 추가)
	@Override
	public void printBookList(List<Book> booklist) { // 파라미터 타입 List로 변경
		// 이 메소드는 Cart 클래스의 역할과 조금 어울리지 않음.
		// 책 목록 표시는 Book 관련 UI나 서비스에서 처리하는 것이 적합.
		// 여기서는 기존 기능 유지를 위해 남겨둠.
		for (int i = 0; i < booklist.size(); i++) {
			Book bookitem = booklist.get(i);
			System.out.print(bookitem.getBookId() + " | ");
			System.out.print(bookitem.getName() + " | ");
			// ... (나머지 출력)
			System.out.println("");
		}
	}

	@Override
	public void insertBook(Book book) { // 책 한 권 추가 (수량 1)
		if (currentUser == null || currentUser.getUserId() == null) {
			System.err.println("장바구니 추가 실패: 로그인된 사용자가 없습니다.");
			return;
		}
		String userId = currentUser.getUserId();
		String bookId = book.getBookId();

		// DB에 추가/업데이트
		if (cartDAO.addBookToCart(userId, bookId, 1)) {
			// 메모리의 mCartItem도 업데이트
			boolean found = false;
			for (CartItem item : mCartItem) {
				if (item.getBookID().equals(bookId)) {
					item.setQuantity(item.getQuantity() + 1); // 수량 증가 및 totalPrice 자동 업데이트
					found = true;
					break;
				}
			}
			if (!found) {
				mCartItem.add(new CartItem(book)); // 새 아이템 추가 (수량은 기본 1)
			}
		} else {
			System.err.println("DB에 장바구니 항목 추가/업데이트 실패: " + bookId);
		}
	}


	@Override
	public void deleteBook() { // 장바구니 전체 비우기
		if (currentUser == null || currentUser.getUserId() == null) {
			System.err.println("장바구니 비우기 실패: 로그인된 사용자가 없습니다.");
			return;
		}
		if (cartDAO.clearCartByUserId(currentUser.getUserId())) {
			mCartItem.clear();
		} else {
			System.err.println("DB에서 장바구니 비우기 실패");
		}
	}

	@Override
	public boolean isCartInBook(String bookId) {
		// 이 메소드는 insertBook 시 중복 체크 및 수량 증가 로직으로 통합되거나,
		// 단순히 메모리상의 mCartItem에 있는지 확인하는 용도로만 사용될 수 있음.
		// DB 기반으로 변경하려면, addBookToCart 내부 로직과 유사해짐.
		// 현재는 메모리 기반으로 남겨두지만, DB 동기화 관점에서는 addBookToCart를 사용하는 것이 좋음.
		for (CartItem item : mCartItem) {
			if (item.getBookID().equals(bookId)) {
				// 여기서 바로 수량 증가시키는 것은 DB와 불일치를 유발할 수 있음.
				// item.setQuantity(item.getQuantity() + 1);
				return true;
			}
		}
		return false;
	}


	// 인덱스 기반 삭제 대신 bookId 기반 삭제로 변경하는 것이 DB와 일관성 있음
	// CartInterface의 removeCart(int numId) 시그니처를 유지해야 한다면,
	// 내부적으로 bookId를 찾아 DB 삭제 호출
	@Override
	public void removeCart(int index) { // 인덱스로 항목 삭제
		if (currentUser == null || currentUser.getUserId() == null) {
			System.err.println("장바구니 항목 삭제 실패: 로그인된 사용자가 없습니다.");
			return;
		}
		if (index >= 0 && index < mCartItem.size()) {
			CartItem itemToRemove = mCartItem.get(index);
			if (cartDAO.removeBookFromCart(currentUser.getUserId(), itemToRemove.getBookID())) {
				mCartItem.remove(index);
			} else {
				System.err.println("DB에서 장바구니 항목 삭제 실패: " + itemToRemove.getBookID());
			}
		}
	}

	// bookId로 장바구니 항목 삭제 (새로운 메소드 또는 removeCart 오버로딩)
	public void removeCartItemByBookId(String bookId) {
		if (currentUser == null || currentUser.getUserId() == null) return;
		if (cartDAO.removeBookFromCart(currentUser.getUserId(), bookId)) {
			mCartItem.removeIf(item -> item.getBookID().equals(bookId));
		}
	}
	public boolean updateQuantity(String bookId, int newQuantity) {
		if (currentUser == null || currentUser.getUserId() == null) {
			System.err.println("수량 변경 실패: 로그인된 사용자가 없습니다.");
			return false;
		}
		if (cartDAO.updateCartItemQuantity(currentUser.getUserId(), bookId, newQuantity)) {
			// 메모리 mCartItem 업데이트
			boolean foundInList = false;
			for (CartItem item : mCartItem) {
				if (item.getBookID().equals(bookId)) {
					if (newQuantity <= 0) {
						mCartItem.remove(item); // 수량이 0 이하면 리스트에서 제거
					} else {
						item.setQuantity(newQuantity); // 수량 변경 및 totalPrice 자동 업데이트
					}
					foundInList = true;
					break;
				}
			}
			// 만약 DB에는 업데이트 성공했는데 메모리 리스트에 없었고, newQuantity > 0인 경우
			// (이런 경우는 동기화 문제일 수 있으므로, 리스트를 다시 로드하는 것이 안전할 수 있음)
			if (!foundInList && newQuantity > 0) {
				loadCartFromDB(); // DB에서 전체 장바구니 다시 로드
			}
			return true;
		}
		System.err.println("DB에서 장바구니 항목 수량 변경 실패: " + bookId);
		return false;
	}
	// 장바구니 항목 수량 변경 (DB 연동 추가)
//	public boolean updateQuantity(String bookId, int newQuantity) {
//		if (currentUser == null || currentUser.getUserId() == null) return false;
//		if (cartDAO.updateCartItemQuantity(currentUser.getUserId(), bookId, newQuantity)) {
//			for (CartItem item : mCartItem) {
//				if (item.getBookID().equals(bookId)) {
//					if (newQuantity <= 0) {
//						mCartItem.remove(item); // 0 이하이면 리스트에서도 제거
//					} else {
//						item.setQuantity(newQuantity);
//					}
//					return true;
//				}
//			}
//			// 만약 newQuantity > 0 인데 mCartItem에 없다면? (DB에는 있는데 메모리에 없는 경우) -> loadCartFromDB() 호출 고려
//			if (newQuantity > 0) loadCartFromDB(); // DB와 동기화
//			return true;
//		}
//		return false;
//	}


	// 기타 메소드
	public void printCart() {
		System.out.println("장바구니 상품 목록 :");
		System.out.println("---------------------------------------------");
		System.out.println("    도서ID \t|     수량 \t|      합계");
		for (CartItem item : mCartItem) {
			System.out.print("    " + item.getBookID() + " \t| ");
			System.out.print("    " + item.getQuantity() + " \t| ");
			System.out.print("    " + item.getTotalPrice());
			System.out.println("  ");
		}
		System.out.println("---------------------------------------------");
	}

	public List<CartItem> getmCartItem() {
		return mCartItem;
	}

	// setmCartItem은 외부에서 직접 리스트를 교체하는 것을 지양하고,
	// loadCartFromDB()나 개별 아이템 추가/삭제 메소드를 통해 관리하는 것이 좋음
	// public void setmCartItem(List<CartItem> mCartItem) {
	//     this.mCartItem = mCartItem;
	// }

	public int getmCartCount() { // mCartCount 필드 대신 mCartItem.size() 사용
		return mCartItem.size();
	}
}