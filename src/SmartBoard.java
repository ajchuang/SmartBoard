// common imports
import java.io.File;
import java.net.URL;

public class SmartBoard {

    public static void logErr (String s) {
        System.out.println ("[ERROR]" + s);
    }
    
    public static void logInfo (String s) {
        System.out.println ("  [INFO]" + s);
    }
    
    static void startCamProc (int i) {
        Thread cam = new Thread (SmartBoard_CamProc.camFactory (i));
        cam.start ();
    }

    public static void main (String args[]) {
        
        SmartBoard.logInfo ("Hello");
        
        // start the cam proc
        //startCamProc (0);
        //startCamProc (1);
        
        SmartBoard_AppCalib calib = new SmartBoard_AppCalib ();
        
        // run the UI
        //SmartBoard_AppMain.getAppMain ();
    }

}