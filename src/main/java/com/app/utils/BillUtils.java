package com.app.utils;

import com.app.common.helper.TestConnection;
import com.app.common.infrastructure.constants.PhuongThucThanhToanConstant;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.request.InuhaFilterHoaDonChiTietRequest;
import com.app.core.inuha.services.InuhaHoaDonChiTietService;
import com.app.core.inuha.services.InuhaHoaDonService;
import io.github.cdimascio.dotenv.Dotenv;
import javax.swing.*;

/**
 *
 * @author inuHa
 */
public class BillUtils {

    private static final String PREFIX_CODE_HOADON = "HD-";
    
    private static final String PREFIX_CODE_HOADONCHITIET = "HDCT-";
	
    private static final int LENGTH_CODE = 6;

    public static String generateCodeHoaDon() {
        int lastId = Integer.parseInt(InuhaHoaDonService.getInstance().getLastId());
        if (lastId == 1) { 
            if (InuhaHoaDonService.getInstance().count(new FilterRequest()) < 1) {
                lastId--;
            }
        }
        return PREFIX_CODE_HOADON + CurrencyUtils.startPad(String.valueOf(++lastId), LENGTH_CODE, '0');
    }

    public static String generateCodeHoaDonChiTiet() {
        int lastId = Integer.parseInt(InuhaHoaDonChiTietService.getInstance().getLastId());
        if (lastId == 1) { 
            if (InuhaHoaDonChiTietService.getInstance().count() < 1) {
                lastId--;
            }
        }
        return PREFIX_CODE_HOADONCHITIET + CurrencyUtils.startPad(String.valueOf(++lastId), LENGTH_CODE, '0');
    }

    
    public static String getTrangThai(int trangThai) { 
	return switch (trangThai) {
	    case TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN -> "Đã thanh toán";
	    case TrangThaiHoaDonConstant.STATUS_DA_HUY -> "Đã huỷ";
	    case TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN -> "Chờ thanh toán";
	    default -> "Không xác định";
	};
    }

    public static String getPhuongThucThanhToan(int phuongThuc) { 
	return switch (phuongThuc) {
	    case PhuongThucThanhToanConstant.TIEN_MAT -> "Tiền mặt";
	    case PhuongThucThanhToanConstant.CHUYEN_KHOAN -> "Chuyển khoản";
	    case PhuongThucThanhToanConstant.KET_HOP -> "Tiền mặt và chuyển khoản";
	    default -> "Không xác định";
	};
    }
    
}
