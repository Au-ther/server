package server;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.UIManager;

import server.dao.UserDAOByMysql;
import server.pane.Server;
import util.QQCreater;
import util.SetFont;

public class ServerStart {

	public final static int manager = 10000;

	public static void main(String[] args) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		Object key = null;
		Object value = null;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			value = UIManager.get(key);
			if (key instanceof String) {
				/**设置全局的背景色*/
				if (((String) key).endsWith(".background")) {
					UIManager.put(key, Color.LIGHT_GRAY);
				}
			}

			/**设置全局的字体*/
			if (value instanceof Font) {
				UIManager.put(key, new Font(Font.DIALOG, Font.PLAIN, 12));
			}
		}
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT,
					new BufferedInputStream(ServerStart.class.getResourceAsStream("/util/simsun.ttc")));
			font = font.deriveFont(Font.PLAIN, 12);
			SetFont.setFont(font);
		} catch (FontFormatException e) {
			System.out.println("错误:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("错误:" + e.getMessage());
		}
		new Server();
		new Thread() {
			public void run() {
				try {
					new UserDAOByMysql().addSysUser(manager);
					new QQCreater().saveIDJQ(1, manager);
				} catch (IOException e) {
					System.out.println("错误:" + e.getMessage());
				}
			}
		}.start();
	}

}
