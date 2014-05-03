// Standard imports
import java.util.*;
import java.io.File;
import java.net.*;
import java.nio.ByteBuffer;

// javacv imports
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class SmartBoard_CamCtrl {
    
    // data members
    int m_camId;
    
    // networking members
    InetAddress m_host;
    int m_port;
    
    // static data members
    static Hashtable<Integer, SmartBoard_CamProc> sm_camIdSet;
    static int m_camWidth   = 240;
    static int m_camHeight  = 180;
    static int m_binThrsh   = 253;
    
    public SmartBoard_CamCtrl (int nCam, InetAddress host, int port) {
        m_camId = nCam;
        m_host = host;
        m_port = port;
    }
    
    // sending the UDP packet to the server
    public void sendToServ (int theta, int confidence) {
        
        SmartBoard_msg msg = new SmartBoard_msg (m_camId, theta, confidence);
        
        try {
            byte[] raw = msg.deflate ();
            DatagramPacket packet = new DatagramPacket (raw, raw.length, m_host, m_port); 
            DatagramSocket socket = new DatagramSocket ();
            socket.send (packet);                         
            socket.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }                         
    }
    
    public void startCap () {
        int w = m_camWidth;
        int h = m_camHeight;
        IplImage raw = null;
        
        CvCapture cap = cvCreateCameraCapture (m_camId);
        cvSetCaptureProperty (cap, CV_CAP_PROP_FRAME_WIDTH, w);
        cvSetCaptureProperty (cap, CV_CAP_PROP_FRAME_HEIGHT, h);
        
        // image caliberation
        System.out.println ("Thread: " + m_camId + " starting");
        
        // starting image processing - to decide the distance
        while (true) {
            
            raw = cvQueryFrame (cap);
            
            if (raw == null) {
                System.out.println ("System Error: Failed to capture");
                System.exit (0);
            }
    
            IplImage grayImage = IplImage.create (w, h, IPL_DEPTH_8U, 1);
            cvCvtColor (raw, grayImage, CV_BGR2GRAY);
            cvThreshold (grayImage, grayImage, m_binThrsh, 255, CV_THRESH_BINARY);
            
            cvShowImage ("Original" + m_camId, raw);
            
            /*
            locateTorch (grayImage);
            CvSeq ctr = null;
            CvMemStorage storage = CvMemStorage.create ();
            
            cvFindContours (
                grayImage,
                storage,
                ctr,
                Loader.sizeof (CvContour.class),
                CV_RETR_LIST,
                CV_LINK_RUNS,
                cvPoint (0,0));
                
            double maxArea = 100.0;
            double curArea = 0.0;
            while (ctr != null && ctr.isNull () == false) {
                
                curArea = cvContourArea (ctr, CV_WHOLE_SEQ, 1);
		
                if (curArea > maxArea) {
                    maxArea = curArea;
                    cvDrawContours (
                        grayImage, 
                        ctr, 
                        CV_RGB (0,0,0), 
                        CV_RGB (0,0,0),
						 0,
                        CV_FILLED,
                        8,
                        cvPoint(0,0));
                }
		
                ctr = ctr.h_next ();
            }
            */
            
            //cvShowImage ("Binary" + m_camId, grayImage);
            sendToServ (15, 90);
        }
    }
    
    public static void main (String[] args) {
        
        if (args.length != 3) {
            System.out.println ("Incorrect input format");
            System.out.println ("java SmartBoard_CamCtrl [host] [port] [camId]");
            return;
        }
        
        String host     = args[0];
        String port     = args[1];
        String camId    = args[2];
        
        try {
            SmartBoard_CamCtrl ctrl = 
                new SmartBoard_CamCtrl (
                    Integer.parseInt (camId),
                    InetAddress.getByName (host), 
                    Integer.parseInt (port));
            
            ctrl.startCap ();
            
        } catch (Exception e) {
            System.out.println ("Failed to start server.");
            e.printStackTrace ();
        }
    }
}