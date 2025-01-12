package com.app.core.inuha.services.impl;

import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.thongke.InuhaThongKeChartModel;
import com.app.core.inuha.models.thongke.InuhaThongKeChiTietModel;
import com.app.core.inuha.models.thongke.InuhaThongKeSanPhamModel;
import com.app.core.inuha.models.thongke.InuhaThongKeTongSoModel;
import com.app.core.inuha.request.InuhaFilterThongKeRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author inuHa
 */
public interface IInuhaThongKeServiceInterface {
    
    List<InuhaSanPhamModel> getListSanPham();
    
    String getFirstDate();
    
    List<InuhaThongKeChartModel> getDataChart(InuhaFilterThongKeRequest filter);
    
    InuhaThongKeTongSoModel getTongSo(InuhaFilterThongKeRequest filter);
    
    List<InuhaThongKeSanPhamModel> getAll(InuhaFilterThongKeRequest request);
    
    List<InuhaThongKeSanPhamModel> getPage(InuhaFilterThongKeRequest request);

    Integer getTotalPage(InuhaFilterThongKeRequest request);
    
    InuhaThongKeChiTietModel getDetail(InuhaFilterThongKeRequest request);
    
}
