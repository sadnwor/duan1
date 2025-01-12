package com.app.utils;

import com.app.Application;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.EventQueue;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.Getter;

/**
 *
 * @author InuHa
 */
public class ThemeUtils {
    
    @Getter
    private static boolean isLight = false;
    
    public static void switchTheme() {
        EventQueue.invokeLater(() -> {
            try {
                if (isLight) {
                    UIManager.setLookAndFeel(new FlatMacDarkLaf());
                    ColorUtils.changeColorDark();
                } else {
                    UIManager.setLookAndFeel(new FlatMacLightLaf());
                    ColorUtils.changeColorLight();
                }

                
                isLight = !isLight;
                SwingUtilities.updateComponentTreeUI(Application.app);
            } catch (UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }
            FlatLaf.updateUI();
        });   
    }
    
}
