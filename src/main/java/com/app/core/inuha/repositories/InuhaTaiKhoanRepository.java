package com.app.core.inuha.repositories;


import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.interfaces.IDAOinterface;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.request.InuhaFilterTaiKhoanRequest;
import com.app.utils.TimeUtils;
import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author InuHa
 */
public class InuhaTaiKhoanRepository implements IDAOinterface<InuhaTaiKhoanModel, Integer> {

    private final static String TABLE_NAME = "TaiKhoan";
    
    private static InuhaTaiKhoanRepository instance = null;
    
    public static InuhaTaiKhoanRepository getInstance() { 
	if (instance == null) { 
	    instance = new InuhaTaiKhoanRepository();
	}
	return instance;
    }
    
    private InuhaTaiKhoanRepository() { 
	
    }
    
    @Override
    public int insert(InuhaTaiKhoanModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            INSERT INTO %s(tai_khoan, mat_khau, email, ho_ten, sdt, gioi_tinh, dia_chi, hinh_anh, otp, trang_thai, adm, ngay_tao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getUsername(),
                model.getPassword(),
                model.getEmail(),
                model.getHoTen(),
                model.getSdt(),
                model.isGioiTinh(),
                model.getDiaChi(),
                model.getAvatar(),
                model.getOtp(),
                model.isTrangThai(),
                model.isAdmin(),
                TimeUtils.currentDate()
	    };
            result = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return result;
    }

