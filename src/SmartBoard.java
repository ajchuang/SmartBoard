// common imports
import java.io.File;
import java.net.URL;

public class SmartBoard {
    
    final public static int M_PORT = 8888;
    SmartBoard_Paint m_mainUi;

    public static void logErr (String s) {
        System.out.println ("[ERROR]" + s);
    }
    
    public static void logInfo (String s) {
        System.out.println ("  [INFO]" + s);
    }
    
    static Thread startCamProc (int i) {
        Thread cam = new Thread (SmartBoard_CamProc.camFactory (i));
        return cam;
        //cam.start ();
    }

    public static void main (String args[]) {
        
        SmartBoard.logInfo ("Hello");
        
        if (args.length != 1) {
            System.out.println ("Incorrect input format: " + args.length);
            System.out.println ("java SmartBoard [nCamera]");
            return;
        }
        
        try {
            SmartBoard_UdpServ serv = new SmartBoard_UdpServ (M_PORT, 4);
            new Thread (serv).start ();
            
            // start the UI
            SmartBoard_Paint.start ();
            //m_mainUi = 
            
            //SmartBoard_AppCalib calib = new SmartBoard_AppCalib ();
        
            // run the UI
            //SmartBoard_AppMain.getAppMain ();
        } catch (Exception e) {
            System.out.println ("Exception: " + e);
            e.printStackTrace ();
        }
    }

}