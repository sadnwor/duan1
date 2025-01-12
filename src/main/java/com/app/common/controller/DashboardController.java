package com.app.common.controller;

import com.app.views.DashboardView;
import com.app.views.UI.scroll.ScrollBarCustomUI;
import com.app.views.UI.sidebarmenu.SidebarMenuContent;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author InuHa
 */
public class DashboardController {

    private DashboardView context;

    private SidebarMenuContent content;

    private static DashboardController instance;

    private DashboardController() {
    }

    public static DashboardController getInstance() {
        if (instance == null) {
            instance = new DashboardController();
        }
        return instance;
    }

    public DashboardView getContext() {
        return context;
    }

    public DashboardController setContext(DashboardView context) {
        this.context = context;
        return this;
    }

    public SidebarMenuContent getContent() {
        return content;
    }

    public DashboardController setContent(SidebarMenuContent content) {
        this.content = content;
        return this;
    }

    public void show(JComponent component) {
        if (this.context == null) {
            System.out.println("Context not found");
            return;
        }
        
	EventQueue.invokeLater(() -> {
	    content.removeAll();
	    JScrollPane scroll = new JScrollPane();
	    scroll.setViewportBorder(null);
	    scroll.setBorder(null);
	    scroll.getViewport().setOpaque(false);
	    scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
	    scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());
	    scroll.setViewportView(component);
	    content.add(scroll);
	    content.revalidate();
	    content.repaint();
	});
    }

}
