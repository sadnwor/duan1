/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.utils;

import java.awt.Color;

/**
 *
 * @author inuHa
 */
public class ColorUtils {

    public static Color SIDEBAR;
    
    public static Color SIDEBAR_HOVER;
    
    public static Color SIDEBAR_TITLE;
    
    public static Color PRIMARY_COLOR;
    
    public static Color PRIMARY_TEXT;
    
    public static Color TEXT_GRAY;

    public static Color DANGER_COLOR;

    public static Color INFO_COLOR;

    public static Color SUCCESS_COLOR;

    public static Color WARNING_COLOR;
    
    public static Color BORDER;

    public static Color BACKGROUND_GRAY;
	
    public static Color BACKGROUND_SELECTED;

    public static Color BACKGROUND_DASHBOARD;
	
    public static Color BACKGROUND_TABLE;
    
    public static Color BACKGROUND_HOVER;
    
    public static Color BACKGROUND_TABLE_ODD;
        
    public static Color TEXT_TABLE;
    
    public static Color TEXT_SELECTION_TABLE;
            
    public static Color BUTTON_PRIMARY;

    public static Color BUTTON_GRAY;
        
    public static Color INPUT_PRIMARY;
    
    static {
        if (ThemeUtils.isLight()) {
            changeColorLight();
        } else {
            changeColorDark();
        }
    }
    
    public static void changeColorLight() { 
        SIDEBAR = new Color(34, 34, 34);
        PRIMARY_TEXT = new Color(63, 66, 72);
        BACKGROUND_GRAY = new Color(248, 249, 249);
        BACKGROUND_SELECTED = new Color(34, 34, 34);
        BACKGROUND_DASHBOARD = new Color(248, 249, 249);
        BACKGROUND_TABLE = new Color(241, 243, 244);
        BACKGROUND_TABLE_ODD = new Color(241, 243, 244);
        TEXT_TABLE = new Color(51, 51, 51);
        BUTTON_PRIMARY = new Color(56, 53, 67);
        BUTTON_GRAY = new Color(221, 226, 228);
        INPUT_PRIMARY = new Color(241, 243, 244);
        BORDER = new Color(212, 219, 221);
    }
    
    public static void changeColorDark() {
        SIDEBAR = new Color(35, 36, 40);
        SIDEBAR_HOVER = new Color(63, 66, 72);
        SIDEBAR_TITLE = new Color(234, 108, 32);
        PRIMARY_COLOR = new Color(234, 108, 32);
        PRIMARY_TEXT = new Color(189, 189, 189);
        DANGER_COLOR = new Color(255, 0, 98);
        INFO_COLOR = new Color(0, 153, 255);
        SUCCESS_COLOR = new Color(74, 175, 9);
        WARNING_COLOR = new Color(253, 174, 0);
        BORDER = new Color(63, 66, 72);
        BACKGROUND_GRAY = new Color(43, 45, 49);
        BACKGROUND_SELECTED = new Color(234, 108, 32);
        BACKGROUND_DASHBOARD = new Color(35, 36, 40);
        BACKGROUND_TABLE = new Color(43, 45, 49);
        BACKGROUND_HOVER = new Color(63, 66, 72);
        BACKGROUND_TABLE_ODD = new Color(43, 45, 49); //new Color(63, 66, 72);
        TEXT_TABLE = new Color(144, 147, 153);
        TEXT_GRAY = new Color(189, 189, 189);
        TEXT_SELECTION_TABLE = new Color(255, 255, 255);
        BUTTON_PRIMARY = new Color(234, 108, 32);
        BUTTON_GRAY = new Color(49, 51, 56);
        INPUT_PRIMARY = new Color(43, 45, 49);
    }
        
    /**
     * Chuyển đổi mã hex thành color
     * @param hex VD: #FFFFFF
     * @return java.awt.Color
     */
    public static Color hexToColor(String hex) {
        hex = hex.replace("#", "");

        int length = hex.length();
        if (length == 6) {
            int r = Integer.valueOf(hex.substring(0, 2), 16);
            int g = Integer.valueOf(hex.substring(2, 4), 16);
            int b = Integer.valueOf(hex.substring(4, 6), 16);
            return new Color(r, g, b);
        } else if (length == 8) {
            int r = Integer.valueOf(hex.substring(0, 2), 16);
            int g = Integer.valueOf(hex.substring(2, 4), 16);
            int b = Integer.valueOf(hex.substring(4, 6), 16);
            int a = Integer.valueOf(hex.substring(6, 8), 16);
            return new Color(r, g, b, a);
        } else {
            throw new IllegalArgumentException("Địng dạng hex không hợp lệ: " + hex);
        }
    }

    /**
     * Làm tối màu từ màu chỉ định
     * @param color VD: new Color(250, 250, 250)
     * @param factor càng thấp càng tối (từ 0.0f -> 1.0f) VD: 0.3f
     * @return java.awt.Color
     */
    public static Color darken(Color color, float factor) {
        int r = Math.max((int)(color.getRed() * factor), 0);
        int g = Math.max((int)(color.getGreen() * factor), 0);
        int b = Math.max((int)(color.getBlue() * factor), 0);
        return new Color(r, g, b, color.getAlpha());
    }

    /**
     * Làm sáng màu từ màu chỉ định
     * @param color VD: new Color(0, 0, 0)
     * @param factor càng cao càng sáng (từ 0.0f -> 1.0f) VD: 0.3f
     * @return java.awt.Color
     */
    public static Color lighten(Color color, float factor) {
        int r = Math.min((int)(color.getRed() + (255 - color.getRed()) * factor), 255);
        int g = Math.min((int)(color.getGreen() + (255 - color.getGreen()) * factor), 255);
        int b = Math.min((int)(color.getBlue() + (255 - color.getBlue()) * factor), 255);
        return new Color(r, g, b, color.getAlpha());
    }

    /**
     * Làm tối màu từ màu chỉ định
     * @param hex VD: #ffffff
     * @param factor càng thấp càng tối (từ 0.0f -> 1.0f) VD: 0.3f
     * @return java.awt.Color
     */
    public static Color darken(String hex, float factor) {
        return darken(hexToColor(hex), factor);
    }

    /**
     * Làm sáng màu từ màu chỉ định
     * @param hex VD: #000000
     * @param factor càng cao càng sáng (từ 0.0f -> 1.0f) VD: 0.3f
     * @return java.awt.Color
     */
    public static Color lighten(String hex, float factor) {
        return lighten(hexToColor(hex), factor);
    }



}
