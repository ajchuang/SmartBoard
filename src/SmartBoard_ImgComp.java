// common imports
import java.awt.*;
import java.awt.image.BufferedImage;

public class SmartBoard_ImgComp extends SmartBoard_Component {
    
    BufferedImage m_img;
    int m_x;
    int m_y;

    public SmartBoard_ImgComp (BufferedImage img, int x, int y) {
        m_img = img;
        m_x = x;
        m_y = y;
    }
    
    public int getX () { return m_x; }
    public int getY () { return m_y; }
    public BufferedImage getImg () { return m_img; }
    
    @Override
    public void draw (Graphics2D g2d) {
        g2d.drawImage (m_img, null, m_x, m_y);
    }

}