package com.app.views.UI.label;

import com.app.utils.ColorUtils;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLabel;

/**
 *
 * @author inuHa
 */
public class Badge extends JLabel {
    
    public Badge(String text) {
        super(text);
        setOpaque(false);
    }

    public void success() {
	setBackground(ColorUtils.SUCCESS_COLOR);
    }

    public void danger() {
	setBackground(ColorUtils.DANGER_COLOR);
    }
	
    public void warning() {
	setBackground(ColorUtils.WARNING_COLOR);
    }
    
    public void info() {
	setBackground(ColorUtils.INFO_COLOR);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(getBackground());
        g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));

        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(getText(), textX, textY);

        g2d.dispose();
    }
    
}