    @Override
    public int update(InuhaTaiKhoanModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            UPDATE %s SET
                tai_khoan = ?,
                mat_khau = ?,
                email = ?,
                ho_ten = ?,
                sdt = ?,
                gioi_tinh = ?,
                dia_chi = ?,
                hinh_anh = ?,
                otp = ?,
                trang_thai = ?,
                adm = ?
            WHERE id = ?
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getUsername(),
                model.getPassword(),
                model.getEmail(),
                model.getHoTen(),
                model.getSdt(),
                model.isGioiTinh(),
                model.getDiaChi(),
                model.getAvatar(),
                model.getOtp(),
                model.isTrangThai(),
                model.isAdmin(),
                model.getId()
            };
            result = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return result;
    }

    @Override
    public int delete(Integer id) throws SQLException {
        int result = 0;
        String query = String.format("""
            DELETE FROM HoaDon WHERE id_tai_khoan = ? AND trang_thai = ?;
            DELETE FROM %s WHERE id = ?
        """, TABLE_NAME);
        try {
            result = JbdcHelper.updateAndFlush(query, id, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN, id);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return result;
    }

    @Override
    public boolean has(Integer id) throws SQLException {
        String query = String.format("SELECT TOP(1) 1 FROM %s WHERE id = ? AND trang_thai_xoa != 1", TABLE_NAME);
        try {
            return JbdcHelper.value(query, id) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }

    public boolean hasUse(Integer id) throws SQLException {
        String query = "SELECT TOP(1) 1 FROM HoaDon WHERE id_tai_khoan = ?";
        try {
            return JbdcHelper.value(query, id) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    public boolean hasUsername(String username) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                tai_khoan LIKE ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, username) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    public boolean hasEmail(String email) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                email LIKE ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, email) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
	
    public boolean hasSdt(String sdt) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                sdt LIKE ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, sdt) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    public boolean hasUsername(InuhaTaiKhoanModel model) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                tai_khoan LIKE ? AND
                id != ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, model.getUsername(), model.getId()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    public boolean hasEmail(InuhaTaiKhoanModel model) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                email LIKE ? AND
                id != ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, model.getEmail(), model.getId()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
	
    public boolean hasSdt(InuhaTaiKhoanModel model) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                sdt LIKE ? AND
                id != ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, model.getSdt(), model.getId()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
	
    @Override
    public Optional<InuhaTaiKhoanModel> getById(Integer id) throws SQLException {
        ResultSet resultSet = null;
        InuhaTaiKhoanModel TaiKhoan = null;

        String query = String.format("SELECT * FROM %s WHERE id = ? AND trang_thai_xoa != 1", TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, id);
            while(resultSet.next()) {
                TaiKhoan = buildData(resultSet);
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        finally {
            JbdcHelper.close(resultSet);
        }

        return Optional.ofNullable(TaiKhoan);
    }

    @Override
    public List<InuhaTaiKhoanModel> selectAll() throws SQLException {
        List<InuhaTaiKhoanModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            SELECT *, ROW_NUMBER() OVER (ORDER BY id DESC) AS stt
            FROM %s
            WHERE trang_thai_xoa != 1
            ORDER BY id DESC
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query);
            while(resultSet.next()) {
                InuhaTaiKhoanModel taiKhoan = buildData(resultSet, true);
                list.add(taiKhoan);
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        finally {
            JbdcHelper.close(resultSet);
        }

        return list;
    }

    @Override
    public List<InuhaTaiKhoanModel> selectPage(FilterRequest request) throws SQLException {
	InuhaFilterTaiKhoanRequest filter = (InuhaFilterTaiKhoanRequest) request;
        List<InuhaTaiKhoanModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            WITH TableCTE AS (
                SELECT
                    *,
                    ROW_NUMBER() OVER (ORDER BY id DESC) AS stt
                FROM %s
                WHERE 
                    trang_thai_xoa != 1 AND
		    (
			(? IS NULL OR tai_khoan LIKE ? OR ho_ten LIKE ? OR email LIKE ?) AND
			(COALESCE(?, -1) < 0 OR adm = ?) AND
			(COALESCE(?, -1) < 0 OR gioi_tinh = ?) AND
                        (COALESCE(?, -1) < 0 OR trang_thai = ?)
		    )
            )
            SELECT *
            FROM TableCTE
            WHERE stt BETWEEN ? AND ?
        """, TABLE_NAME);

        int[] offset = FilterRequest.getOffset(request.getPage(), request.getSize());
        int start = offset[0];
        int limit = offset[1];

        Object[] args = new Object[] {
	    filter.getKeyword(),
            String.format("%%%s%%", filter.getKeyword()),
            String.format("%%%s%%", filter.getKeyword()),
	    String.format("%%%s%%", filter.getKeyword()),
	    filter.getChucVu().getValue(),
	    filter.getChucVu().getValue(),
	    filter.getGioiTinh().getValue(),
	    filter.getGioiTinh().getValue(),
	    filter.getTrangThai().getValue(),
	    filter.getTrangThai().getValue(),
            start,
            limit
        };

        try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaTaiKhoanModel taiKhoan = buildData(resultSet, true);
                list.add(taiKhoan);
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        finally {
            JbdcHelper.close(resultSet);
        }

        return list;
    }

    @Override
    public int count(FilterRequest request) throws SQLException {
        InuhaFilterTaiKhoanRequest filter = (InuhaFilterTaiKhoanRequest) request;
        int totalPages = 0;
        int totalRows = 0;

        String query = String.format("""
            SELECT COUNT(*)
            FROM %s 
            WHERE 
                trang_thai_xoa != 1 AND
		(
		    (? IS NULL OR tai_khoan LIKE ? OR ho_ten LIKE ? OR email LIKE ?) AND
		    (COALESCE(?, -1) < 0 OR adm = ?) AND
		    (COALESCE(?, -1) < 0 OR gioi_tinh = ?) AND
		    (COALESCE(?, -1) < 0 OR trang_thai = ?)
		)
        """, TABLE_NAME);

        Object[] args = new Object[] {
	    filter.getKeyword(),
            String.format("%%%s%%", filter.getKeyword()),
            String.format("%%%s%%", filter.getKeyword()),
	    String.format("%%%s%%", filter.getKeyword()),
	    filter.getChucVu().getValue(),
	    filter.getChucVu().getValue(),
	    filter.getGioiTinh().getValue(),
	    filter.getGioiTinh().getValue(),
	    filter.getTrangThai().getValue(),
	    filter.getTrangThai().getValue()
        };

        try {
            totalRows = (int) JbdcHelper.value(query, args);
            totalPages = (int) Math.ceil((double) totalRows / filter.getSize());
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return totalPages;
    }

    private InuhaTaiKhoanModel buildData(ResultSet resultSet) throws SQLException {
	return buildData(resultSet, false);
    }
    
    private InuhaTaiKhoanModel buildData(ResultSet resultSet, boolean addSTT) throws SQLException {
        return InuhaTaiKhoanModel.builder()
            .id(resultSet.getInt("id"))
	    .stt(addSTT ? resultSet.getInt("stt") : -1)
            .username(resultSet.getString("tai_khoan"))
            .password(resultSet.getString("mat_khau"))
            .email(resultSet.getString("email"))
            .hoTen(resultSet.getString("ho_ten"))
            .sdt(resultSet.getString("sdt"))
            .gioiTinh(resultSet.getBoolean("gioi_tinh"))
            .diaChi(resultSet.getString("dia_chi"))
            .avatar(resultSet.getString("hinh_anh"))
            .otp(resultSet.getString("otp"))
            .trangThai(resultSet.getBoolean("trang_thai"))
            .isAdmin(resultSet.getBoolean("adm"))
            .ngayTao(resultSet.getString("ngay_tao"))
	    .trangThaiXoa(resultSet.getBoolean("trang_thai_xoa"))            
	    .build();
    }

    public Optional<InuhaTaiKhoanModel> findByEmail(String email) throws SQLException {
        ResultSet resultSet = null;
        InuhaTaiKhoanModel taiKhoan = null;

        String query = String.format("SELECT * FROM %s WHERE email LIKE ? AND trang_thai_xoa != 1", TABLE_NAME);

        try {

            resultSet = JbdcHelper.query(query, email);
            while(resultSet.next()) {
                    taiKhoan = buildData(resultSet);
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        finally {
            JbdcHelper.close(resultSet);
        }

    return Optional.ofNullable(taiKhoan);
    }

    public Optional<InuhaTaiKhoanModel> findByUsername(String username) throws SQLException {
        ResultSet resultSet = null;
        InuhaTaiKhoanModel taiKhoan = null;

        String query = String.format("SELECT * FROM %s WHERE tai_khoan LIKE ? AND trang_thai_xoa != 1", TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, username);
            while(resultSet.next()) {
                taiKhoan = buildData(resultSet);
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        finally {
            JbdcHelper.close(resultSet);
        }

        return Optional.ofNullable(taiKhoan);
    }

    public Integer updateOTPById(int id, String otp) throws SQLException {
        int row = 0;

        String query = String.format("UPDATE %s SET otp = ? WHERE id = ? AND trang_thai_xoa != 1", TABLE_NAME);

        Object[] args = new Object[] {
            otp,
            id
        };

        try {
            row = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return row;
    }

    public boolean checkOtp(String email, String otp) throws SQLException {
        String query = String.format("SELECT TOP(1) 1 FROM %s WHERE email LIKE ? AND otp = ? AND trang_thai_xoa != 1", TABLE_NAME);

        Object[] args = new Object[] {
            email,
            otp
        };

        try {
            return JbdcHelper.value(query, args) != null;
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }

    public Integer updateForgotPassword(String password, String email, String otp) throws SQLException {
        int row = 0;

        String query = String.format("""
            UPDATE %s
            SET mat_khau = ?, otp = NULL
            WHERE email LIKE ? AND otp = ? AND trang_thai_xoa != 1
        """, TABLE_NAME);

        Object[] args = new Object[] {
            password,
            email,
            otp
        };

        try {
            row = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return row;
    }

    public Integer updateAvatar(int id, String avatar) throws SQLException {
        int row = 0;

        String query = String.format("UPDATE %s SET hinh_anh = ? WHERE id = ? AND trang_thai_xoa != 1", TABLE_NAME);

        Object[] args = new Object[] {
            avatar,
            id
        };

        try {
            row = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return row;
    }

    public Integer updatePassword(int id, String oldPassword, String newPassword) throws SQLException {
        int row = 0;

        String query = String.format("UPDATE %s SET mat_khau = ? WHERE id = ? AND mat_khau = ? AND trang_thai_xoa != 1", TABLE_NAME);

        Object[] args = new Object[] {
            newPassword,
            id,
            oldPassword
        };

        try {
            row = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return row;
    }

    public int getLastId() throws SQLException {
        String query = String.format("SELECT IDENT_CURRENT('%s') AS NextId", TABLE_NAME);
        try {
	    BigDecimal id = (BigDecimal) JbdcHelper.value(query);
            return id.intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
}
