package com.app.core.common.models.invoice;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author inuHa
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDataModel {

    private int id;
    
    private String maHoaDon;
    
    private String taiKhoan;
    
    private String tenKhachHang;
    
    private String soDienThoai;
    
    private double tongTienHang;
    
    private double tongTienGiam;
    
    private double tienKhachTra;
    
    private List<InvoiceProduct> hoaDonChiTiet;
    
}
