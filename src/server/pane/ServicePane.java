package server.pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import beans.FriendUser;
import beans.HistoryMessage;
import beans.Log;
import beans.LoginUser;
import beans.QQMessage;
import beans.Record;
import beans.RecordImage;
import beans.RegUser;
import beans.SendFileBean;
import beans.User;
import beans.UserState;
import server.dao.LogDAOByFile;
import server.dao.RecordDAOByFile;
import server.dao.RecordDAOByMysql;
import server.dao.UserDAOByMysql;
import util.Base64Utils;
import util.DateDeal;
import util.DateUtil;
import util.GetParameter;
import util.QQCreater;
import util.RSAUtils;

public class ServicePane extends JPanel implements ActionListener, Runnable {
	private static final long											serialVersionUID	= 1L;
	/** 启动QQ服务按钮 */
	private JButton																btnStart					= new JButton("启动" + Server.name + "服务");
	/** 停止QQ服务按钮 */
	private JButton																btnStop						= new JButton("停止" + Server.name + "服务");
	/**  */
	private ServecieProcessBar										bar								= new ServecieProcessBar(300, 30);
	/** 显示连接日志 */
	private JTextArea															areaLog						= new JTextArea();

	private ServerSocket													server						= null;

	public static Hashtable<Integer, ClientLink>	table							= null;

	@SuppressWarnings("unused")
	private Thread																thread;
	private static boolean												isServiceRun			= false;

	private PrintWriter														raf								= null;
	//	private String paths = "users";
	//	private String suffixName = ".dat";
	private String																path							= "日志.txt";

	//	private Record rec;

