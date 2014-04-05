// common imports
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SmartBoard_AppPanel extends JPanel {
    
    Vector<SmartBoard_Component> m_strokes;
    
    public SmartBoard_AppPanel () {
        m_strokes = new Vector<SmartBoard_Component> (); 
    }
    
    @Override
    protected void paintComponent (Graphics g) {
        
        super.paintComponent (g);
        
        g.setColor (new Color (59, 101, 61));
        g.fillRect (0, 0, this.getWidth (), this.getHeight ());
         
        Graphics2D g2d = (Graphics2D) g.create();

        for (SmartBoard_Component ss: m_strokes) {
            
            if (ss instanceof SmartBoard_Stroke) { 
                SmartBoard_Stroke i = (SmartBoard_Stroke)ss;
                g2d.setStroke (new BasicStroke (i.getWidth (), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine (i.getX_Begin (), i.getY_Begin (), i.getX_End (), i.getY_End ());
            } else if (ss instanceof SmartBoard_ImgComp) {
                SmartBoard_ImgComp x = (SmartBoard_ImgComp)ss;
                g2d.drawImage (x.getImg (), null, x.getX (), x.getY ());
            }
        }

        g2d.dispose();
    }
    
    public void drawLine (int x_begin, int y_begin, int x_end, int y_end, int width, int color) {
        
        SmartBoard_Stroke newStroke = new SmartBoard_Stroke (x_begin, y_begin, x_end, y_end, width);
        m_strokes.add (newStroke);
    }
    
    public void drawImage (BufferedImage img, int x, int y) {
        
        SmartBoard_ImgComp newImg = new SmartBoard_ImgComp (img, x, y);
        m_strokes.add (newImg);
    }
    
    public void clearPanel () {
        m_strokes.clear ();
    }

}