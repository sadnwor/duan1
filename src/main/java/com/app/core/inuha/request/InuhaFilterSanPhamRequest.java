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
public class InuhaFilterSanPhamRequest extends FilterRequest {

    private String keyword;
    
    private ComboBoxItem<Integer> danhMuc = new ComboBoxItem<>();
    
    private ComboBoxItem<Integer> thuongHieu = new ComboBoxItem<>();
    
    private ComboBoxItem<Integer> xuatXu = new ComboBoxItem<>();
    
    private ComboBoxItem<Integer> kieuDang = new ComboBoxItem<>();
    
    private ComboBoxItem<Integer> chatLieu = new ComboBoxItem<>();
    
    private ComboBoxItem<Integer> deGiay = new ComboBoxItem<>();
        
    private ComboBoxItem<Integer> soLuong = new ComboBoxItem<>();
    
    private ComboBoxItem<Integer> trangThai = new ComboBoxItem<>();

}
