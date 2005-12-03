import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;

import java.net.URL;

public class Starry extends JApplet {
    static String starryFile = "images/Starry.gif";

    public void init() {
	Image starry = getImage(getURL(starryFile));
	StarPanel starPanel = new StarPanel(starry);
	getContentPane().add(starPanel, BorderLayout.CENTER);
    }

    protected URL getURL(String filename) {
        URL codeBase = this.getCodeBase();
        URL url = null;

        try {
            url = new URL(codeBase, filename);
        } catch (java.net.MalformedURLException e) {
            System.out.println("Couldn't create image: "
                             + "badly specified URL");
            return null;
        }

        return url;
    }

    public static void main(String[] args) {
        Image starImage = Toolkit.getDefaultToolkit().getImage(
                                        Starry.starryFile);

        StarPanel starPanel = new StarPanel(starImage);

        JFrame f = new JFrame("Starry");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        f.getContentPane().add(starPanel, BorderLayout.CENTER);
        f.setSize(new Dimension(550, 200));
        f.setVisible(true);
    }
}

class StarPanel extends JPanel {

    Image img;
    int w, h;

    public StarPanel(Image img) {
	this.img = img;
    }

    public void paintComponent(Graphics g) {
	super.paintComponent(g);
        setBackground(Color.white);
        w = getSize().width;
        h = getSize().height;
        Graphics2D g2;
        g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        FontRenderContext frc = g2.getFontRenderContext();
	Font f = new Font("Helvetica", 1, w/10);
	String s = new String("The Starry Night");
        TextLayout textTl = new TextLayout(s, f, frc);
        AffineTransform transform = new AffineTransform();
	Shape outline = textTl.getOutline(null);
        Rectangle r = outline.getBounds();
        transform = g2.getTransform();
        transform.translate(w/2-(r.width/2), h/2+(r.height/2));
        g2.transform(transform);
	g2.setColor(Color.blue);
	g2.draw(outline);
        g2.setClip(outline);
        g2.drawImage(img, r.x, r.y, r.width, r.height, this);

    }
}
