package com.app.core.inuha.services;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.sanpham.InuhaDanhMucModel;
import com.app.core.inuha.repositories.sanpham.InuhaDanhMucRepository;
import com.app.core.inuha.services.impl.IInuhaDanhMucServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaDanhMucService implements IInuhaDanhMucServiceInterface {

    private final InuhaDanhMucRepository repository = InuhaDanhMucRepository.getInstance();
    
    private static InuhaDanhMucService instance = null;
    
    public static InuhaDanhMucService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaDanhMucService();
	}
	return instance;
    }
    
    private InuhaDanhMucService() { 
	
    }
    
    @Override
    public InuhaDanhMucModel getById(Integer id) {
        return null;
    }

    @Override
    public Integer insert(InuhaDanhMucModel model) {
        try {
            if (repository.has(model.getTen())) { 
                throw new ServiceResponseException("Tên danh mục đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm danh mục");
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
            throw new ServiceResponseException("Không thể tìm kiếm danh mục");
        }
    }

    @Override
    public void update(InuhaDanhMucModel model) {
        try {
            Optional<InuhaDanhMucModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy danh mục");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Tên danh mục đã tồn tại trên hệ thống");
            }
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật danh mục này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaDanhMucModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy danh mục");
            }
            
            if (repository.hasUse(id)) { 
                InuhaDanhMucModel item = find.get();
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá danh mục này");
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
    }

    @Override
    public List<InuhaDanhMucModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách danh mục");
        }
    }

    @Override
    public List<InuhaDanhMucModel> getPage(FilterRequest request) {
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
    
    public InuhaDanhMucModel insertByExcel(String name) {
        try {
	    Optional<InuhaDanhMucModel> find = repository.getByName(name);
	    if (find.isPresent()) { 
		return find.get();
	    }
	    InuhaDanhMucModel model = new InuhaDanhMucModel();
	    model.setTen(name);
	    insert(model);
            return getById(JbdcHelper.getLastInsertedId());
        } catch (Exception ex) {
	    throw new ServiceResponseException("Không thể thêm dữ liệu");
        }
    }
    
}
