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
    static int m_camWidth   = 1024;
    static int m_camHeight  = 768;
    
    
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
        
        while (true) {
            
            raw = cvQueryFrame (cap);
            
            IplImage grayImage = IplImage.create (w, h, IPL_DEPTH_8U, 1);
            cvCvtColor (raw, grayImage, CV_BGR2GRAY);
            cvThreshold (grayImage, grayImage, 250, 255, CV_THRESH_BINARY);
            
            if (raw == null) {
                SmartBoard.logErr ("Failed to capture");
                System.exit (0);
            }
            
            //cvShowImage ("Original", raw);
            cvShowImage ("Binary", grayImage);
            locateTorch (grayImage);
        }
    } 
    
    public void run () {
        
        liveCapFrame ();
    }
}