package server.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

import beans.User;
import beans.UserFriend;
import beans.UserState;

public class UserDAOByMysql implements DAO<User, Integer> {
	private static Logger log = Logger.getLogger(UserDAOByMysql.class);

	/**
	 * ������û���
	 * 
	 * @param user
	 *            User�û�����
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public boolean add(User user) throws Exception {
		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "insert into User (qqnum, realname, nickname, sex, age, password, signature, email, photo, state, registerTime,onhours,rsapublic,rsaprivate) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			log.info(sql);
			qr.update(conn, sql, user.getQQnum(), user.getRealname(), user.getNickname(), user.getSex(), user.getAge(), user.getPassword(),
					user.getSignature(), user.getEmail(), user.getPhoto(), user.getState(), user.getRegisterTime(), 0, user.getRsapublic(),
					user.getRsaprivate());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return true;

	}

	/**
	 * ���ϵͳ����Ա
	 * 
	 * @param
	 *            ����Ա��qq����
	 */
	public void addSysUser(Integer qqnum) {
		// User user = new User();
		// user.setId(1);
		// user.setQQnum(qqnum);
		// user.setNickname("ϵͳ����Ա");
		// user.setRealname("Admin");
		// user.setPassword("admin");
		// user.setEmail("admin@163.com");
		// user.setAge(21);
		// user.setRegisterTime(new Date());
		// try {
		// add(user);
		// } catch (FileNotFoundException e) {
		// log.info("���ϵͳ����Աʱ��������:" + e.getMessage());
		// } catch (IOException e) {
		// log.info("���ϵͳ����Աʱ��������:" + e.getMessage());
		// }
		return;
	}

	/**
	 * ɾ���û�
	 * 
	 * @param user
	 *            User�û�����
	 */
	public boolean delete(User user) {
		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "delete from User where qqnum=?";
			log.info(sql);
			qr.update(conn, sql, user.getQQnum());

			sql = "delete from user_friends where selfid=?";
			log.info(sql);
			qr.update(conn, sql, user.getQQnum());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return true;

	}

	public boolean deleteUserFriend(int selfqq, int friendqq) {
		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);

