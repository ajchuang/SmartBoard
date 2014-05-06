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
        }

        g2d.dispose ();
    }
    
    public void drawLine (int x_begin, int y_begin, int x_end, int y_end, int width, Color c) {
        
        SmartBoard_LineComp newStroke = new SmartBoard_LineComp (x_begin, y_begin, x_end, y_end, width, c);
        m_strokes.push (newStroke);
        m_undoStrokes.clear ();
    }
    
    public void drawImage (BufferedImage img, int x, int y) {
        
        SmartBoard_ImgComp newImg = new SmartBoard_ImgComp (img, x, y);
        m_strokes.push (newImg);
        m_undoStrokes.clear ();
    }
    
    public void drawCircle (int x, int y, int r, Color c) {
        
        SmartBoard_CircleComp cComp = new SmartBoard_CircleComp (x, y, r, 3, c);
        m_strokes.push (cComp);
        m_undoStrokes.clear ();
    }
    
    public void drawString (String s, int x, int y, int w, Color c) {
        
        SmartBoard_StringComp sComp = new SmartBoard_StringComp (x, y, w, s, c);
        m_strokes.push (sComp);
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