package ca.mcpnet.sample.appbundle;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ImageIcon imageIcon;
	
	public ImagePanel(String path) {
        java.net.URL imgURL = SampleApp.class.getResource(path);
        if (imgURL != null) {
            imageIcon = new ImageIcon(imgURL);
        } else {
        	throw new RuntimeException("Could not load :"+path);
        }
        setPreferredSize(new Dimension(imageIcon.getIconWidth(),imageIcon.getIconHeight()));
	}
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		imageIcon.paintIcon(this, g, 0, 0);
	}
}
