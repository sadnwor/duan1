package com.app.core.inuha.models.thongke;

import com.app.core.inuha.models.*;
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
public class InuhaThongKeSanPhamModel {
    
    private int stt;
        
    private int id;
    
    private String ma;
    
    private String ten;
    
    private int soLuongBan;
    
    private int soLuongTon;
    
    private double doanhThu;
    
    private double loiNhuan;

    public String[] toDataRow() { 
        return new String[] { 
            String.valueOf(stt),
            ma,
            ten,
	    CurrencyUtils.parseString(doanhThu),
	    CurrencyUtils.parseString(loiNhuan),
            CurrencyUtils.parseNumber(soLuongBan),
	    CurrencyUtils.parseNumber(soLuongTon)
	};
    }
	
}
