package page;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import member.AuthService;

import javax.swing.*;

public class AdminLoginDialog extends JDialog {

	JTextField pwField, idField;
	public boolean isLogin = false;

	public AdminLoginDialog(JFrame frame, String str) {
		super(frame, "관리자 로그인", true);

		Font ft;
		ft = new Font("맑은 고딕", Font.BOLD, 15);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - 400) / 2, (screenSize.height - 300) / 2);
		setSize(400, 300);
		setLayout(null);

		JPanel titlePanel = new JPanel();
		titlePanel.setBounds(0, 20, 400, 50);
		add(titlePanel);
		JLabel titleLabel = new JLabel("온라인 서점");
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		titlePanel.add(titleLabel);

		JPanel idPanel = new JPanel();
		idPanel.setBounds(0, 70, 400, 50);
		add(idPanel);
		JLabel idLabel = new JLabel("아 이 디 : ");
		idLabel.setFont(ft);
		idField = new JTextField(10);
		idField.setFont(ft);
		idPanel.add(idLabel);
		idPanel.add(idField);

		JPanel pwPanel = new JPanel();
		pwPanel.setBounds(0, 120, 400, 50);
		add(pwPanel);
		JLabel pwLabel = new JLabel("비밀번호 : ");
		pwLabel.setFont(ft);
		pwField = new JTextField(10);
		pwField.setFont(ft);
		pwPanel.add(pwLabel);
		pwPanel.add(pwField);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds(0, 170, 400, 50);
		add(buttonPanel);
		JLabel okLabel = new JLabel("확인");
		okLabel.setFont(ft);
		JButton okButton = new JButton();
		okButton.add(okLabel);
		buttonPanel.add(okButton);

		JLabel cancelLabel = new JLabel("취소");
		cancelLabel.setFont(ft);
		JButton cancelBtn = new JButton();
		cancelBtn.add(cancelLabel);
		buttonPanel.add(cancelBtn);

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String adminIdInput = idField.getText();
				String adminPasswordInput = pwField.getText(); // JTextField에서 가져오도록 수정 (만약 JPasswordField라면 new String(pwField.getPassword()))

				// Admin admin = new Admin("", -1); // 더 이상 사용 안 함

				if (AuthService.loginAdmin(adminIdInput, adminPasswordInput)) {
					isLogin = true;
					dispose();
				} else {
					JOptionPane.showMessageDialog(okButton, "관리자 정보가 일치하지 않거나 권한이 없습니다.");
				}
                /* // 기존 하드코딩된 로직 삭제
                System.out.println(pwField.getText() + idField.getText());
                System.out.println(admin.getId() + admin.getPassword());
                if (admin.getId().equals(idField.getText()) && admin.getPassword().equals(pwField.getText())) {
                    isLogin = true;
                    dispose();
                } else
                    JOptionPane.showMessageDialog(okButton, "관리자 정보가 일치하지 않습니다");
                */
			}
		});

		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isLogin = false;
				dispose();
			}
		});
	}
}