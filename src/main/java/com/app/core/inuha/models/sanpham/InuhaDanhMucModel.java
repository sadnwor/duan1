package com.app.core.inuha.models.sanpham;

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
public class InuhaDanhMucModel {
    
    private int id;
    
    private int stt;
    
    private String ten;
    
    private String ngayTao;
    
    private boolean trangThaiXoa;
    
    private String ngayCapNhat;
    
    public Object[] toDataRow() { 
        return new Object[] { 
            stt,
            ten,
            ngayTao,
            ngayCapNhat
        };
    }
}
