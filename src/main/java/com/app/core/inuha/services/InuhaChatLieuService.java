package com.app.core.inuha.services;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.sanpham.InuhaChatLieuModel;
import com.app.core.inuha.repositories.sanpham.InuhaChatLieuRepository;
import com.app.core.inuha.services.impl.IInuhaChatLieuServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaChatLieuService implements IInuhaChatLieuServiceInterface {

    private final InuhaChatLieuRepository repository = InuhaChatLieuRepository.getInstance();
    
    private static InuhaChatLieuService instance = null;
    
    public static InuhaChatLieuService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaChatLieuService();
	}
	return instance;
    }
    
    private InuhaChatLieuService() { 
	
    }
    
    @Override
    public InuhaChatLieuModel getById(Integer id) {
        return null;
    }

    @Override
    public Integer insert(InuhaChatLieuModel model) {
        try {
            if (repository.has(model.getTen())) { 
                throw new ServiceResponseException("Tên chất liệu đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm chất liệu");
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
            throw new ServiceResponseException("Không thể tìm kiếm chất liệu");
        }
    }

    @Override
    public void update(InuhaChatLieuModel model) {
        try {
            Optional<InuhaChatLieuModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy chất liệu");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Tên chất liệu đã tồn tại trên hệ thống");
            }
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật chất liệu này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaChatLieuModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy chất liệu");
            }
            
            if (repository.hasUse(id)) { 
                InuhaChatLieuModel item = find.get();
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá chất liệu này");
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
    }

    @Override
    public List<InuhaChatLieuModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách chất liệu");
        }
    }

    @Override
    public List<InuhaChatLieuModel> getPage(FilterRequest request) {
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
    
    public InuhaChatLieuModel insertByExcel(String name) {
        try {
	    Optional<InuhaChatLieuModel> find = repository.getByName(name);
	    if (find.isPresent()) { 
		return find.get();
	    }
	    InuhaChatLieuModel model = new InuhaChatLieuModel();
	    model.setTen(name);
	    insert(model);
            return getById(JbdcHelper.getLastInsertedId());
        } catch (Exception ex) {
	    throw new ServiceResponseException("Không thể thêm dữ liệu");
        }
    }
}
