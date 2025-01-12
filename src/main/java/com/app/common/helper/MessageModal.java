/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.common.helper;

import com.app.utils.ColorUtils;
import com.app.utils.ResourceUtils;

import java.awt.*;
import java.util.concurrent.CountDownLatch;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.popup.component.PopupController;
/**
 *
 * @author inuHa
 */
public class MessageModal {

    public static final int TYPE_INFO = 0;
    
    public static final int TYPE_DANGER = 1;
    
    public static final int TYPE_SUCCESS = 2;
    
    public static final int TYPE_WARNING = 3;

    public static final String[] BUTTON_DEFAULT = new String[] {"Huỷ", "Tiếp tục"};

    private static JPanel renderBody(String message, Integer type) {
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 0 40 10 40", "[left]", "[center]"));
        JLabel msg = new JLabel();
        msg.setMaximumSize(new Dimension(400, 100));
        msg.setText("<html><body style='line-height: 2;padding-left:20px'>"+ message +"</body></html>");
        msg.setForeground(ColorUtils.PRIMARY_TEXT);
        msg.setFont(msg.getFont().deriveFont(Font.PLAIN, 14));
        if (type != null) {
            msg.setIcon(getIcon(type));
        }

        panel.add(msg);
        return panel;
    }
    
    private static FlatSVGIcon getIcon(int icon) {
        String name = null;
        switch (icon) {
            case TYPE_INFO:
                name = "info.svg";
                break;

            case TYPE_DANGER:
                name = "error.svg";
                break;

            case TYPE_SUCCESS:
                name = "success.svg";
                break;

            case TYPE_WARNING:
                name = "warning.svg";
                break;

            default:
                return null;
        }

        return ResourceUtils.getSVG("/svg/" + name, new Dimension(48, 48));
    }
	
    private static void messageModal(String message) {
	messageModal(null, message, null);
    }
    
    private static void messageModal(String message, Integer type) {
	messageModal(null, message, type);
    }
	
    private static void messageModal(String title, String message, Integer type) {
        SimplePopupBorder simplePopupBorder = new SimplePopupBorder(
            renderBody(message, type),
            title,
            new String[] {"OK"},
            (controller, action) -> {
                controller.closePopup();
            }
        );

        GlassPanePopup.showPopup(simplePopupBorder);
    }
	
    private static void messageModal(JComponent component) {
	messageModal(null, component);
    }
    
    private static void messageModal(String title, JComponent component) {
        SimplePopupBorder simplePopupBorder = new SimplePopupBorder(
            component,
            title,
            new String[] {"OK"},
            (controller, action) -> {
                controller.closePopup();
            }
        );

        GlassPanePopup.showPopup(simplePopupBorder);
    }
    
    public static void info(String message) {
	info(null, message);
    }
    
    public static void info(String title, String message) {
	messageModal(title, message, TYPE_INFO);
    }
  
    public static void error(String message) {
	error(null, message);
    }
    
    public static void error(String title, String message) {
	messageModal(title, message, TYPE_DANGER);
    }

    public static void warning(String message) {
	warning(null, message);
    }
	
    public static void warning(String title, String message) {
	messageModal(title, message, TYPE_WARNING);
    }
    
    public static void success(String message) {
	success(null, message);
    }
	
    public static void success(String title, String message) {
	messageModal(title, message, TYPE_SUCCESS);
    }

    private static boolean messageConfirm(String title, JComponent content, String[] labelButton) {
        final String name = "POPUP_MODAL_CONFIRM";
        if (GlassPanePopup.isShowing(name)) { 
            return false;
        }
        
        final boolean[] isConfirm = { false };
        CountDownLatch latch = new CountDownLatch(1);

        SimplePopupBorder simplePopupBorder = new SimplePopupBorder(
            content,
            title,
            labelButton,
            (controller, action) -> {
                isConfirm[0] = action == 1;

                controller.closePopup();
                latch.countDown();
            }
        );

        
        GlassPanePopup.showPopup(simplePopupBorder, name);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isConfirm[0];
    }

