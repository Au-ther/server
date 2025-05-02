package server.pane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;

public class ServecieProcessBar extends JLabel implements ActionListener {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private Timer							timer							= null;

	@SuppressWarnings("unused")
	private boolean						isPaintLeft				= true;

	/** 渐变矩形的宽度 */
	private int								width							= 0;
	/** 渐变矩形的高度 */
	private int								height						= 0;
	/** 渐变矩形的水平位置 */
	private int								nowX							= 0;
	/** 渐变举行的垂直位置 */
	private int								nowY							= 0;
	/** 是否从左边往右边移动 */
	private boolean						isLeft						= true;
	/** 是否开始滚动 */
	private boolean						isBeginRoll				= false;

	public ServecieProcessBar(int width, int height) {
		setBackground(Color.WHITE);
		timer = new Timer(16, this);

		this.width = width * 3 / 4;
		this.height = height;
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));

	}

	public void actionPerformed(ActionEvent e) {
		if (isLeft) {
			if (nowX <= getWidth())
				nowX += 3;
			else
				isLeft = false;
		} else {
			if (nowX >= -1 * width)
				nowX -= 3;
			else
				isLeft = true;
		}
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		//super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		Color c = g2.getColor();
		if (isBeginRoll) {
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());

			if (isLeft)
				g2.setPaint(new GradientPaint(nowX, 0, Color.WHITE, nowX + width, 0, new Color(110, 132, 224, 200)));
			else
				g2.setPaint(new GradientPaint(nowX, 0, new Color(110, 132, 224, 200), nowX + width, 0, Color.WHITE));
			g2.fillRect(nowX, nowY, width, height);
			g2.setColor(Color.BLACK);
			g2.drawString(Server.name + "服务运行中...", (getWidth() - 50) / 2, (getHeight() + 4) / 2);
			g2.setColor(Color.WHITE);
			if (isLeft)
				g2.fillRect(0, 0, 3, getHeight());
			else
				g2.fillRect(getWidth() - 3, 0, 3, getHeight());

		} else {
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
		g2.setColor(c);
	}

	/**
	 * 开始滚动滚动条
	 */
	public void startRoll() {
		nowX = -1 * this.width;
		nowY = 0;
		isBeginRoll = true;
		timer.start();
	}

	/**
	 * 停止滚动滚动条。
	 */
	public void stopRoll() {
		isBeginRoll = false;
		timer.stop();
		repaint();
	}
}
