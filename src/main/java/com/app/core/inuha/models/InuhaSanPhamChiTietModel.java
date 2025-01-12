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
public class InuhaSanPhamChiTietModel {
    
    private int stt;
        
    private int id;
    
    private String ma;
    
    private int soLuong;
    
    private boolean trangThai;
    
    private String ngayTao;
    
    private String ngayCapNhat;
    
    private boolean trangThaiXoa;
    
    private InuhaSanPhamModel sanPham;
    
    private InuhaKichCoModel kichCo;
    
    private InuhaMauSacModel mauSac;
    
    public boolean getTrangThai() { 
	return sanPham.isTrangThai() && trangThai;
    }
	
    public boolean isTrangThai() { 
	return trangThai;
    }
    
    public Object[] toDataRowSanPhamChiTiet() { 
        return new Object[] { 
            false,
            stt,
            ma,
            kichCo.getTen(),
            mauSac.getTen(),
            CurrencyUtils.parseNumber(soLuong),
            getTrangThai()
	};
    }
    
    public Object[] toDataRowAllSanPhamChiTiet() { 
        return new Object[] { 
            false,
            stt,
            ma,
	    sanPham.getMa(),
	    ProductUtils.getImage(sanPham.getHinhAnh()),
            sanPham.getTen(),
	    CurrencyUtils.parseNumber(soLuong),
	    sanPham.getDanhMuc().getTen(),
	    sanPham.getThuongHieu().getTen(),
	    sanPham.getXuatXu().getTen(),
	    sanPham.getKieuDang().getTen(),
	    sanPham.getChatLieu().getTen(),
	    sanPham.getDeGiay().getTen(),
	    kichCo.getTen(),
	    mauSac.getTen(),
	    CurrencyUtils.parseString(sanPham.getGiaNhap()),
	    CurrencyUtils.parseString(sanPham.getGiaBan()),
            getTrangThai(),
	    sanPham.getMoTa()
	};
    }
	
}
