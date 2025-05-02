package server.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

import beans.Record;
import beans.RecordImage;

public class RecordDAOByMysql implements DAO<Record, Integer> {

	 
	
	public boolean add(Record record) throws Exception {
		Connection conn = null;
		try {
			conn = DB.getConnection();
			log.info(conn == null);
			QueryRunner qr = new QueryRunner(true);
			String sql = "insert into record (fromid, toid, fromName, toName, sendTime, readTime, isRead, content) values(?,?,?,?,?,?,?,?)";
			String isRead = record.isRead() ? "1" : "0";
			Object[] ps = new Object[] { record.getFromid(), record.getToid(), record.getFromName(), record.getToName(), record.getSendTime(),
					record.getReadTime(), isRead, record.getContent() };
			log.info(sql + " : " + Arrays.toString(ps));
			qr.update(conn, sql, ps);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return true;
	}

	private static Logger log = Logger.getLogger(RecordDAOByMysql.class);

	@SuppressWarnings("unchecked")
	public List<Record> findRecord(int fromqq, int toqq) {

		List<Record> list = new ArrayList<>();
		Connection conn = null;
		try {
			conn = DB.getConnection();
			QueryRunner qr = new QueryRunner(true);
			String sql = "select id, fromid, toid, (select nickname from user as tu where tu.qqnum=tr.fromid) fromName, (select nickname from user as tu where tu.qqnum=tr.toid) toName, sendTime, readTime, isRead, content from Record as tr where (fromid=? and toid=?) or (toid=? and fromid=?) order by sendTime";
			Object[] ps = new Object[] { fromqq, toqq, fromqq, toqq };
			log.info(sql + " : " + Arrays.toString(ps));
			list = (List<Record>) qr.query(conn, sql, new BeanListHandler(Record.class), ps);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return list;
	}

	public static void main(String[] args) {
		RecordDAOByMysql dao = new RecordDAOByMysql();
		List<Record> list = dao.findRecord(1, 2);
		log.info(list.get(0));
	}

	/**
	 * ����Ա���һ���¼�¼��
	 * 
	 * @param record
	 *            ��¼��
	 * @return ������ӳɹ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public boolean addRecordForAdmin(Record record) throws FileNotFoundException, IOException {

		return true;
	}

	/**
	 * ɾ��һ����¼��
	 * 
	 * @param record
	 *            Ҫɾ���ļ�¼��
	 * @return ����ɾ���ɹ���
	 */
	public boolean delete(Record record) throws Exception {

		return false;
	}

	/**
	 * �������м�¼��
	 * 
	 * @throws Exception
	 * @return ������ζ����ؿա�δ������
	 */
	public Vector<Record> findAll() throws Exception {
		return null;
	}

	/**
	 * ���Һ��ѷ��͵����Լ�¼��
	 * 
	 * @param QQ
	 *            ���û���QQ
	 * @return ���ظ��û������Լ�¼
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Vector<Record> findLeaveRecord(int jqnum) throws FileNotFoundException, IOException {

		return null;

	}

	/**
	 * ɾ������Ա�ļ�¼��
	 * 
	 * @param QQ
	 * @return ɾ���ɹ���
	 */
	public boolean deleteRecordForAdmin(int jqnum) {

		return false;
	}

	/**
	 * ��id���Ҽ�¼
	 * 
	 * @param id
	 *            ��¼��id
	 * @throws Exception
	 * @return ������ζ����ؿա�δ������
	 */
	public Record findById(Integer id) throws Exception {

		return null;
	}

	/**
	 * ���¼�¼��
	 * 
	 * @param record
	 *            �����µļ�¼��
	 * @throws Exception
	 * @return ������ζ�����false��δ������
	 */
	public boolean update(Record record) throws Exception {

		return false;
	}

}
