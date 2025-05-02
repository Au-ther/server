package server.pane;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class LogPane extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox comboBox = new JComboBox();
	private JButton btnLook = new JButton("查看日志");
	private JTextArea areaLog = new JTextArea();
	private FileInputStream fis;
	
	public LogPane() {
		init();
	}
	
	public JTextArea getAreaLog() {
		return areaLog;
	}

	/**
	 * 初始化面板。因为时间关系，可能写的比较戳，望见谅。
	 */
	private void init(){
		File files = new File("logs");
		if(files.exists()){
			Object[] obj = files.list();
			comboBox = new JComboBox(obj);
		}
		comboBox.addActionListener(this);
		btnLook.addActionListener(this);
		JPanel paneNorth = new JPanel();
		paneNorth.setLayout(new FlowLayout(FlowLayout.RIGHT));
		paneNorth.add(comboBox);
		paneNorth.add(btnLook);
		
		areaLog.setEditable(false);
		areaLog.setLineWrap(true);
		setLayout(new BorderLayout());
		add(paneNorth,BorderLayout.NORTH);
		add(new FillWidth(5,5),BorderLayout.EAST);
		add(new FillWidth(5,5),BorderLayout.WEST);
		add(new JScrollPane(areaLog),BorderLayout.CENTER);
	}
	
	/**
	 * 查看日志的事件
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnLook){
			Object obj = comboBox.getSelectedItem();
			if(obj==null)return;
			String name = obj.toString();
			try {
				fis = new FileInputStream("logs" + File.separator + name);
				byte[] b = new byte[2048];
				try {
					fis.read(b);
					areaLog.setText(new String(b));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
//			if(Desktop.isDesktopSupported()){
//				try {
//					
//					File files = new File("logs");
//					if(files.exists()){
//						System.out.println(files.getAbsolutePath());
//						Desktop desktop = Desktop.getDesktop();
//						for(File file:files.listFiles())
//							desktop.open(file);
//					}
//				} catch (IOException e1) {
//					System.out.println("打开文件时，发生错误:"+e1.getMessage());
//				} 
//			}else
//				JOptionPane.showMessageDialog(null, "不支持文件打开!");
		}
	}
}
