package server.pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import beans.User;
import beans.UserSex;
import beans.UserState;
import server.dao.UserDAOByMysql;
import util.DateDeal;

public class UserPane extends JPanel implements ActionListener, Runnable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	/** 查询QQ用户 */
	private JLabel						lblQuery					= new JLabel("查询用户");
	/** 输入的查询的关键字 */
	private JTextField				txtQuery					= new JTextField("");
	/** 查询的类型 */
	private JComboBox					boxQuery					= new JComboBox();
	/** 查询 */
	private JButton						btnQuery					= new JButton("查询");
	/** 添加新用户 */
	private JButton						btnAddNew					= new JButton("添加新用户");
	/** 刷新重新获得所有用户 */
	private JButton						btnFlash					= new JButton("刷新");
	/** 显示用户的表格 */
	private JTable						table							= null;
	/** 表格的模型 */
	private DefaultTableModel	model							= null;
	/** 状态栏 */
	private JLabel						lblInfo						= new JLabel("共有用户：0");

	private JPopupMenu				popupMenu					= new JPopupMenu();
	private JMenuItem					itemChange				= new JMenuItem("修改");
	private JMenuItem					itemDelete				= new JMenuItem("删除");

	public UserPane() {
		init();
		btnAddNew.addActionListener(this);
		btnFlash.addActionListener(this);
		btnQuery.addActionListener(this);
	}

	private void init() {
		itemChange.addActionListener(this);
		itemDelete.addActionListener(this);
		popupMenu.add(itemChange);
		popupMenu.addSeparator();
		popupMenu.add(itemDelete);

		txtQuery.setPreferredSize(new Dimension(100, 25));
		boxQuery.addItem(new QueryWay("按号查询", 0));
		boxQuery.addItem(new QueryWay("按昵称查询", 1));

		model = new MyDefaultTableModel();
		model.addColumn("ID");
		model.addColumn("账号");
		model.addColumn("密码");
		model.addColumn("昵称");
		model.addColumn("姓名");
		model.addColumn("性别");
		model.addColumn("年龄");
		model.addColumn("E-mail");
		model.addColumn("状态");
		model.addColumn("在线时长");
		table = new JTable(model);
		table.addMouseListener(new TableMouseAdapter());
		table.addMouseMotionListener(new TableMouseAdapter());
		TableColumn tcID = table.getColumn(model.getColumnName(0));
		tcID.setPreferredWidth(25);
		tcID.setMaxWidth(25);
		tcID.setMinWidth(25);
		TableColumn tcQQ = table.getColumn(model.getColumnName(1));
		tcQQ.setPreferredWidth(70);
		tcQQ.setMaxWidth(80);
		tcQQ.setMinWidth(60);
		TableColumn tcPassWord = table.getColumn(model.getColumnName(2));
		tcPassWord.setPreferredWidth(70);
		tcPassWord.setMaxWidth(80);
		tcPassWord.setMinWidth(60);
		TableColumn tcNick = table.getColumn(model.getColumnName(3));
		tcNick.setPreferredWidth(70);
		tcNick.setMaxWidth(80);
		tcNick.setMinWidth(60);
		TableColumn tcName = table.getColumn(model.getColumnName(4));
		tcName.setPreferredWidth(60);
		tcName.setMaxWidth(65);
		tcName.setMinWidth(55);
		TableColumn tcSex = table.getColumn(model.getColumnName(5));
		tcSex.setPreferredWidth(30);
		tcSex.setMaxWidth(30);
		tcSex.setMinWidth(30);
		TableColumn tcAge = table.getColumn(model.getColumnName(6));
		tcAge.setPreferredWidth(30);
		tcAge.setMaxWidth(30);
		tcAge.setMinWidth(30);
		TableColumn tcState = table.getColumn(model.getColumnName(7));
		tcState.setPreferredWidth(15);
		tcState.setMaxWidth(30);
		tcState.setMinWidth(30);

		JPanel paneNorth = new JPanel();
		paneNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		paneNorth.add(lblQuery);
		paneNorth.add(txtQuery);
		paneNorth.add(boxQuery);
		paneNorth.add(btnQuery);
		paneNorth.add(btnAddNew);
		paneNorth.add(btnFlash);

		JPanel paneSouth = new JPanel();
		paneSouth.add(lblInfo);

		setLayout(new BorderLayout());
		add(paneNorth, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(new FillWidth(5, 5), BorderLayout.WEST);
		add(new FillWidth(5, 5), BorderLayout.EAST);
		add(paneSouth, BorderLayout.SOUTH);
	}

	/**
	 * 添加新用户、刷新、查询、修改、删除等按钮事件。
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAddNew) {
			System.out.println("添加用户");
			UserInfoFrame frame = new UserInfoFrame();
			frame.setVisible(true);
			// 添加用户
		}
		if (e.getSource() == btnFlash) {
			// 刷新用户
			flushUser();
		}
		if (e.getSource() == btnQuery) {
			// 查询用户
			QueryUser(txtQuery.getText(), ((QueryWay) boxQuery.getSelectedItem()).getWay());
		}
		if (e.getSource() == itemChange) {
			// 修改用户资料
			System.out.println("修改用户");
			UserDAOByMysql userDAO = new UserDAOByMysql();
			int[] rows = table.getSelectedRows();
			int qqnum = (Integer) table.getValueAt(rows[0], 1);
			User user = userDAO.findById(qqnum);
			UserInfoFrame frame = new UserInfoFrame(user);
			frame.setVisible(true);
		}
		if (e.getSource() == itemDelete) {
			// 删除用户
			int[] rows = table.getSelectedRows();
			UserDAOByMysql userDAO = new UserDAOByMysql();
			for (int i = 0; i < rows.length; i++) {
				int qqnum = (Integer) table.getValueAt(rows[i], 1);
				if (ServicePane.table == null || !ServicePane.table.containsKey(qqnum)) {
					User user = userDAO.findById(qqnum);
					if (user != null) {
						int n = JOptionPane.showConfirmDialog(null, "确认删除用户[" + user.getNickname() + "(" + user.getQQnum() + ")]", "确认删除?",
								JOptionPane.OK_CANCEL_OPTION);
						if (n == JOptionPane.OK_OPTION)
							userDAO.delete(user);
					}
				}
			}
			flushUser();
		}
	}

	private class QueryWay {
		private String	name;
		private int			way;

		public QueryWay(String name, int way) {
			this.name = name;
			this.way = way;
		}

		public String toString() {
			return this.name;
		}

		public int getWay() {
			return way;
		}
	}

	// @Override
	public void run() {
		btnFlash.setEnabled(false);
		if (Server.isFileWay) {
			table.setAutoCreateRowSorter(false);
			model.setRowCount(0);
			UserDAOByMysql userDAO = new UserDAOByMysql();
			Vector<User> users = userDAO.findAll();
			int totalNum = 0;
			int onlineNum = 0;
			int hiddenNum = 0;
			int departureNum = 0;
			int busyNum = 0;
			int offlineNum = 0;
			for (User user : users) {
				if (user != null) {
					totalNum++;
					if (user.getState() == UserState.ONLINESTATE.getState())
						onlineNum++;
					if (user.getState() == UserState.DEPARTURESTATE.getState())
						departureNum++;
					if (user.getState() == UserState.BUSYSTATE.getState())
						busyNum++;
					if (user.getState() == UserState.HIDDENSTATE.getState())
						hiddenNum++;
					if (user.getState() == UserState.OFFLIENSTATE.getState())
						offlineNum++;
				}
				addUserToTable(user);
			}
			table.setAutoCreateRowSorter(true);
			lblInfo.setText("共有用户:" + totalNum + "位.其中," + "在线用户:" + onlineNum + "位." + "隐身用户:" + hiddenNum + "位." + "离开用户:" + departureNum + "位."
					+ "繁忙用户:" + busyNum + "位." + "离线用户:" + offlineNum + "位.");
		} else
			lblInfo.setText("注意:系统暂不支持数据库方式!请使用文件方式保存数据!");
		btnFlash.setEnabled(true);
	}

	/**
	 * 刷新当前所有用户。
	 */
	public void flushUser() {
		new Thread(this).start();
	}

	/**
	 * 按查询方式、查询类型查找用户。
	 * 
	 * @param query
	 *            查询条件
	 * @param type
	 *            查询方式，0:按QQ号码查询，1：按昵称查询
	 */
	private void QueryUser(String query, int type) {
		if (query.equals(""))
			return;
		// 按jq号查询
		UserDAOByMysql userDAO = new UserDAOByMysql();
		model.setRowCount(0);
		if (type == 0) {
			try {
				int jqnum = Integer.parseInt(query);
				User user = userDAO.findById(jqnum);
				if (user != null)
					addUserToTable(user);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "请确保输入的正确的号码!");
				return;
			}
			return;
		}
		// 按昵称号查询
		if (type == 1) {
			Vector<User> users = userDAO.findUserByName(query);
			for (User user : users) {
				if (user != null)
					addUserToTable(user);
			}
			return;
		}

	}

	/**
	 * 向table中添加用户
	 * 
	 * @param user
	 *            User对象。
	 */
	private void addUserToTable(User user) {
		if (user != null) {
			Vector<Object> v = new Vector<Object>();
			v.add(user.getId());
			v.add(user.getQQnum());
			v.add(user.getPassword());
			v.add(user.getNickname());
			v.add(user.getRealname());
			v.add(UserSex.getSex(user.getSex()));
			v.add(user.getAge());
			v.add(user.getEmail());
			v.add(UserState.getStateName(user.getState()));
			String t = DateDeal.getGapTime(user.getOnhours());
			v.add(t);
			model.addRow(v);
		}
	}

	

	private class MyDefaultTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			super.isCellEditable(row, column);
			return false;
		}
	}

	private class TableMouseAdapter extends MouseAdapter {
		public void mouseEntered(MouseEvent e) {
			if (e.getSource() == table) {

			}

		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				popupMenu.show(table, e.getX(), e.getY());
			}
		}

		public void mouseMoved(MouseEvent e) {
			if (e.getSource() == table) {
				int row = table.rowAtPoint(e.getPoint());
				table.changeSelection(row, 0, false, true);
			}
		}
	}

}
