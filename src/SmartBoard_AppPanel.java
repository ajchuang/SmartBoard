// common imports
import javax.swing.*;
import java.awt.*;

public class SmartBoard_AppPanel extends JPanel {
    
    @Override
    protected void paintComponent (Graphics g) {
        
        super.paintComponent(g); 
        Graphics2D g2d = (Graphics2D) g.create();

        int width = getWidth();
        int height = getHeight();

        int xDif = width / 4;
        int yDif = height / 4;

        g2d.setStroke (new BasicStroke (1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine (xDif, yDif, width - xDif, yDif);
        g2d.setStroke (new BasicStroke (2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine (width - xDif, yDif, width - xDif, height - yDif);
        g2d.setStroke (new BasicStroke (3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine (width - xDif, height - yDif, xDif, height - yDif);
        g2d.setStroke(new BasicStroke (4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine (xDif, height - yDif, xDif, yDif);

        g2d.dispose();
    }

}