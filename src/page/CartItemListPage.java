package page;

import javax.swing.*;
import cart.Cart;
import cart.CartItem;
import member.AuthService; // 로그인 여부 확인용
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List; // List 사용을 위해 import
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CartItemListPage extends JPanel {

	JTable cartTable;
	Object[] tableHeader = { "도서ID", "도서명", "단가", "수량", "총가격" };

	Cart mCartLocal;
	public static int selectedRow = -1;

	JLabel totalPricelabel; // 총금액 표시 레이블을 멤버 변수로 선언

	JButton decreaseQuantityButton; // 수량 감소 버튼
	JButton increaseQuantityButton; // 수량 증가 버튼

	public CartItemListPage(JPanel panel, Cart cartFromMainWindow) {
		Font ft;
		ft = new Font("맑은 고딕", Font.BOLD, 15);
		this.mCartLocal = cartFromMainWindow;
		this.setLayout(null);

		Rectangle rect = panel.getBounds();
		System.out.println(rect);
		this.setPreferredSize(rect.getSize());

		// 장바구니 테이블이 표시될 패널
		JPanel bookPanel = new JPanel();
		bookPanel.setBounds(0, 0, rect.width, 350); // 테이블 높이 조정
		add(bookPanel);

		// JTable 및 JScrollPane 생성
		cartTable = new JTable(); // 초기 모델은 refreshCartTable에서 설정
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setPreferredSize(new Dimension(rect.width - 50, 330)); // 패널 크기에 맞게 조정
		jScrollPane.setViewportView(cartTable);
		bookPanel.add(jScrollPane);


		// 총 금액 표시 패널
		JPanel totalPricePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 오른쪽 정렬
		totalPricePanel.setBounds(0, 360, rect.width - 20, 30); // 위치 및 크기 조정
		totalPricelabel = new JLabel("총금액: 0 원");
		totalPricelabel.setForeground(Color.red);
		totalPricelabel.setFont(ft);
		totalPricePanel.add(totalPricelabel);
		add(totalPricePanel);

		// 버튼 패널 (장바구니 비우기, 항목 삭제, 새로고침)
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // 버튼 간 간격 추가
		bottomButtonPanel.setBounds(0, 440, rect.width, 50); // 위치 조정
		add(bottomButtonPanel);

		JButton clearButton = new JButton("장바구니 비우기");
		clearButton.setFont(ft);
		bottomButtonPanel.add(clearButton);

		JButton removeButton = new JButton("선택 항목 삭제");
		removeButton.setFont(ft);
		bottomButtonPanel.add(removeButton);

		JButton refreshButton = new JButton("새로 고침");
		refreshButton.setFont(ft);
		bottomButtonPanel.add(refreshButton);

		// 수량 조절 버튼 패널
		JPanel quantityControlPanel = new JPanel();
		quantityControlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		quantityControlPanel.setBounds(0, 400, rect.width, 40); // 총금액 패널과 버튼 패널 사이
		add(quantityControlPanel);

		decreaseQuantityButton = new JButton("-");
		decreaseQuantityButton.setFont(new Font("Arial", Font.BOLD, 16));
		quantityControlPanel.add(decreaseQuantityButton);

		increaseQuantityButton = new JButton("+");
		increaseQuantityButton.setFont(new Font("Arial", Font.BOLD, 16));
		quantityControlPanel.add(increaseQuantityButton);


		// 초기 테이블 데이터 로드
		refreshCartTable();

		// --- Event Listeners ---

		cartTable.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				selectedRow = cartTable.getSelectedRow();
				// 선택된 행이 있으면 수량 조절 버튼 활성화
				updateQuantityButtonsState();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});

		clearButton.addActionListener(new ActionListener() { // AbstractAction -> ActionListener
			public void actionPerformed(ActionEvent e) {
				if (!AuthService.isLoggedIn() || mCartLocal == null) {
					JOptionPane.showMessageDialog(CartItemListPage.this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (mCartLocal.getmCartCount() == 0) {
					JOptionPane.showMessageDialog(CartItemListPage.this, "장바구니에 항목이 없습니다", "알림", JOptionPane.INFORMATION_MESSAGE);
				} else {
					int select = JOptionPane.showConfirmDialog(CartItemListPage.this,
							"장바구니의 모든 항목을 삭제하겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
					if (select == JOptionPane.YES_OPTION) {
						mCartLocal.deleteBook(); // DB 연동된 Cart의 deleteBook() 호출
						refreshCartTable(); // 테이블 새로고침
						JOptionPane.showMessageDialog(CartItemListPage.this, "장바구니의 모든 항목을 삭제했습니다", "완료", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				updateQuantityButtonsState(); // 버튼 상태 업데이트
			}
		});

		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!AuthService.isLoggedIn() || mCartLocal == null) {
					JOptionPane.showMessageDialog(CartItemListPage.this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (selectedRow == -1) {
					JOptionPane.showMessageDialog(CartItemListPage.this, "삭제할 항목을 테이블에서 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
				} else if (mCartLocal.getmCartCount() == 0) {
					JOptionPane.showMessageDialog(CartItemListPage.this, "장바구니에 항목이 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					int confirm = JOptionPane.showConfirmDialog(CartItemListPage.this,
							"선택한 항목을 장바구니에서 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						// 테이블 모델에서 직접 bookId를 가져오는 것이 더 안전함
						// CartItem itemToRemove = mCartLocal.getmCartItem().get(selectedRow); // 인덱스 기반 접근은 리스트 변경 시 위험
						String bookIdToRemove = (String) cartTable.getValueAt(selectedRow, 0); // 0번 열이 book_id라고 가정

						mCartLocal.removeCartItemByBookId(bookIdToRemove); // bookId 기반 삭제 메소드 호출
						refreshCartTable(); // 테이블 새로고침
						selectedRow = -1; // 선택 해제
						JOptionPane.showMessageDialog(CartItemListPage.this, "선택한 항목을 삭제했습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				updateQuantityButtonsState(); // 버튼 상태 업데이트
			}
		});

		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mCartLocal == null) {
					JOptionPane.showMessageDialog(CartItemListPage.this, "장바구니 정보를 불러올 수 없습니다. (로그인 확인)", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				refreshCartTable();
				selectedRow = -1; // 새로고침 후 선택 해제
				updateQuantityButtonsState(); // 버튼 상태 업데이트
				JOptionPane.showMessageDialog(CartItemListPage.this, "장바구니를 새로고침했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		decreaseQuantityButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleChangeQuantity(-1);
			}
		});

		increaseQuantityButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleChangeQuantity(1);
			}
		});

		// 초기 버튼 상태 설정
		updateQuantityButtonsState();
	}

	// 테이블 및 총금액 새로고침 메소드
	private void refreshCartTable() {
		if (mCartLocal == null || !AuthService.isLoggedIn()) {
			// 로그인되지 않았거나 Cart 객체가 없는 경우 빈 테이블 표시 또는 메시지
			DefaultTableModel model = new DefaultTableModel(new Object[0][tableHeader.length], tableHeader) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false; // 모든 셀 수정 불가
				}
			};
			cartTable.setModel(model);
			if (totalPricelabel != null) totalPricelabel.setText("총금액: 0 원");
			if (!AuthService.isLoggedIn() && totalPricelabel != null) { // 로그인 안된 경우 명시적 메시지
				totalPricelabel.setText("로그인이 필요합니다.");
			}
			return;
		}

		List<CartItem> cartItemList = mCartLocal.getmCartItem();
		Object[][] content = new Object[cartItemList.size()][tableHeader.length];
		int currentTotalPrice = 0;

		for (int i = 0; i < cartItemList.size(); i++) {
			CartItem item = cartItemList.get(i);
			content[i][0] = item.getBookID();
			content[i][1] = item.getItemBook().getName(); // CartItem이 Book 객체를 가지고 있다고 가정
			content[i][2] = item.getItemBook().getUnitPrice();
			content[i][3] = item.getQuantity();
			content[i][4] = item.getTotalPrice();
			currentTotalPrice += item.getTotalPrice();
		}

		DefaultTableModel model = new DefaultTableModel(content, tableHeader) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // 모든 셀 수정 불가 (수량 변경은 버튼으로)
			}
		};
		cartTable.setModel(model);

		if (totalPricelabel != null) {
			totalPricelabel.setText("총금액: " + currentTotalPrice + " 원");
		}
	}

	// 수량 변경 버튼 상태 업데이트 (선택된 항목이 있을 때만 활성화)
	private void updateQuantityButtonsState() {
		boolean enabled = selectedRow != -1 && mCartLocal != null && mCartLocal.getmCartCount() > 0;
		if (decreaseQuantityButton != null) decreaseQuantityButton.setEnabled(enabled);
		if (increaseQuantityButton != null) increaseQuantityButton.setEnabled(enabled);
	}

	// 수량 변경 처리 메소드
	private void handleChangeQuantity(int change) {
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "수량을 변경할 항목을 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		if (mCartLocal == null || !AuthService.isLoggedIn()) {
			JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}

		List<CartItem> cartItems = mCartLocal.getmCartItem();
		if (selectedRow >= 0 && selectedRow < cartItems.size()) {
			CartItem selectedCartItem = cartItems.get(selectedRow);
			int currentQuantity = selectedCartItem.getQuantity();
			int newQuantity = currentQuantity + change;

			if (newQuantity < 0) newQuantity = 0; // 수량이 0 미만으로 내려가지 않도록

			if (newQuantity == 0) {
				// 수량이 0이 되면 삭제 여부 확인
				int confirm = JOptionPane.showConfirmDialog(this,
						"수량이 0이 됩니다. 항목을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					mCartLocal.updateQuantity(selectedCartItem.getBookID(), 0); // DB에서도 삭제 (Cart 클래스에 구현)
				} else {
					// 삭제 취소 시 아무것도 안 함 (또는 이전 수량으로 복원 - 여기선 단순화)
					return;
				}
			} else {
				// DB에 수량 업데이트 요청
				mCartLocal.updateQuantity(selectedCartItem.getBookID(), newQuantity);
			}
			refreshCartTable(); // 테이블 새로고침
			// 선택 유지 또는 해제 (선택 해제하는 것이 더 간단)
			// cartTable.setRowSelectionInterval(selectedRow, selectedRow); // 선택 유지 시
			selectedRow = -1; // 선택 해제
			updateQuantityButtonsState();
		}
	}

	public static void main(String[] args) {

		//Cart mCart = new Cart();
		JFrame frame = new JFrame();
		frame.setBounds(0, 0, 1000, 750);
		frame.setLayout(null);

		JPanel mPagePanel = new JPanel();
		mPagePanel.setBounds(0, 150, 1000, 750);

		frame.add(mPagePanel);
		//mPagePanel.add("장바구니의 상품 목록 보기", new CartItemListPage(mPagePanel, mCart));
		frame.setVisible(true);
	}
}