			String sql = "delete from user_friends where selfid=? and friendid=?";
			Object[] ps = new Object[] { selfqq, friendqq };
			Object[] ps1 = new Object[] { friendqq, selfqq };
			log.info(sql + " : " + Arrays.toString(ps));
			qr.update(conn, sql, ps);
			log.info(sql + " : " + Arrays.toString(ps1));
			qr.update(conn, sql, ps1);

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return true;
	}

	/**
	 * ���������û���
	 * 
	 * @return ���ر���User�����Vector
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector<User> findAll() {
		Vector<User> v = new Vector<User>();
		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "select *  from User";
			log.info(sql);
			List<User> pset = (List) qr.query(conn, sql, new BeanListHandler(User.class));
			if (pset != null && !pset.isEmpty()) {
				for (User user : pset) {
					sql = "select * from user_friends where selfid=" + user.getQQnum();
					log.info(sql);
					List<UserFriend> flist = (List<UserFriend>) qr.query(conn, sql, new BeanListHandler(UserFriend.class));
					Vector<Integer> vv = new Vector<Integer>();
					if (flist != null && !flist.isEmpty()) {
						for (UserFriend uf : flist) {
							vv.add(uf.getFriendid());
						}
					}
					user.setListFriend(vv);
				}
			}
			v.addAll(pset);
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			DbUtils.closeQuietly(conn);
		}

		return v;
	}

	/**
	 * ��id�����û���
	 * 
	 * @param id
	 *            �û���id��
	 * @return ����User�û�����
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public User findById(Integer id) {

		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "select *  from User where qqnum=?";
			log.info(sql + ":" + id);
			User pset = (User) qr.query(conn, sql, new BeanHandler(User.class), id);
			if (pset == null) {
				return null;
			}
			sql = "select * from user_friends where selfid=" + id;
			log.info(sql);
			List<UserFriend> flist = (List<UserFriend>) qr.query(conn, sql, new BeanListHandler(UserFriend.class));
			Vector<Integer> v = new Vector<Integer>();
			if (flist != null && !flist.isEmpty()) {
				for (UserFriend uf : flist) {
					v.add(uf.getFriendid());
				}
			}
			pset.setListFriend(v);
			return pset;
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			DbUtils.closeQuietly(conn);
		}

		return null;
	}

	public User findByIdAndNickName(Integer id, String name) {

		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "select *  from User where qqnum=? and nickname=?";
			log.info(sql + ":" + id);
			User pset = (User) qr.query(conn, sql, new BeanHandler(User.class), id, name);
			if (pset == null) {
				return null;
			}
			sql = "select * from user_friends where selfid=" + id;
			log.info(sql);
			List<UserFriend> flist = (List<UserFriend>) qr.query(conn, sql, new BeanListHandler(UserFriend.class));
			Vector<Integer> v = new Vector<Integer>();
			if (flist != null && !flist.isEmpty()) {
				for (UserFriend uf : flist) {
					v.add(uf.getFriendid());
				}
			}
			pset.setListFriend(v);
			return pset;
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			DbUtils.closeQuietly(conn);
		}

		return null;
	}

	public List<User> findByNickName(String name) {

		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "select *  from User where nickname like '%" + name + "%'";
			List<User> pset = (List<User>) qr.query(conn, sql, new BeanListHandler(User.class));
			if (pset == null) {
				return null;
			}
			if (pset != null) {
				for (User user : pset) {
					sql = "select * from user_friends where selfid=" + user.getId();
					log.info(sql);
					List<UserFriend> flist = (List<UserFriend>) qr.query(conn, sql, new BeanListHandler(UserFriend.class));
					Vector<Integer> v = new Vector<Integer>();
					if (flist != null && !flist.isEmpty()) {
						for (UserFriend uf : flist) {
							v.add(uf.getFriendid());
						}
					}
					user.setListFriend(v);
				}
			}
			return pset;
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			DbUtils.closeQuietly(conn);
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public User findByNickname(String name) {

		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "select *  from User where nickname=?";
			log.info(sql + ":" + name);
			User pset = (User) qr.query(conn, sql, new BeanHandler(User.class), name);
			if (pset == null) {
				return null;
			}
			sql = "select * from user_friends where selfid=" + pset.getQQnum();
			log.info(sql);
			List<UserFriend> flist = (List<UserFriend>) qr.query(conn, sql, new BeanListHandler(UserFriend.class));
			Vector<Integer> v = new Vector<Integer>();
			if (flist != null && !flist.isEmpty()) {
				for (UserFriend uf : flist) {
					v.add(uf.getFriendid());
				}
			}
			pset.setListFriend(v);
			return pset;
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			DbUtils.closeQuietly(conn);
		}

		return null;
	}

	/**
	 * ���ǳƲ����û���
	 * 
	 * @param NickName
	 *            �û��ǳơ�
	 * @return ���ر���Use��Vector����
	 */
	public Vector<User> findUserByName(String NickName) {
		Vector<User> allUser = findAll();
		Vector<User> users = new Vector<User>();
		for (User user : allUser) {
			if (user.getNickname().indexOf(NickName) != -1)
				users.add(user);
		}
		return users;
	}

	/**
	 * ���ǳƲ��ҵ�ǰ���ߵ��û���
	 * 
	 * @param NickName
	 *            �ǳơ�
	 * @return ���ر���User��Vector����
	 */
	public Vector<User> findOnlineUserByName(String NickName) {
		Vector<User> allUser = findAll();
		Vector<User> users = new Vector<User>();
		for (User user : allUser) {
			if ((user.getState() == UserState.ONLINESTATE.getState() || user.getState() == UserState.BUSYSTATE.getState()
					|| user.getState() == UserState.DEPARTURESTATE.getState() || user.getState() == UserState.HIDDENSTATE.getState())
					&& user.getNickname().indexOf(NickName) != -1)
				users.add(user);
		}
		return users;
	}

	public boolean updateUser(User user) {
		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			if (user.getPassword() != null && !"".equals(user.getPassword().trim())) {
				String sql = "update User set realname=?, nickname=?, sex=?, age=?, password=?, signature=?,  photo=? where qqnum=?";
				log.info(sql);
				qr.update(conn, sql, user.getRealname(), user.getNickname(), user.getSex(), user.getAge(), user.getPassword(), user.getSignature(),
						user.getPhoto(), user.getQQnum());
			} else {
				String sql = "update User set realname=?, nickname=?, sex=?, age=?, signature=?,  photo=? where qqnum=?";
				log.info(sql);
				qr.update(conn, sql, user.getRealname(), user.getNickname(), user.getSex(), user.getAge(), user.getSignature(), user.getPhoto(),
						user.getQQnum());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return true;
	}

	/**
	 * �����û���
	 * 
	 * @param user
	 *            User�û�����
	 * @return ���³ɹ���
	 */
	public boolean update(User user) throws Exception {
		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "update User set realname=?, nickname=?, sex=?, age=?, password=?, signature=?, email=?, photo=?, state=?, registerTime=? where qqnum=?";
			log.info(sql);
			qr.update(conn, sql, user.getRealname(), user.getNickname(), user.getSex(), user.getAge(), user.getPassword(), user.getSignature(),
					user.getEmail(), user.getPhoto(), user.getState(), user.getRegisterTime(), user.getQQnum());

			Vector<Integer> v = user.getListFriend();
			if (v != null && !v.isEmpty()) {
				sql = "delete from user_friends where selfid=" + user.getQQnum();
				log.info(sql);
				qr.update(conn, sql);
				for (Integer fid : v) {
					sql = "insert into user_friends (selfid,friendid) values(?,?)";
					qr.update(conn, sql, user.getQQnum(), fid);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return true;
	}

	public boolean updateTime(User user, long time) throws Exception {
		Connection conn = null;
		try {
			conn = DB.getConntion();
			QueryRunner qr = new QueryRunner(true);
			String sql = "update User set onhours=onhours+? where qqnum=?";
			log.info(sql);
			qr.update(conn, sql, time, user.getQQnum());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return true;
	}

	/**
	 * ���ҵ�ǰ���������û���
	 * 
	 * @return ���ر�������User��Vector����
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Vector<User> findOnlineAll() throws FileNotFoundException, IOException, ClassNotFoundException {
		Vector<User> v = new Vector<User>();
		Vector<User> list = findAll();
		for (User user : list) {
			if (user.getState() == UserState.ONLINESTATE.getState() || user.getState() == UserState.BUSYSTATE.getState()
					|| user.getState() == UserState.DEPARTURESTATE.getState() || user.getState() == UserState.HIDDENSTATE.getState())
				v.add(user);
		}
		return v;
	}

	/**
	 * ���ص�ǰ���ߣ���״̬Ϊ���ߡ��뿪����æ���û���
	 * 
	 * @return ���ر�������User��Vector����
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Vector<User> findOnline() throws Exception {
		Vector<User> v = new Vector<User>();
		Vector<User> list = findAll();
		for (User user : list) {
			if (user.getState() == UserState.ONLINESTATE.getState() || user.getState() == UserState.BUSYSTATE.getState()
					|| user.getState() == UserState.DEPARTURESTATE.getState())
				v.add(user);
		}
		return v;
	}
}
