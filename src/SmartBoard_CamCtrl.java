// Standard imports
import java.util.*;
import java.io.*;
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
    
    final static int m_mountTop    = 0;
    final static int m_mountBottom = 1;
    final static int m_mountLeft   = 2;
    final static int m_mountRight  = 3;
    
    // constants
    final static int m_camWidth    = 320;
    final static int m_camHeight   = 240;
    final static int m_binThrsh    = 100;
    final static int m_minRetrArea = 3000;
    
    // data members
    int m_camId;
    int m_camPosX;
    int m_camPosY;
    int m_flipping;
    int m_mount;
    
    // networking members
    InetAddress m_host;
    int m_port;
    
    // static data members
    static Hashtable<Integer, SmartBoard_CamProc> sm_camIdSet;
    
    
    public static void log (String s) {
        System.out.println ("  [Cam] " + s);
    } 
    
    public SmartBoard_CamCtrl (String config) {
    
        try {
            m_camId = 0;
            m_host = InetAddress.getByName ("localhost");
            m_port = 8888;
            m_camPosX = 0;
            m_camPosY = 0;
            m_flipping = 0;
            m_mount = m_mountTop;
            
            parseConfig (config);
        } catch (Exception e) {
            SmartBoard.logErr ("Bad error");
            System.exit (0);
        }
    }
    
    void parseConfig (String fname) {
    
        try {    
            BufferedReader br = new BufferedReader (new FileReader (fname));
            String strLine;
            
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                
                if (strLine.startsWith ("id=")) {
                    String id = strLine.substring (3);
                    m_camId = Integer.parseInt (id);
                } else if (strLine.startsWith ("host=")) {
                    String host = strLine.substring (5);
                    m_host = InetAddress.getByName (host);
                } else if (strLine.startsWith ("port=")) {
                    String port = strLine.substring (5);
                    m_port = Integer.parseInt (port);
                } else if (strLine.startsWith ("x=")) {
                    String x = strLine.substring (2);
                    m_camPosX = Integer.parseInt (x);
                } else if (strLine.startsWith ("y=")) {
                    String y = strLine.substring (2);
                    m_camPosY = Integer.parseInt (y);
                } else if (strLine.startsWith ("mount=")) {
                    String mount = strLine.substring (6).trim ();
                    
                    if (mount.equals ("top")) {
                        m_mount = m_mountTop;
                    } else if (mount.equals ("bottom")) {
                        m_mount = m_mountBottom;
                    } else if (mount.equals ("right")) {
                        m_mount = m_mountRight;
                    } else if (mount.equals ("left")) {
                        m_mount = m_mountLeft;
                    } 
                }
                    
                // Print the content on the console
                System.out.println (strLine);
            }
            
            br.close();
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    // sending the UDP packet to the server
    public void sendToServ (int x, int y, int confidence) {
        
        SmartBoard_msg msg = new SmartBoard_msg (m_mount, x, y, confidence);
        
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
        
        // opencv variables
        IplImage raw = null;
        IplImage grayImage  = cvCreateImage (cvSize(w,h), IPL_DEPTH_8U, 1);
        IplImage binImage   = cvCreateImage (cvSize(w,h), IPL_DEPTH_8U, 1);
        
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
            
            double curArea = 0.0;
            double maxArea = 0.0;
            
            // step 1. capture the frame
            raw = cvQueryFrame (cap);
            
            if (raw == null) {
                log ("System Error: Failed to capture");
                System.exit (0);
            } else {
                
                if (m_mount == m_mountRight || m_mount == m_mountTop) 
                    cvFlip (raw, raw, 1);
                    
                cvShowImage ("Raw_" + m_camId, raw);
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
                //log ("cvFindContours returns -1 - no contour this time.");
                pause (200);
                continue; 
            }
            
            ctrIdx = ctrList;
            int idx = 0, maxIdx = 0;
            
            // find the largest area
            while (ctrList != null && !ctrList.isNull ()) {
                
                curArea = cvContourArea (ctrList, CV_WHOLE_SEQ, 1);
                
                if (curArea > maxArea) {
                    maxArea = curArea;
                    maxIdx = idx;
                }
		
                // traverse the list
                idx++;
                ctrList = ctrList.h_next ();
            }
            
            //log ("maxArea: " + maxArea + ", maxIdx: " + maxIdx);
            
            // mark the non-max contour
            idx = 0;
            
            // show the largest area
            while (ctrIdx !=null && !ctrIdx.isNull()) {
                
                //log ("maxArea: testing id @" + idx);
                
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
                } else {
                    log ("maxArea: unpainted @" + idx);
                }
                
                idx++;
                ctrIdx = ctrIdx.h_next ();
            }
            
            cvShowImage ("Bin_" + m_camId, binImage);
            
            // Step 4. Calculate center of mass
            double moment10, moment01, centerArea;
        
            cvMoments (binImage, moments, 1);
            moment10    = cvGetSpatialMoment (moments, 1, 0);
            moment01    = cvGetSpatialMoment (moments, 0, 1);
            centerArea  = cvGetCentralMoment (moments, 0, 0);
            
            int cordX;
            int cordY;
            
            if (m_mount == m_mountBottom || m_mount == m_mountTop) {
                
                int obs_X = (int)(moment10 / centerArea);
                double param = 1.0;
                
                /*
                if (maxArea > 2000.0)
                    param = 1.3;
                else if (maxArea > 1500.0)
                    param = 1.5;
                else if (maxArea > 1000.0)
                    param = 1.8;
                */
                    
                cordX = (int) (((double)(obs_X - m_camWidth/2) * param) + m_camPosX);
                
            } else {
                int obs_X = (int)(moment10 / centerArea);
                double param = 1.3;
                
                /*
                if (maxArea > 800.0)
                    param = 1.6;
                else if (maxArea > 400.0)
                    param = 1.9;
                else if (maxArea > 200.0)
                    param = 2.2;
                */
                
                cordX = (int) (((double)(obs_X - m_camWidth/2) * param) + m_camPosY);
            }
                
            cordY = (int) (moment01 / centerArea);
            
            log ("Cam id: " + m_mount + " X: " + cordX + ", Y: " + cordY + ", area = " + maxArea);
            
            // Step 5. Send to the UDP server        
            //if (maxArea < m_minRetrArea) {
                // the smaller area the better.
                int conf = (-1) * (int)maxArea;
                sendToServ (cordX, cordY, conf);
            //}
            
            // maintain 5-fps
            //pause ();
        }
    }
    
    void pause (long value) {
        try {
            Thread.sleep (value);
        } catch (Exception e) {
            System.out.println ("Exception @ pause: " + e);
            e.printStackTrace ();
        }
    }
    
    public static void main (String[] args) {
        
        if (args.length != 1) {
            log ("Incorrect input format");
            log ("java SmartBoard_CamCtrl [config file]");
            return;
        }
        
        String config = args[0];
        //parseConfig (config);
        
        //String host  = args[0];
        //String port  = args[1];
        //String camId = args[2];
        //String camX  = args[3];
        //String camY  = args[4];
        
        try {
            SmartBoard_CamCtrl ctrl = 
                new SmartBoard_CamCtrl (config);
            
            ctrl.startCap ();
            
        } catch (Exception e) {
            log ("Failed to start server.");
            e.printStackTrace ();
        }
    }
}