// common imports
import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;

public class SmartBoard_AppCalib extends JFrame implements ActionListener {

    final static int CALIB_GRID_SIZE = 64;
    final static int DEFAULT_TIME_OUT = 1000;
    
    SmartBoard_AppPanel m_mainPanel;
    javax.swing.Timer   m_timer;
    
    static SmartBoard_AppCalib sm_calibWin;
    
    public static SmartBoard_AppCalib getCalibWin () {
        return sm_calibWin;
    }

    public SmartBoard_AppCalib () {
    
        setupUiComponents ();
        m_timer = new javax.swing.Timer (DEFAULT_TIME_OUT, this);
        m_timer.start ();
        
        sm_calibWin = this;
    }
    
    public static void drawPoint (int x, int y) {
        sm_calibWin.app_drawPoint (x, y);
    }
    
    void app_drawPoint (int x, int y) {
        m_mainPanel.drawCircle (100, 100, 10, new Color (255, 255, 255));
        
        // repaint to trigger
        m_mainPanel.repaint ();
    }
    
    void setupUiComponents () {
        
        // setup layout
        setLayout (new BorderLayout());
        
        // main panels
        m_mainPanel = new SmartBoard_AppPanel ();
        add (m_mainPanel, BorderLayout.CENTER);
        pack ();
        
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);
        setExtendedState (Frame.MAXIMIZED_BOTH); 
        setVisible (true);
    }
    
    void drawCalibIcons () {
        
        try {
            int width   = m_mainPanel.getWidth ();
            int height  = m_mainPanel.getHeight ();
            
            int left_x  = 64 - 24;
            int right_x = width - 64 - 24;
            int upper_y = 64 - 24;
            int lower_y = height - 64 - 24;
            
            File f = new File ("./res/calib_icon.png");
            BufferedImage in = ImageIO.read (f);
            m_mainPanel.drawImage (in, left_x, upper_y);
            m_mainPanel.drawImage (in, left_x, lower_y);
            m_mainPanel.drawImage (in, right_x, upper_y);
            m_mainPanel.drawImage (in, right_x, lower_y);
            
            File calib = new File ("./res/cam_calib.png");
            BufferedImage c = ImageIO.read (calib);
            m_mainPanel.drawImage (c, width*2/3, 0);
            m_mainPanel.drawImage (c, width/3, height - 48);
            m_mainPanel.drawImage (c, width - 48, height*2/3);
            m_mainPanel.drawImage (c, 0, height/3);
            
            //m_mainPanel.drawCircle (width/2, height/2, 20, Color.WHITE);
            //m_mainPanel.drawString ("Calibration", width/2, height/2 - 50, 3, Color.WHITE);
            m_mainPanel.repaint ();
            
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    
    
    void drawCalibLines () {
        
        int width   = m_mainPanel.getWidth ();
        int height  = m_mainPanel.getHeight (); 
        
        /*
        int nWidth  = width / CALIB_GRID_SIZE;
        int nHeight = height / CALIB_GRID_SIZE;
        
        SmartBoard.logInfo ("drawCalibLines: " + width + ":" + height);
        
        for (int i = 1; i <= nWidth; ++i) {
            m_mainPanel.drawLine (i * CALIB_GRID_SIZE, 0, i * CALIB_GRID_SIZE, height - 1, 2, 0);
        }
        
        for (int i = 1; i <= nHeight; ++i) {
            m_mainPanel.drawLine (0, i * CALIB_GRID_SIZE, width - 1, i * CALIB_GRID_SIZE, 2, 0);
        }
        */
        
        m_mainPanel.repaint ();
    }
    
    public void actionPerformed (ActionEvent a) {
        //drawCalibIcons ();
        //drawCalibLines ();
        m_timer.stop ();
    }
} 