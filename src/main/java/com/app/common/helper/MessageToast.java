/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.common.helper;

import raven.toast.Notifications;

/**
 *
 * @author inuHa
 */
public class MessageToast {

    public static void info(String message) {
        show(message, Notifications.Type.INFO);
    }

    public static void error(String message) {
        show(message, Notifications.Type.ERROR);
    }

    public static void warning(String message) {
        show(message, Notifications.Type.WARNING);
    }

    public static void success(String message) {
        show(message, Notifications.Type.SUCCESS);
    }

    private static void show(String message, Notifications.Type type) {
        Notifications.getInstance().show(type, message);
    }

    public static void clear() {
        Notifications.getInstance().clearHold();
    }

    public static void clearAll() {
        Notifications.getInstance().clearAll();
    }

}
