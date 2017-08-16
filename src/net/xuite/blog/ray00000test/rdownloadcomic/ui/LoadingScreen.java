package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JWindow;

class LoadingScreen extends JWindow {
	Panel panel = new Panel();
	public LoadingScreen() {
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					dispose();
				}
			}
		});
		panel.setBackground(Color.YELLOW);
		add(panel);
	}
	class Panel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(new Font("Verdana", Font.BOLD, 24));
			g.drawString("Loading...", 270, 210);
		}
	}
	
	 public static void main(String[] args) {
		 LoadingScreen m = new LoadingScreen();
		    m.setSize(640, 480);
		    m.setLocationRelativeTo(null);
		    m.setVisible(true);
		  }
}
