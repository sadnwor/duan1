package com.app.core.inuha.services;

import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.repositories.InuhaPhieuGiamGiaRepository;
import com.app.core.inuha.services.impl.IInuhaPhieuGiamGiaServiceInterface;
import com.app.utils.SessionUtils;
import com.app.utils.VoucherUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaPhieuGiamGiaService implements IInuhaPhieuGiamGiaServiceInterface {

    private final InuhaPhieuGiamGiaRepository repository = InuhaPhieuGiamGiaRepository.getInstance();
    
    private static InuhaPhieuGiamGiaService instance = null;
    
    public static InuhaPhieuGiamGiaService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaPhieuGiamGiaService();
	}
	return instance;
    }
    
    private InuhaPhieuGiamGiaService() { 
	
    }
    
    @Override
    public InuhaPhieuGiamGiaModel getById(Integer id) {
        return null;
    }

    @Override
    public Integer insert(InuhaPhieuGiamGiaModel model) {
        try {
            if (repository.has(model)) { 
                throw new ServiceResponseException("Phiếu giảm giá đã tồn tại trên hệ thống");
            }
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm phiếu giảm giá");
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
            throw new ServiceResponseException("Không thể tìm kiếm phiếu giảm giá");
        }
    }

    @Override
    public void update(InuhaPhieuGiamGiaModel model) {
        try {
            Optional<InuhaPhieuGiamGiaModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy phiếu giảm giá");
            }
            
            if (repository.has(model)) { 
                throw new ServiceResponseException("Phiếu giảm giá đã tồn tại trên hệ thống");
            }
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật phiếu giảm giá");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaPhieuGiamGiaModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy phiếu giảm giá");
            }
            
            if (repository.hasUse(id)) { 
                InuhaPhieuGiamGiaModel item = find.get();
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá phiếu giảm giá này");
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
    }

    @Override
    public List<InuhaPhieuGiamGiaModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách phiếu giảm giá");
        }
    }

    @Override
    public List<InuhaPhieuGiamGiaModel> getPage(FilterRequest request) {
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
    
    public InuhaPhieuGiamGiaModel getByCode(String ma) {
        try {
            Optional<InuhaPhieuGiamGiaModel> find = repository.getByCode(ma);
            if (find.isEmpty()) { 
                throw new SQLException();
            }
	    InuhaPhieuGiamGiaModel item = find.get();
	    switch(VoucherUtils.getTrangThai(item)) { 
		case VoucherUtils.STATUS_SAP_DIEN_RA -> {
		    throw new ServiceResponseException("Phiếu giảm giá chưa được diễn ra");
		}
		case VoucherUtils.STATUS_DA_DIEN_RA -> {
		    throw new ServiceResponseException("Phiếu giảm giá đã hết hạn");
		}
		default -> {
		    if (item.getSoLuong() < 1) { 
			throw new ServiceResponseException("Số lượng phiếu giảm giá đã hết");
		    }
		    return item;
		}
	    }
	    
        } catch (SQLException ex) {
            throw new ServiceResponseException("Phiếu giảm giá không tồn tại hoặc đã hết hạn");
        }
    }
}
