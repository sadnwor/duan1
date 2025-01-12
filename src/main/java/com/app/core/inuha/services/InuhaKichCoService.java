package com.app.core.inuha.services;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.repositories.sanpham.InuhaKichCoRepository;
import com.app.core.inuha.services.impl.IInuhaKichCoServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaKichCoService implements IInuhaKichCoServiceInterface {

    private final InuhaKichCoRepository repository = InuhaKichCoRepository.getInstance();

    private static InuhaKichCoService instance = null;
    
    public static InuhaKichCoService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaKichCoService();
	}
	return instance;
    }
    
    private InuhaKichCoService() { 
	
    }
    
    @Override
    public InuhaKichCoModel getById(Integer id) {
        return null;
    }

    @Override
    public Integer insert(InuhaKichCoModel model) {
        try {
            if (repository.has(model.getTen())) { 
                throw new ServiceResponseException("Tên kích cỡ đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm kích cỡ");
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
            throw new ServiceResponseException("Không thể tìm kiếm kích cỡ");
        }
    }

    @Override
    public void update(InuhaKichCoModel model) {
        try {
            Optional<InuhaKichCoModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy kích cỡ");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Tên kích cỡ đã tồn tại trên hệ thống");
            }
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật kích cỡ này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaKichCoModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy kích cỡ");
            }
            
            if (repository.hasUse(id)) { 
                InuhaKichCoModel item = find.get();
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá kích cỡ này");
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
    }

    @Override
    public List<InuhaKichCoModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách kích cỡ");
        }
    }

    @Override
    public List<InuhaKichCoModel> getPage(FilterRequest request) {
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
    

    public InuhaKichCoModel insertByExcel(String name) {
        try {
	    Optional<InuhaKichCoModel> find = repository.getByName(name);
	    if (find.isPresent()) { 
		return find.get();
	    }
	    InuhaKichCoModel model = new InuhaKichCoModel();
	    model.setTen(name);
	    insert(model);
            return getById(JbdcHelper.getLastInsertedId());
        } catch (Exception ex) {
	    throw new ServiceResponseException("Không thể thêm dữ liệu");
        }
    }
    

}
