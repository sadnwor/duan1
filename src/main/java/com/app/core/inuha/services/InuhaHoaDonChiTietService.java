package com.app.core.inuha.services;

import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaHoaDonChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.repositories.InuhaHoaDonChiTietRepository;
import com.app.core.inuha.services.impl.IInuhaHoaDonChiTietServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaHoaDonChiTietService implements IInuhaHoaDonChiTietServiceInterface {

    private final InuhaHoaDonChiTietRepository repository = InuhaHoaDonChiTietRepository.getInstance();
    
    private static InuhaHoaDonChiTietService instance = null;
    
    public static InuhaHoaDonChiTietService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaHoaDonChiTietService();
	}
	return instance;
    }
    
    private InuhaHoaDonChiTietService() { 
	
    }
    
    @Override
    public InuhaHoaDonChiTietModel getById(Integer id) {
        try {
            Optional<InuhaHoaDonChiTietModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new SQLException();
            }
            return find.get();
        } catch (SQLException ex) {
            throw new ServiceResponseException("Không tìm thấy hoá đơn chi tiết");
        }
    }

    @Override
    public Integer insert(InuhaHoaDonChiTietModel model) {
        try {
	    int rows = -1;
	    int soLuong = model.getSoLuong();
	    Optional<InuhaHoaDonChiTietModel> check = repository.getDuplicate(model);
	    if (check.isPresent()) { 
		model.setSoLuong(check.get().getSoLuong() + soLuong);
		model.setId(check.get().getId());
		rows = repository.update(model);
	    } else {
		rows = repository.insert(model);
	    }
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm sản phẩm vào giỏ hàng");
            }
	    InuhaSanPhamChiTietModel sanPhamChiTiet = model.getSanPhamChiTiet();
	    int soLuongTon = sanPhamChiTiet.getSoLuong() - soLuong;
	    if (soLuongTon < 0) { 
		soLuongTon = 0;
	    }
	    sanPhamChiTiet.setSoLuong(soLuongTon);
	    InuhaSanPhamChiTietService.getInstance().update(sanPhamChiTiet);
            return rows;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
        }
    }

    @Override
    public boolean has(Integer id) {
        try {
            return repository.has(id);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể tìm kiếm hoá đơn chi tiết");
        }
    }

    @Override
    public void update(InuhaHoaDonChiTietModel model) {
        try {
            Optional<InuhaHoaDonChiTietModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy hoá đơn chi tiết");
            }
            
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật hoá đơn chi tiết này");
        }
    }

    public void update(InuhaHoaDonChiTietModel model, int soLuongChenhLech) {
	model.setSoLuong(model.getSoLuong() + soLuongChenhLech);
        update(model);
	InuhaSanPhamChiTietModel sanPhamChiTiet = model.getSanPhamChiTiet();
	int soLuongTon = sanPhamChiTiet.getSoLuong() - soLuongChenhLech;
	if (soLuongTon < 0) { 
	    soLuongTon = 0;
	}
	sanPhamChiTiet.setSoLuong(soLuongTon);
	InuhaSanPhamChiTietService.getInstance().update(sanPhamChiTiet);
    }
	
    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaHoaDonChiTietModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy hoá đơn chi tiết");
            }
            
            repository.delete(id);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá hoá đơn chi tiết này");
        }
    }

    public void delete(InuhaHoaDonChiTietModel model) {
        delete(model.getId());
	InuhaSanPhamChiTietModel sanPhamChiTiet = model.getSanPhamChiTiet();
	sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + model.getSoLuong());
	InuhaSanPhamChiTietService.getInstance().update(sanPhamChiTiet);
    }
	
    @Override
    public void deleteAll(List<Integer> ids) {
        int errors = 0;
        for(int id: ids) { 
            try {
                delete(id);
            } catch (Exception e) { 
                e.printStackTrace();
                errors++;
            }
        }
        
        if (errors > 0) { 
            throw new ServiceResponseException("Không thể xoá " + errors + " hoá đơn chi tiết đã chọn");
        }
    }

    @Override
    public List<InuhaHoaDonChiTietModel> getAll() {
        return null;
    }

    public List<InuhaHoaDonChiTietModel> getAll(int idHoaDon) {
        try {
            return repository.selectAll(idHoaDon);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách hoá đơn chi tiết");
        }
    }
	
    @Override
    public List<InuhaHoaDonChiTietModel> getPage(FilterRequest request) {
        try {
            return repository.selectPage(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Integer getTotalPage(FilterRequest request) {
        try {
            return repository.count(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public String getLastId() {
        try {
            return repository.getLastId();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<InuhaHoaDonChiTietModel> getAllIdsByIdSanPham(int idSanPham) {
        try {
            return repository.getAllIdsByIdSanPham(idSanPham);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }
	
    public List<InuhaHoaDonChiTietModel> getAllIdsByIdSanPhamChiTiet(int idSanPhamChiTiet) {
        try {
            return repository.getAllIdsByIdSanPhamChiTiet(idSanPhamChiTiet);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    public int count() {
        try {
            return repository.count();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
