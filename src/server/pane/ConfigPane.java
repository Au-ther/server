
package server.pane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import server.pane.Server;
import util.GetParameter;

/**
 * 系统配置类。
 * 完成的功能:1.端口配置。
 * 2.要屏蔽的QQ号
 * 3.生成的QQ号的位数，最小位和最大位
 * 4.是否自动备份日志、自动删除日志的天数
 * 5.数据方式选择1.文件方式2.数据库方式(连接参数、用户名、密码、解码集)
 */
public class ConfigPane extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 系统启动的端口
     */
    private JLabel lblPort = new MyJLalel("端    口", 3608);
    private JTextField txtPort = new MyJTextField();
    /**
     * QQ要屏蔽申请的QQ号
     */
    private JLabel lblNoQQ = new MyJLalel("屏蔽号码", 90);
    private JTextArea areaNoQQ = new JTextArea();
    /**
     * 生成QQ的最小位数和最大位数
     */
    private JLabel lblMinDigit = new MyJLalel("最小位数", 90);
    private JTextField txtMinDigit = new MyJTextField();
    private JLabel lblMaxDigit = new MyJLalel("最小位数", 90);
    private JTextField txtMaxDigit = new MyJTextField();
    /**
     * 是否自动备份日志
     */
    private JLabel lblIsAutoBak = new MyJLalel("备份日志", 90);
    private JCheckBox cbIsAutoBak = new JCheckBox("自动备份日志", true);
    /**
     * 自动删除n天前的日志
     */
    private JLabel lblDelDay = new MyJLalel("自动删除", 90);
    private JTextField txtDelDay = new MyJTextField();
    /**
     * 采用数据的方式
     */
    private JLabel lblDataWay = new MyJLalel("数据方式", 90);
    private JRadioButton rbFile = new JRadioButton("文件方式");
    private JRadioButton rbData = new JRadioButton("数据库方式");

    private JLabel lblDriver = new MyJLalel("驱动程序", 90);
    private JTextField txtDriver = new MyJTextField();
    /**
     * 连接参数
     */
    private JLabel lblLinkParameter = new MyJLalel("连接参数", 90);
    private JTextField txtLinkParameter = new MyJTextField();
    /**
     * 用户名
     */
    private JLabel lblUserName = new MyJLalel("用 户 名", 90);
    private JTextField txtUserName = new MyJTextField();
    /**
     * 密码
     */
    private JLabel lblPassword = new MyJLalel("密    码", 90);
    private JTextField txtPassword = new MyJTextField();
    /**
     * 库名
     */
    private JLabel lblDBName = new MyJLalel("数据库名", 90);
    private JTextField txtDBName = new MyJTextField();
    /**
     * 解码集
     */
    private JLabel lblCharset = new MyJLalel("解 码 集", 90);
    private JTextField txtCharset = new MyJTextField();

    /**
     * 保存配置
     */
    private JButton btnSave = new JButton("保存配置");
    private JButton btnDefault = new JButton("恢复缺省值");

    public ConfigPane(Properties prop) {
        cbIsAutoBak.addActionListener(this);
        rbFile.addActionListener(this);
        rbData.addActionListener(this);
        btnDefault.addActionListener(this);
        btnSave.addActionListener(this);

        init();
        try {
            getProp(prop);
        } catch (Exception e) {
            System.out.println("错误:" + e.getMessage());
        }
    }


    private void init() {

        areaNoQQ.setLineWrap(true);
        JScrollPane sp = new JScrollPane(areaNoQQ);
        sp.setPreferredSize(new Dimension(170, 70));

        cbIsAutoBak.setPreferredSize(new Dimension(170, 20));

        ButtonGroup group = new ButtonGroup();
        group.add(rbData);
        group.add(rbFile);
        rbFile.setSelected(true);
        setDataWay(rbData.isSelected());
        rbData.setPreferredSize(new Dimension(85, 20));
        rbFile.setPreferredSize(new Dimension(75, 20));


        JPanel paneSet = new JPanel();
        paneSet.setPreferredSize(new Dimension(520, 220));
        paneSet.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 3));
        paneSet.setBorder(new TitledBorder(new LineBorder(Color.DARK_GRAY), " 服务器设置 "));
        paneSet.setFont(new Font("宋体", Font.PLAIN, 12));

        paneSet.add(lblPort);
        paneSet.add(txtPort);
        paneSet.add(new MyJLalel("服务器启动服务开启的端口"));
        paneSet.add(lblMinDigit);
        paneSet.add(txtMinDigit);
        paneSet.add(new MyJLalel("生成号最小位数"));
        paneSet.add(lblMaxDigit);
        paneSet.add(txtMaxDigit);
        paneSet.add(new MyJLalel("生成号最大位数"));
        paneSet.add(lblNoQQ);
        paneSet.add(sp);
        paneSet.add(new MyJLalel("要屏蔽的号，用分号[\";\"]分隔"));

        paneSet.add(lblIsAutoBak);
        paneSet.add(cbIsAutoBak);
        paneSet.add(new MyJLalel("是否备份日志"));
        paneSet.add(lblDelDay);
        paneSet.add(txtDelDay);
        paneSet.add(new MyJLalel("自动删除多少天前的日志"));

        JPanel paneDataWay = new JPanel();
        paneDataWay.setPreferredSize(new Dimension(520, 190));
        paneDataWay.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 3));
        paneDataWay.setBorder(new TitledBorder(new LineBorder(Color.DARK_GRAY), " 数据存储方式 "));

        paneDataWay.add(lblDataWay);
        paneDataWay.add(rbFile);
        paneDataWay.add(rbData);
        paneDataWay.add(new MyJLalel("数据保存采用的方式[数据库未实现]"));
        paneDataWay.add(lblDriver);
        paneDataWay.add(txtDriver);
        paneDataWay.add(new MyJLalel("数据库驱动程序包"));
        paneDataWay.add(lblLinkParameter);
        paneDataWay.add(txtLinkParameter);
        paneDataWay.add(new MyJLalel("数据库驱动程序连接参数"));
        paneDataWay.add(lblUserName);
        paneDataWay.add(txtUserName);
        paneDataWay.add(new MyJLalel("数据库de用户名  "));
        paneDataWay.add(lblPassword);
        paneDataWay.add(txtPassword);
        paneDataWay.add(new MyJLalel("数据库de密码"));
        paneDataWay.add(lblDBName);
        paneDataWay.add(txtDBName);
        paneDataWay.add(new MyJLalel("数据库de库名"));
        paneDataWay.add(lblCharset);
        paneDataWay.add(txtCharset);
        paneDataWay.add(new MyJLalel("连接数据库解码集"));

        JPanel paneBtn = new JPanel();
        paneBtn.setPreferredSize(new Dimension(520, 30));
        paneBtn.setLayout(new FlowLayout(FlowLayout.RIGHT, 50, 5));
        paneBtn.add(btnDefault);
        paneBtn.add(btnSave);

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        add(paneSet);
        add(paneDataWay);
        add(paneBtn);
    }

    /**
     * 保存按钮、回复缺省值按钮等的事件。
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cbIsAutoBak) {
            txtDelDay.setEditable(cbIsAutoBak.isSelected());
        }
        if (e.getSource() == rbFile)
            setDataWay(false);
        if (e.getSource() == rbData)
            setDataWay(true);
        if (e.getSource() == btnDefault) {
            try {
                GetParameter.setDefault();
                getProp(Server.prop);
            } catch (Exception e1) {
                System.out.println("错误:" + e1.getMessage());
            }
            //
        }
        if (e.getSource() == btnSave) {
            try {
                setProp();
            } catch (IOException e1) {
                System.out.println("错误:" + e1.getMessage());
            }
        }
    }

    private void setDataWay(boolean b) {
        txtDriver.setEditable(b);
        txtLinkParameter.setEditable(b);
        txtUserName.setEditable(b);
        txtPassword.setEditable(b);
        txtDBName.setEditable(b);
        txtCharset.setEditable(b);
    }

    private class MyJLalel extends JLabel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public MyJLalel(String text, int width) {
            super(text);
            setHorizontalAlignment(SwingConstants.RIGHT);
            setSize(width, 20);
            setPreferredSize(new Dimension(width, 20));
        }

        public MyJLalel(String text) {
            super(text);
            setHorizontalAlignment(SwingConstants.LEFT);
            setSize(200, 20);
            setPreferredSize(new Dimension(220, 20));
        }
    }

    private class MyJTextField extends JTextField {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public MyJTextField() {
            this(170);
        }

        public MyJTextField(int width) {
            super();
            setSize(width, 20);
            setPreferredSize(new Dimension(width, 20));
        }
    }

    /**
     * 获取系统的配置文件
     *
     * @param prop 配置文件映射成Properties对象。
     * @throws Exception
     */
    public void getProp(Properties prop) throws Exception {

        int len = GetParameter.keys.length;
        String[] values = new String[len];
        for (int i = 0; i < len; i++)
            values[i] = prop.getProperty(GetParameter.keys[i]);
        txtPort.setText(values[0]);
        txtMinDigit.setText(values[1]);
        txtMaxDigit.setText(values[2]);
        areaNoQQ.setText(values[3]);
        int isAutoBak = Integer.parseInt(values[4]);
        if (isAutoBak == 1)
            cbIsAutoBak.setSelected(true);
        else
            cbIsAutoBak.setSelected(false);
        txtDelDay.setText(values[5]);
        txtDelDay.setEditable(cbIsAutoBak.isSelected());

        int dataWay = Integer.parseInt(values[6]);
        if (dataWay == 0)
            rbFile.setSelected(true);

        txtDriver.setText(values[7]);
        txtLinkParameter.setText(values[8]);
        txtUserName.setText(values[9]);
        txtPassword.setText(values[10]);
        txtDBName.setText(values[11]);
        txtCharset.setText(values[12]);
        setDataWay(rbData.isSelected());
    }

    /**
     * 保存配置文件。
     *
     * @throws IOException
     */
    public void setProp() throws IOException {
        String values[] = {
                txtPort.getText(), txtMinDigit.getText(), txtMaxDigit.getText(), areaNoQQ.getText(),
                cbIsAutoBak.isSelected() ? "1" : "0", txtDelDay.getText(), rbFile.isSelected() ? "0" : "1",
                txtDriver.getText(), txtLinkParameter.getText(), txtUserName.getText(), txtPassword.getText(), txtDBName.getText(), txtCharset.getText()};
        GetParameter.saveProp(GetParameter.keys, values);
    }
}
