package com.app.views.UI.table.celll;

import com.app.utils.ColorUtils;
import com.app.views.UI.table.HoverIndex;
import com.formdev.flatlaf.ui.FlatBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *
 * @author InuHa
 */
public class TableCustomCellRender extends DefaultTableCellRenderer {
    
    private final HoverIndex hoverRow;

    public TableCustomCellRender(HoverIndex hoverRow) {
        this.hoverRow = hoverRow;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(new EmptyBorder(0, 10, 0, 10));
//        if (value instanceof Number) {
//            setHorizontalAlignment(SwingConstants.CENTER);
//        } else {
//            setHorizontalAlignment(SwingConstants.LEFT);
//        }
                
        if (isSelected) {
            com.setBackground(table.getSelectionBackground());
            com.setForeground(table.getSelectionForeground());
        } else {
            if (row == hoverRow.getIndex()) {
                com.setBackground(ColorUtils.BACKGROUND_HOVER);
                com.setForeground(table.getSelectionForeground());
            } else {
                com.setBackground(row % 2 == 0 ? ColorUtils.BACKGROUND_TABLE_ODD : table.getBackground());
                com.setForeground(ColorUtils.TEXT_TABLE);
            }
        }

        com.setFont(table.getFont());
        return com;
    }
    
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
    
    @Override
    protected void paintBorder(Graphics g) {
    }
    
}
