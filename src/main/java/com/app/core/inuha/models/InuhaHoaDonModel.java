package com.app.core.inuha.models;

import com.app.utils.BillUtils;
import com.app.utils.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author InuHa
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InuhaHoaDonModel {
    
    private int stt;
        
    private int id;
    
    private String ma;
    
    private double tienGiam;
        
    private double tienMat;
    
    private double tienChuyenKhoan;
	
    private int phuongThucThanhToan;
    
    private int trangThai;
    
    private String ngayTao;
    
    private String ngayCapNhat;
    
    private boolean trangThaiXoa;
    
    private double tongTienHang;
    
    private double tongTienGiam;
    
    private InuhaKhachHangModel khachHang;
    
    private InuhaTaiKhoanModel taiKhoan;
    
    private InuhaPhieuGiamGiaModel phieuGiamGia;
    
    public Object[] toDataRowBanHang() { 
	return new Object[] { 
	    stt,
	    ma,
	    taiKhoan.getUsername(),
	    TimeUtils.date("dd-MM-yyy HH:mm", ngayTao),
	    BillUtils.getTrangThai(trangThai)
	};
    }
}
