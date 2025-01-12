package com.app.views.UI.scroll;

import com.app.utils.ColorUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author InuHa
 */
public class ScrollBarCustomUI extends BasicScrollBarUI {

    private boolean isMin;
    private boolean isMax;

    private final static Color COLOR_PRIMARY = ColorUtils.hexToColor("#5b5d65");

    private final static Color COLOR_HOVER = ColorUtils.lighten(COLOR_PRIMARY, .3f);

    private final static Color COLOR_DRAG = COLOR_PRIMARY;

    public static void apply(JScrollPane scroll) { 
        scroll.setViewportBorder(null);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
        scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());
    }
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        scrollbar.setOpaque(false);
        scrollbar.setUnitIncrement(20);
        scrollbar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (scrollbar == null) { 
                    return;
                }
                
                BoundedRangeModel br = scrollbar.getModel();

                boolean min = br.getValue() == br.getMinimum();
                boolean max = br.getValue() + br.getExtent() == br.getMaximum();
                if (isMin != min) {
                    isMin = min;
                    scrollbar.repaint();
                } else if (isMax != max) {
                    isMax = max;
                    scrollbar.repaint();
                }
            }
        });
    }

//    @Override
//    protected void paintTrack(Graphics g, JComponent jc, Rectangle rctngl) {
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setColor(jc.getBackground());
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//        int x = rctngl.x;
//        int y = rctngl.y;
//        int width = rctngl.width;
//        int height = rctngl.height;
//        g2.fillRoundRect(x, y, width, height, 1, 1);
//        g2.setComposite(AlphaComposite.SrcOver);
//    }

    @Override
    protected void paintThumb(Graphics grphcs, JComponent jc, Rectangle rctngl) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isDragging) {
            g2.setColor(COLOR_DRAG);
        } else {
            if (isThumbRollover()) {
                g2.setColor(COLOR_HOVER);
            } else {
                g2.setColor(COLOR_PRIMARY);
            }
        }
        int round = 8;
        int spaceX = 2;
        int spaceY = 8;
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            g2.fill(new RoundRectangle2D.Double(rctngl.getX() + spaceX, rctngl.getY() + spaceY, rctngl.getWidth() - spaceX * 2, rctngl.getHeight() - spaceY * 2, round, round));
        } else {
            g2.fill(new RoundRectangle2D.Double(rctngl.getX() + spaceY, rctngl.getY() + spaceX, rctngl.getWidth() - spaceY * 2, rctngl.getHeight() - spaceX * 2, round, round));
        }
        g2.dispose();
    }

    @Override
    protected JButton createIncreaseButton(int i) {
        return new CreateDisabledButton();
    }

    @Override
    protected JButton createDecreaseButton(int i) {
        return new CreateDisabledButton();
    }

    private class CreateDisabledButton extends JButton {

        public CreateDisabledButton() {
            setOpaque(false);
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
        }

    }
}
