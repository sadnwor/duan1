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
public class InuhaThongKeChartModel {

    private String label;
    
    private double doanhThu;
    
    private double loiNhuan;
    
}
