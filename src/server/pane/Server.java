package server.pane;

import java.awt.Toolkit;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
* JavaQQ服务端类。<br>
* 完成的功能：1.系统服务<br>
* 2.系统配置<br>
* 3.用户管理<br>
* 4.在线用户<br>
* 5.日志管理<br>
* 6.关于<br>
* 
*/
public class Server extends JFrame implements ChangeListener {
	public static final String	name							= "文件传输工具";
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;
	/** 选项卡 */
	private JTabbedPane					panes							= null;
	/** 系统服务面板 */
	private ServicePane					servicePane				= new ServicePane();
	/** 系统配置面板 */
	private ConfigPane					configPane				= null;
	/** 用户管理面板 */
	private UserPane						userPane					= new UserPane();
	/** 在线用户面板 */
	private OnlinePane					onlinePane				= new OnlinePane();
	/** 日志显示面板 */
	public static LogPane				logPane						= new LogPane();
	/** 属性配置文件 */
	public static Properties		prop							= null;

	public static boolean				isFileWay					= true;
	public static boolean				isSaveLog					= true;

	public Server() {
		servicePane.initProp();

		configPane = new ConfigPane(prop);
		setTitle(Server.name + "服务端");
		setSize(600, 520);
		setResizable(false);
		setIconImage(new ImageIcon("images/main.png").getImage());
		Toolkit tk = Toolkit.getDefaultToolkit();
		setLocation((tk.getScreenSize().width - getSize().width) / 2, (tk.getScreenSize().height - getSize().height) / 2);

		panes = new JTabbedPane();
		panes.add("系统服务", servicePane);
		//		panes.add("系统配置",configPane);
		panes.add("用户管理", userPane);
		panes.add("在线用户", onlinePane);
		panes.add("日志", logPane);

		add(panes);
		panes.addChangeListener(this);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * 选择用户管理、在线用户选项卡时出发查找所有用户和当前在线用户。
	 */
	public void stateChanged(ChangeEvent e) {
		if (panes.getSelectedComponent() == onlinePane)
			onlinePane.flushOnlineUser();
		if (panes.getSelectedComponent() == userPane)
			userPane.flushUser();
	}
}
