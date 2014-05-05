// Standard imports
import java.util.*;
import java.io.File;
import java.net.*;
import java.nio.ByteBuffer;
import java.awt.AWTException;
import java.awt.event.InputEvent;

// javacv imports
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class SmartBoard_CamCtrl {
    
    // constants
    final static int m_camWidth   = 320;
    final static int m_camHeight  = 240;
    final static int m_binThrsh   = 253;
    
    // data members
    int m_camId;
    
    // networking members
    InetAddress m_host;
    int m_port;
    
    // static data members
    static Hashtable<Integer, SmartBoard_CamProc> sm_camIdSet;
    
    
    public static void log (String s) {
        System.out.println ("  [Cam] " + s);
    } 
    
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
        double curArea = 0.0;
        double maxArea = 0.0;
        
        // opencv variables
        IplImage raw = null;
        IplImage hsvImage = cvCreateImage (cvSize(w,h), IPL_DEPTH_8U, 3);
        IplImage grayImage = cvCreateImage (cvSize(w,h), IPL_DEPTH_8U, 1);
        IplImage binImage = cvCreateImage (cvSize(w,h), IPL_DEPTH_8U, 1);
        
        CvMemStorage storage = CvMemStorage.create ();
        CvSeq ctrIdx = null;
         
        CvMoments moments = new CvMoments (Loader.sizeof (CvMoments.class));
        
        // setup the capture and property
        CvCapture cap = cvCreateCameraCapture (m_camId);
        cvSetCaptureProperty (cap, CV_CAP_PROP_FRAME_WIDTH, w);
        cvSetCaptureProperty (cap, CV_CAP_PROP_FRAME_HEIGHT, h);
        
        // image caliberation
        log ("Thread: " + m_camId + " starting");
        long frameIdx = 0;
        // starting image processing - to decide the equation
        while (true) {
            
            // step 1. capture the frame
            raw = cvQueryFrame (cap);
            //log ("frame: " + frameIdx++);
            
            if (raw == null) {
                log ("System Error: Failed to capture");
                System.exit (0);
            }
            
            // step 2. do binary threshholding
            cvCvtColor (raw, grayImage, CV_BGR2GRAY);
            cvThreshold (grayImage, binImage, m_binThrsh, 255, CV_THRESH_BINARY);
            
            // step 3. find the max contour
            CvSeq ctrList = new CvSeq ();
            int r = 
                cvFindContours (
                    binImage,
                    storage,
                    ctrList,
                    Loader.sizeof (CvContour.class),
                    CV_RETR_LIST,
                    CV_LINK_RUNS,
                    cvPoint (0,0));
            
            if (r == -1) {
                // contour finds nothing, return
                log ("cvFindContours returns -1");
                continue; 
            }
            
            ctrIdx = ctrList;
            int idx = 0, maxIdx = 0;
            
            // find the largest area
            while (ctrList != null && !ctrList.isNull ()) {
                
                curArea = cvContourArea (ctrList, CV_WHOLE_SEQ, 1);
                idx++;
		
                if (curArea > maxArea) {
                    
                    maxArea = curArea;
                    maxIdx = idx;
                }
		
                // traverse the list
                ctrList = ctrList.h_next ();
            }
            
            idx = 0;
            
            // show the largest area
            while (ctrIdx !=null && !ctrIdx.isNull()) {
                
                if (idx != maxIdx) {
                    // make the smaller contour black   
                    cvDrawContours (
                        binImage, 
                        ctrIdx,
                        CV_RGB (0,0,0), CV_RGB(0,0,0),
                        0,
                        CV_FILLED,
                        8,
                        cvPoint(0,0));
                }
                
                idx++;
                ctrIdx = ctrIdx.h_next ();
            }
            
            // Step 4. Calculate center of mass
            double moment10, moment01, centerArea;
        
            cvMoments (binImage, moments, 1);
            moment10    = cvGetSpatialMoment (moments, 1, 0);
            moment01    = cvGetSpatialMoment (moments, 0, 1);
            centerArea  = cvGetCentralMoment (moments, 0, 0);
            
            int cordX = (int) (moment10 / centerArea - m_camWidth/2);
            int cordY = (int) (moment01 / centerArea);
            log ("X: " + cordX + ", Y: " + cordY + ", area: " + centerArea);
            
            // Step 5. Transform into Theta (TODO)
        
            cvShowImage ("Bin_" + m_camId, binImage);
            //sendToServ (15, 90);
        }
    }
    
    public static void main (String[] args) {
        
        if (args.length != 3) {
            log ("Incorrect input format");
            log ("java SmartBoard_CamCtrl [host] [port] [camId]");
            return;
        }
        
        String host  = args[0];
        String port  = args[1];
        String camId = args[2];
        
        try {
            SmartBoard_CamCtrl ctrl = 
                new SmartBoard_CamCtrl (
                    Integer.parseInt (camId),
                    InetAddress.getByName (host), 
                    Integer.parseInt (port));
            
            ctrl.startCap ();
            
        } catch (Exception e) {
            log ("Failed to start server.");
            e.printStackTrace ();
        }
    }
}