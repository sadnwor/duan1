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
public class InuhaFilterTaiKhoanRequest extends FilterRequest {

    private String keyword;

    private ComboBoxItem<Integer> trangThai = new ComboBoxItem<>();
    
    private ComboBoxItem<Integer> gioiTinh = new ComboBoxItem<>();
    
    private ComboBoxItem<Integer> chucVu = new ComboBoxItem<>();
    
}
