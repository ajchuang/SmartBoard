// Standard imports
import java.util.*;
import java.io.File;
import java.net.URL;

// javacv imports
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

// we have a factory method to create the process - to avoid ID collision
// Note that the factory is not thread safe - Do it @ main function.
public class SmartBoard_CamProc implements Runnable {
    
    // data members
    int m_camId;
    
    // static data members
    static Hashtable<Integer, SmartBoard_CamProc> sm_camIdSet;
    static int m_camWidth   = 320;
    static int m_camHeight  = 240;
    static int m_binThrsh   = 253;
    
    
    // class initilizer
    static {
         sm_camIdSet = new Hashtable<Integer, SmartBoard_CamProc> ();
    }

    // create the camera process (the factory method to provide central control)
    public static SmartBoard_CamProc camFactory (int camId) {
        
        if (sm_camIdSet.contains (camId) == true) {
            SmartBoard.logErr ("Creating duplicate camera ID: " + camId); 
            return null;
        }
        
        SmartBoard_CamProc newProc = new SmartBoard_CamProc (camId);
        sm_camIdSet.put (camId, newProc);
        return newProc;
    } 
    
    // private initializer
    private SmartBoard_CamProc (int camId) {
        m_camId = camId;
    }
    
    private int locateTorch (IplImage binImage) {
        return 100;
    }
    
    private void liveCapFrame () {
        
        int w = m_camWidth;
        int h = m_camHeight;
        IplImage raw = null;
        
        CvCapture cap = cvCreateCameraCapture (m_camId);
        cvSetCaptureProperty (cap, CV_CAP_PROP_FRAME_WIDTH, w);
        cvSetCaptureProperty (cap, CV_CAP_PROP_FRAME_HEIGHT, h);
        
        // image caliberation
        
        // starting image processing - to decide the distance
        while (true) {
            
            raw = cvQueryFrame (cap);
            
            if (raw == null) {
                SmartBoard.logErr ("System Error: Failed to capture");
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
            
            cvShowImage ("Binary" + m_camId, grayImage);
        }
    } 
    
    public void run () {
        
        liveCapFrame ();
    }
}