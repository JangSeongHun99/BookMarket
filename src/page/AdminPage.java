package page;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import bookitem.Book; // Book 클래스 import
import bookitem.BookInIt; // BookInIt 클래스 import

public class AdminPage extends JPanel {
	JTextField idTextField; // ISBN 자동생성 대신 직접 입력받도록 변경 고려 또는 유지
	public AdminPage(JPanel panel) {

		Font ft;
		ft = new Font("맑은 고딕", Font.BOLD, 15);

		setLayout(null);

		Rectangle rect = panel.getBounds();
		System.out.println(rect);
		setPreferredSize(rect.getSize());

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
		String strDate = formatter.format(date);

		JPanel idPanel = new JPanel();
		idPanel.setBounds(100, 0, 700, 50);
		JLabel idLabel = new JLabel("도서ID : ");
		idLabel.setFont(ft);
		JLabel idTextField = new JLabel();
		idTextField.setFont(ft);
		idTextField.setPreferredSize(new Dimension(290, 50));
		idTextField.setText("ISBN" + strDate);
		idPanel.add(idLabel);
		idPanel.add(idTextField);
		add(idPanel);

		JPanel namePanel = new JPanel();
		namePanel.setBounds(100, 50, 700, 50);
		JLabel nameLabel = new JLabel("도서명 : ");
		nameLabel.setFont(ft);
		JTextField nameTextField = new JTextField(20);
		nameTextField.setFont(ft);
		namePanel.add(nameLabel);
		namePanel.add(nameTextField);
		add(namePanel);

		JPanel pricePanel = new JPanel();
		pricePanel.setBounds(100, 100, 700, 50);
		JLabel priceLabel = new JLabel("가   격 : ");
		priceLabel.setFont(ft);
		JTextField priceTextField = new JTextField(20);
		priceTextField.setFont(ft);
		pricePanel.add(priceLabel);
		pricePanel.add(priceTextField);
		add(pricePanel);

		JPanel authorPanel = new JPanel();
		authorPanel.setBounds(100, 150, 700, 50);
		JLabel authorLabel = new JLabel("저   자 : ");
		authorLabel.setFont(ft);
		JTextField authorTextField = new JTextField(20);
		authorTextField.setFont(ft);
		authorPanel.add(authorLabel);
		authorPanel.add(authorTextField);
		add(authorPanel);

		JPanel descPanel = new JPanel();
		descPanel.setBounds(100, 200, 700, 50);
		JLabel descLabel = new JLabel("설   명 : ");
		descLabel.setFont(ft);
		JTextField descTextField = new JTextField(20);
		descTextField.setFont(ft);
		descPanel.add(descLabel);
		descPanel.add(descTextField);
		add(descPanel);

		JPanel categoryPanel = new JPanel();
		categoryPanel.setBounds(100, 250, 700, 50);
		JLabel categoryLabel = new JLabel("분   야 : ");
		categoryLabel.setFont(ft);
		JTextField categoryTextField = new JTextField(20);
		categoryTextField.setFont(ft);
		categoryPanel.add(categoryLabel);
		categoryPanel.add(categoryTextField);
		add(categoryPanel);

		JPanel datePanel = new JPanel();
		datePanel.setBounds(100, 300, 700, 50);
		JLabel dateLabel = new JLabel("출판일 : ");
		dateLabel.setFont(ft);
		JTextField dateTextField = new JTextField(20);
		dateTextField.setFont(ft);
		datePanel.add(dateLabel);
		datePanel.add(dateTextField);
		add(datePanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds(100, 350, 700, 50);
		add(buttonPanel);
		JLabel okLabel = new JLabel("추가");
		okLabel.setFont(ft);
		JButton okButton = new JButton();
		okButton.add(okLabel);
		buttonPanel.add(okButton);



		okButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				// String[] writeBook = new String[7]; // 더 이상 사용 안 함

				// 입력 값 유효성 검사 (가격은 숫자인지 등) 추가 권장
				String bookId = idTextField.getText(); // 자동 생성된 ID 또는 관리자 입력 ID
				String name = nameTextField.getText();
				int unitPrice;
				try {
					unitPrice = Integer.parseInt(priceTextField.getText());
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(okButton, "가격은 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String author = authorTextField.getText();
				String description = descTextField.getText();
				String category = categoryTextField.getText();
				String releaseDate = dateTextField.getText();

				Book newBook = new Book(bookId, name, unitPrice, author, description, category, releaseDate);

				if (BookInIt.addBook(newBook)) { // BookInIt을 통해 DAO 호출
					JOptionPane.showMessageDialog(okButton, "새 도서 정보가 저장되었습니다");

					// 입력 필드 초기화
					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
					String strDate = formatter.format(date);
					// idTextField.setText("ISBN" + strDate); // 다음 ISBN 자동 생성 (만약 JLabel이라면)
					// 만약 idTextField가 JTextField이고 관리자 입력이라면, 이 부분은 필요 없음

					nameTextField.setText("");
					priceTextField.setText("");
					authorTextField.setText("");
					descTextField.setText("");
					categoryTextField.setText("");
					dateTextField.setText("");

					System.out.println("새 도서 정보가 저장되었습니다.");
				} else {
					JOptionPane.showMessageDialog(okButton, "도서 정보 저장에 실패했습니다.", "저장 오류", JOptionPane.ERROR_MESSAGE);
				}
                /* // 파일 저장 로직 삭제
                try {
                    FileWriter fw = new FileWriter("book.txt", true);
                    // ...
                    fw.close();
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                */
			}
		});

		JLabel noLabel = new JLabel("취소");
		noLabel.setFont(ft);
		JButton noButton = new JButton();
		noButton.add(noLabel);
		buttonPanel.add(noButton);

		noButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				nameTextField.setText("");
				priceTextField.setText("");
				authorTextField.setText("");
				descTextField.setText("");
				categoryTextField.setText("");
				dateTextField.setText("");
			}
		});
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setBounds(0, 0, 1000, 750);
		frame.setLayout(null);

		JPanel mPagePanel = new JPanel();
		mPagePanel.setBounds(0, 150, 1000, 750);

		frame.add(mPagePanel);
		mPagePanel.add("주문하기", new AdminPage(mPagePanel));
		frame.setVisible(true);
	}
}