package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class GetParameter {

	/**
	 * 属性文件的名称。
	 */
	public static String[] keys = {"Port",
									"MinDigit",
									"MaxDigit",
									"ShieldJQ",
									"IsBak",
									"DeleteDays",
									"DataWay",
									"Driver",
									"LinkParameter",
									"UserName",
									"Password",
									"DBName",
									"Charset"};
	/**
	 * 属性文件的缺省值。
	 */
	public static String[] values = {"3608",
										"5",
										"9",
										"10000;88888",
										"1",
										"5",
										"0",
										"com.mysql.jdbc.Driver",
										"jdbc:mysql://127.0.0.1:3306/",
										"root",
										"123",
										"javaqq",
										"gb2312"};
	/**
	 * 保存属性文件的名称。
	 */
	private static String path = "server.properties";
	
	/**
	 * 获得属性文件。
	 * @return 返回属性文件的Properties对象。
	 * @throws Exception 加载属性文件错误时抛出此异常。
	 */
	public static Properties getProp() throws Exception{
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			setDefault();
			prop.load(new FileInputStream(new File(path)));
		} catch (IOException e) {
			setDefault();
			prop.load(new FileInputStream(new File(path)));
		}
		return prop;
	}
	
	/**
	 * 保存属性文件。
	 * @param keys 属性键
	 * @param values 属性值
	 * @return 返回保存成功否
	 * @throws IOException 
	 */
	public static boolean saveProp(String[] keys,String[] values) throws IOException{
		if(keys.length==values.length){
			Properties prop = new Properties();
			FileOutputStream fos = new FileOutputStream(new File(path));
			for(int i=0;i<keys.length;i++)
				prop.setProperty(keys[i], values[i]);
			prop.store(fos, "slandi@hk");
			fos.flush();
			fos.close();
		}else
			return false;
		return true;
	}
	
	/**
	 * 回复缺省值
	 * @return 返回回复缺省值成功否
	 * @throws IOException
	 */
	public static boolean setDefault() throws IOException{
		return saveProp(keys, values);
	}
	
	/**
	 * 取得缺省值的配置文件。
	 * @return 返回Properties对象。
	 * @throws IOException
	 */
	public static Properties getDefault() throws IOException{
		Properties prop = new Properties();
		for(int i=0;i<keys.length;i++)
			prop.setProperty(keys[i], values[i]);
		return prop;
	}
}
