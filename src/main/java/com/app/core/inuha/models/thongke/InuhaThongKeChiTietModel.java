package com.app.core.inuha.models.thongke;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class InuhaThongKeChiTietModel {
    
    private int tongHoaDon;
    
    private int choThanhToan;
    
    private int daThanhToan;
    
    private int daHuy;
    
   private int khachHang;
    
}
