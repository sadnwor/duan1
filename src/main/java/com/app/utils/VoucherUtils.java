package com.app.utils;

import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author inuHa
 */
public class VoucherUtils {
    
    public final static String STATUS_DANG_DIEN_RA = "Đang diễn ra";
    
    public final static String STATUS_SAP_DIEN_RA = "Sắp diễn ra";
    
    public final static String STATUS_DA_DIEN_RA = "Đã diễn ra";
	
    public static String getKieuGiam(boolean giamTheoPhanTram) { 
	return giamTheoPhanTram ? "Phần trăm" : "Giá trị";
    }
    
    public static String getTextGiaTriGiam(InuhaPhieuGiamGiaModel model) { 
	String value = null;
	if (model.isGiamTheoPhanTram()) { 
	    value = String.valueOf((int) model.getGiaTriGiam()) + "%";
	} else {
	    value = CurrencyUtils.parseString((int) model.getGiaTriGiam());
	}
	return value;
    }
    
    public static double getTienGiam(double tongTienHang, InuhaPhieuGiamGiaModel model) { 
	double value = 0;
	if (model.isGiamTheoPhanTram()) { 
	    value = (tongTienHang / 100) * model.getGiaTriGiam();
	} else {
	    value = model.getGiaTriGiam();
	}
	
	if (model.getGiamToiDa() > 0 && value > model.getGiamToiDa()) {
	    value = (int) model.getGiamToiDa();
	}
	
	value = Math.min(tongTienHang, value);
	return value;
    }
    
    public static String getTrangThai(InuhaPhieuGiamGiaModel model) { 
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	LocalDate today = LocalDate.now();
	
	String startDateString = model.getNgayBatDau();
        String endDateString = model.getNgayKetThuc();
	
        try {
            LocalDate startDate = LocalDate.parse(startDateString, formatter);
            LocalDate endDate = LocalDate.parse(endDateString, formatter);

            if (today.isBefore(startDate)) {
                return STATUS_SAP_DIEN_RA;
            } else if (!today.isAfter(endDate)) {
                return STATUS_DANG_DIEN_RA;
            } else {
                return STATUS_DA_DIEN_RA;
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
	return "Không xác định";
    }

}
