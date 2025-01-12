package com.app.core.inuha.services;

import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.thongke.InuhaThongKeChartModel;
import com.app.core.inuha.models.thongke.InuhaThongKeChiTietModel;
import com.app.core.inuha.models.thongke.InuhaThongKeSanPhamModel;
import com.app.core.inuha.models.thongke.InuhaThongKeTongSoModel;
import com.app.core.inuha.repositories.InuhaThongKeRepository;
import com.app.core.inuha.request.InuhaFilterThongKeRequest;
import com.app.core.inuha.services.impl.IInuhaThongKeServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author InuHa
 */
public class InuhaThongKeService implements IInuhaThongKeServiceInterface {

    private final InuhaThongKeRepository repository = InuhaThongKeRepository.getInstance();
    
    private static InuhaThongKeService instance = null;
    
    public static InuhaThongKeService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaThongKeService();
	}
	return instance;
    }
    
    private InuhaThongKeService() { 
	
    }

    @Override
    public List<InuhaSanPhamModel> getListSanPham() {
        try {
            return repository.getListSanPham();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách sản phẩm");
        }
    }

    @Override
    public String getFirstDate() {
        try {
            return repository.getFirstDate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy ngày đầu tiên bán hàng");
        }
    }

    @Override
    public List<InuhaThongKeChartModel> getDataChart(InuhaFilterThongKeRequest filter) {
        try {
            return repository.getDataChart(filter);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy dữ liệu biểu đồ");
        }
    }

    @Override
    public InuhaThongKeTongSoModel getTongSo(InuhaFilterThongKeRequest filter) {
        try {
            return repository.getTongSo(filter);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy dữ tổng");
        }
    }

    @Override
    public List<InuhaThongKeSanPhamModel> getPage(InuhaFilterThongKeRequest request) {
	try {
            return repository.selectPage(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Integer getTotalPage(InuhaFilterThongKeRequest request) {
        try {
            return repository.count(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<InuhaThongKeSanPhamModel> getAll(InuhaFilterThongKeRequest request) {
        try {
            return repository.selectAll(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách sản phẩm");
        }
    }

    @Override
    public InuhaThongKeChiTietModel getDetail(InuhaFilterThongKeRequest request) {
        try {
            return repository.getDetail(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy thông tin hoá đơn");
        }
    }
    
}
