// common imports
import javax.swing.*;
import java.awt.*;

public class SmartBoard_AppMain extends JFrame {

    static SmartBoard_AppMain sm_appMain = null;
    
    SmartBoard_AppPanel m_mainPanel;
    
    JMenuItem   m_exitItem;
    JMenuItem   m_aboutItem;
    
    // toolbar buttons
    JButton     m_funcNewPageBtn;
    
    JButton     m_eraserBtn;
    JButton     m_penBlackBtn;
    JButton     m_penRedBtn;
    JButton     m_penGreenBtn;
    JButton     m_penBlueBtn;
    
    JButton     m_lineThinBtn;
    JButton     m_lineThickBtn;
    
    
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
        toolBar.setMargin (new Insets (2, 2, 2, 2));
        toolBar.setBorderPainted (true);
        toolBar.setFloatable (false);
        add (toolBar, BorderLayout.NORTH);
        
        // Add toolbar items
        m_funcNewPageBtn = new JButton (new ImageIcon ("res/func_newPage.png"));
        toolBar.add (m_funcNewPageBtn);
        toolBar.addSeparator ();
    
        m_eraserBtn = new JButton (new ImageIcon ("res/eraser.png"));
        toolBar.add (m_eraserBtn);
        
        m_penBlackBtn = new JButton (new ImageIcon ("res/pen_black.png"));
        toolBar.add (m_penBlackBtn);
        
        m_penRedBtn = new JButton (new ImageIcon ("res/pen_red.png"));
        toolBar.add (m_penRedBtn);
        
        m_penGreenBtn = new JButton (new ImageIcon ("res/pen_green.png"));
        toolBar.add (m_penGreenBtn);
        
        m_penBlueBtn = new JButton (new ImageIcon ("res/pen_blue.png"));
        toolBar.add (m_penBlueBtn);
        toolBar.addSeparator ();
        
        m_lineThinBtn = new JButton (new ImageIcon ("res/line_thin.png"));
        toolBar.add (m_lineThinBtn);
        
        m_lineThickBtn = new JButton (new ImageIcon ("res/line_thick.png"));
        toolBar.add (m_lineThickBtn);
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