package com.app.core.inuha.services;

import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.models.sanpham.InuhaMauSacModel;
import com.app.core.inuha.repositories.InuhaSanPhamChiTietRepository;
import com.app.core.inuha.repositories.InuhaSanPhamRepository;
import com.app.core.inuha.services.impl.IInuhaSanPhamChiTietServiceInterface;
import com.app.utils.ProductUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaSanPhamChiTietService implements IInuhaSanPhamChiTietServiceInterface {

    private final InuhaSanPhamChiTietRepository repository = InuhaSanPhamChiTietRepository.getInstance();
    
    private static InuhaSanPhamChiTietService instance = null;
    
    public static InuhaSanPhamChiTietService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaSanPhamChiTietService();
	}
	return instance;
    }
    
    private InuhaSanPhamChiTietService() { 
	
    }
    
    
    @Override
    public InuhaSanPhamChiTietModel getById(Integer id) {
        try {
            Optional<InuhaSanPhamChiTietModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new SQLException();
            }
            return find.get();
        } catch (SQLException ex) {
            throw new ServiceResponseException("Không tìm thấy sản phẩm chi tiết");
        }
    }

    @Override
    public Integer insert(InuhaSanPhamChiTietModel model) {
        try {
            if (repository.has(model.getSanPham().getId(), model.getKichCo().getId(), model.getMauSac().getId())) { 
                throw new ServiceResponseException("Sản phẩm chi tiết đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm sản phẩm chi tiết");
            }
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
            throw new ServiceResponseException("Không thể tìm kiếm sản phẩm chi tiết");
        }
    }

    @Override
    public void update(InuhaSanPhamChiTietModel model) {
        try {
            Optional<InuhaSanPhamChiTietModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy sản phẩm chi tiết");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Sản phẩm chi tiết đã tồn tại trên hệ thống");
            }
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật sản phẩm chi tiết này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaSanPhamChiTietModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy sản phẩm chi tiết");
            }
            
            if (repository.hasUse(id)) { 
                InuhaSanPhamChiTietModel item = find.get();
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá sản phẩm chi tiết này");
        }
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
            throw new ServiceResponseException("Không thể xoá " + errors + " sản phẩm chi tiết đã chọn");
        }
    }

    @Override
    public List<InuhaSanPhamChiTietModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách sản phẩm chi tiết");
        }
    }
    
    public List<InuhaSanPhamChiTietModel> getAll(int id) {
        try {
            return repository.selectAll(id);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách sản phẩm chi tiết");
        }
    }

    @Override
    public List<InuhaSanPhamChiTietModel> getPage(FilterRequest request) {
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
            return repository.getNextId();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public boolean insertByExcel(InuhaSanPhamChiTietModel model) {
        try {
	    if (model.getMa() == null && model.getSanPham().getMa() == null) {
		return false;
	    }
	    
	    Optional<InuhaSanPhamChiTietModel> findChiTiet = repository.getByCode(model.getMa());
	    if (findChiTiet.isPresent()) {
		model.setId(findChiTiet.get().getId());
		update(model);
		return true;
	    }
	    
	    Optional<InuhaSanPhamModel> findSanPham = InuhaSanPhamRepository.getInstance().getByCode(model.getSanPham().getMa());
	    if (findSanPham.isEmpty()) {
		throw new IllegalArgumentException();
	    }
	    model.getSanPham().setId(findSanPham.get().getId());
	    model.setMa(ProductUtils.generateCodeSanPhamChiTiet());
	    insert(model);
	    return true;
        } catch (Exception ex) {
	    throw new ServiceResponseException("Không thể thêm dữ liệu");
        }
    }
    
            
    public List<InuhaKichCoModel> getAllKichCo(int idSanPham) { 
        try {
            return repository.getAllKichCo(idSanPham);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách kích cỡ");
        }
    }
    
    public List<InuhaSanPhamChiTietModel> getAllByKichCo(int idSanPham, int idKichCo) { 
        try {
            return repository.getAllByKichCo(idSanPham, idKichCo);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách sản phẩm chi tiết");
        }
    }
    
    
    public int count(FilterRequest request) {
        try {
            return repository.count(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
