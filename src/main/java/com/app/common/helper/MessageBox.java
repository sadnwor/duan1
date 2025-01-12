package com.app.common.helper;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author InuHa
 */
public class MessageBox {

    private static final String TITLE_DEFAULT = System.getProperty("APP_NAME");

    public static void alert(Component parent, String message) {
        message(parent, TITLE_DEFAULT, message, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void alert(Component parent, String title, String message) {
        message(parent, title, message, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        message(parent, TITLE_DEFAULT, message, JOptionPane.ERROR_MESSAGE);
    }

    public static void error(Component parent, String title, String message) {
        message(parent, title, message, JOptionPane.ERROR_MESSAGE);
    }

    public static void warning(Component parent, String message) {
        message(parent, TITLE_DEFAULT, message, JOptionPane.WARNING_MESSAGE);
    }

    public static void warning(Component parent, String title, String message) {
        message(parent, title, message, JOptionPane.WARNING_MESSAGE);
    }

    private static void message(Component parent, String title, String message, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }

    public static boolean confirm(Component parent, String message) {
        return confirm(parent, TITLE_DEFAULT, message);
    }

    public static boolean confirm(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    public static String prompt(Component parent, String message) {
        return prompt(parent, TITLE_DEFAULT, message);
    }

    public static String prompt(Component parent, String title, String message) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

}
