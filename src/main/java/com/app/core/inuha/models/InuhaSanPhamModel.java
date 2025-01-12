package com.app.core.inuha.models;

import com.app.core.inuha.models.sanpham.*;
import com.app.utils.CurrencyUtils;
import com.app.utils.ProductUtils;
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
public class InuhaSanPhamModel {
    
    private int stt;
        
    private int id;
    
    private String ma;
    
    private String ten;
    
    private String moTa;
    
    private double giaNhap;
	
    private double giaBan;
    
    private String hinhAnh;
    
    private boolean trangThai;
    
    private String ngayTao;
    
    private String ngayCapNhat;
    
    private boolean trangThaiXoa;
    
    private int soLuong;
    
    private InuhaDanhMucModel danhMuc;
    
    private InuhaThuongHieuModel thuongHieu;
    
    private InuhaXuatXuModel xuatXu;
    
    private InuhaKieuDangModel kieuDang;
    
    private InuhaChatLieuModel chatLieu;
    
    private InuhaDeGiayModel deGiay;
    
    public Object[] toDataRowSanPham() { 
        return new Object[] { 
            false,
            stt,
            ma,
            ProductUtils.getImage(hinhAnh),
            ten,
            danhMuc.getTen(),
            thuongHieu.getTen(),
            CurrencyUtils.parseNumber(soLuong),
            CurrencyUtils.parseString(giaBan),
            trangThai
        };
    }
    
    public Object[] toDataRowBanHang() { 
        return new Object[] { 
            stt,
            ma,
            ProductUtils.getImage(hinhAnh),
            ten,
            CurrencyUtils.parseString(giaBan),
            CurrencyUtils.parseNumber(soLuong),
            danhMuc.getTen(),
            thuongHieu.getTen(),
	    xuatXu.getTen(),
	    kieuDang.getTen(),
	    chatLieu.getTen(),
	    deGiay.getTen(),
	    moTa
        };
    }
    
}