	public ServicePane() {
		try {
			raf = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File(path), true)));
		} catch (FileNotFoundException e) {
			areaLog.append("发生异常错误，请确保" + path + "文件可写!原因如下:" + e.getMessage());
			btnStart.setEnabled(false);
		}
		thread = new Thread(this);
		setLayout(new FlowLayout(FlowLayout.CENTER));

		areaLog.setEditable(false);
		areaLog.setLineWrap(true);
		bar.setPreferredSize(new Dimension(580, 27));
		btnStart.addActionListener(this);
		btnStop.addActionListener(this);
		btnStop.setEnabled(false);

		JPanel pane = new JPanel();
		pane.setPreferredSize(new Dimension(600, 70));
		pane.setLayout(new FlowLayout(FlowLayout.CENTER));
		pane.add(btnStart);
		pane.add(btnStop);
		pane.add(bar);

		setLayout(new BorderLayout());
		add(pane, BorderLayout.NORTH);
		add(new JScrollPane(areaLog), BorderLayout.CENTER);
		add(new FillWidth(4, 4), BorderLayout.WEST);
		add(new FillWidth(4, 4), BorderLayout.EAST);

	}

	/**
	 * 初始化配置文件。
	 */
	public void initProp() {
		try {
			Server.prop = GetParameter.getProp();
		} catch (Exception e) {
			writeSysLog(DateDeal.getCurrentTime() + ",加载配置文件时发生错误！原因如下:" + e.getMessage());
		}
		int dataWay = Integer.parseInt(Server.prop.getProperty(GetParameter.keys[6]));
		if (dataWay == 0)
			Server.isFileWay = true;
		else {
			Server.isFileWay = false;
			writeSysLog("注意:系统暂不支持数据库方式!请使用文件方式保存数据!");
		}
		int saveLog = Integer.parseInt(Server.prop.getProperty(GetParameter.keys[4]));
		if (saveLog == 1)
			Server.isSaveLog = true;
		else
			Server.isSaveLog = false;
	}

	/**
	 * 启动按钮、停止按钮的事件。
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			initProp();
			if (!Server.isFileWay) {
				JOptionPane.showMessageDialog(null, "暂不支持数据库存储方式!请选择文件方式!");
				return;
			}
			try {
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				bar.startRoll();
				startServer();
			} catch (IOException e1) {
				writeSysLog(DateDeal.getCurrentTime() + ",服务器启动服务时发生错误，原因如下:" + e1.getMessage());
			}
		}
		if (e.getSource() == btnStop) {
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
			bar.stopRoll();
			try {
				stopServer();
			} catch (IOException e1) {
				writeSysLog(DateDeal.getCurrentTime() + ",服务器停止服务时发生错误，原因如下:" + e1.getMessage());
			}
		}
	}

	/**
	 * 书写系统日志。
	 * 
	 * @param log
	 *            日志。
	 */
	public void writeSysLog(String log) {
		areaLog.append(log + "\n");
		// 滚动条自动下滚
		areaLog.setCaretPosition(areaLog.getDocument().getLength());
		raf.write(log + "\n");
		raf.flush();
	}

	/**
	 * 书写日志。
	 * 
	 * @param log
	 *            日志。
	 */
	private void writeLog(Log log) {
		try {
			LogDAOByFile LogDAO = new LogDAOByFile();
			LogDAO.add(log);
			Server.logPane.getAreaLog().append(log.toString() + "");
			Server.logPane.getAreaLog().setCaretPosition(Server.logPane.getAreaLog().getDocument().getLength());
		} catch (IOException e) {
			writeSysLog(DateDeal.getCurrentTime() + ",写入操作日志[" + log.toString() + "]时发生错误:" + e.getMessage());
		}
	}

	/**
	 * 启动服务器。
	 * 
	 * @throws IOException
	 *             IO异常。
	 */
	public void startServer() throws IOException {
		isServiceRun = true;
		int port = Integer.parseInt(Server.prop.getProperty(GetParameter.keys[0]));
		table = new Hashtable<Integer, ClientLink>();
		server = new ServerSocket(port);
		new Thread(this).start();
		writeSysLog(DateDeal.getCurrentTime() + ",服务器服务启动成功!等待用户上线...");
	}

	/**
	 * 停止服务器。
	 * 
	 * @throws IOException
	 *             IO异常。
	 */
	public void stopServer() throws IOException {
		isServiceRun = false;
		Enumeration<ClientLink> en = table.elements();
		while (en.hasMoreElements()) {
			ClientLink client = en.nextElement();
			client.updateUserState(client.qqnum, UserState.OFFLIENSTATE.getState(), client.getOnhours());
			client.letClientQuit();
		}
		table.clear();
		table = null;
		if (server != null)
			server.close();
		server = null;
		writeSysLog(DateDeal.getCurrentTime() + ",JQ服务器服务停止成功!");
	}

	// @Override
	public void run() {
		while (isServiceRun) {
			try {
				Socket client = server.accept();
				new Thread(new ClientLink(client)).start();
			} catch (IOException e) {
				writeSysLog(DateDeal.getCurrentTime() + ",JQ服务器接受客户端时发生异常:" + e.getMessage());
			}
		}
	}

	private class ClientLink implements Runnable, Serializable {
		private static final long	serialVersionUID	= 1L;
		public Socket							client						= null;
		public ObjectInputStream	ois								= null;
		public ObjectOutputStream	oos								= null;
		public int								qqnum							= -1;

		private long							logintime					= 0l;

		public ClientLink(Socket client) {

			this.client = client;
			writeSysLog(DateDeal.getCurrentTime() + ",客户端" + getClientIP() + "]连接到服务端");
			try {
				ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
				oos = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
			} catch (IOException e) {
				writeSysLog(DateDeal.getCurrentTime() + ",获取到客户端" + getClientIP() + "的连接发生错误:" + e.getMessage());
			}
		}

		/*
		 * 以1开头的：注册相关消息 10:客户端发送注册信息到服务端 11:服务端回复注册成功到客户端 12:服务端回复注册失败到客户端 以2开头的：登陆相关消息 20:客户端发送登陆信息到服务端（判断此用户是否已登录） 21:登陆成功服务端发送好友信息到客户端 22:登录失败服务端发送错误信息到客户端 23:服务端发送账号在别处登陆 24:客户端发送退出到服务端
		 * 25:服务端发送好友上线功能 26:客户端发送查看聊天记录请求 27:服务端发送聊天记录 以3开头的：发送记录相关消息 30:客户端发送消息到服务端 31:服务端根据消息发送到客户端 以4开头的：搜索在线用户加好友相关消息 40:客户端发送获取在线用户数 41:服务端发送在线用户数 42:客户端发送获取当前在线用户 43:服务端发送在线列表用户
		 * 44:客户端发送添加好友的qq号 45:服务端发送添加好友成功 46:服务端发送添加好友失败 以9开头的：系统相关消息 90:服务端发送下线功能到客户端 91:服务端发送广播消息到客户端
		 */

		// @Override
		public void run() {
			// TODO 处理客户端发送信息
			if (Server.isFileWay) {
				try {
					while (isServiceRun && ois != null && oos != null) {
						Object obj = ois.readObject();
						if (obj instanceof QQMessage) {
							QQMessage message = (QQMessage) obj;
							System.out.println("服务器接受到消息: " + message);
							int type = message.getType();
							if (type == 10) {
								dealRegiter(message);
								break;
							} else {
								switch (type) {
								case 20:
									DealLogin(message);
									break;
								case 24:
									dealQuit(message);
									break;
								case 26:
									findRecord(message);
									break;
								case 27:
									deleteFriend(message);
									break;
								case 30:
									dealMessage(message);
									// RecordDAOByFile rdf = new
									// RecordDAOByFile();
									// if (message.getObj() instanceof Record) {
									// rec = (Record) message.getObj();
									// rdf.add(rec);
									// }
									break;
								case 333:
									dealSoundMessage(message);
									break;
								case 40:
									dealFindOnlineUserNum();
									break;
								case 42:
									dealFindOnlineUser();
									break;
								case 44:
									dealAddUser(message);
									break;
								case 45:
									dealAddUserResult(message);
									break;
								case 47:
									dealFindUser(message);
									break;
								case 60:
									try {
										User user = (User) message.getObj();
										UserDAOByMysql dao = new UserDAOByMysql();
										boolean suc = dao.updateUser(user);
										if (suc) {
											User temp = dao.findById(user.getQQnum());
											QQMessage message2 = new QQMessage();
											message2.setType(45);
											message2.setObj(temp);
											writeToClient(message2);
										} else {
											QQMessage message2 = new QQMessage();
											message2.setType(45);
											message2.setObj(false);
											writeToClient(message2);
										}
									} catch (Exception e) {
										e.printStackTrace();
										QQMessage message2 = new QQMessage();
										message2.setType(45);
										message2.setObj(false);
										writeToClient(message2);
									}
									break;
								case 100:
									dealVedioMessage(message);
									break;
								case 101:
									dealVedioMessage(message);
									break;
								case 102:
									dealVedioMessage(message);
									break;
								case 103:
									dealVedioMessage(message);
									break;
								case 104:
									dealVedioMessage(message);
									break;
								case 105:
									dealVedioMessage(message);
									break;
								case 500:
									dealFileConnection(message);
									break;
								}
							}
						} else
							writeSysLog("客户端" + getClientIP() + "发送错误的数据信息到服务端");
					}
				} catch (IOException e) {
					removeClientForException(this);
					closeClient();
				} catch (Exception e) {
					removeClientForException(this);
					closeClient();
				}
			} else {
				writeSysLog("注意:系统暂不支持数据库方式!请使用文件方式保存数据!");
				letClientQuit();
			}
		}

		private void dealFileConnection(QQMessage message) {

			try {
				if (message.getObj() instanceof SendFileBean) {
					SendFileBean bean = (SendFileBean) message.getObj();
					bean.setFromip(client.getInetAddress().getHostAddress());
					int qqnum = bean.getQqnum();
					ClientLink link = table.get(qqnum);
					link.sendFileNotify(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void sendFileNotify(SendFileBean bean) {
			try {
				UserDAOByMysql dao = new UserDAOByMysql();
				User user = dao.findById(this.qqnum);
				bean.setFromUser(user);
				QQMessage retmsg = new QQMessage();
				retmsg.setType(500);
				retmsg.setObj(bean);
				writeToClient(retmsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void deleteFriend(QQMessage message) {
			int friendqq = 0;
			try {
				friendqq = (int) message.getObj();
				UserDAOByMysql dao = new UserDAOByMysql();
				boolean d = dao.deleteUserFriend(this.qqnum, friendqq);
				int type = 0;
				if (d) {
					type = 271;
				} else {
					type = 270;
				}
				QQMessage retmsg = new QQMessage();
				retmsg.setType(type);
				retmsg.setObj(friendqq);
				writeToClient(retmsg);
			} catch (Exception e) {
				e.printStackTrace();
				QQMessage retmsg = new QQMessage();
				retmsg.setType(270);
				retmsg.setObj(friendqq);
				writeToClient(retmsg);
			}
		}

		/**
		 * 查看聊天记录
		 */

		private void findRecord(QQMessage message) {
			int friendqq = (int) message.getObj();
			try {
				HistoryMessage hm = new HistoryMessage();
				User user = new UserDAOByMysql().findById(friendqq);
				hm.setFriendName(user.getNickname());
				List<Record> list = new RecordDAOByMysql().findRecord(this.qqnum, friendqq);
				hm.setList(list);

				QQMessage retmsg = new QQMessage();
				retmsg.setType(260);
				retmsg.setObj(hm);
				writeToClient(retmsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 获取客户端IP
		 * 
		 * @return 返回字符串的IP。
		 */
		private String getClientIP() {
			return client == null ? "[客户端已关闭,不能获取信息]" : "[" + client.getInetAddress().toString() + ":" + client.getPort() + "]";
		}

		/**
		 * 使得客户端下线。
		 */
		private void letClientQuit() {
			QQMessage message = new QQMessage();
			message.setType(90);
			message.setObj("和服务端断开连接");
			writeToClient(message);
			closeClient();
		}

		/**
		 * 关闭和客户端的连接
		 */
		private void closeClient() {
			String ip = getClientIP();
			writeSysLog(DateDeal.getCurrentTime() + ",客户端" + getClientIP() + "下线.");
			try {
				if (oos != null)
					oos.close();
				oos = null;
				if (ois != null)
					ois.close();
				ois = null;
				if (client != null)
					client.close();
				client = null;
			} catch (IOException e) {
				writeSysLog(DateDeal.getCurrentTime() + ",关闭到客户端" + ip + "的连接时时发生错误:" + e.getMessage());
			}
		}

		/**
		 * 处理客户端退出消息。
		 * 
		 * @param message
		 *            QQMessage对象。
		 */
		private void dealQuit(QQMessage message) {
			Object obj = message.getObj();
			if (obj instanceof FriendUser) {
				FriendUser user = (FriendUser) obj;
				writeLog(getLog(user, "用户退出"));
			}
			updateUserState(qqnum, UserState.OFFLIENSTATE.getState(), this.getOnhours());
			UserDAOByMysql userDAO = new UserDAOByMysql();
			try {
				telFriendState(userDAO.findById(qqnum));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
			table.remove(qqnum);
			closeClient();
		}

		/**
		 * TODO:添加好友
		 * 
		 * @param message
		 *            QQMessage对象。
		 */
		@SuppressWarnings("unchecked")
		private void dealAddUser(QQMessage message) {

			try {
				if (message.getObj() instanceof Integer) {
					int qqnum = (Integer) message.getObj();
					ClientLink link = table.get(qqnum);
					if (link == null) {
						QQMessage message2 = new QQMessage();
						message2.setType(440);
						message2.setObj(qqnum + "用户不在线,无法接受到请求!");
						writeToClient(message2);
						return;
					} else {
						UserDAOByMysql dao = new UserDAOByMysql();
						User user = dao.findById(this.qqnum);
						link.sendFriendRequest(user);
					}
				} else if (message.getObj() instanceof ArrayList) {
					List<Integer> list = (List<Integer>) message.getObj();
					for (int qqnum : list) {
						//int qqnum = (Integer) message.getObj();
						ClientLink link = table.get(qqnum);
						if (link == null) {
							QQMessage message2 = new QQMessage();
							message2.setType(440);
							message2.setObj(qqnum + "用户不在线,无法接受到请求!");
							writeToClient(message2);
							return;
						} else {
							UserDAOByMysql dao = new UserDAOByMysql();
							User user = dao.findById(this.qqnum);
							link.sendFriendRequest(user);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// TODO 完成好友请求结果
		private void dealAddUserResult(QQMessage message) {
			try {
				if (message.getObj() instanceof String) {
					String ret = (String) message.getObj();
					String[] tempary = ret.split(",");
					String option = tempary[0];
					int qqnum = Integer.valueOf(tempary[1]);

					QQMessage message2 = new QQMessage();
					if (option.equals("拒绝")) {
						message2.setType(91);
						String msg = this.qqnum + " 拒绝了您的好友请求!";
						message2.setObj("  系统消息:" + msg + "\n[该消息发布于" + DateDeal.getCurrentTime() + "]");
						ClientLink link = table.get(qqnum);
						if (link != null) {
							link.writeToClient(message2);
						}
						return;
					}

					boolean b = eachAddFriend(qqnum);
					String msg = null;
					if (b) {
						msg = "添加好友成功";
						UserDAOByMysql userDAO = new UserDAOByMysql();
						User user = userDAO.findById(qqnum);
						User self = userDAO.findById(this.qqnum);
						if (user != null)
							telfriendOnline(this.qqnum, user);
						if (self != null)
							telfriendOnline(qqnum, self);
					} else {
						msg = "添加好友失败";
					}
					message2.setType(91);
					message2.setObj("  系统消息:" + msg + "\n[该消息发布于" + DateDeal.getCurrentTime() + "]");
					ClientLink link = table.get(qqnum);
					if (link != null) {
						link.writeToClient(message2);
					}
					writeToClient(message2);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void sendFriendRequest(User user) {
			try {
				QQMessage message2 = new QQMessage();
				message2.setType(442);
				message2.setObj(user);
				writeToClient(message2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 告诉好友我的状态。
		 * 
		 * @param qqnum
		 *            qq号码。
		 * @param user
		 *            User对象。
		 */
		private void telfriendOnline(int qqnum, User user) {
			try {
				if (isHasLoged(qqnum)) {
					QQMessage message = new QQMessage();
					FriendUser friendUser = new FriendUser();
					friendUser.setQQnum(user.getQQnum());
					friendUser.setNickName(user.getNickname());
					friendUser.setPhoto(user.getPhoto());
					friendUser.setSignature(user.getSignature());
					friendUser.setState(user.getState());
					message.setType(25);
					message.setObj(friendUser);
					table.get(qqnum).writeToClient(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 相互添加好友。
		 * 
		 * @param qqnum
		 *            qq号码。
		 * @return 然会添加成功否。
		 */
		private boolean eachAddFriend(int qqnum) {
			UserDAOByMysql userDAO = new UserDAOByMysql();
			User user = userDAO.findById(qqnum);
			User self = userDAO.findById(this.qqnum);
			if (user != null && self != null) {
				Vector<Integer> v = user.getListFriend();
				if (!v.contains(this.qqnum)) {
					v.add(this.qqnum);
					user.setListFriend(v);
					writeLog(getLog(user, " 用户[" + user.getNickname() + "(" + user.getQQnum() + ")]添加好友[" + self.getNickname() + "(" + self.getQQnum() + ")]"));

					try {
						userDAO.update(user);
					} catch (FileNotFoundException e) {
						writeLog(getLog(user, " 用户[" + user.getNickname() + "(" + user.getQQnum() + ")]添加好友[" + self.getNickname() + "(" + self.getQQnum()
								+ ")]时发生错误:" + e.getMessage()));
						return false;
					} catch (Exception e) {
						writeLog(getLog(user, " 用户[" + user.getNickname() + "(" + user.getQQnum() + ")]添加好友[" + self.getNickname() + "(" + self.getQQnum()
								+ ")]时发生错误:" + e.getMessage()));
						return false;
					}
				}
				Vector<Integer> v2 = self.getListFriend();
				if (!v2.contains(qqnum)) {
					v2.add(qqnum);
					self.setListFriend(v2);
					writeLog(getLog(user, " 用户[" + self.getNickname() + "(" + self.getQQnum() + ")]添加好友[" + user.getNickname() + "(" + user.getQQnum() + ")]"));
					try {
						userDAO.update(self);
					} catch (FileNotFoundException e) {
						v.remove(this.qqnum);
						user.setListFriend(v);
						try {
							userDAO.update(user);
						} catch (Exception e1) {
						}
						writeLog(getLog(self, " 用户[" + self.getNickname() + "(" + self.getQQnum() + ")]添加好友[" + user.getNickname() + "(" + user.getQQnum()
								+ ")]时发生错误:" + e.getMessage()));
						return false;
					} catch (Exception e) {
						v.remove(this.qqnum);
						user.setListFriend(v);
						try {
							userDAO.update(user);
						} catch (Exception e1) {
						}
						writeLog(getLog(self, " 用户[" + self.getNickname() + "(" + self.getQQnum() + ")]添加好友[" + user.getNickname() + "(" + user.getQQnum()
								+ ")]时发生错误:" + e.getMessage()));
						return false;
					}
				}
				return true;
			} else
				return false;
		}

		/**
		 * 传输当前的在线用户FriendUser。
		 */
		private void dealFindOnlineUser() {
			UserDAOByMysql userDAO = new UserDAOByMysql();
			Enumeration<Integer> en = table.keys();
			while (en.hasMoreElements()) {
				int qqnum = en.nextElement();
				if (qqnum != this.qqnum) {
					User user = userDAO.findById(qqnum);
					if (user != null && !user.getListFriend().contains(this.qqnum)) {
						FriendUser friendUser = new FriendUser();
						friendUser.setQQnum(qqnum);
						friendUser.setPhoto(user.getPhoto());
						friendUser.setNickName(user.getNickname());
						friendUser.setSignature(user.getSignature());
						friendUser.setState(user.getState());
						QQMessage message = new QQMessage();
						message.setType(43);
						message.setObj(friendUser);
						writeToClient(message);
					}
				}
			}
		}

		/**
		 * 返回精确查找结果。
		 */
		// 按照QQ号查询
		private void dealFindUser(QQMessage msg) {

			try {
				String qm = (String) msg.getObj();
				int index = qm.indexOf(',');
				String idstr = qm.substring(0, index);
				String name = qm.substring(index + 1);
				UserDAOByMysql dao = new UserDAOByMysql();

				/////////////////////////////////////////
				//				Object obj = null;
				//				if (idstr != null && !"".equals(idstr.trim()) && !"".equals(name)) {
				//					int id = Integer.valueOf(idstr);
				//					obj = dao.findByIdAndNickName(id, name);
				//				} else if (idstr != null && !"".equals(idstr.trim())) {
				//					int id = Integer.valueOf(idstr);
				//					obj = dao.findById(id);
				//					
				//				} else {
				//					obj = dao.findByNickname(name);
				//				}
				//				if (obj instanceof User) {
				//					QQMessage message = new QQMessage();
				//					message.setType(48);
				//					message.setObj(obj);
				//					writeToClient(message);
				//				} else
				//					writeToClient(null);
				///////////////////////////////////////////////
				List<User> userlist = new ArrayList<User>();

				if (idstr != null && !"".equals(idstr.trim())) {
					int id = Integer.valueOf(idstr);
					User tempuser = dao.findById(id);
					userlist.add(tempuser);
				} else {
					List<User> templist = dao.findByNickName(name);
					if (templist != null && templist.size() > 0) {
						userlist.addAll(templist);
					}
				}

				QQMessage message = new QQMessage();
				message.setType(48);
				message.setObj(userlist);
				writeToClient(message);
			} catch (Exception e) {
				System.out.println("读取用户[" + msg + "]信息时错误:" + e.getMessage());
			}

		}

		// 按照亲昵查询
		@SuppressWarnings("unused")
		private void dealFindUser(String name) {
			UserDAOByMysql usf = new UserDAOByMysql();
			Vector<User> allUser = usf.findAll();
			Vector<User> users = new Vector<User>();
			for (User user : allUser) {
				if (user.getNickname().indexOf(name) != -1)
					users.add(user);
			}
			QQMessage message = new QQMessage();
			message.setType(49);
			message.setObj(users);
			writeToClient(message);
		}

		/**
		 * 传输当前在线用户数。
		 */
		private void dealFindOnlineUserNum() {
			QQMessage message = new QQMessage();
			message.setType(41);
			message.setObj(table.size());
			writeToClient(message);
		}

		private void dealSoundMessage(QQMessage message) {
			try {
				Record record = (Record) message.getObj();
				record.setSendTime(DateUtil.getCurrentTime(DateUtil.FULL));
				int qqnum = record.getToid();

				if (isHasLoged(qqnum)) {
					ClientLink client = table.get(qqnum);
					QQMessage msg = new QQMessage();
					msg.setType(333);
					msg.setObj(record);
					client.writeToClient(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		/**
		 * TODO: 处理客户端发送过来的消息。
		 * 
		 * @param message
		 *            发送过来的消息。
		 * @throws FileNotFoundException
		 *             发送给好友失败时抛出此异常。
		 * @throws IOException
		 *             发送给好友失败是抛出此异常。
		 */
		private void dealMessage(QQMessage message) {
			try {
				if (message.getObj() instanceof RecordImage) {
					RecordImage record = (RecordImage) message.getObj();
					record.setSendTime(DateUtil.getCurrentTime(DateUtil.FULL));
					int qqnum = record.getToid();
					Date date = new Date();
					if (isHasLoged(qqnum)) {
						ClientLink client = table.get(qqnum);
						sendRecordToClient(client, record);
						record.setReadTime(DateUtil.getCurrentTime(DateUtil.FULL));
						record.setRead(true);
					}
					try {
						record.setRead(true);
						record.setSendTime(DateUtil.getCurrentTime(DateUtil.FULL));
						RecordDAOByMysql dao = new RecordDAOByMysql();
						dao.add(record);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (message.getObj() instanceof Record) {

					Record record = (Record) message.getObj();

					//TODO 发送者私钥解密
//					System.out.println("服务器-接收到密文:\n" + record.getContent());
					UserDAOByMysql userdao = new UserDAOByMysql();
					User fuser = userdao.findById(record.getFromid());
					String rsadate = record.getContent();
					byte[] encodedData = Base64Utils.decode(rsadate);
					byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, fuser.getRsaprivate());
					String content = new String(decodedData);
					record.setContent(content);
//					System.out.println("服务器-发送者私钥解密:\n" + record.getContent());

					record.setSendTime(DateUtil.getCurrentTime(DateUtil.FULL));
					int qqnum = record.getToid();

					try {
						record.setRead(true);
						record.setSendTime(DateUtil.getCurrentTime(DateUtil.FULL));
						RecordDAOByMysql dao = new RecordDAOByMysql();
						dao.add(record);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (isHasLoged(qqnum)) {
						//接收者私钥加密
						userdao = new UserDAOByMysql();
						fuser = userdao.findById(record.getToid());

						encodedData = RSAUtils.encryptByPrivateKey(content.getBytes(), fuser.getRsaprivate());
						String rsadata = Base64Utils.encode(encodedData);
						record.setContent(rsadata);

						System.out.println("服务器-接收者私钥加密:\n" + record.getContent());
						ClientLink client = table.get(qqnum);
						sendRecordToClient(client, record);
						record.setReadTime(DateUtil.getCurrentTime(DateUtil.FULL));
						record.setRead(true);
					}
				} else
					writeSysLog("客户端" + getClientIP() + "发送错误的数据信息[" + message.getObj() + "]到服务端");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 发送消息到客户端。
		 * 
		 * @param client
		 *            发送的目标Socket
		 * @param record
		 *            发送的记录
		 * @throws FileNotFoundException
		 *             发送失败抛出此异常
		 * @throws IOException
		 *             发送失败抛出此异常
		 */
		private void sendRecordToClient(ClientLink client, Record record) throws Exception {
			QQMessage message = new QQMessage();
			message.setType(31);
			message.setObj(record);
			if (client.writeToClient(message)) {
				record.setRead(true);
				record.setReadTime(DateUtil.getCurrentTime(DateUtil.FULL));
			} else {
				record.setRead(false);
				RecordDAOByFile recordDAO = new RecordDAOByFile();
				recordDAO.addRecordForAdmin(record);
			}
		}

		/**
		 * 处理注册用户。
		 * 
		 * @param message
		 *            QQMessage对象。
		 * @throws FileNotFoundException
		 * @throws IOException
		 */
		private void dealRegiter(QQMessage message) throws Exception {
			if (message.getObj() instanceof User) {
				User user = (User) message.getObj();
				QQCreater creater = new QQCreater();
				int id = creater.createID();
				int num = creater.createJQ();
				creater.saveIDJQ(id, num);
				user.setId(id);
				user.setQQnum(num);
				user.setRegisterTime(DateUtil.getCurrentTime(DateUtil.FULL));
				UserDAOByMysql userDAO = new UserDAOByMysql();
				Object temp = userDAO.findByNickname(user.getNickname());
				QQMessage regResult = new QQMessage();
				if (temp != null) {
					regResult.setType(12);
					regResult.setObj("昵称已经被使用!");
					writeLog(getLog(user, "昵称已经被使用!"));
				} else {
					Map<String, Object> keyMap = RSAUtils.genKeyPair();
					String publicKey = RSAUtils.getPublicKey(keyMap);
					String privateKey = RSAUtils.getPrivateKey(keyMap);
					user.setRsapublic(publicKey);
					user.setRsaprivate(privateKey);
					boolean b = userDAO.add(user);
					// eachAddFriend(QTYServer.manager);
					if (b) {
						regResult.setType(11);
						RegUser regUser = new RegUser();
						regUser.setQQnum(user.getQQnum());
						regUser.setNickname(user.getNickname());
						regUser.setRealname(user.getRealname());
						regUser.setPassword(user.getPassword());
						regResult.setObj(regUser);
						writeLog(getLog(user, "新用户注册成功!"));
					} else {
						regResult.setType(12);
						regResult.setObj("用户注册失败!");
						writeLog(getLog(user, "用户注册失败!"));
					}
				}
				writeToClient(regResult);
			} else {
				writeSysLog("客户端" + getClientIP() + "发送错误的数据信息到服务端");
			}
			// closeClient();
		}

		/**
		 * 处理用户登陆。
		 * 
		 * @param message
		 *            QQMessage对象。
		 */
		private void DealLogin(QQMessage message) {
			// 用户登陆
			if (message.getObj() instanceof LoginUser) {
				LoginUser loginUser = (LoginUser) message.getObj();
				String inputPassword = loginUser.getPassword();
				int qqnum = loginUser.getQQnum();
				UserDAOByMysql userDAO = new UserDAOByMysql();
				try {
					User user = userDAO.findById(qqnum);
					QQMessage loginResult = new QQMessage();
					// 用户存在
					if (user != null) {
						if (user.getPassword().equals(inputPassword)) {
							this.qqnum = qqnum;
							// 正确登陆
							// 检测用户是否已经登陆，若已登陆，将之前的登陆用户下线。
							if (isHasLoged(qqnum))
								letClientLogout(qqnum, client.getInetAddress().toString());
							// 更改用户状态
							// System.out.println("状态:"+loginUser.getState());
							user.setState(loginUser.getState());
							userDAO.update(user);
							// 发送用户的好友的状态.
							loginResult.setType(21);
							Vector<FriendUser> friends = new Vector<FriendUser>();
							FriendUser SelfUser = new FriendUser();
							SelfUser.setQQnum(user.getQQnum());
							SelfUser.setNickName(user.getNickname());
							SelfUser.setPhoto(user.getPhoto());
							SelfUser.setSignature(user.getSignature());
							SelfUser.setState(user.getState());
							SelfUser.setAge(user.getAge());
							SelfUser.setSex(user.getSex());
							SelfUser.setEmail(user.getEmail());
							SelfUser.setTxtRealName(user.getRealname());
							SelfUser.setRsapublic(user.getRsapublic());
							friends.add(SelfUser);
							Vector<Integer> listFriend = user.getListFriend();
							for (int i = 0; i < listFriend.size(); i++) {
								Integer friend_jqnum = listFriend.get(i);
								User friend_user = userDAO.findById(friend_jqnum);
								if (friend_user != null) {
									FriendUser friendUser = new FriendUser();
									friendUser.setQQnum(friend_user.getQQnum());
									friendUser.setNickName(friend_user.getNickname());
									friendUser.setPhoto(friend_user.getPhoto());
									friendUser.setSignature(friend_user.getSignature());
									friendUser.setState(friend_user.getState());
									friendUser.setAge(friend_user.getAge());
									friendUser.setEmail(friend_user.getEmail());
									friendUser.setSex(friend_user.getSex());
									friendUser.setTxtRealName(friend_user.getRealname());
									friends.add(friendUser);
								}
							}
							loginResult.setObj(friends);
							// 向table中添加该用户
							if (writeToClient(loginResult)) {
								this.logintime = System.currentTimeMillis();
								table.put(user.getQQnum(), this);
							} else
								return;
							// 通知好友，我上线了
							writeLog(getLog(user, "用户登录"));
							telFriendState(user);
							// 检测是否存在好友的留言，发送过去给用户。
							RecordDAOByFile recordDAO = new RecordDAOByFile();
							Vector<Record> v = recordDAO.findLeaveRecord(qqnum);
							try {
								if (v != null) {
									for (Record record : v)
										sendRecordToClient(this, record);
									recordDAO.deleteRecordForAdmin(qqnum);
									// System.out.println(recordDAO.deleteRecordForAdmin(qqnum));
								}
							} catch (RuntimeException e) {
								writeLog(getLog(user, "发送留言给用户时发生错误:" + e.getMessage()));
							}
						} else {
							// 密码错误
							loginResult.setType(22);
							loginResult.setObj("错误的登陆密码[" + loginUser.getQQnum() + "]");
							writeToClient(loginResult);
							writeLog(getLoginLog(loginUser, "错误的用户[" + loginUser.getQQnum() + "]登录密码"));
							closeClient();
						}
					} else {
						// 不存在的用户.
						loginResult.setType(22);
						loginResult.setObj("不存在的用户[" + loginUser.getQQnum() + "]");
						writeToClient(loginResult);
						writeLog(getLoginLog(loginUser, "不存在的用户[" + loginUser.getQQnum() + "]登录"));
						closeClient();
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					writeSysLog("错误:" + e.getMessage());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					writeSysLog("错误:" + e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					writeSysLog("错误:" + e.getMessage());
				}
			} else {
				writeSysLog("客户端" + getClientIP() + "发送错误的数据信息到服务端");
				closeClient();
			}

		}

		/**
		 * 告诉当前用户的好友自己的状态。
		 * 
		 * @param user
		 *            当前用户.
		 * @throws FileNotFoundException
		 *             程序运行异常将抛出此异常。
		 * @throws IOException
		 *             程序运行异常将抛出此异常。
		 * @throws ClassNotFoundException
		 *             程序运行异常将抛出此异常。
		 */
		private void telFriendState(User user) throws FileNotFoundException, IOException, ClassNotFoundException {
			FriendUser meState = new FriendUser();
			meState.setQQnum(user.getQQnum());
			meState.setState(user.getState());
			meState.setNickName(user.getNickname());
			meState.setPhoto(user.getPhoto());
			meState.setSignature(user.getSignature());
			QQMessage friendLogin = new QQMessage();
			friendLogin.setType(25);
			friendLogin.setObj(meState);
			Vector<Integer> listFriend = user.getListFriend();
			for (int i = 0; i < listFriend.size(); i++) {
				Integer friend_jqnum = listFriend.get(i);
				if (isHasLoged(friend_jqnum)) {
					ClientLink client = table.get(friend_jqnum);
					client.writeToClient(friendLogin);
				}
			}
		}

		/**
		 * 向客户端发送消息。
		 * 
		 * @param message
		 *            QQMessage对象。
		 * @return 返回发送成功否。
		 */
		public boolean writeToClient(QQMessage message) {

			/*
			 * if(oos!=null){ try { oos.writeObject(message); oos.flush(); return true; } catch (IOException e) { writeSysLog("向客户端["+client. getLocalAddress().toString()+":"+client
			 * .getLocalPort()+"]发送数据失败!"); return false; } }else return false;
			 */
			new ClientWrite(this, message).start();
			return true;

		}

		/**
		 * 根据User的情况和日志内容获得日志对象。
		 * 
		 * @param user
		 *            User对象。
		 * @param what
		 *            日志内容。
		 * @return 返回日志对象。
		 */
		public Log getLog(User user, String what) {
			Log log = new Log();
			log.setUserid(user.getQQnum());
			log.setIp(client.getLocalAddress().toString());
			log.setNickname(user.getNickname());
			log.setTime(new Date());
			log.setUserid(user.getQQnum());
			log.setWhat(what);
			return log;
		}

		/**
		 * 根据FriendUser的情况和日志内容获得日志对象。
		 * 
		 * @param user
		 *            FriendUser对象。
		 * @param what
		 *            日志内容。
		 * @return 返回日志对象。
		 */
		public Log getLog(FriendUser user, String what) {
			Log log = new Log();
			log.setUserid(user.getQQnum());
			log.setIp(client.getLocalAddress().toString());
			log.setNickname(user.getNickName());
			log.setTime(new Date());
			log.setUserid(user.getQQnum());
			log.setWhat(what);
			return log;
		}

		/**
		 * 根据LoginUser和日志内容返回日志对象。
		 * 
		 * @param user
		 *            LoginUser对象。
		 * @param what
		 *            日志内容。
		 * @return 返回日志对象。
		 */
		private Log getLoginLog(LoginUser user, String what) {
			Log log = new Log();
			log.setNickname("未知用户");
			log.setUserid(user.getQQnum());
			log.setIp(client.getLocalAddress().toString());
			log.setTime(new Date());
			log.setWhat(what);
			return log;

		}

		/**
		 * 更新用户的状态。
		 * 
		 * @param qqnum
		 *            qq号码。
		 * @param state
		 *            用户的当前状态。
		 */
		private void updateUserState(int qqnum, int state, long time) {
			UserDAOByMysql userDAO = new UserDAOByMysql();
			User user = userDAO.findById(qqnum);
			if (user != null) {
				try {
					user.setState(state);
					userDAO.update(user);
					userDAO.updateTime(user, time);
				} catch (FileNotFoundException e) {
					writeLog(getLog(user, "更改用户状态时发生错误:" + e.getMessage()));
				} catch (Exception e) {
					writeLog(getLog(user, "更改用户状态时发生错误:" + e.getMessage()));
				}
			}
		}

		// 处理接收到的视频信号信息
		public void dealVedioMessage(QQMessage message) {

			if (message.getObj() instanceof Record) {
				Record record = (Record) message.getObj();
				int jqnum = record.getToid();
				if (isHasLoged(jqnum)) {
					ClientLink clients = table.get(jqnum);// 拿到好友的链接，通过好友链接把消息发给好友。。。
					String dd = client.getInetAddress().toString();
					record.setContent(dd.substring(1, dd.length()));
					try {
						if (message.getType() == 100) {
							sendVedioToClient(clients, record);
						} else if (message.getType() == 101) {
							sendSoundToClient(clients, record);
						} else if (message.getType() == 102) {
							sendStopSoundToClient(clients, record);
						} else if (message.getType() == 103) {
							sendVedioStopToClient(clients, record);
						} else if (message.getType() == 104) {
							sendFileToClient(clients, record);
						} else {
							sendVirationToClient(clients, record);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					record.setRead(false);
					record.setReadTime(DateUtil.getCurrentTime(DateUtil.FULL));
					record.setSendTime(DateUtil.getCurrentTime(DateUtil.FULL));
					RecordDAOByFile recordDAO = new RecordDAOByFile();
					try {
						recordDAO.addRecordForAdmin(record);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else
				writeSysLog("客户端" + getClientIP() + "发送错误的数据信息[" + message.getObj() + "]到服务端");
		}

		public long getOnhours() {
			long c = System.currentTimeMillis();
			long ret = c - this.logintime;
			ret = ret < 0 ? 0 : ret;
			return ret;
		}
	}

	private void sendVedioToClient(ClientLink client, Record record) throws FileNotFoundException, IOException {
		QQMessage message = new QQMessage();
		message.setType(100);
		message.setObj(record);
		if (client.writeToClient(message)) {
			record.setRead(true);
			// record.setReadTime(new Date());
		} else {
			record.setRead(false);
			RecordDAOByFile recordDAO = new RecordDAOByFile();
			recordDAO.addRecordForAdmin(record);
		}
	}

	private void sendSoundToClient(ClientLink client, Record record) throws FileNotFoundException, IOException {
		QQMessage message = new QQMessage();
		message.setType(101);
		message.setObj(record);
		if (client.writeToClient(message)) {
			record.setRead(true);
			// record.setReadTime(new Date());
		} else {
			record.setRead(false);
			RecordDAOByFile recordDAO = new RecordDAOByFile();
			recordDAO.addRecordForAdmin(record);
		}
	}

	private void sendStopSoundToClient(ClientLink client, Record record) throws FileNotFoundException, IOException {
		QQMessage message = new QQMessage();
		message.setType(102);
		message.setObj(record);
		if (client.writeToClient(message)) {
			record.setRead(true);
			// record.setReadTime(new Date());
		} else {
			record.setRead(false);
			RecordDAOByFile recordDAO = new RecordDAOByFile();
			recordDAO.addRecordForAdmin(record);
		}
	}

	private void sendVedioStopToClient(ClientLink client, Record record) throws FileNotFoundException, IOException {
		QQMessage message = new QQMessage();
		message.setType(103);
		message.setObj(record);
		if (client.writeToClient(message)) {
			record.setRead(true);
			// record.setReadTime(new Date());
		} else {
			record.setRead(false);
			RecordDAOByFile recordDAO = new RecordDAOByFile();
			recordDAO.addRecordForAdmin(record);
		}
	}

	private void sendFileToClient(ClientLink client, Record record) throws FileNotFoundException, IOException {
		QQMessage message = new QQMessage();
		message.setType(104);
		message.setObj(record);
		if (client.writeToClient(message)) {
			record.setRead(true);
			// record.setReadTime(new Date());
		} else {
			record.setRead(false);
			RecordDAOByFile recordDAO = new RecordDAOByFile();
			recordDAO.addRecordForAdmin(record);
		}
	}

	private void sendVirationToClient(ClientLink client, Record record) throws FileNotFoundException, IOException {
		QQMessage message = new QQMessage();
		message.setType(105);
		message.setObj(record);
		if (client.writeToClient(message)) {
			record.setRead(true);
			// record.setReadTime(new Date());
		} else {
			record.setRead(false);
			RecordDAOByFile recordDAO = new RecordDAOByFile();
			recordDAO.addRecordForAdmin(record);
		}
	}

	/**
	 * 客户端的输出线程类。
	 */
	private class ClientWrite extends Thread {

		private ClientLink	clientLink;
		private QQMessage		message;

		public ClientWrite(ClientLink clientLink, QQMessage message) {
			this.clientLink = clientLink;
			this.message = message;
		}

		public void run() {
			if (clientLink.oos != null) {
				try {
					clientLink.oos.writeObject(message);
					clientLink.oos.flush();
				} catch (Exception e) {
					e.printStackTrace();
					writeSysLog("向客户端" + clientLink.getClientIP() + "发送数据失败!");
				}
			}
		}
	}

	/**
	 * 检测qqnum用户是否已经登陆。
	 * 
	 * @param qqnum
	 * @return 返回用户是否登陆否。
	 */
	private boolean isHasLoged(int qqnum) {
		return table.containsKey(qqnum);
	}

	/**
	 * 断开和服务端的连接。
	 * 
	 * @param qqnum
	 * @param ip
	 */
	private void letClientLogout(int qqnum, String ip) {
		if (isHasLoged(qqnum)) {
			ClientLink clientLink = table.get(qqnum);
			QQMessage message = new QQMessage();
			message.setType(23);
			message.setObj("您的账号在别处[IP:" + ip + "]登录,程序将退出!");
			clientLink.writeToClient(message);
			table.remove(qqnum);
			clientLink.closeClient();
		}
	}

	/**
	 * 客户端异常发生和服务端断开连接时，清空table中断开的客户端~~，这是绝对的需要处理的。
	 * 
	 * @param client
	 */
	private void removeClientForException(ClientLink client) {
		if (client != null && table != null && table.contains(client)) {
			Enumeration<Integer> en = table.keys();
			while (en.hasMoreElements()) {
				Integer qqnum = en.nextElement();
				if (table.get(qqnum).equals(client) || table.get(qqnum) == client) {
					table.remove(qqnum);
					// 更改用户状态
					client.updateUserState(qqnum, UserState.OFFLIENSTATE.getState(), client.getOnhours());
					break;
				}
			}
		}
	}

	/**
	 * 广播系统消息。
	 * 
	 * @param msg
	 *            消息内容
	 * @return 返回广播成功否。
	 */
	public static boolean broadcast(String msg) {
		if (isServiceRun) {
			QQMessage message = new QQMessage();
			message.setType(91);
			message.setObj("  系统消息:" + msg + "\n[该消息发布于" + DateDeal.getCurrentTime() + "]");
			Enumeration<ClientLink> en = table.elements();
			while (en.hasMoreElements()) {
				en.nextElement().writeToClient(message);
			}
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "系统服务未启动，无法发送广播!");
			return false;
		}
	}

}
