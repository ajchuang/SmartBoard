// Standard imports
import java.util.*;
import java.io.File;
import java.net.*;
import java.nio.ByteBuffer;

public class SmartBoard_msg {

    public final int M_DEFLATE_SIZE = 20;
    int     m_camId;
    int     m_theta;
    int     m_conf;
    long    m_timeStamp;
    
    public int getId () {
        return m_camId;
    }
    
    public int getTheta () {
        return m_theta;
    }
    
    public int getConf () {
        return m_conf;
    }
    
    public long getTime () {
        return m_timeStamp;
    }
    
    public SmartBoard_msg (int id, int theta, int conf) {
        m_camId = id;
        m_theta = theta;
        m_conf = conf;
        m_timeStamp = System.currentTimeMillis ();
    }
    
    // inflate constructor
    public SmartBoard_msg (byte[] rawData) {
        
        byte[] bCam   = new byte[4];
        byte[] bTheta = new byte[4];
        byte[] bConf  = new byte[4];
        byte[] bTime  = new byte[8];
        
        System.arraycopy (rawData,  0 , bCam,   0, 4);
        System.arraycopy (rawData,  4 , bTheta, 0, 4);
        System.arraycopy (rawData,  8 , bConf,  0, 4);
        System.arraycopy (rawData, 12 , bTime,  0, 8);
        
        m_camId = ByteBuffer.wrap (bCam).getInt ();
        m_theta = ByteBuffer.wrap (bTheta).getInt ();
        m_conf = ByteBuffer.wrap (bConf).getInt ();
        m_timeStamp = ByteBuffer.wrap (bTime).getLong ();
    }
    
    public byte[] deflate () {
        
        byte buffer[] = new byte[M_DEFLATE_SIZE];
        
        byte[] bCam     = ByteBuffer.allocate(4).putInt(m_camId).array();
        byte[] bTheta   = ByteBuffer.allocate(4).putInt(m_theta).array();
        byte[] bConf    = ByteBuffer.allocate(4).putInt(m_conf).array();
        byte[] bTime    = ByteBuffer.allocate(8).putLong(m_timeStamp).array();
        
        System.arraycopy (bCam,     0 ,buffer,  0, 4);
        System.arraycopy (bTheta,   0 ,buffer,  4, 4);
        System.arraycopy (bConf,    0 ,buffer,  8, 4);
        System.arraycopy (bTime,    0 ,buffer, 12, 8);
        
        return buffer;
    }
    
    @Override
    public String toString () {
        return new String ("msg: " + m_camId + ":" + m_theta + ":" + m_conf + ":" + m_timeStamp);
    }
}