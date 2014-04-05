
public class SmartBoard_Stroke extends SmartBoard_Component {
    
    int m_xBegin;
    int m_yBegin;
    int m_xEnd;
    int m_yEnd;
    int m_width;
    
    public SmartBoard_Stroke (int x, int y, int x2, int y2, int width) {
        m_xBegin = x;
        m_yBegin = y;
        m_xEnd = x2;
        m_yEnd = y2;     
        m_width = width;
    }
    
    public int getX_Begin () { return m_xBegin; }
    public int getY_Begin () { return m_yBegin; }
    
    public int getX_End () { return m_xEnd; }
    public int getY_End () { return m_yEnd; }
    
    public int getWidth () { return m_width; }
    
    public void printStroke () {
        SmartBoard.logInfo (
            "Stroke: " + m_xBegin   + ":" 
                       + m_yBegin   + ":"
                       + m_xEnd     + ":"
                       + m_yEnd);
    }
}