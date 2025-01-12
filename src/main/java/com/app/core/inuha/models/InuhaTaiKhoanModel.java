package com.app.core.inuha.models;

import com.app.utils.SessionUtils;
import com.app.utils.TimeUtils;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author inuHa
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InuhaTaiKhoanModel {

    private int stt;
    
    private int id;

    private String username;
    
    private String password;

    private String email;

    private String hoTen;

    private String sdt;

    private boolean gioiTinh;

    private String diaChi;

    private String avatar;

    private String otp;

    private boolean trangThai;

    private boolean isAdmin;

    private String ngayTao;
    
    private boolean trangThaiXoa;
    
    public Object[] toDataRow() {
	return new Object[] {
	    stt,
	    SessionUtils.getAvatar(this),
	    username,
	    email,
	    hoTen,
	    sdt,
	    SessionUtils.getGioiTinh(gioiTinh),
	    diaChi,
	    SessionUtils.getChucVu(isAdmin),
	    trangThai,
	    TimeUtils.date("dd-MM-yyyy", ngayTao)
	};
    }

}
