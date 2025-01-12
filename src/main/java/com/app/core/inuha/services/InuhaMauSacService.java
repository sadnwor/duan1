package com.app.core.inuha.services;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.sanpham.InuhaDanhMucModel;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.models.sanpham.InuhaKieuDangModel;
import com.app.core.inuha.models.sanpham.InuhaMauSacModel;
import com.app.core.inuha.repositories.sanpham.InuhaMauSacRepository;
import com.app.core.inuha.repositories.sanpham.InuhaXuatXuRepository;
import com.app.core.inuha.services.impl.IInuhaMauSacServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaMauSacService implements IInuhaMauSacServiceInterface {

    private final InuhaMauSacRepository repository = InuhaMauSacRepository.getInstance();
    
    private static InuhaMauSacService instance = null;
    
    public static InuhaMauSacService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaMauSacService();
	}
	return instance;
    }
    
    private InuhaMauSacService() { 
	
    }
    
    @Override
    public InuhaMauSacModel getById(Integer id) {
        return null;
    }

    @Override
    public Integer insert(InuhaMauSacModel model) {
        try {
            if (repository.has(model.getTen())) { 
                throw new ServiceResponseException("Tên màu sắc đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm màu sắc");
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
            throw new ServiceResponseException("Không thể tìm kiếm màu sắc");
        }
    }

    @Override
    public void update(InuhaMauSacModel model) {
        try {
            Optional<InuhaMauSacModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy màu sắc");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Tên màu sắc đã tồn tại trên hệ thống");
            }
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật màu sắc này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaMauSacModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy màu sắc");
            }
            
            if (repository.hasUse(id)) { 
                InuhaMauSacModel item = find.get();
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá màu sắc này");
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
    }

    @Override
    public List<InuhaMauSacModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách màu sắc");
        }
    }

    @Override
    public List<InuhaMauSacModel> getPage(FilterRequest request) {
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
    
    public InuhaMauSacModel insertByExcel(String name) {
        try {
	    Optional<InuhaMauSacModel> find = repository.getByName(name);
	    if (find.isPresent()) { 
		return find.get();
	    }
	    InuhaMauSacModel model = new InuhaMauSacModel();
	    model.setTen(name);
	    insert(model);
            return getById(JbdcHelper.getLastInsertedId());
        } catch (Exception ex) {
	    throw new ServiceResponseException("Không thể thêm dữ liệu");
        }
    }

    
}
