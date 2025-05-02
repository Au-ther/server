package server.dao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import beans.Log;
import server.pane.Server;
import util.DateDeal;
import util.FileDeal;
import util.GetParameter;



public class LogDAOByFile implements DAO<Log,Integer> {

	private String path = "logs";
	private String suffixName = ".txt";
	
	/** 
	 * 添加一条新日志。
	 * @param log 日志。
	 * @throws IOException
	 * @return 添加成功否。
	 */
	public boolean add(Log log) throws IOException{
		File category = new File(path);
		if(!category.exists())
			category.mkdir();
		int delDay = Integer.parseInt(Server.prop.getProperty(GetParameter.keys[5]));
		deleteBeforeSomeDays(delDay);
		File file = new File(path+File.separator+DateDeal.getCurrentDate()+suffixName);
		PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file,true)));
		String xlog = "时间:"+DateDeal.getCurrentTime()+",用户:"+log.getNickname()+"["+log.getUserid()+"],IP:"+log.getIp()+",操作:"+log.getWhat()+"\n";
		pw.write(xlog);
		pw.flush();
		pw.close();
		return true;
	}
	
	/**
	 * 删除多少天前的日志。
	 * @param n 天数。
	 */
	public void deleteBeforeSomeDays(int n){
		File filePath = new File(path+File.separator);
		File[] xfile = filePath.listFiles();
		for(File file:xfile){
			if(FileDeal.isKindOFType(file, suffixName))
				if(file.lastModified()-System.currentTimeMillis()>n*24*60*60*1000)
					file.delete();
		}
	}
	
	/**
	 * 删除日志。
	 * @param log 日志。
	 * @throws Exception
	 * @return 无论如何都返回false，未作处理。
	 */
	public boolean delete(Log log) throws Exception {
		return false;
	}
	
	/**
	 * 所有日志。
	 * @throws Exception
	 * @return 无论如何都返回空，未作处理。
	 */
	public Vector<Log> findAll() throws Exception {
		return null;
	}
	
	
	/**
	 * 按id查找日志。
	 * @param id 日志id
	 * @throws Exception
	 * @return 无论如何都返回空，未作处理。
	 */
	public Log findById(Integer id) throws Exception {
		return null;
	}
	
	/**
	 * 更新日志。
	 * @param log 日志。
	 * @throws Exception
	 * @return 无论如何都返回false，未作处理。
	 */
	public boolean update(Log log) throws Exception {
		return false;
	}
}
