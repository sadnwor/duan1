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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author inuHa
 */
public class TableImageCellRender implements TableCellRenderer {

    private final TableCellRenderer oldCellRenderer;

    public TableImageCellRender(JTable table) {
        oldCellRenderer = table.getDefaultRenderer(Object.class);
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
        Component com = oldCellRenderer.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
       
        ImageIcon icon = o instanceof ImageIcon ? (ImageIcon) o : ResourceUtils.getImageAssets("images/no-image.jpeg");
        TableImagePanel cell = new TableImagePanel(icon) {
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