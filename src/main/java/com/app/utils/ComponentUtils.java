/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.utils;

import com.app.utils.ColorUtils;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author inuHa
 */
public class ComponentUtils {

    public static ImageIcon resizeImageByWidth(ImageIcon imageIcon, int newWidth) {
        Image image = imageIcon.getImage();

        double scale = (double) newWidth / imageIcon.getIconWidth();

        int newHeight = (int) (imageIcon.getIconHeight() * scale);

        Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(resizedImage);
    }

    public static ImageIcon resizeImageByHeight(ImageIcon imageIcon, int newHeight) {
        Image image = imageIcon.getImage();

        double scale = (double) newHeight / imageIcon.getIconHeight();

        int newWidth = (int) (imageIcon.getIconWidth() * scale);

        Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(resizedImage);
    }

    public static ImageIcon resizeImage(ImageIcon imageIcon, int newWidth, int newHeight) {
        Image image = imageIcon.getImage();
        Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public static String hiddenText(String text, int maxLength) {
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength - 3) + "...";
        }
        return text;
    }

    public static void setErrorLabel(JLabel label, boolean error, String message) {
        if(error) {
            label.setText(message);
            label.setForeground(ColorUtils.DANGER_COLOR);
            return;
        }

        label.setText(message);
        label.setForeground(ColorUtils.PRIMARY_TEXT);
    }
    
}
