package com.app.views.UI.table.celll;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import com.app.views.UI.table.ITableActionEvent;
import com.app.views.UI.table.TableActionPanel;

/**
 *
 * @author InuHa
 */
public class TableActionCellEditor extends DefaultCellEditor {

    protected ITableActionEvent event;
    
    public TableActionCellEditor(ITableActionEvent event) {
        super(new JCheckBox());
        this.event = event;
    }
        
    @Override
    public Component getTableCellEditorComponent(JTable jtable, Object o, boolean bln, int row, int column) {
        TableActionPanel actionPanel = new TableActionPanel();
        actionPanel.initEvent(event, row);
        actionPanel.setBackground(jtable.getSelectionBackground());
        return actionPanel;
    }
    
}