// Standard imports
import java.util.*;
import java.io.File;
import java.net.*;
import java.nio.ByteBuffer;
import java.awt.Point;
import java.awt.*;

public class SmartBoard_UdpServ implements Runnable {

    final static int M_MAX_MSG_SIZE = 32;
    final static int m_mountTop    = 0;
    final static int m_mountBottom = 1;
    final static int m_mountLeft   = 2;
    final static int m_mountRight  = 3;
    
    int m_nCam;
    int m_port;
    int m_screenResWidth;
    int m_screenResHeight;
    ArrayList<SmartBoard_msg> m_msgBuf;
    
    public SmartBoard_UdpServ (int port, int nCam) {
        m_nCam = nCam;
        m_port = port;
        m_msgBuf = new ArrayList<SmartBoard_msg> ();
        
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        m_screenResWidth  = gd.getDisplayMode().getWidth();
        m_screenResHeight = gd.getDisplayMode().getHeight();
        SmartBoard.logInfo ("Resolution: " + m_screenResWidth + ":" + m_screenResHeight);
        
        // force to create sufficient entries
        for (int i=0; i<nCam; ++i) {
            m_msgBuf.add (null);
        }
    }
    
    // solve the interaction of the equation.
    Point solveEquation (SmartBoard_msg v_msg, SmartBoard_msg h_msg) {
        
        // rescale to the screen
        //int x = (int) h_msg.getX () * (m_screenResWidth/320);
        //int y = (int) v_msg.getX () * (m_screenResHeight/320);
        
        // create the new point
        Point p = new Point (h_msg.getX (), v_msg.getX ());
        return p;
    }
    
    // send point to UI
    void paintPoint (Point p) {
        
        SmartBoard.logInfo ("Painting: (" + p.getX () + ", " + p.getY () + ")");
        SmartBoard_Paint paint = SmartBoard_Paint.getInstance ();
        Point newP = new Point ((int)(p.getX () - 64), (int)(p.getY () - 108));
        paint.draw (newP);
        //SmartBoard_AppCalib.getCalibWin ().drawPoint ((int)p.getX (), (int)p.getY ());
    }
    
    //TODO
    void analyze () {
        
        // cam0 is the upper one, and cam1 is the lower one.
        // cam2 is the right one, and cam3 is the left one
        SmartBoard_msg[] cams = new SmartBoard_msg[4];
        
        for (int i=0; i<m_nCam; ++i) {
            cams[i] = m_msgBuf.get (i);
        }
        
        //if (cams[m_mountTop]  == null || cams[m_mountBottom] == null ||
        //    cams[m_mountLeft] == null || cams[m_mountRight]  == null) {
        //    SmartBoard.logInfo ("One cam is blind.");
        //    return;
        //}
        
        // when one side is completely blind
        if ((cams[m_mountTop]  == null && cams[m_mountBottom] == null) ||
            (cams[m_mountLeft] == null && cams[m_mountRight] == null)) {
            SmartBoard.logInfo ("One side is blind.");
            return;
        } 
        
        // pick the best 2.
        SmartBoard_msg ver;
        SmartBoard_msg hor;
        
        // pick the horizontal one
        if (cams[m_mountLeft] == null)
            ver = cams[m_mountRight];
        else if (cams[m_mountRight] == null) 
            ver = cams[m_mountLeft];
        else if (cams[m_mountRight].getConf () >= cams[m_mountLeft].getConf ()) 
            ver = cams[m_mountRight]; 
        else 
            ver = cams[m_mountLeft];
        
        // piack the vertical one
        if (cams[m_mountTop] == null)
            hor = cams[m_mountBottom];
        else if (cams[m_mountBottom] == null)
            hor = cams[m_mountTop];
        else if (cams[m_mountTop].getConf () >= cams[m_mountBottom].getConf ())
            hor = cams[m_mountTop]; 
        else 
            hor = cams[m_mountBottom];
        
        // solve the equation
        if (hor != null && ver != null) {
            paintPoint (solveEquation (ver, hor));
        }
        
        for (int i=0; i<m_nCam; ++i) {
            cams[i] = m_msgBuf.set (i, null);
        }
    }

    public void run () {
        
        byte buffer[] = new byte[M_MAX_MSG_SIZE];
        
        SmartBoard.logInfo ("Starting UDP server");
        
        DatagramSocket socket = null;
        
        try {
            socket = new DatagramSocket (m_port);
        } catch (Exception e) {
            SmartBoard.logInfo ("Failed to create socket.");
            System.exit (0);
        }
        
        while (true) {
            
            try {
                DatagramPacket packet = new DatagramPacket (buffer, buffer.length);
                socket.receive (packet);
            
                SmartBoard_msg msg = new SmartBoard_msg (packet.getData ());
                //System.out.println (msg);
            
                // put the msg to the right bucket 
                m_msgBuf.add (msg.getId (), msg);
                
                // Now we can run the algorithm
                analyze ();
                
            } catch (Exception e) {
                SmartBoard.logInfo ("Exception: " + e);
                e.printStackTrace ();
            }
        }
    }
}