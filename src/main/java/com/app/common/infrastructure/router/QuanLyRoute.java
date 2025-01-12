package com.app.common.infrastructure.router;

import com.app.common.infrastructure.constants.RouterConstant;
import com.app.common.infrastructure.session.SessionLogin;
import com.app.views.UI.sidebarmenu.SidebarMenuItem;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author InuHa
 */
public class QuanLyRoute {

    private static QuanLyRoute instance;

    @Getter
    private List<SidebarMenuItem> itemSideMenu = new ArrayList<>();

    private QuanLyRoute() {
        this.init();
    }

    public static QuanLyRoute getInstance() {
        if (instance == null) {
            instance = new QuanLyRoute();
        }
        return instance;
    }

    private void init() {
        itemSideMenu.add(new SidebarMenuItem(1, "statistic-l", "Thống kê", RouterConstant.THONG_KE));
        itemSideMenu.add(new SidebarMenuItem(2, "sell-l", "Bán hàng", RouterConstant.BAN_HANG));
        itemSideMenu.add(new SidebarMenuItem(3, "product-l", "Sản phẩm", RouterConstant.SAN_PHAM));
        itemSideMenu.add(new SidebarMenuItem(4, "voucher-l", "Phiếu giảm giá", RouterConstant.PHIEU_GIAM_GIA));
        itemSideMenu.add(new SidebarMenuItem(5, "receipt-l", "Hoá đơn", RouterConstant.HOA_DON));
        itemSideMenu.add(new SidebarMenuItem(6, "customer-l", "Khách hàng", RouterConstant.KHACH_HANG));
        itemSideMenu.add(new SidebarMenuItem(7, "users-l", "Nhân viên", RouterConstant.NHAN_VIEN));
        itemSideMenu.add(new SidebarMenuItem(9, "password-l", "Đổi mật khẩu", (button) -> {
            SessionLogin.getInstance().changePassword();
        }));
        itemSideMenu.add(new SidebarMenuItem(10, "logout-l", "Đăng xuất", (button) -> {
            SessionLogin.getInstance().logout();
        }));
    }

}
