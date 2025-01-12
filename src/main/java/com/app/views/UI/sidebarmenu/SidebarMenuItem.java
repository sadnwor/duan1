package com.app.views.UI.sidebarmenu;

import lombok.Getter;

import javax.swing.*;

/**
 *
 * @author InuHa
 */
public class SidebarMenuItem {

    @Getter
    private int index;

    @Getter
    private String icon;

    @Getter
    private String label;

    @Getter
    private String packageComponent;

    @Getter
    private ISidebarMenuButtonCallback callback;

    public SidebarMenuItem(int index, String icon, String label, String packageComponent) {
        this.index = index;
        this.icon = icon;
        this.label = label;
        this.packageComponent = packageComponent;
    }

    public SidebarMenuItem(int index, String icon, String label, ISidebarMenuButtonCallback callback) {
        this.index = index;
        this.icon = icon;
        this.label = label;
        this.callback = callback;
    }

}
