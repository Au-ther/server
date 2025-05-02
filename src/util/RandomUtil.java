
package util;

import java.util.Random;


public class RandomUtil {

	/**
	 * 生成a-b的随机数
	 * @param a 整数a
	 * @param b 整数b
	 * @return a-b的随机数
	 */
	public static int randomInt(int a,int b){
		int t,n=0;
		if(a>b)
		{
			t=a;a=b;b=t;
		}
		t=(int)(Math.ceil(Math.log10(b)));
		while(true)
		{
			n=(int)(Math.random()*Math.pow(10,t));
			if(n>=a && n<=b)
				break;
		}
		return n;
	}
	
	/**
	 * 返回0-a的随机数。
	 * @param a 整数a。
	 * @return 返回0-a的随机数。
	 */
	public static int randomInt(int a){
		return new Random().nextInt(a);
	}
}
