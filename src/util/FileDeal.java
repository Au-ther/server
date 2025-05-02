package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class FileDeal {

	/**
	 * 根据文件的后缀名判断文件是否属于某种类型的
	 * @param file 文件
	 * @param suffixName 后缀名
	 * @return 以该后缀名结束返回true，否则false
	 */
	public static boolean isKindOFType(File file,String suffixName){
		String name = file.getName();
		if(name.endsWith(suffixName))
			return true;
		else
			return false;
	}
	
	/**
	 * 加载BufferedImage对象。
	 * @param fileName 图像文件的路径。
	 * @return 返回加载后的图像。路径错误或发生未知错误时均返回空。
	 */
	public static BufferedImage getICON(String fileName){
		try {
			return ImageIO.read(FileDeal.class.getResource(fileName));
		} catch (IOException e) {
			System.out.println("加载图标["+fileName+"]文件时发生错误:"+e.getMessage());
			return null;
		}
	}
}
