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
        SmartBoard_AppMain.getAppMain ();
    }

}