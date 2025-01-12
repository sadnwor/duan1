package com.app.core.inuha.services;

import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaHoaDonModel;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.repositories.InuhaHoaDonRepository;
import com.app.core.inuha.services.impl.IInuhaHoaDonServiceInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaHoaDonService implements IInuhaHoaDonServiceInterface {

    private final InuhaHoaDonRepository repository = InuhaHoaDonRepository.getInstance();
    
    private static InuhaHoaDonService instance = null;
    
    public static InuhaHoaDonService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaHoaDonService();
	}
	return instance;
    }
    
    private InuhaHoaDonService() { 
	
    }
    
    @Override
    public InuhaHoaDonModel getById(Integer id) {
        try {
            Optional<InuhaHoaDonModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new SQLException();
            }
            return find.get();
        } catch (SQLException ex) {
            throw new ServiceResponseException("Không tìm thấy hoá đơn");
        }
    }

    @Override
    public Integer insert(InuhaHoaDonModel model) {
        try {
	    if (repository.isMaxHoaDonCho()) { 
		throw new ServiceResponseException("Chỉ có thể tạo tối đa cùng lúc " + InuhaHoaDonRepository.MAX_WAIT_BILL + " hoá đơn chờ!");
	    }
	    
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể tạo mới hoá đơn chờ");
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
            throw new ServiceResponseException("Không thể tìm kiếm hoá đơn");
        }
    }

    @Override
    public void update(InuhaHoaDonModel model) {
        try {
            Optional<InuhaHoaDonModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy hoá đơn");
            }
	    
	    InuhaPhieuGiamGiaModel phieuGiamGia = null;
	    if (model.getPhieuGiamGia() != null) {
		phieuGiamGia = InuhaPhieuGiamGiaService.getInstance().getByCode(model.getPhieuGiamGia().getMa());
	    }

            repository.update(model);
	    
	    if (phieuGiamGia != null) {
		int soLuongPhieuGiamGia = phieuGiamGia.getSoLuong() - 1;
		phieuGiamGia.setSoLuong(Math.max(0, soLuongPhieuGiamGia));
		InuhaPhieuGiamGiaService.getInstance().update(phieuGiamGia);
	    }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá hoá đơn này");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaHoaDonModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy hoá đơn");
            }
            
	    InuhaHoaDonModel item = find.get();
            if (repository.hasUse(id)) { 
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá hoá đơn này");
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
            throw new ServiceResponseException("Không thể xoá " + errors + " hoá đơn đã chọn");
        }
    }

    @Override
    public List<InuhaHoaDonModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách hoá đơn chờ");
        }
    }

    @Override
    public List<InuhaHoaDonModel> getPage(FilterRequest request) {
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
    
    public int count(FilterRequest request) {
        try {
            return repository.count(request);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
}
