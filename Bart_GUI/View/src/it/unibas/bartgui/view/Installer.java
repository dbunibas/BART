/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.view.panel.run.BusyDialog;
import it.unibas.centrallookup.CentralLookup;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;


public class Installer extends ModuleInstall {

    @Override
    public boolean closing() {
        EGTaskDataObjectDataObject dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        if(dto != null)   {
            return dto.close();
        }else{
            return true;
        }
    }

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable(){
            
            @Override
            public void run() {
                BusyDialog.initBusyDialog();
            }
        });
    }
}
