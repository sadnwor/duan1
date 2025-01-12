package com.app.core.inuha.services;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiXoaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.common.infrastructure.session.SessionLogin;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.repositories.InuhaTaiKhoanRepository;
import com.app.core.inuha.services.impl.IInuhaNhanVienServiceInterface;
import com.app.utils.ProductUtils;
import com.app.utils.SessionUtils;
import com.app.utils.ValidateUtils;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author inuHa
 */

public class InuhaTaiKhoanService implements IInuhaNhanVienServiceInterface {

    private final InuhaTaiKhoanRepository repository = InuhaTaiKhoanRepository.getInstance();

    private static InuhaTaiKhoanService instance = null;
    
    public static InuhaTaiKhoanService getInstance() { 
	if (instance == null) { 
	    instance = new InuhaTaiKhoanService();
	}
	return instance;
    }
    
    private InuhaTaiKhoanService() { 
	
    }
    
    @Override
    public InuhaTaiKhoanModel getById(Integer id) {
        return null;
    }

    @Override
    public Integer insert(InuhaTaiKhoanModel model) {
        try {
            if (repository.hasUsername(model.getUsername())) { 
                throw new ServiceResponseException("Tên tài khoản đã tồn tại trên hệ thống");
            }
            if (repository.hasEmail(model.getEmail())) { 
                throw new ServiceResponseException("Email đã tồn tại trên hệ thống");
            }
            if (repository.hasSdt(model.getSdt())) { 
                throw new ServiceResponseException("Số điện thoại đã tồn tại trên hệ thống");
            }
	    
            if (!SessionUtils.sendPassword(model.getPassword(), model.getEmail())) {
                throw new ServiceResponseException("Không thể gửi mật khẩu. Vui lòng thử lại sau ít phút.");
            }
	    
            int rows = repository.insert(model);
            if (rows < 1) { 
                throw new ServiceResponseException("Không thể thêm nhân viên. Vui lòng thử lại sau ít phút!");
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
            throw new ServiceResponseException("Không thể tìm kiếm nhân viên");
        }
    }

    @Override
    public void update(InuhaTaiKhoanModel model) {
        try {
            Optional<InuhaTaiKhoanModel> find = repository.getById(model.getId());
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy nhân viên");
            }
            
            if (repository.hasUsername(model)) { 
                throw new ServiceResponseException("Tên tài khoản đã tồn tại trên hệ thống");
            }
            if (repository.hasEmail(model)) { 
                throw new ServiceResponseException("Email đã tồn tại trên hệ thống");
            }
            if (repository.hasSdt(model)) { 
                throw new ServiceResponseException("Số điện thoại đã tồn tại trên hệ thống");
            }
	    
            repository.update(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể cập nhật nhân viên");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Optional<InuhaTaiKhoanModel> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new ServiceResponseException("Không tìm thấy nhân viên");
            }
            
	    InuhaTaiKhoanModel item = find.get();
            if (repository.hasUse(id)) { 
                item.setTrangThaiXoa(TrangThaiXoaConstant.DA_XOA);
                repository.update(item);
            } else { 
                repository.delete(id);
		SessionUtils.removeImageAvatar(item.getAvatar());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể xoá nhân viên này");
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
    }

    @Override
    public List<InuhaTaiKhoanModel> getAll() {
        try {
            return repository.selectAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServiceResponseException("Không thể lấy danh sách nhân viên");
        }
    }

    @Override
    public List<InuhaTaiKhoanModel> getPage(FilterRequest request) {
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

    public InuhaTaiKhoanModel login(String username, String password) {
        try {
            Optional<InuhaTaiKhoanModel> find = ValidateUtils.isEmail(username) ? repository.findByEmail(username) : repository.findByUsername(username);
            if (find.isEmpty() || !find.get().getPassword().equals(password)) {
                throw new ServiceResponseException("Tài khoản hoặc mật khẩu không chính xác!");
            }
            InuhaTaiKhoanModel nhanVien = find.get();
            if (!nhanVien.isTrangThai()) {
                throw new ServiceResponseException("Tài khoản của bạn đã bị khoá!");
            }

            return nhanVien;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
        }
    }

    public void requestForgotPassword(String email) {

        if (!ValidateUtils.isEmail(email)) {
            throw new ServiceResponseException(ErrorConstant.EMAIL_FORMAT);
        }

        try {
            Optional<InuhaTaiKhoanModel> nhanVien = repository.findByEmail(email);
            if (nhanVien.isEmpty()) {
                throw new ServiceResponseException("Email vừa nhập không tồn tại trên hệ thống.");
            }

            String codeOTP = SessionUtils.generateCode(100000, 999999);

            if (repository.updateOTPById(nhanVien.get().getId(), codeOTP) < 1) {
                throw new RuntimeException("Không thể update OTP");
            }

            if (!SessionUtils.sendOtp(codeOTP, email)) {
                throw new ServiceResponseException("Không thể gửi mã. Vui lòng thử lại sau ít phút.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
        }
    }

    public void validOtp(String email, String otp) {
        try {
            if (!ValidateUtils.isEmail(email)) {
                throw new ServiceResponseException(ErrorConstant.EMAIL_FORMAT);
            }

            if (!repository.checkOtp(email, otp)) {
                throw new ServiceResponseException("Mã OTP không chính xác.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
        }
    }


    public void changePassword(String email, String otp, String password, String confirmPassword) {

        try {
            if (!ValidateUtils.isPassword(password)) {
                throw new ServiceResponseException("Mật khẩu không hợp lệ");
            }

            if (!password.equals(confirmPassword)) {
                throw new ServiceResponseException("Mật khẩu nhập lại không chính xác");
            }

            if (repository.updateForgotPassword(password, email, otp) < 1) {
                throw new ServiceResponseException("Không thể cập nhật mật khẩu. Vui lòng thử lại");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
        }

    }

    public void changeAvatar(String avatar) {
        try {
            if (!SessionLogin.getInstance().isLogin()) {
                throw new ServiceResponseException("Vui lòng đăng nhập để sử dụng chức năng này");
            }

            int id = SessionLogin.getInstance().getData().getId();

            if (repository.updateAvatar(id, avatar) < 1) {
                throw new ServiceResponseException("Không thể cập nhật ảnh đại diện. Vui lòng thử lại");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
        }
    }

    public void changePassword(String oldPassword, String newPassword, String confirmPassword) {

        try {
            if (!oldPassword.equals(SessionLogin.getInstance().getData().getPassword())) {
                throw new ServiceResponseException("Mật khẩu cũ không chính xác");
            }

            if (!ValidateUtils.isPassword(newPassword)) {
                throw new ServiceResponseException("Mật khẩu mới không hợp lệ");
            }

            if (!newPassword.equals(confirmPassword)) {
                throw new ServiceResponseException("Mật khẩu nhập lại không chính xác");
            }

            int id = SessionLogin.getInstance().getData().getId();
            if (repository.updatePassword(id, oldPassword, newPassword) < 1) {
                throw new ServiceResponseException("Không thể đổi mật khẩu. Vui lòng thử lại");
            }

            String username = SessionLogin.getInstance().getData().getUsername();
            InuhaTaiKhoanModel data = login(username, newPassword);
            SessionLogin.getInstance().create(username, newPassword, data);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
        }

    }
    
    public void changePassword(InuhaTaiKhoanModel model, String newPassword, String confirmPassword) {

        try {

            if (!ValidateUtils.isPassword(newPassword)) {
                throw new ServiceResponseException("Mật khẩu mới không hợp lệ");
            }

            if (!newPassword.equals(confirmPassword)) {
                throw new ServiceResponseException("Mật khẩu nhập lại không chính xác");
            }

	    model.setPassword(newPassword);
            if (repository.update(model) < 1) {
                throw new ServiceResponseException("Không thể đổi mật khẩu. Vui lòng thử lại");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
        }
    }
       
       
    public int getLastId() {
        try {
	    return repository.getLastId();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceResponseException(ErrorConstant.DEFAULT_ERROR);
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