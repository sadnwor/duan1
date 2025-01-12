package com.app.views.UI.table;


import com.app.views.UI.table.celll.TableCustomCellRender;
import com.app.views.UI.table.celll.TableHeaderCustomCellRender;
import com.app.views.UI.table.celll.BooleanCellRenderer;
import com.app.views.UI.table.celll.TextAreaCellRenderer;
import com.app.utils.ColorUtils;
import static com.app.utils.ColorUtils.TEXT_SELECTION_TABLE;
import com.app.views.UI.scroll.ScrollBarCustomUI;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author InuHa
 */
public class TableCustomUI {
    
    public static void apply(JScrollPane scroll, TableType type) {

        JTable table = (JTable) scroll.getViewport().getComponent(0);

        table.setFocusable(false);
        table.setBorder(BorderFactory.createEmptyBorder());
        table.setBackground(ColorUtils.BACKGROUND_TABLE);
        table.setSelectionForeground(ColorUtils.TEXT_SELECTION_TABLE);
        table.setSelectionBackground(ColorUtils.BACKGROUND_SELECTED);
        table.setGridColor(ColorUtils.BORDER);
	
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, """
            height:30;
            hoverBackground:null;
            pressedBackground:null;
            separatorColor:null;
            font:bold;
        """);
        table.getTableHeader().setDefaultRenderer(new TableHeaderCustomCellRender(table));
        //table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table));

        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        table.setRowHeight(40);
        HoverIndex hoverRow = new HoverIndex();
        TableCellRenderer cellRender;
        if (type == TableType.DEFAULT) {
            cellRender = new TableCustomCellRender(hoverRow);
        } else {
            cellRender = new TextAreaCellRenderer(hoverRow);
        }
        table.setDefaultRenderer(Object.class, cellRender);
        table.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(hoverRow));
        
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(false);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        scroll.setBorder(BorderFactory.createEmptyBorder());
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ColorUtils.BORDER);
                g2.setStroke(new BasicStroke(4));
                //g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        
        panel.setBackground(ColorUtils.BACKGROUND_TABLE);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, panel);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(table.getBackground());
        scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
        scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());
        
        table.getTableHeader().setBackground(table.getBackground());
        table.getTableHeader().setForeground(ColorUtils.TEXT_TABLE);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow.setIndex(-1);
                table.repaint();
            }

        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoverRow.getIndex()) {
                    hoverRow.setIndex(row);
                    table.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoverRow.getIndex()) {
                    hoverRow.setIndex(row);
                    table.repaint();
                }
            }
        });
    }

    public static void resizeColumnHeader(JTable table) {
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = table.getTableHeader();
        TableColumnModel columnModel = table.getColumnModel();

        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = columnModel.getColumn(column);

            int maxWidth = 0;

            TableCellRenderer headerRenderer = tableColumn.getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = header.getDefaultRenderer();
            }
            Component headerComp = headerRenderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, 0, column);
            maxWidth = headerComp.getPreferredSize().width;

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component cellComp = cellRenderer.getTableCellRendererComponent(table, table.getValueAt(row, column), false, false, row, column);
                maxWidth = Math.max(maxWidth, cellComp.getPreferredSize().width);
            }

            tableColumn.setPreferredWidth(maxWidth + table.getIntercellSpacing().width + 30);
        }
    }
    
    private static void adjustLastColumnWidth(JTable table) {
        int totalWidth = table.getPreferredSize().width;
        int visibleWidth = table.getVisibleRect().width;
        TableColumnModel columnModel = table.getColumnModel();
        int lastColumnIndex = columnModel.getColumnCount() - 1;
        TableColumn lastColumn = columnModel.getColumn(lastColumnIndex);

        // Đảm bảo cột cuối cùng bám sát vào bên phải
        int lastColumnWidth = visibleWidth - getTotalColumnWidthsExceptLast(table, lastColumnIndex);
        lastColumn.setPreferredWidth(lastColumnWidth);
    }

    private static int getTotalColumnWidthsExceptLast(JTable table, int lastColumnIndex) {
        TableColumnModel columnModel = table.getColumnModel();
        int totalWidth = 0;

        for (int i = 0; i < lastColumnIndex; i++) {
            TableColumn column = columnModel.getColumn(i);
            totalWidth += column.getPreferredWidth();
        }

        return totalWidth;
    }
    
    public static enum TableType {
        MULTI_LINE, DEFAULT
    }

}
