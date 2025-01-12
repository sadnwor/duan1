package com.app.core.inuha.models;

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
public class InuhaHoaDonChiTietModel {
    
    private int stt;
        
    private int id;
    
    private String ma;
    
    private double giaNhap;
    
    private double giaBan;
    	
    private int soLuong;
    
    private InuhaSanPhamChiTietModel sanPhamChiTiet;
    
    private InuhaHoaDonModel hoaDon;
    
    public Object[] toDataRowBanHang() { 
	return new Object[] { 
	    ProductUtils.getImage(sanPhamChiTiet.getSanPham().getHinhAnh()),
	    "x" + soLuong,
	    CurrencyUtils.parseString(giaBan),
	    sanPhamChiTiet.getMa() + " - " + sanPhamChiTiet.getSanPham().getTen(),
	    sanPhamChiTiet.getMauSac().getTen(),
	    sanPhamChiTiet.getKichCo().getTen()
	};
    }
}
