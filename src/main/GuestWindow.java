package main;

import javax.swing.*;
import java.awt.*;
import member.AuthService;
import member.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class GuestWindow extends JFrame {
	JTextField userIdField;
	JPasswordField passwordField; // 비밀번호 필드 추가
	JTextField nameField;
	JTextField phoneField;

	JButton enterButton;
	JLabel titleLabel; // 창 제목 레이블

	// 패널들을 멤버 변수로 선언하여 동적으로 숨기거나 보이게 하기 위함
	JPanel namePanel;
	JPanel phonePanel;
	JPanel idPanel;
	JPanel passwordPanel;
	// (선택) JPanel addressPanel;
	JLabel buttonLabel;
	JRadioButton loginRadioButton;
	JRadioButton registerRadioButton;

	private boolean isLoginMode = true; // 기본은 로그인 모드
	public GuestWindow(String title, int x, int y, int width, int height) {

		initContainer(title, x, y, width, height);
		setVisible(true);
		setResizable(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon("./images/shop.png").getImage());
		updateFieldsVisibility(); // 초기 모드에 맞게 필드 보이기/숨기기
	}

	private void initContainer(String title, int x, int y, int width, int height) {
		setTitle(title);
		setBounds(x, y, width, height);
		setLayout(null);

		Font ft;
		ft = new Font("맑은 고딕", Font.BOLD, 15);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - 1000) / 2, (screenSize.height - 750) / 2);

		JPanel userPanel = new JPanel();
		userPanel.setBounds(0, 50, 1000, 256);

		ImageIcon imageIcon = new ImageIcon("./images/user.png");
		imageIcon.setImage(imageIcon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH));
		JLabel userLabel = new JLabel(imageIcon);
		userPanel.add(userLabel);
		add(userPanel);

		JPanel titlePanel = new JPanel();
		titlePanel.setBounds(0, 300, 1000, 50);
		add(titlePanel);

		titleLabel = new JLabel("-- 고객 정보를 입력하세요 --");
		titleLabel.setFont(ft);
		titleLabel.setForeground(Color.BLUE);
		titlePanel.add(titleLabel);

		// --- 로그인/회원가입 선택 라디오 버튼 ---
		JPanel modeSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		modeSelectionPanel.setBounds(0, 600, width, 50);
		add(modeSelectionPanel);

		loginRadioButton = new JRadioButton("로그인");
		loginRadioButton.setFont(ft);
		loginRadioButton.setSelected(true); // 기본 선택
		registerRadioButton = new JRadioButton("회원가입");
		registerRadioButton.setFont(ft);

		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(loginRadioButton);
		modeGroup.add(registerRadioButton);

		modeSelectionPanel.add(loginRadioButton);
		modeSelectionPanel.add(registerRadioButton);

		namePanel = new JPanel();
		namePanel.setBounds(0, 350, 1000, 50);
		add(namePanel);

		JLabel nameLabel = new JLabel("이   름: ");
		nameLabel.setFont(ft);
		namePanel.add(nameLabel);

		nameField = new JTextField(10);
		nameField.setFont(ft);
		namePanel.add(nameField);

		idPanel = new JPanel();
		idPanel.setBounds(0, 400, 1000, 50);
		add(idPanel);

		JLabel idLabel = new JLabel("아이디: ");
		idLabel.setFont(ft);
		idPanel.add(idLabel);

		userIdField = new JTextField(10);
		userIdField.setFont(ft);
		idPanel.add(userIdField);

		phonePanel = new JPanel();
		phonePanel.setBounds(0, 500, 1000, 50);
		add(phonePanel);

		JLabel phoneLabel = new JLabel("연락처 : ");
		phoneLabel.setFont(ft);
		phonePanel.add(phoneLabel);

		phoneField = new JTextField(10);
		phoneField.setFont(ft);
		phonePanel.add(phoneField);

		passwordPanel = new JPanel();
		passwordPanel.setBounds(0, 450, 1000, 50); // phonePanel 아래에 위치시키세요.
		add(passwordPanel);

		JLabel passwordLabel = new JLabel("비밀번호: ");
		passwordField = new JPasswordField(10);
		passwordField.setFont(ft);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds(0, 550, 1000, 100);
		add(buttonPanel);

		buttonLabel = new JLabel("쇼핑하기", new ImageIcon("images/shop.png"), JLabel.LEFT);
		buttonLabel.setFont(ft);
		enterButton = new JButton();
		enterButton.add(buttonLabel);
		buttonPanel.add(enterButton);


		// --- 이벤트 리스너 ---
		loginRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					isLoginMode = true;
					updateFieldsVisibility();
				}
			}
		});

		registerRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					isLoginMode = false;
					updateFieldsVisibility();
				}
			}
		});


		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userId = userIdField.getText();
				String password = new String(passwordField.getPassword());

				if (userId.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(GuestWindow.this, "ID와 비밀번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (isLoginMode) { // 로그인 모드
					if (AuthService.login(userId, password)) {
						User loggedInUser = AuthService.getCurrentUser();
						JOptionPane.showMessageDialog(GuestWindow.this, loggedInUser.getName() + "님, 환영합니다!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
						dispose();
						new MainWindow("온라인 서점", 0, 0, 1000, 750);
					} else {
						JOptionPane.showMessageDialog(GuestWindow.this, "ID 또는 비밀번호가 일치하지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
					}
				} else { // 회원가입 모드
					String name = nameField.getText();
					String phoneStr = phoneField.getText();

					if (name.isEmpty() || phoneStr.isEmpty()) {
						JOptionPane.showMessageDialog(GuestWindow.this, "이름과 연락처를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
						return;
					}

					int phone;
					try {
						phone = Integer.parseInt(phoneStr);
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(GuestWindow.this, "연락처는 숫자로 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
						return;
					}
					// 주소는 여기서 생략. 필요하면 addressField 추가하고 값 가져오기

					User newUser = new User(userId, password, name, phone, ""); // 주소는 빈 문자열, role은 User 생성자에서 "USER"로 자동 설정
					if (AuthService.registerUser(newUser)) {
						JOptionPane.showMessageDialog(GuestWindow.this, "회원가입이 완료되었습니다. 로그인해주세요.", "회원가입 성공", JOptionPane.INFORMATION_MESSAGE);
						// 회원가입 성공 후 로그인 모드로 전환하고 필드 비우기
						loginRadioButton.setSelected(true);
						isLoginMode = true;
						updateFieldsVisibility();
						// userIdField.setText(""); // 비우거나 유지
						// passwordField.setText("");
					} else {
						JOptionPane.showMessageDialog(GuestWindow.this, "회원가입에 실패했습니다. (ID 중복 또는 시스템 오류)", "회원가입 실패", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}
	// 모드에 따라 입력 필드 및 버튼 텍스트 업데이트
	private void updateFieldsVisibility() {
		if (isLoginMode) {
			titleLabel.setText("-- 로그인 --");
			namePanel.setVisible(false);
			phonePanel.setVisible(false);
			// if (addressPanel != null) addressPanel.setVisible(false);
			buttonLabel.setText("로그인");
		} else {
			titleLabel.setText("-- 회원가입 --");
			namePanel.setVisible(true);
			phonePanel.setVisible(true);
			// if (addressPanel != null) addressPanel.setVisible(true);
			buttonLabel.setText("회원가입");
		}
		// 변경사항 적용을 위해 revalidate, repaint (필요한 경우)
		// this.revalidate();
		// this.repaint();
	}

}