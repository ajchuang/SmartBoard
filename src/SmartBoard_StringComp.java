// common imports
import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.geom.*;

public class SmartBoard_StringComp extends SmartBoard_Component {
    
    String  m_string;
    Color   m_color;
    int     m_x;
    int     m_y;
    int     m_width;
    
    public SmartBoard_StringComp (int x, int y, int w, String s, Color c) {
        m_x = x;
        m_y = y;
        m_width = w;
        m_string = s;
        m_color = c;
    }
    
    @Override
    public void draw (Graphics2D g2d) {
        
        g2d.setColor (m_color);
        g2d.setStroke (new BasicStroke (m_width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));        
        g2d.drawString (m_string, m_x, m_y);
    }
}
        