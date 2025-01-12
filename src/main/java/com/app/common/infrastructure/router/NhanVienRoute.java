package com.app.common.infrastructure.router;

import com.app.common.infrastructure.constants.RouterConstant;
import com.app.common.infrastructure.session.SessionLogin;
import com.app.views.UI.sidebarmenu.SidebarMenuItem;
import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author InuHa
 */
public class NhanVienRoute {

    private static NhanVienRoute instance;

    private NhanVienRoute() {
	
    }

    public static NhanVienRoute getInstance() {
        if (instance == null) {
            instance = new NhanVienRoute();
        }
        return instance;
    }

    public List<SidebarMenuItem> getItemSideMenu() {
	List<SidebarMenuItem> itemSideMenu = new ArrayList<>();
	
        itemSideMenu.add(new SidebarMenuItem(1, "sell-l", "Bán hàng", RouterConstant.BAN_HANG));
        itemSideMenu.add(new SidebarMenuItem(2, "receipt-l", "Hoá đơn", RouterConstant.HOA_DON));
        itemSideMenu.add(new SidebarMenuItem(3, "customer-l", "Khách hàng", RouterConstant.KHACH_HANG));
        itemSideMenu.add(new SidebarMenuItem(4, "password-l", "Đổi mật khẩu", (button) -> {
            SessionLogin.getInstance().changePassword();
        }));
        itemSideMenu.add(new SidebarMenuItem(5, "logout-l", "Đăng xuất", (button) -> {
            SessionLogin.getInstance().logout();
        }));
	
	return itemSideMenu;
    }

}
