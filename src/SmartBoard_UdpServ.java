// Standard imports
import java.util.*;
import java.io.File;
import java.net.*;
import java.nio.ByteBuffer;
import java.awt.Point;

public class SmartBoard_UdpServ implements Runnable {

    final int M_MAX_MSG_SIZE = 32;
    
    int m_nCam;
    int m_port;
    ArrayList<SmartBoard_msg> m_msgBuf;
    
    
    public SmartBoard_UdpServ (int port, int nCam) {
        m_nCam = nCam;
        m_port = port;
        m_msgBuf = new ArrayList<SmartBoard_msg> ();
        
        // force to create sufficient entries
        for (int i=0; i<nCam; ++i) {
            m_msgBuf.add (null);
        }
    }
    
    // solve the interaction of the equation.
    Point solveEquation (SmartBoard_msg v_msg, SmartBoard_msg h_msg) {
        Point p = new Point ((int)v_msg.getX (), (int)h_msg.getY ());
        return p;
    }
    
    // send point to UI
    void paintPoint (Point p) {
        SmartBoard.logInfo ("Painting: " + p.getX () + ":" + p.getY ());
        SmartBoard_AppCalib.getCalibWin ().drawPoint ((int)p.getX (), (int)p.getY ());
    }
    
    //TODO
    void analyze () {
        
        // cam0 is the upper one, and cam1 is the lower one.
        // cam2 is the right one, and cam3 is the left one
        SmartBoard_msg[] cams = new SmartBoard_msg[4];
        
        for (int i=0; i<m_nCam; ++i) {
            cams[i] = m_msgBuf.get (i);
        }
        
        // when one side is completely blind
        if ((cams[0] == null && cams[1] == null) ||
            (cams[2] == null && cams[3] == null)) {
            SmartBoard.logInfo ("One side is blind.");
            return;
        }
        
        // pick the best 2.
        SmartBoard_msg ver;
        SmartBoard_msg hor;
        
        // pick the horizontal one
        if (cams[0] == null)
            ver = cams[1];
        else if (cams[1] == null) 
            ver = cams[0];
        else if (cams[0].getConf () >= cams[0].getConf ()) 
            ver = cams[0]; 
        else 
            ver = cams[1];
        
        // piack the vertical one
        if (cams[2] == null)
            hor = cams[3];
        else if (cams[3] == null)
            hor = cams[2];
        else if (cams[2].getConf () >= cams[3].getConf ())
            hor = cams[2]; 
        else 
            hor = cams[3];
        
        // solve the equation
        if (hor != null && ver != null) {
            paintPoint (solveEquation (ver, hor));
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