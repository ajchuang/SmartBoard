// common imports
import javax.swing.*;
import java.awt.*;

public class SmartBoard_AppMain extends JFrame {

    static SmartBoard_AppMain sm_appMain = null;
    
    SmartBoard_AppPanel m_mainPanel;
    
    JMenuItem   m_exitItem;
    JMenuItem   m_aboutItem;
    JButton     m_clearBtn;
    
    
    private SmartBoard_AppMain () {
        
        setupSwingComponents ();
    }
    
    public static SmartBoard_AppMain getAppMain () {
        
        if (sm_appMain == null) {
            sm_appMain = new SmartBoard_AppMain ();
        }
        
        return sm_appMain;
    }
    
    // config UI functions 
    void setupSwingComponents () {
        
        setTitle ("SmartBoard");
        setLayout (new BorderLayout());
        
        // create menubar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar (menuBar);
        
        // menubar items
        JMenu fileMenu = new JMenu ("File");
        menuBar.add (fileMenu);
        
        JMenu aboutMenu = new JMenu ("About");
        menuBar.add (aboutMenu);
        
        // menu items
        m_exitItem = new JMenuItem ("Exit");
        fileMenu.add (m_exitItem);
        
        m_aboutItem = new JMenuItem ("Info");
        aboutMenu.add (m_aboutItem);
        
        // Toolbar config
        JToolBar toolBar = new JToolBar ();
        add (toolBar, BorderLayout.NORTH);
        
        // Add toolbar items
        m_clearBtn = new JButton ("Clear");
        toolBar.add (m_clearBtn);
        toolBar.addSeparator ();
        
        // main panels
        m_mainPanel = new SmartBoard_AppPanel ();
        add (m_mainPanel, BorderLayout.CENTER);
        pack ();
        
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);
        setExtendedState (Frame.MAXIMIZED_BOTH); 
        setVisible (true);
    }

}