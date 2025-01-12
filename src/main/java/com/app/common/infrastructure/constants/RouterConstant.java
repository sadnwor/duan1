package com.app.common.infrastructure.constants;

import com.app.core.dattv.views.DatHoaDonView;
import com.app.core.inuha.views.all.InuhaBanHangView;
import com.app.core.inuha.views.quanly.InuhaNhanVienView;
import com.app.core.inuha.views.quanly.InuhaThongKeView;
import com.app.core.inuha.views.quanly.InuhaSanPhamView;
import com.app.core.inuha.views.quanly.InuhaPhieuGiamGiaView;
import com.app.core.lam.views.LamKhachHangView;
import com.app.utils.RouterUtils;

/**
 *
 * @author inuHa
 */
public class RouterConstant {

    public final static String THONG_KE = RouterUtils.getPackageName(InuhaThongKeView.class);
    
    public final static String BAN_HANG = RouterUtils.getPackageName(InuhaBanHangView.class);
	
    public final static String SAN_PHAM = RouterUtils.getPackageName(InuhaSanPhamView.class);
    
    public final static String PHIEU_GIAM_GIA = RouterUtils.getPackageName(InuhaPhieuGiamGiaView.class);
        
    public final static String HOA_DON = RouterUtils.getPackageName(DatHoaDonView.class);
    
    public final static String KHACH_HANG = RouterUtils.getPackageName(LamKhachHangView.class);
    
    public final static String NHAN_VIEN = RouterUtils.getPackageName(InuhaNhanVienView.class);
    
}
