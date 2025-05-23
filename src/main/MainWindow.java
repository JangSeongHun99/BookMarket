package main;

import bookitem.BookInIt;
import member.AuthService; // AuthService import
import member.User; // User import
import cart.Cart;
import page.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
	private Cart mCart;
	static JPanel mMenuPanel, mPagePanel;

	public MainWindow(String title, int x, int y, int width, int height) {
		User currentUser = AuthService.getCurrentUser();
		if (currentUser == null) {
			// 로그인되지 않은 사용자는 MainWindow를 사용할 수 없음
			JOptionPane.showMessageDialog(null, "로그인이 필요합니다. 프로그램을 다시 시작하여 로그인해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
			// GuestWindow를 다시 띄우거나 프로그램 종료
			// dispose(); // 현재 MainWindow 닫기
			// System.exit(0); // 프로그램 종료
			// return;
			// 일단은 비로그인 상태로도 볼 수 있게 하되, 장바구니 등은 막히도록
			this.mCart = new Cart(null); // 비로그인 사용자를 위한 임시 빈 장바구니
		} else {
			this.mCart = new Cart(currentUser); // 로그인된 사용자의 Cart 객체 생성 및 DB 로드
		}


		initContainer(title, x, y, width, height);
		initMenu();

		setVisible(true);
		setResizable(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon("./images/shop.png").getImage());
	}

	private void initContainer(String title, int x, int y, int width, int height) {
		setTitle(title);
		setBounds(x, y, width, height);
		setLayout(null);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - 1000) / 2, (screenSize.height - 750) / 2);

		mMenuPanel = new JPanel();
		mMenuPanel.setBounds(0, 20, width, 130);
		menuIntroduction();
		add(mMenuPanel);

		mPagePanel = new JPanel();
		mPagePanel.setBounds(0, 150, width, height);
		add(mPagePanel);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				setVisible(false); // 현재 프레임 감추기
				new GuestWindow("고객 정보 입력", 0, 0, 1000, 750);
			}
		});
	}

	private void menuIntroduction() {
		Font ft;
		ft = new Font("맑은 고딕", Font.BOLD, 15);

		JButton bt1 = new JButton("고객 정보 확인하기", new ImageIcon("./images/1.png"));
		bt1.setBounds(0, 0, 100, 50);
		bt1.setFont(ft);
		mMenuPanel.add(bt1);

		bt1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mPagePanel.removeAll(); // 패널(mPagePanel)에 표시된 구성 요소 모두 삭제

				mPagePanel.add("고객 정보 확인", new GuestInfoPage(mPagePanel)); // 패널(mPagePanel)에 GuestInfoPage의 내용 출력
				mPagePanel.revalidate(); // 구성 요소 가로/세로 속성 변경하여 호출
				mPagePanel.repaint(); // 구성요소 모양을 변경하여 호출
			}
		});

		JButton bt2 = new JButton("장바구니 상품목록보기", new ImageIcon("./images/2.png"));
		bt2.setBounds(0, 0, 100, 30);
		bt2.setFont(ft);
		mMenuPanel.add(bt2);

		bt2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!AuthService.isLoggedIn()) {
					JOptionPane.showMessageDialog(bt2, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (mCart.getmCartCount() == 0) // mCart.mCartCount -> mCart.getmCartCount()
					JOptionPane.showMessageDialog(bt2, "장바구니에 항목이 없습니다", "장바구니 상품 목록 보기", JOptionPane.ERROR_MESSAGE);
				else {
					mPagePanel.removeAll();
					// CartItemListPage 생성자에 현재 사용자의 mCart 전달
					mPagePanel.add("장바구니 상품 목록 보기", new CartItemListPage(mPagePanel, mCart));
					mPagePanel.revalidate();
					mPagePanel.repaint();
				}
			}
		});

		JButton bt3 = new JButton("장바구니 비우기", new ImageIcon("./images/3.png"));
		bt3.setBounds(0, 0, 100, 30);
		bt3.setFont(ft);
		mMenuPanel.add(bt3);

		// bt3 (장바구니 비우기)
		bt3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!AuthService.isLoggedIn()) {
					JOptionPane.showMessageDialog(bt3, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (mCart.getmCartCount() == 0)
					JOptionPane.showMessageDialog(bt3, "장바구니에 항목이 없습니다", "장바구니 비우기", JOptionPane.ERROR_MESSAGE);
				else {
					mPagePanel.removeAll();
					menuCartClear(bt3); // 내부적으로 mCart.deleteBook() 호출 (DB 연동됨)
					// CartItemListPage를 다시 로드하여 빈 장바구니를 보여줌
					mPagePanel.add("장바구니 비우기 후 목록", new CartItemListPage(mPagePanel, mCart));
					mPagePanel.revalidate();
					mPagePanel.repaint();
				}
			}
		});

		JButton bt4 = new JButton("장바구니에 항목추가하기", new ImageIcon("./images/4.png"));
		bt4.setFont(ft);
		mMenuPanel.add(bt4);
		// bt4 (장바구니에 항목 추가하기)
		bt4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!AuthService.isLoggedIn()) {
					JOptionPane.showMessageDialog(bt4, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				mPagePanel.removeAll();
				BookInIt.init(); // 책 목록 DB에서 다시 로드 (필요시)
				// CartAddItemPage 생성자에 현재 사용자의 mCart 전달
				mPagePanel.add("장바구니에 항목 추가하기", new CartAddItemPage(mPagePanel, mCart));
				mPagePanel.revalidate();
				mPagePanel.repaint();
			}
		});

