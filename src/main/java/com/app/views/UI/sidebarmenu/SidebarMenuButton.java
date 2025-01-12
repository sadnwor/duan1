package com.app.views.UI.sidebarmenu;

import com.app.utils.ColorUtils;
import static com.app.utils.ColorUtils.TEXT_GRAY;
import com.app.utils.ThemeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author InuHa
 */
public class SidebarMenuButton extends JButton {

    private int index;

    private ISidebarMenuButtonCallback callback;

    private float animate;

    public SidebarMenuButton(int index, ISidebarMenuButtonCallback callback) {
        this.index = index;
        this.callback = callback;
        setContentAreaFilled(false);
        setForeground(ColorUtils.TEXT_GRAY);

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.LEFT);
        setBackground(ColorUtils.SIDEBAR_HOVER);
        setBorder(new EmptyBorder(8, 30, 8, 15));
    }

    public float getAnimate() {
        return animate;
    }

    public void setAnimate(float animate) {
        this.animate = animate;
        this.repaint();
    }

    public int getIndex() {
        return index;
    }

    public ISidebarMenuButtonCallback getCallback() {
        return callback;
    }

    @Override
    public void paint(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double width = getWidth();
        double height = getHeight();
        double x = animate * width - width;
        Area area = new Area(new RoundRectangle2D.Double(x, 0, width, height, height, height));
        area.add(new Area(new Rectangle2D.Double(x, 0, height, height)));
        g2.setColor(getBackground());
        g2.fill(area);
        g2.dispose();
        super.paint(grphcs);
    }

}
