package com.app.views.UI.table.celll;

import com.app.utils.ColorUtils;
import com.app.utils.ResourceUtils;
import com.app.views.UI.table.HoverIndex;
import com.app.views.UI.table.TableActionPanel;
import com.app.views.UI.table.TableImagePanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author inuHa
 */
public class TableAlignCenterCellRender extends DefaultTableCellRenderer {

    protected HoverIndex hoverRow = new HoverIndex();

    public TableAlignCenterCellRender(JTable table) {
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

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
        Component com = super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
	setHorizontalAlignment(SwingConstants.CENTER);
        if (bln == false) {
            if (i == hoverRow.getIndex()) {
                com.setBackground(ColorUtils.BACKGROUND_HOVER);
            } else {
                com.setBackground(i % 2 == 0 ? ColorUtils.BACKGROUND_TABLE_ODD : jtable.getBackground());
            }
        } else {
            com.setBackground(jtable.getSelectionBackground());
        }
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
}