//		JButton bt5 = new JButton("장바구니에 항목수량 줄이기", new ImageIcon("./images/5.png"));
//		bt5.setFont(ft);
//		mMenuPanel.add(bt5);
//
//		// bt5 (장바구니 항목 수량 줄이기) - 이 버튼의 기능 구현 필요
//		// 예: 선택된 항목의 수량을 1 줄이거나, 수량 변경 다이얼로그 표시
//		// mCart.updateQuantity(bookId, newQuantity) 사용
//		bt5.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if (!AuthService.isLoggedIn()) {
//					JOptionPane.showMessageDialog(bt5, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
//					return;
//				}
//				// 현재 선택된 장바구니 항목 가져오기 (CartItemListPage와 연동 필요)
//				// 또는 별도의 UI로 수량 변경 기능 구현
//				JOptionPane.showMessageDialog(bt5, "이 기능은 CartItemListPage에서 구현하거나 별도 UI가 필요합니다.");
//				// 임시로 CartItemListPage를 다시 로드하여 사용자가 직접 수량 변경 UI를 보도록 유도할 수 있음
//				mPagePanel.removeAll();
//				mPagePanel.add("장바구니 상품 목록 보기 (수량변경)", new CartItemListPage(mPagePanel, mCart));
//				mPagePanel.revalidate();
//				mPagePanel.repaint();
//			}
//		});

//		JButton bt6 = new JButton("장바구니에 항목삭제하기", new ImageIcon("./images/6.png"));
//		bt6.setFont(ft);
//		mMenuPanel.add(bt6);

		// bt6 (장바구니 항목 삭제하기)
