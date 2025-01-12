package com.app.core.inuha.services;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaKhachHangModel;
import com.app.core.inuha.repositories.InuhaKhachHangRepository;
import com.app.core.inuha.services.impl.IInuhaKhachHangServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaKhachHangService implements IInuhaKhachHangServiceInterface {

    private final InuhaKhachHangRepository repository = InuhaKhachHangRepository.getInstance();
    
    private static InuhaKhachHangService instance = null;
    
    public static InuhaKhachHangService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaKhachHangService();
	}
	return instance;
    }
    
    private InuhaKhachHangService() { 
	
    }
    
    @Override
    public InuhaKhachHangModel getById(Integer id) {
        return null;
    }

    @Override
    public Integer insert(InuhaKhachHangModel model) {
        try {
            if (repository.has(model.getSdt())) { 
                throw new ServiceResponseException("Số điện thoại khách hàng đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm khách hàng");
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
            throw new ServiceResponseException("Không thể tìm kiếm khách hàng");
        }
    }

    @Override
    public void update(InuhaKhachHangModel model) {
        try {
            Optional<InuhaKhachHangModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy khách hàng");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Số điện thoại khách hàng đã tồn tại trên hệ thống");
            }
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật khách hàng này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaKhachHangModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy khách hàng");
            }
            
            if (repository.hasUse(id)) { 
                InuhaKhachHangModel item = find.get();
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá khách hàng này");
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
    }

    @Override
    public List<InuhaKhachHangModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách khách hàng");
        }
    }

    @Override
    public List<InuhaKhachHangModel> getPage(FilterRequest request) {
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
    
    
}
