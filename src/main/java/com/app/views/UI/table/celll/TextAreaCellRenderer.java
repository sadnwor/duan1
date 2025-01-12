package com.app.views.UI.table.celll;

import com.app.utils.ColorUtils;
import com.app.views.UI.table.HoverIndex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author InuHa
 */
public class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {

    private final List<List<Integer>> rowAndCellHeights = new ArrayList<>();
    private final HoverIndex hoverRow;

   
    public TextAreaCellRenderer(HoverIndex hoverRow) {
        this.hoverRow = hoverRow;
        setWrapStyleWord(true);
        setLineWrap(true);
        setOpaque(true);
        setEditable(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(Objects.toString(value, ""));
        adjustRowHeight(table, row, column);
        
                
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            if (row == hoverRow.getIndex()) {
                setBackground(ColorUtils.BACKGROUND_HOVER);
            } else {
                setBackground(row % 2 == 0 ? ColorUtils.BACKGROUND_TABLE_ODD : table.getBackground());
            }

        }
        setFont(table.getFont());
        return this;
    }
  

    private void adjustRowHeight(JTable table, int row, int column) {
        setBounds(table.getCellRect(row, column, false));
        int preferredHeight = getPreferredSize().height;
        while (rowAndCellHeights.size() <= row) {
            rowAndCellHeights.add(new ArrayList<>(column));
        }
        List<Integer> list = rowAndCellHeights.get(row);
        while (list.size() <= column) {
            list.add(0);
        }
        list.set(column, preferredHeight);
        int max = list.stream().max(Integer::compare).get();
        if (table.getRowHeight(row) != max) {
            table.setRowHeight(row, max);
        }
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
    
}
