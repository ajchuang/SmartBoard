// common imports
import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.geom.*;

public class SmartBoard_CircleComp extends SmartBoard_Component {
    
    int     m_centerX;
    int     m_centerY;
    int     m_radius;
    int     m_strokeWidth;
    Color   m_color;
    
    
    public SmartBoard_CircleComp (int x, int y, int radius, int strokeWidth, Color c) {
        m_centerX = x;
        m_centerY = y;
        m_radius  = radius;
        m_strokeWidth = strokeWidth;
        m_color = c;
    }
    
    @Override
    public void draw (Graphics2D g2d) {
        
        int diameter = m_radius * 2;
        double edgeLength = m_radius * 1.414;
        int x = m_centerX - (int) edgeLength;
        int y = m_centerY - (int) edgeLength;

        g2d.setColor (m_color);
        g2d.setStroke (new BasicStroke (m_strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));        
        g2d.drawOval (x, y, diameter, diameter);
    }
    
}