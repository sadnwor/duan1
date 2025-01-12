package com.app.core.inuha.services;

import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.models.sanpham.InuhaMauSacModel;
import com.app.core.inuha.repositories.InuhaSanPhamRepository;
import com.app.core.inuha.services.impl.IInuhaSanPhamServiceInterface;
import com.app.utils.ProductUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaSanPhamService implements IInuhaSanPhamServiceInterface {

    private final InuhaSanPhamRepository repository = InuhaSanPhamRepository.getInstance();
    
    private static InuhaSanPhamService instance = null;
    
    public static InuhaSanPhamService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaSanPhamService();
	}
	return instance;
    }
    
    private InuhaSanPhamService() { 
	
    }
    
    @Override
    public InuhaSanPhamModel getById(Integer id) {
        try {
            Optional<InuhaSanPhamModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new SQLException();
            }
            return find.get();
        } catch (SQLException ex) {
            throw new ServiceResponseException("Không tìm thấy sản phẩm");
        }
    }

    @Override
    public Integer insert(InuhaSanPhamModel model) {
        try {
            if (repository.has(model.getTen())) { 
                throw new ServiceResponseException("Tên sản phẩm đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm sản phẩm");
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
            throw new ServiceResponseException("Không thể tìm kiếm sản phẩm");
        }
    }

    @Override
    public void update(InuhaSanPhamModel model) {
        try {
            Optional<InuhaSanPhamModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy sản phẩm");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Tên sản phẩm đã tồn tại trên hệ thống");
            }
	    
	    
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật sản phẩm này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaSanPhamModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy sản phẩm");
            }
            
	    InuhaSanPhamModel item = find.get();
            if (repository.hasUse(id)) { 
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
		ProductUtils.removeImageProduct(item.getHinhAnh());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá sản phẩm này");
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
            throw new ServiceResponseException("Không thể xoá " + errors + " sản phẩm đã chọn");
        }
    }

    @Override
    public List<InuhaSanPhamModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách sản phẩm");
        }
    }

    @Override
    public List<InuhaSanPhamModel> getPage(FilterRequest request) {
        try {
            return repository.selectPage(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<InuhaSanPhamModel> getPage(FilterRequest request, boolean isBanHang) {
        try {
            return repository.selectPage(request, isBanHang);
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
    
    public InuhaSanPhamModel getByCode(String ma) {
        try {
            Optional<InuhaSanPhamModel> find = repository.getByCode(ma);
            if (find.isEmpty()) { 
                throw new SQLException();
            }
            return find.get();
        } catch (SQLException ex) {
            throw new ServiceResponseException("Không tìm thấy sản phẩm");
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
