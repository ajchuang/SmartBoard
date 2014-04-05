// common imports
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SmartBoard_AppPanel extends JPanel {
    
    Stack<SmartBoard_Component> m_strokes;
    Stack<SmartBoard_Component> m_undoStrokes;
    
    final static Color m_chalkboardGreen;
    
    static {
        m_chalkboardGreen = new Color (59, 101, 61); 
    }
    
    public SmartBoard_AppPanel () {
        m_strokes = new Stack<SmartBoard_Component> (); 
        m_undoStrokes = new Stack<SmartBoard_Component> ();
    }
    
    @Override
    protected void paintComponent (Graphics g) {
        
        super.paintComponent (g);
        
        g.setColor (m_chalkboardGreen);
        g.fillRect (0, 0, this.getWidth (), this.getHeight ());
         
        Graphics2D g2d = (Graphics2D) g.create();

        for (SmartBoard_Component ss: m_strokes) {
            
            ss.draw (g2d);
            
            /*
            if (ss instanceof SmartBoard_Stroke) { 
                
                SmartBoard_Stroke i = (SmartBoard_Stroke)ss;
                g2d.setStroke (new BasicStroke (i.getWidth (), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine (i.getX_Begin (), i.getY_Begin (), i.getX_End (), i.getY_End ());
                
            } else if (ss instanceof SmartBoard_ImgComp) {
                
                SmartBoard_ImgComp x = (SmartBoard_ImgComp)ss;
                g2d.drawImage (x.getImg (), null, x.getX (), x.getY ());
                
            }
            */
        }

        g2d.dispose();
    }
    
    public void drawLine (int x_begin, int y_begin, int x_end, int y_end, int width, int color) {
        
        SmartBoard_LineComp newStroke = new SmartBoard_LineComp (x_begin, y_begin, x_end, y_end, width);
        m_strokes.push (newStroke);
        m_undoStrokes.clear ();
    }
    
    public void drawImage (BufferedImage img, int x, int y) {
        
        SmartBoard_ImgComp newImg = new SmartBoard_ImgComp (img, x, y);
        m_strokes.push (newImg);
        m_undoStrokes.clear ();
    }
    
    public void clearPanel () {
        m_strokes.clear ();
    }
    
    public void undoOperation () {
        
        if (m_strokes.empty () == false) {
            m_undoStrokes.push (m_strokes.pop ());
        }
    }
    
    public void redoOperation () {
        
        if (m_undoStrokes.empty () == false) {
            m_strokes.push (m_undoStrokes.pop ());
        }
    }

}