//		bt6.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if (!AuthService.isLoggedIn()) {
//					JOptionPane.showMessageDialog(bt6, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
//					return;
//				}
//				if (mCart.getmCartCount() == 0)
//					JOptionPane.showMessageDialog(bt6, "장바구니에 항목이 없습니다", "장바구니 항목 삭제", JOptionPane.ERROR_MESSAGE); // 오타 수정: bt3 -> bt6
//				else {
//					// CartItemListPage에서 선택된 항목의 인덱스나 bookId를 가져와야 함
//					// 여기서는 CartItemListPage를 먼저 띄우고 거기서 삭제하도록 유도
//					mPagePanel.removeAll();
//					// CartItemListPage 생성자에 mCart 전달, 이 페이지 내에서 삭제 로직 처리
//					mPagePanel.add("장바구니의 항목 삭제하기", new CartItemListPage(mPagePanel, mCart));
//					// CartItemListPage의 mSelectRow는 static이므로 MainWindow에서 직접 접근하는 것은 좋지 않음.
//					// CartItemListPage 내부에서 삭제 로직을 처리하도록 유도.
//					mPagePanel.revalidate();
//					mPagePanel.repaint();
//				}
//			}
//		});

		JButton bt7 = new JButton("주문하기", new ImageIcon("./images/5.png"));
		bt7.setFont(ft);
		mMenuPanel.add(bt7);

		// bt7 (주문하기)
		bt7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!AuthService.isLoggedIn()) {
					JOptionPane.showMessageDialog(bt7, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (mCart.getmCartCount() == 0)
					JOptionPane.showMessageDialog(bt7, "장바구니에 항목이 없습니다", "주문처리", JOptionPane.ERROR_MESSAGE);
				else {
					mPagePanel.removeAll();
					// CartShippingPage 생성자에 mCart 전달
					mPagePanel.add("주문 배송지", new CartShippingPage(mPagePanel, mCart));
					mPagePanel.revalidate();
					mPagePanel.repaint();
				}
			}
		});

		JButton bt8 = new JButton("종료", new ImageIcon("./images/6.png"));
		bt8.setFont(ft);
		mMenuPanel.add(bt8);

		bt8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int select = JOptionPane.showConfirmDialog(bt8, "쇼핑몰을 종료하겠습니까? ");

				if (select == 0) {
					System.exit(1);
				}
			}
		});

		JButton bt9 = new JButton("관리자", new ImageIcon("./images/7.png"));
		bt9.setFont(ft);
		mMenuPanel.add(bt9);

		bt9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AdminLoginDialog adminDialog;
				JFrame frame = new JFrame();
				adminDialog = new AdminLoginDialog(frame, "관리자 로그인");
				adminDialog.setVisible(true);
				if (adminDialog.isLogin) {
					mPagePanel.removeAll();
					mPagePanel.add("관리자", new AdminPage(mPagePanel));
					mPagePanel.revalidate();
					mPagePanel.repaint();
				}
			}
		});

	}

	private void initMenu() {
		Font ft;
		ft = new Font("함초롬돋움", Font.BOLD, 15);

		JMenuBar menuBar = new JMenuBar();

		JMenu menu01 = new JMenu("고객");
		menu01.setFont(ft);
		JMenuItem item01 = new JMenuItem("고객 정보");
		JMenuItem item11 = new JMenuItem("종료");
		menu01.add(item01);
		menu01.add(item11);
		menuBar.add(menu01);

		JMenu menu02 = new JMenu("상품");
		menu02.setFont(ft);
		JMenuItem item02 = new JMenuItem("상품 목록");
		menu02.add(item02);
		menuBar.add(menu02);

		JMenu menu03 = new JMenu("장바구니");
		menu03.setFont(ft);
		JMenuItem item03 = new JMenuItem("항목 추가");
		JMenuItem item04 = new JMenuItem("항목 수량 줄이기");
		JMenuItem item05 = new JMenuItem("항목 삭제하기");
		JMenuItem item06 = new JMenuItem("장바구니 비우기");
		menu03.add(item03);
		menu03.add(item04);
		menu03.add(item05);
		menu03.add(item06);
		menuBar.add(menu03);

		JMenu menu04 = new JMenu("주문");
		menu04.setFont(ft);
		JMenuItem item07 = new JMenuItem("영수증 표시");
		menu04.add(item07);
		menuBar.add(menu04);
		setJMenuBar(menuBar);

		item01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mPagePanel.removeAll();
				mPagePanel.add("고객 정보 확인 ", new GuestInfoPage(mPagePanel));
				add(mPagePanel);
				mPagePanel.revalidate();
			}
		});

		item02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mPagePanel.removeAll();
				BookInIt.init();
				mPagePanel.add("장바구니에 항목 추가하기", new CartAddItemPage(mPagePanel, mCart));
				add(mPagePanel);
				mPagePanel.revalidate();
			}
		});

		item03.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!AuthService.isLoggedIn()) {
					JOptionPane.showMessageDialog(MainWindow.this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				mPagePanel.removeAll();
				BookInIt.init();
				mPagePanel.add("장바구니에 항목 추가하기", new CartAddItemPage(mPagePanel, mCart));
				// add(mPagePanel); // 불필요
				mPagePanel.revalidate();
				mPagePanel.repaint(); // repaint 추가
			}
		});

		item11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mPagePanel.removeAll();
				setVisible(false);
				new GuestWindow("고객 정보 입력", 0, 0, 1000, 750);
				add(mPagePanel);
				mPagePanel.revalidate();
			}
		});
	}

	private void menuCartClear(JButton button) {

		if (!AuthService.isLoggedIn()) {
			JOptionPane.showMessageDialog(button, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (mCart.getmCartCount() == 0)
			JOptionPane.showMessageDialog(button, "장바구니의 항목이 없습니다");
		else {
			int select = JOptionPane.showConfirmDialog(button, "장바구니의 모든 항목을 삭제하겠습니까? ");
			if (select == 0) {
				mCart.deleteBook(); // 내부적으로 DB 연동됨
				JOptionPane.showMessageDialog(button, "장바구니의 모든 항목을 삭제했습니다");
			}
		}
	}
}