package page;

import javax.swing.*;
import bookitem.Book;
import bookitem.BookInIt;
import cart.Cart;
import member.AuthService;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class CartAddItemPage extends JPanel {

	ImageIcon imageBook; // 선택된 책의 이미지를 표시할 ImageIcon
	JLabel imageLabel;   // 이미지를 담을 JLabel (멤버 변수로 변경하여 업데이트 가능하게)
	JPanel imagePanel;   // 이미지를 표시할 패널

	int mSelectRow = 0; // 테이블에서 선택된 행의 인덱스 (기본 0번 행 선택)

	Cart mCartLocal; // MainWindow로부터 전달받은 현재 사용자의 Cart 객체

	JTable bookTable; // 책 목록을 표시할 JTable

	public CartAddItemPage(JPanel panel, Cart cartFromMainWindow) {
		Font ft;
		ft = new Font("함초롬돋움", Font.BOLD, 15); // 폰트 설정

		setLayout(null); // 절대 위치 레이아웃 사용

		Rectangle rect = panel.getBounds(); // 부모 패널의 크기를 가져옴
		// System.out.println(rect); // 디버깅용
		setPreferredSize(rect.getSize()); // 이 패널의 선호 크기를 부모 패널 크기로 설정

		this.mCartLocal = cartFromMainWindow; // 전달받은 Cart 객체를 멤버 변수에 할당

		// --- 책 이미지 표시 패널 ---
		imagePanel = new JPanel(); // FlowLayout 기본 사용
		imagePanel.setBounds(20, 20, 280, 360); // 위치와 크기 설정 (여백 고려)
		// 초기 이미지는 첫 번째 책 또는 기본 이미지로 설정
		imageLabel = new JLabel(); // 빈 레이블로 초기화
		imagePanel.add(imageLabel);
		add(imagePanel);

		// --- 책 목록 테이블 표시 패널 ---
		JPanel tablePanel = new JPanel(); // FlowLayout 기본 사용
		tablePanel.setBounds(310, 20, rect.width - 310 - 20, 360); // 위치와 크기 설정 (여백 고려)
		add(tablePanel);

		// BookInIt.init(); // 책 목록 초기화 (DB에서 가져오도록 수정됨)
		// getmBookList() 호출 시 필요하면 내부적으로 init() 호출됨
		List<Book> booklist = BookInIt.getmBookList();

		if (booklist == null || booklist.isEmpty()) {
			tablePanel.setLayout(new BorderLayout()); // 메시지를 중앙에 표시하기 위한 레이아웃
			JLabel noBooksLabel = new JLabel("현재 판매 중인 책이 없습니다.", SwingConstants.CENTER);
			noBooksLabel.setFont(ft);
			tablePanel.add(noBooksLabel, BorderLayout.CENTER);
			// 책이 없으면 초기 이미지 설정 불가능하므로 기본 이미지 설정
			setDefaultBookImage();
		} else {
			Object[] tableHeader = {"도서ID", "도서명", "가격", "저자", "설명", "분야", "출판일"};
			Object[][] content = new Object[booklist.size()][tableHeader.length];
			for (int i = 0; i < booklist.size(); i++) {
				Book bookitem = booklist.get(i);
				content[i][0] = bookitem.getBookId();
				content[i][1] = bookitem.getName();
				content[i][2] = bookitem.getUnitPrice();
				content[i][3] = bookitem.getAuthor();
				content[i][4] = bookitem.getDescription(); // 실제로는 너무 길 수 있으므로 요약 또는 tooltip 고려
				content[i][5] = bookitem.getCategory();
				content[i][6] = bookitem.getReleaseDate();
			}

			bookTable = new JTable(content, tableHeader);
			bookTable.setRowSelectionAllowed(true);
			bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 단일 행 선택 모드
			if (booklist.size() > 0) {
				bookTable.setRowSelectionInterval(0, 0); // 기본으로 첫 번째 행 선택
				updateBookImage(booklist.get(0).getBookId()); // 첫 번째 책 이미지로 초기화
			} else {
				setDefaultBookImage(); // 책이 있지만 첫번째 항목이 없는 경우 (이론상으론 드묾)
			}


			JScrollPane jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(tablePanel.getWidth() - 20, tablePanel.getHeight() - 20)); // 스크롤 패인 크기 조정
			jScrollPane.setViewportView(bookTable);
			tablePanel.add(jScrollPane);

			bookTable.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					int row = bookTable.getSelectedRow();
					if (row != -1) { // 유효한 행이 선택되었는지 확인
						mSelectRow = row;
						String selectedBookId = (String) bookTable.getValueAt(row, 0); // 0번 열이 도서ID라고 가정
						updateBookImage(selectedBookId);
					}
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}
			});
		}


		// --- "장바구니에 담기" 버튼 패널 ---
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 버튼을 중앙에 배치
		buttonPanel.setBounds(0, 400, rect.width, 50); // 위치 조정 (하단에 배치)
		add(buttonPanel);

		JButton addButton = new JButton("장바구니에 담기");
		addButton.setFont(ft);
		buttonPanel.add(addButton);

		// "장바구니에 담기" 버튼 액션 리스너
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!AuthService.isLoggedIn()) {
					JOptionPane.showMessageDialog(CartAddItemPage.this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (mCartLocal == null) {
					JOptionPane.showMessageDialog(CartAddItemPage.this, "장바구니 정보를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}

				List<Book> currentBookList = BookInIt.getmBookList(); // 최신 책 목록 가져오기
				if (currentBookList == null || currentBookList.isEmpty()) {
					JOptionPane.showMessageDialog(CartAddItemPage.this, "선택할 책이 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (mSelectRow < 0 || mSelectRow >= currentBookList.size()) {
					JOptionPane.showMessageDialog(CartAddItemPage.this, "목록에서 책을 선택해주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
					return;
				}

				Book selectedBook = currentBookList.get(mSelectRow);
				int selectConfirm = JOptionPane.showConfirmDialog(CartAddItemPage.this,
						"'" + selectedBook.getName() + "'을(를) 장바구니에 추가하겠습니까?",
						"장바구니 추가 확인", JOptionPane.YES_NO_OPTION);

				if (selectConfirm == JOptionPane.YES_OPTION) {
					mCartLocal.insertBook(selectedBook); // Cart 클래스의 insertBook이 DB와 메모리 모두 처리
					JOptionPane.showMessageDialog(CartAddItemPage.this, "'" + selectedBook.getName() + "'이(가) 장바구니에 추가되었습니다.", "추가 완료", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	}

	// 선택된 책의 이미지를 업데이트하는 메소드
	private void updateBookImage(String bookId) {
		if (imageLabel == null || imagePanel == null) return;

		String imagePath = "./images/" + bookId + ".jpg"; // 이미지 파일 경로 규칙 (예: ISBN1234.jpg)
		ImageIcon newImage = new ImageIcon(imagePath);

		// 이미지 파일 존재 여부 확인 (선택 사항, 파일 없으면 기본 이미지 표시)
		if (newImage.getImageLoadStatus() != MediaTracker.COMPLETE) {
			System.err.println("이미지 로드 실패: " + imagePath + " (기본 이미지 표시)");
			setDefaultBookImage();
			return;
		}


		// 이미지 크기 조절 (imagePanel 크기에 맞게)
		Image scaledImage = newImage.getImage().getScaledInstance(
				imagePanel.getWidth() - 20, // 패널 내부 여백 고려
				imagePanel.getHeight() - 20,
				Image.SCALE_SMOOTH); // 부드럽게 스케일링

		imageBook = new ImageIcon(scaledImage);
		imageLabel.setIcon(imageBook);

		// imagePanel.revalidate(); // 레이아웃 변경 시 필요
		// imagePanel.repaint();   // 다시 그리기
		// JLabel에 setIcon만 해도 대부분의 경우 자동으로 갱신됨.
		// 만약 갱신 안되면 위 revalidate/repaint 호출.
	}

	// 기본 이미지를 설정하는 메소드
	private void setDefaultBookImage() {
		if (imageLabel == null || imagePanel == null) return;
		// 기본 이미지 경로 설정 (예: placeholder.jpg)
		String defaultImagePath = "./images/placeholder.jpg"; // 기본 이미지 파일 준비 필요
		ImageIcon defaultImage = new ImageIcon(defaultImagePath);

		if (defaultImage.getImageLoadStatus() != MediaTracker.COMPLETE) {
			System.err.println("기본 이미지 로드 실패: " + defaultImagePath);
			imageLabel.setIcon(null); // 이미지 로드 실패 시 아이콘 제거
			imageLabel.setText("이미지 없음"); // 텍스트로 표시
			return;
		}

		Image scaledDefaultImage = defaultImage.getImage().getScaledInstance(
				imagePanel.getWidth() - 20,
				imagePanel.getHeight() - 20,
				Image.SCALE_SMOOTH);
		imageBook = new ImageIcon(scaledDefaultImage);
		imageLabel.setIcon(imageBook);
	}
}