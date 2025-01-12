package com.app.core.inuha.services;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.sanpham.InuhaKieuDangModel;
import com.app.core.inuha.repositories.sanpham.InuhaKieuDangRepository;
import com.app.core.inuha.services.impl.IInuhaKieuDangServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaKieuDangService implements IInuhaKieuDangServiceInterface {

    private final InuhaKieuDangRepository repository = InuhaKieuDangRepository.getInstance();
    
    private static InuhaKieuDangService instance = null;
    
    public static InuhaKieuDangService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaKieuDangService();
	}
	return instance;
    }
    
    private InuhaKieuDangService() { 
	
    }
    
    @Override
    public InuhaKieuDangModel getById(Integer id) {
        return null;
    }

    @Override
    public Integer insert(InuhaKieuDangModel model) {
        try {
            if (repository.has(model.getTen())) { 
                throw new ServiceResponseException("Tên kiểu dáng đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm kiểu dáng");
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
            throw new ServiceResponseException("Không thể tìm kiếm kiểu dáng");
        }
    }

    @Override
    public void update(InuhaKieuDangModel model) {
        try {
            Optional<InuhaKieuDangModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy kiểu dáng");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Tên kiểu dáng đã tồn tại trên hệ thống");
            }
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật kiểu dáng này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaKieuDangModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy kiểu dáng");
            }
            
            if (repository.hasUse(id)) { 
                InuhaKieuDangModel item = find.get();
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá kiểu dáng này");
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
    }

    @Override
    public List<InuhaKieuDangModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách kiểu dáng");
        }
    }

    @Override
    public List<InuhaKieuDangModel> getPage(FilterRequest request) {
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
    
    public InuhaKieuDangModel insertByExcel(String name) {
        try {
	    Optional<InuhaKieuDangModel> find = repository.getByName(name);
	    if (find.isPresent()) { 
		return find.get();
	    }
	    InuhaKieuDangModel model = new InuhaKieuDangModel();
	    model.setTen(name);
	    insert(model);
            return getById(JbdcHelper.getLastInsertedId());
        } catch (Exception ex) {
	    throw new ServiceResponseException("Không thể thêm dữ liệu");
        }
    }
}
