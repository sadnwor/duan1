package com.app.core.inuha.request;

import com.app.common.infrastructure.constants.ChartConstant;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.views.UI.combobox.ComboBoxItem;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InuhaFilterThongKeRequest extends FilterRequest {

    private ComboBoxItem<Integer> sanPham = new ComboBoxItem<>();
    
    private ChartConstant type;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private ComboBoxItem<Integer> orderBy;
    

}
