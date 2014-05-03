// Standard imports
import java.util.*;
import java.io.File;
import java.net.*;
import java.nio.ByteBuffer;

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
    
    //TODO
    void analyze () {
        
        SmartBoard_msg[] cams = new SmartBoard_msg[4];
        
        // check if null
        for (int i=0; i<m_nCam; ++i) {
            cams[i] = m_msgBuf.get (i);
            if (cams[i] == null) 
                return;
        }
        
        // pick the best 2.
        
        // solve the equation.
        
        // plot the point
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
                System.out.println (msg);
            
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