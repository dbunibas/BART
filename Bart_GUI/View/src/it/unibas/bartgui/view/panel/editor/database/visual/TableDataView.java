package it.unibas.bartgui.view.panel.editor.database.visual;


import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.statistics.Statistic;
import it.unibas.centrallookup.CentralLookup;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import org.jdesktop.swingx.JXTable;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import speedy.model.database.ITable;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class TableDataView extends JPanel  implements MultiViewElement   {
    
    private static Logger log = Logger.getLogger(TableDataView.class.getName());
    
    private MultiViewElementCallback callback = null;
    private final JToolBar toolbar;
    private final ITable table;
    private PaginationCtrlPanel pageCtrlPanel;
    private ValidationPanel panelValdPagCtrl;
    private ValidationGroup vgPagControl;
    private JXTable valueTable;
    private final TableDataModel dataModel;
    private int pageSize = 100;
    private int currentPage = -1;
    private List<Page> pages;
    private Lookup.Result<Statistic> result;
    private final LookupListStat listener = new LookupListStat();
    
    public TableDataView(ITable table) {
        setLayout(new BorderLayout(10,10));
        this.toolbar = new JToolBar();     
        this.table = table;
        this.dataModel = new TableDataModel(this.table);
        initPaginationControlPanel();
        initJTable();
        initVisibleRowCountTextFied();
        createPages();
        initFirstPage();
        initPageSizeTextField();
        initButtonFirstPage();
        initButtonLastPage();
        initButtonPrevPage();
        initButtonNextPage();
        initSelectPageTextField();
        initLayout();     
    }
    
    private void initLayout()    {
        JScrollPane scrTable = new JScrollPane();
        scrTable.setViewportView(valueTable);
        JPanel screen = new JPanel(new BorderLayout());
        screen.add(panelValdPagCtrl,BorderLayout.NORTH);
        screen.add(scrTable,BorderLayout.CENTER);
        JScrollPane scrScreen = new JScrollPane();
        scrScreen.setViewportView(screen);
        add(scrScreen,BorderLayout.CENTER);       
    }
    
    
    private void initJTable()   {
        valueTable = new JXTable();
        valueTable.setColumnControlVisible(true);
        valueTable.setEditable(false);
        valueTable.setDefaultRenderer(Object.class, new TableValueCellRender());
        valueTable.setCellSelectionEnabled(true);
        valueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        valueTable.setModel(dataModel);
        valueTable.setShowGrid(true);
        valueTable.setDragEnabled(false); 
        valueTable.setSelectionBackground(new Color(214, 217, 223));
        valueTable.setVisibleRowCount(20);
        pageCtrlPanel.setVisibleRow(20+"");
    }
    
    private void initVisibleRowCountTextFied()   {
        pageCtrlPanel.getVisibleRowTextField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int rwc = Integer.parseInt(pageCtrlPanel.getVisibleRow());
                    if(rwc <=0)   {
                        valueTable.setVisibleRowCount(20);
                        pageCtrlPanel.setVisibleRow(20+"");
                        valueTable.packAll();
                    }
                    valueTable.setVisibleRowCount(rwc);
                    valueTable.packAll();
                }catch(Exception ex)   {
                    valueTable.setVisibleRowCount(20);
                    pageCtrlPanel.setVisibleRow(20+"");
                    valueTable.packAll();
                }
            }
        });
    }
    
    private void initPaginationControlPanel()   {
        pageCtrlPanel = new PaginationCtrlPanel();
        pageCtrlPanel.setPageSize(pageSize+"");
        
        panelValdPagCtrl = new ValidationPanel();
        panelValdPagCtrl.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Pagination Control", 
                                                        TitledBorder.CENTER,TitledBorder.TOP));
        panelValdPagCtrl.setInnerComponent(pageCtrlPanel);
        vgPagControl = panelValdPagCtrl.getValidationGroup();
        vgPagControl.add(pageCtrlPanel.getPageSizeTextField(), 
                        StringValidators.REQUIRE_VALID_INTEGER,
                        StringValidators.REQUIRE_NON_NEGATIVE_NUMBER,
                        StringValidators.REQUIRE_NON_EMPTY_STRING);
        vgPagControl.add(pageCtrlPanel.getVisibleRowTextField(), 
                        StringValidators.REQUIRE_VALID_INTEGER,
                        StringValidators.REQUIRE_NON_NEGATIVE_NUMBER,
                        StringValidators.REQUIRE_NON_EMPTY_STRING);
        vgPagControl.add(pageCtrlPanel.getSelectedPageTextField(), 
                        StringValidators.REQUIRE_VALID_INTEGER,
                        StringValidators.REQUIRE_NON_NEGATIVE_NUMBER,
                        StringValidators.REQUIRE_NON_EMPTY_STRING);
    }
    
    private void createPages()   { 
        log.setLevel(Level.INFO);
        long tableSize = table.getSize();
        log.fine("...createPages....");
        log.fine("tableS SIze "+tableSize);
        if(tableSize == 0)  {
            pageCtrlPanel.setTableSize(0+"");
            pageCtrlPanel.setNumOfPages(0+"");
            return;
        }
        if(pages == null)pages = new ArrayList<Page>();
        pages.clear();
        if(tableSize <= pageSize)   {
            pages.add(new Page(0, (int)tableSize));
            pageCtrlPanel.setNumOfPages(1+"");
            pageCtrlPanel.setTableSize(tableSize+"");
            return;
        }
        int pgs =(int)(table.getSize() / pageSize);
        long mod = table.getSize() % pageSize;       
        int offset = 0;      
        for(int i = 0; i < pgs; i++)   {
            pages.add(new Page(offset, pageSize));
            offset = (offset + pageSize);
        }
        if(mod > 0)   {
            pages.add(new Page(offset, offset+(int)mod));
        }
        pageCtrlPanel.setTableSize(tableSize+"");
        pageCtrlPanel.setNumOfPages(pages.size()+"");
        /*for(int i=0;i<pages.size();i++)   {
            StringBuilder sb = new StringBuilder();
            sb.append("Page n : ");sb.append(i);
            sb.append(" - Offset : ");sb.append(pages.get(i).getOffset());
            sb.append(" - Limit : ");sb.append(pages.get(i).getLimit());
            log.fine(sb.toString());
        }*/
    }
    
    private void initFirstPage()  {
        if((pages == null) || (pages.size()==0))   {
            pageCtrlPanel.setSelectedPage(0+"");
            currentPage=-1;
            return;
        }
        Page p = pages.get(0);
        dataModel.setPage(table.getTupleIterator(p.getOffset(), p.getLimit()));
        valueTable.packAll();
        currentPage = 0;
        pageCtrlPanel.setSelectedPage(1+"");
        //forceGarbage();
    }
    
    private void initPageSizeTextField()   {
        pageCtrlPanel.getPageSizeTextField()
                .addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int size = Integer.parseInt(pageCtrlPanel.getPageSize());
                    if(size <= 0)  {
                        pageCtrlPanel.setPageSize(pageSize+"");
                        return;
                    }
                    if(size == pageSize)return;
                    pageSize = size;
                    pages.clear();
                    dataModel.clear();
                    createPages();
                    initFirstPage();
                }catch(Exception ex)   {
                    pageCtrlPanel.setPageSize(pageSize+"");
                }
            }
        });
    }
    
    private void initButtonFirstPage()   {
        pageCtrlPanel.getFirstButton()
                .addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if((pages == null) || (pages.size()==0) || currentPage == -1)   {
                    pageCtrlPanel.setSelectedPage(0+"");
                    return;
                }
                if(currentPage == 0)return;
                Page p = pages.get(0);
                dataModel.setPage(table.getTupleIterator(p.getOffset(), p.getLimit()));
                valueTable.packAll();
                currentPage = 0;
                pageCtrlPanel.setSelectedPage(1+"");
                //forceGarbage();                
            }
        });
    }
    
    private void initButtonLastPage()   {
        pageCtrlPanel.getLastButton()
                .addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if((pages == null) || (pages.size()==0) || currentPage == -1)   {
                    pageCtrlPanel.setSelectedPage(0+"");
                    return;
                }
                if(currentPage == (pages.size()-1))return;
                    Page p = pages.get(pages.size()-1);
                    dataModel.setPage(table.getTupleIterator(p.getOffset(), p.getLimit()));
                    valueTable.packAll();
                    currentPage = (pages.size()-1);
                    pageCtrlPanel.setSelectedPage((pages.size())+"");
                    //forceGarbage();
            }
        });
    }
    
    private void initButtonPrevPage()   {
        pageCtrlPanel.getPrevButton()
                .addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if((pages == null) || (pages.size()==0) || currentPage == -1)   {
                    pageCtrlPanel.setSelectedPage(0+"");
                    return;
                }
                if(currentPage == 0)return;  
                Page p = pages.get((currentPage-1));
                dataModel.setPage(table.getTupleIterator(p.getOffset(), p.getLimit()));
                valueTable.packAll();
                pageCtrlPanel.setSelectedPage((currentPage)+"");
                currentPage--;
                //forceGarbage();
            }
        });
    }
    
    private void initButtonNextPage()   {
        pageCtrlPanel.getNextButton()
                .addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if((pages == null) || (pages.size()==0) || (currentPage == -1))   {
                    pageCtrlPanel.setSelectedPage(0+"");
                    return;
                }
                if(currentPage == (pages.size()-1))return;   
                    Page p = pages.get(currentPage+1);
                    dataModel.setPage(table.getTupleIterator(p.getOffset(), p.getLimit()));
                    valueTable.packAll();
                    currentPage++;
                    pageCtrlPanel.setSelectedPage((currentPage+1)+"");
                    //forceGarbage();
            }
        });
    }
    
    private void initSelectPageTextField()   {
        pageCtrlPanel.getSelectedPageTextField()
                .addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if((pages == null) || (pages.size()==0) || (currentPage == -1))   {
                    pageCtrlPanel.setSelectedPage(0+"");
                    return;
                }
                try{
                    final int page = Integer.parseInt(pageCtrlPanel.getSelectedPage());
                    if((page <= 0) || (page > pages.size()) )   {
                        pageCtrlPanel.setSelectedPage((currentPage+1)+"");
                        return;
                    }
                    if(currentPage == (page-1))return;
                    Page p = pages.get(page-1);
                    dataModel.setPage(table.getTupleIterator(p.getOffset(), p.getLimit()));
                    valueTable.packAll();
                    currentPage = (page - 1);   
                    //forceGarbage();
                }catch(Exception ex)   {
                    pageCtrlPanel.setSelectedPage((currentPage+1)+"");
                }
            }
        });
    }
    
    
    private void forceGarbage()   {
        System.gc();
        //System.runFinalization();
        //System.gc();
    }
    
    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        if(callback != null)   {
            return callback.createDefaultActions();
        }
        Action[] a = {};
        return a;
    }

    @Override
    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(Statistic.class);
        result.addLookupListener(listener);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(listener);
    }

    @Override
    public void componentShowing() {
        
    }

    @Override
    public void componentHidden() {
        
    }

    @Override
    public void componentActivated() {
        
    }

    @Override
    public void componentDeactivated() {
        
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
    
    
    private class LookupListStat implements LookupListener   {

        @Override
        public void resultChanged(LookupEvent ev) {
            Statistic s = Utilities.actionsGlobalContext().lookup(Statistic.class);
            EGTaskDataObjectDataObject dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
            if(dto == null)return;
            if(s!=null)   {
                dataModel.setCellChanges(s.getCellChanges(dto.getPrimaryFile().getName()));
            }else{
                //dataModel.setCellChanges(null);
            }
        }
        
    }

}
