package server.pane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import beans.User;
import server.dao.UserDAOByMysql;
import util.DateUtil;

public class UserInfoFrame extends JFrame implements ActionListener {

	private JPanel						contentPane;
	private JTextField				qqnumF;
	private JTextField				realnameF;
	private JTextField				nicknameF;
	private JTextField				ageF;
	private JTextField				passwordF;
	private JTextField				emailF;
	private JComboBox<String>	sexBox;
	private JButton						submit;
	private User							user;

	public UserInfoFrame(User user) {
		this();
		this.user = user;
		qqnumF.setText(user.getQQnum() + "");
		qqnumF.setEditable(false);
		realnameF.setText(user.getRealname());
		nicknameF.setText(user.getNickname());
		ageF.setText(user.getAge() + "");
		passwordF.setText(user.getPassword());
		emailF.setText(user.getEmail());
		String sex = user.getSex() == 0 ? "ÄÐ" : "Å®";
		sexBox.getModel().setSelectedItem(sex);
	}

	/**
	 * Create the frame.
	 */
	public UserInfoFrame() {
		setTitle("\u7528\u6237\u4FE1\u606F");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 453, 516);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel qqnumL = new JLabel("\u8D26\u53F7:");
		qqnumL.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 13));
		qqnumL.setBounds(43, 24, 63, 16);
		contentPane.add(qqnumL);

		qqnumF = new JTextField();
		qqnumF.setBounds(165, 21, 154, 22);
		contentPane.add(qqnumF);
		qqnumF.setColumns(10);

		JLabel realnameL = new JLabel("\u59D3\u540D:");
		realnameL.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 13));
		realnameL.setBounds(43, 128, 63, 16);
		contentPane.add(realnameL);

		realnameF = new JTextField();
		realnameF.setBounds(165, 125, 154, 22);
		contentPane.add(realnameF);
		realnameF.setColumns(10);

		JLabel nicknameL = new JLabel("\u6635\u79F0:");
		nicknameL.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 13));
		nicknameL.setBounds(43, 176, 63, 16);
		contentPane.add(nicknameL);

		nicknameF = new JTextField();
		nicknameF.setBounds(165, 173, 154, 22);
		contentPane.add(nicknameF);
		nicknameF.setColumns(10);

		JLabel sexL = new JLabel("\u6027\u522B:");
		sexL.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 13));
		sexL.setBounds(43, 217, 63, 16);
		contentPane.add(sexL);

		sexBox = new JComboBox<String>();
		sexBox.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 13));
		sexBox.setModel(new DefaultComboBoxModel<String>(new String[] { "ÄÐ", "Å®" }));
		sexBox.setBounds(165, 214, 76, 22);
		contentPane.add(sexBox);

		JLabel ageL = new JLabel("\u5E74\u9F84:");
		ageL.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 13));
		ageL.setBounds(43, 262, 63, 16);
		contentPane.add(ageL);

		ageF = new JTextField();
		ageF.setBounds(165, 253, 154, 22);
		contentPane.add(ageF);
		ageF.setColumns(10);

		JLabel passwordL = new JLabel("\u5BC6\u7801:");
		passwordL.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 13));
		passwordL.setBounds(43, 67, 63, 16);
		contentPane.add(passwordL);

		passwordF = new JTextField();
		passwordF.setBounds(165, 64, 154, 22);
		contentPane.add(passwordF);
		passwordF.setColumns(10);

		JLabel emailL = new JLabel("\u90AE\u7BB1:");
		emailL.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 13));
		emailL.setBounds(43, 305, 63, 16);
		contentPane.add(emailL);

		emailF = new JTextField();
		emailF.setBounds(165, 302, 154, 22);
		contentPane.add(emailF);
		emailF.setColumns(10);

		submit = new JButton("\u63D0    \u4EA4");
		submit.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 13));
		submit.setBounds(165, 376, 103, 25);
		contentPane.add(submit);

		submit.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String qqnum = qqnumF.getText();
			String realname = realnameF.getText();
			String nickname = nicknameF.getText();
			int age = Integer.valueOf(ageF.getText());
			String password = passwordF.getText();
			String email = emailF.getText();
			String sextemp = sexBox.getModel().getSelectedItem().toString();
			int sex = sextemp.equals("ÄÐ") ? 0 : 1;
			int id = Integer.valueOf(qqnum);
			if (user != null) {
				UserDAOByMysql dao = new UserDAOByMysql();
				User temp = dao.findById(id);
				temp.setRealname(realname);
				temp.setNickname(nickname);
				temp.setAge(age);
				temp.setPassword(password);
				temp.setEmail(email);
				temp.setSex(sex);
				System.out.println(temp);
				dao.update(temp);
			} else {
				UserDAOByMysql dao = new UserDAOByMysql();
				User temp = new User();
				temp.setQQnum(id);
				temp.setRealname(realname);
				temp.setNickname(nickname);
				temp.setAge(age);
				temp.setPassword(password);
				temp.setEmail(email);
				temp.setSex(sex);
				temp.setRegisterTime(DateUtil.getCurrentTime(DateUtil.FULL));
				System.out.println(temp);
				dao.add(temp);
			}
			JOptionPane.showMessageDialog(null, "²Ù×÷³É¹¦", "ÌáÊ¾", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "²Ù×÷Ê§°Ü", "ÌáÊ¾", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}
}
