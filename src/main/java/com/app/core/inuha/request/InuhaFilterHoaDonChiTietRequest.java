package com.app.core.inuha.request;

import com.app.common.infrastructure.request.FilterRequest;
import com.app.views.UI.combobox.ComboBoxItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InuhaFilterHoaDonChiTietRequest extends FilterRequest {

    private int idHoaDon;
    
    private int idSanPhamChiTiet;

}
