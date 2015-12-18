package it.unibas.bartgui.resources;

import org.openide.awt.StatusDisplayer;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class StatusBar {
    
    private StatusBar(){}

    public static void setStatus(String message,int importance,int time)   {
        StatusDisplayer.Message m = StatusDisplayer.getDefault().setStatusText(message, importance);
        m.clear(time);
    }
    
    public static void setStatus(String message)   {
        StatusDisplayer.getDefault().setStatusText(message);
    }
    
}
