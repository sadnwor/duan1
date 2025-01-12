package com.app.core.inuha.views.quanly.components.table.khachhang;

import com.app.utils.ColorUtils;
import com.app.views.UI.table.celll.TableActionCellRender;
import java.awt.Component;
import javax.swing.JTable;

/**
 *
 * @author InuHa
 */
public class InuhaKhachHangTableActionCellRender extends TableActionCellRender {
    
    public InuhaKhachHangTableActionCellRender(JTable table) {
        super(table);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object o, boolean isSeleted, boolean bln1, int row, int column) {
        Component com = super.getTableCellRendererComponent(table, o, isSeleted, bln1, row, column);
        
        InuhaKhachHangTableActionPanel actionPanel = new InuhaKhachHangTableActionPanel();
        
        if (isSeleted == false) {
            if (row == hoverRow.getIndex()) {
                actionPanel.setBackground(ColorUtils.BACKGROUND_HOVER);
                actionPanel.setForeground(table.getSelectionForeground());
            } else {
                actionPanel.setBackground(row % 2 == 0 ? ColorUtils.BACKGROUND_TABLE_ODD : table.getBackground());
                actionPanel.setForeground(ColorUtils.TEXT_TABLE);
            }
        } else {
            actionPanel.setBackground(table.getSelectionBackground());
            actionPanel.setForeground(table.getSelectionForeground());
        }
        
        return actionPanel;
    }
}
