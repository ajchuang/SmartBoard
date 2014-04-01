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

    public static void main (String args[]) {
        
        SmartBoard.logInfo ("Hello");
        
        // start the cam proc
        Thread cam_0 = new Thread (SmartBoard_CamProc.camFactory (0));
        cam_0.start ();
        
        // run the UI
        SmartBoard_AppMain.getAppMain ();
    }

}