    public static boolean confirm(String message) {
        return confirm(null, message, BUTTON_DEFAULT);
    }

    public static boolean confirm(String title, String message) {
        return confirm(title, message, BUTTON_DEFAULT);
    }

    public static boolean confirm(String message, String[] labelButton) {
        return confirm(null, message, labelButton);
    }

    public static boolean confirm(String title, String message, String[] labelButton) {
        JPanel content = new JPanel(new MigLayout("wrap, fillx, insets 0 40 10 40", "[left]", "[center]"));
        JLabel msg = new JLabel(message);
        msg.setForeground(ColorUtils.PRIMARY_TEXT);
        msg.setFont(msg.getFont().deriveFont(Font.PLAIN, 14));
        content.add(msg);
        return messageConfirm(title, content, labelButton);
    }

    public static boolean confirm(JComponent content) {
        return confirm(null, content, BUTTON_DEFAULT);
    }

    public static boolean confirm(String title, JComponent content) {
        return confirm(title, content, BUTTON_DEFAULT);
    }

    public static boolean confirm(JComponent content, String[] labelButton) {
        return confirm(null, content, labelButton);
    }

    public static boolean confirm(String title, JComponent content, String[] labelButton) {
        return messageConfirm(title, content, labelButton);
    }

    public static boolean confirmInfo(String message) {
        return confirmInfo(null, message, BUTTON_DEFAULT);
    }

    public static boolean confirmInfo(String title, String message) {
        return confirmInfo(title, message, BUTTON_DEFAULT);
    }

    public static boolean confirmInfo(String message, String[] labelButton) {
        return confirmInfo(null, message, labelButton);
    }

    public static boolean confirmInfo(String title, String message, String[] labelButton) {
        JPanel content = renderBody(message, TYPE_INFO);
        return messageConfirm(title, content, labelButton);
    }

    public static boolean confirmError(String message) {
        return confirmError(null, message, BUTTON_DEFAULT);
    }

    public static boolean confirmError(String title, String message) {
        return confirmError(title, message, BUTTON_DEFAULT);
    }

    public static boolean confirmError(String message, String[] labelButton) {
        return confirmError(null, message, labelButton);
    }

    public static boolean confirmError(String title, String message, String[] labelButton) {
        JPanel content = renderBody(message, TYPE_DANGER);
        return messageConfirm(title, content, labelButton);
    }


    public static boolean confirmWarning(String message) {
        return confirmWarning(null, message, BUTTON_DEFAULT);
    }

    public static boolean confirmWarning(String title, String message) {
        return confirmWarning(title, message, BUTTON_DEFAULT);
    }

    public static boolean confirmWarning(String message, String[] labelButton) {
        return confirmWarning(null, message, labelButton);
    }

    public static boolean confirmWarning(String title, String message, String[] labelButton) {
        JPanel content = renderBody(message, TYPE_WARNING);
        return messageConfirm(title, content, labelButton);
    }


    public static boolean confirmSuccess(String message) {
        return confirmSuccess(null, message, BUTTON_DEFAULT);
    }

    public static boolean confirmSuccess(String title, String message) {
        return confirmSuccess(title, message, BUTTON_DEFAULT);
    }

    public static boolean confirmSuccess(String message, String[] labelButton) {
        return confirmSuccess(null, message, labelButton);
    }

    public static boolean confirmSuccess(String title, String message, String[] labelButton) {
        JPanel content = renderBody(message, TYPE_SUCCESS);
        return messageConfirm(title, content, labelButton);
    }

    public static void close() {
        GlassPanePopup.closePopupLast();
    }

    public static void closeAll() {
        GlassPanePopup.closePopupAll();
    }

    public interface Callback {

        void onConfirm(PopupController controller);

        void onCancel(PopupController controller);

    }

}
