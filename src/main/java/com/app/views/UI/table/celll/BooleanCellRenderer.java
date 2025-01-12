package com.app.views.UI.table.celll;

import com.app.utils.ColorUtils;
import com.app.views.UI.table.HoverIndex;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author InuHa
 */
public class BooleanCellRenderer extends TableCustomCellRender {

    
    public BooleanCellRenderer(HoverIndex hoverRow) {
        super(hoverRow);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        JCheckBox ch = new JCheckBox() { 
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
        ch.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ch.setOpaque(true);
        ch.setSelected((boolean) value);
        ch.setBackground(com.getBackground());
        return ch;
    }

}
