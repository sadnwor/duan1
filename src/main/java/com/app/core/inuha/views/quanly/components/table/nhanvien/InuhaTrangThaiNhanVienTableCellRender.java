package com.app.core.inuha.views.quanly.components.table.nhanvien;

import com.app.core.inuha.views.quanly.components.table.trangthai.*;
import com.app.utils.ColorUtils;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author InuHa
 */
public class InuhaTrangThaiNhanVienTableCellRender implements TableCellRenderer {
    
    private final TableCellRenderer oldCellRenderer;
    
    public InuhaTrangThaiNhanVienTableCellRender(JTable table) {
	this.oldCellRenderer = table.getDefaultRenderer(Object.class);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object o, boolean isSeleted, boolean bln1, int row, int column) {
        Component com = oldCellRenderer.getTableCellRendererComponent(table, o, isSeleted, bln1, row, column);
        
	boolean trangThai = (boolean) o;
        InuhaTrangThaiNhanVienTablePanel cell = new InuhaTrangThaiNhanVienTablePanel(trangThai) { 
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ColorUtils.BORDER);
                g2.setStroke(new BasicStroke(1));
                //g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
	};
        cell.setBackground(com.getBackground());
        return cell;
    }
}
