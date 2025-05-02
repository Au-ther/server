package server.pane;

import java.awt.Dimension;

import javax.swing.JPanel;


public class FillWidth extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FillWidth(int width,int height) {
		setPreferredSize(new Dimension(width,height));
	}
}
