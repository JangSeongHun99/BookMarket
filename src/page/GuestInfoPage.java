package page;

import javax.swing.*;
import java.awt.*;

import member.AuthService;
import member.User;

public class GuestInfoPage extends JPanel {

	public GuestInfoPage(JPanel panel) {
		Font ft;
		ft = new Font("함초롬돋움", Font.BOLD, 15);

		setLayout(null);

		Rectangle rect = panel.getBounds();
		System.out.println(rect);
		setPreferredSize(rect.getSize());

		// 현재 로그인된 사용자 정보 가져오기
		User currentUser = AuthService.getCurrentUser();

		JPanel namePanel = new JPanel();
		namePanel.setBounds(0, 100, 1000, 50);
		add(namePanel);
		JLabel nameLabel = new JLabel("이   름 : ");
		nameLabel.setFont(ft);
		nameLabel.setBackground(Color.BLUE);

		JLabel nameField = new JLabel();
		// nameField.setText("입력된 고객이름");
		nameField.setText(AuthService.getCurrentUser().getName());
		nameField.setFont(ft);

		namePanel.add(nameLabel);
		namePanel.add(nameField);

		JPanel phonePanel = new JPanel();
		phonePanel.setBounds(0, 150, 1000, 100);
		add(phonePanel);
		JLabel phoneLabel = new JLabel("연락처 : ");
		phoneLabel.setFont(ft);
		JLabel phoneField = new JLabel();
		// phoneField.setText("입력된 고객 연락처");
		phoneField.setText(String.valueOf(AuthService.getCurrentUser().getPhone()));
		phoneField.setFont(ft);

		if (currentUser != null) {
			nameField.setText(currentUser.getName());
			// Person 클래스의 phone이 int 타입이므로 String으로 변환
			phoneField.setText(String.valueOf(currentUser.getPhone()));
		} else {
			// 로그인되지 않은 경우 처리
			nameField.setText("로그인 정보가 없습니다.");
			phoneField.setText("");
			// 사용자에게 로그인하라는 메시지를 보여주는 것도 좋음
			JLabel notLoggedInLabel = new JLabel("로그인이 필요합니다. 프로그램을 다시 시작하여 로그인해주세요.");
			notLoggedInLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
			notLoggedInLabel.setForeground(Color.RED);
			notLoggedInLabel.setBounds(50, 300, 900, 50); // 위치 조정 필요
			add(notLoggedInLabel);
		}

		phonePanel.add(phoneLabel);
		phonePanel.add(phoneField);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setBounds(0, 0, 1000, 750);
		frame.setLayout(null);

		JPanel mPagePanel = new JPanel();
		mPagePanel.setBounds(0, 150, 1000, 750);

		frame.add(mPagePanel);
		mPagePanel.add("고객 정보 확인하기", new GuestInfoPage(mPagePanel));
		frame.setVisible(true);
	}
}