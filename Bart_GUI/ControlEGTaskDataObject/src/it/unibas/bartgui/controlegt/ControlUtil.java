package it.unibas.bartgui.controlegt;


import java.awt.Dialog;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Musicrizz
 */
public class ControlUtil {

    private ControlUtil() {
    }
    
    public static Dialog createDialog(Object innerPane, Object[] options)   {
         DialogDescriptor dsc = new DialogDescriptor(innerPane, 
                                null, 
                                true, 
                                options, 
                                null,
                                DialogDescriptor.DEFAULT_ALIGN, 
                                HelpCtx.DEFAULT_HELP, 
                                null);
        return DialogDisplayer.getDefault().createDialog(dsc);
    }
}
