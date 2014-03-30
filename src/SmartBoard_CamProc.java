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
    
    private static void captureFrame () {
        
        try {
            FrameGrabber grabber = FrameGrabber.createDefault(0);
            grabber.start ();
            IplImage img = grabber.grab ();
            int width  = img.width ();
            int height = img.height ();
            
            IplImage grayImage = IplImage.create (width, height, IPL_DEPTH_8U, 1);
            cvCvtColor (img, grayImage, CV_BGR2GRAY);
            
            // Let's find some contours! but first some thresholding...
            cvThreshold (grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

            // To check if an output argument is null we may call either isNull() or equals(null).
            /*
            CvSeq contour = new CvSeq(null);
            cvFindContours(grayImage, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
            while (contour != null && !contour.isNull()) {
                if (contour.elem_size() > 0) {
                    CvSeq points = cvApproxPoly(contour, Loader.sizeof(CvContour.class),
                            storage, CV_POLY_APPROX_DP, cvContourPerimeter(contour)*0.02, 0);
                    cvDrawContours(img, points, CvScalar.BLUE, CvScalar.BLUE, -1, 1, CV_AA);
                }
                contour = contour.h_next();
            }
            */
            
            if (img != null) {
                cvSaveImage ("capture.jpg", grayImage);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run () {
        
        while (true) {
            captureFrame ();
        }
    }
}