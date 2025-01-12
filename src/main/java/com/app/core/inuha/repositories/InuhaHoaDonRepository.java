package com.app.core.inuha.repositories;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.PhuongThucThanhToanConstant;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.interfaces.IDAOinterface;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.common.infrastructure.session.SessionLogin;
import com.app.core.inuha.models.InuhaHoaDonModel;
import com.app.core.inuha.models.InuhaKhachHangModel;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.utils.SessionUtils;
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
public class InuhaHoaDonRepository implements IDAOinterface<InuhaHoaDonModel, Integer> {
    
    private final static String TABLE_NAME = "HoaDon";
    
    public final static int MAX_WAIT_BILL = 10;
    
    private final InuhaTaiKhoanRepository taiKhoanRepository = InuhaTaiKhoanRepository.getInstance();
    
    private final InuhaKhachHangRepository khachHangRepository = InuhaKhachHangRepository.getInstance();
    
    private final InuhaPhieuGiamGiaRepository phieuGiamGiaRepository = InuhaPhieuGiamGiaRepository.getInstance();
    
    private static InuhaHoaDonRepository instance = null;
    
    public static InuhaHoaDonRepository getInstance() { 
	if (instance == null) { 
	    instance = new InuhaHoaDonRepository();
	}
	return instance;
    }
    
    private InuhaHoaDonRepository() { 
	
    }
    
    @Override
    public int insert(InuhaHoaDonModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            INSERT INTO %s(id_tai_khoan, ma, phuong_thuc_thanh_toan, trang_thai, ngay_tao, ngay_cap_nhat)
            VALUES (?, ?, ?, ?, ?, ?)
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                SessionLogin.getInstance().getData().getId(),
		model.getMa(),
		PhuongThucThanhToanConstant.TIEN_MAT,
		TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN,
		TimeUtils.currentDateTime(),
		TimeUtils.currentDateTime()
            };
            result = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return result;
    }

    @Override
    public int update(InuhaHoaDonModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            UPDATE %s SET
                id_khach_hang = ?,
                id_phieu_giam_gia = ?,
                tien_giam = ?,
		tien_mat = ?,
		tien_chuyen_khoan = ?,
		phuong_thuc_thanh_toan = ?,
		trang_thai = ?,
		ngay_cap_nhat = ?,
		trang_thai_xoa = ?
            WHERE id = ?
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getKhachHang() != null ? model.getKhachHang().getId() : null,
                model.getPhieuGiamGia() != null ? model.getPhieuGiamGia().getId() : null,
		model.getTienGiam(),
		model.getTienMat(),
		model.getTienChuyenKhoan(),
		model.getPhuongThucThanhToan(),
		model.getTrangThai(),
                TimeUtils.currentDateTime(),
		model.isTrangThaiXoa(),
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
        String query = String.format("DELETE FROM %s WHERE id = ?", TABLE_NAME);
        try {
            result = JbdcHelper.updateAndFlush(query, id);
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
        String query = "SELECT TOP(1) 1 FROM HoaDonChiTiet WHERE id_hoa_don = ?";
        try {
            return JbdcHelper.value(query, id) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    @Override
    public Optional<InuhaHoaDonModel> getById(Integer id) throws SQLException {
        ResultSet resultSet = null;
        InuhaHoaDonModel model = null;

        String query = String.format("""
            SELECT * ,
		(SELECT ISNULL(SUM(gia_ban), 0) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) AS tong_tien_hang
            FROM %s AS hd
            WHERE 
                hd.id = ? AND
                hd.trang_thai_xoa != 1
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, id);
            while(resultSet.next()) {
                model = buildData(resultSet, false);
                Optional<InuhaTaiKhoanModel> taiKhoan = taiKhoanRepository.getById(resultSet.getInt("id_tai_khoan"));
                if (taiKhoan.isPresent()) { 
                    model.setTaiKhoan(taiKhoan.get());
                }
		
		Optional<InuhaKhachHangModel> khachHang = khachHangRepository.getById(resultSet.getInt("id_khach_hang"));
                if (khachHang.isPresent()) { 
                    model.setKhachHang(khachHang.get());
                }
		
		Optional<InuhaPhieuGiamGiaModel> phieuGiamGia = phieuGiamGiaRepository.getById(resultSet.getInt("id_phieu_giam_gia"));
                if (phieuGiamGia.isPresent()) { 
                    model.setPhieuGiamGia(phieuGiamGia.get());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        finally {
            JbdcHelper.close(resultSet);
        }

        return Optional.ofNullable(model);
    }

    @Override
    public List<InuhaHoaDonModel> selectAll() throws SQLException {
        List<InuhaHoaDonModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            SELECT TOP(%d)
                hd.*,
                ROW_NUMBER() OVER (ORDER BY hd.id DESC) AS stt,
                (SELECT ISNULL(SUM(gia_ban), 0) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) AS tong_tien_hang
            FROM %s AS hd
            WHERE 
                hd.trang_thai_xoa != 1 AND
                hd.trang_thai = %d
                %s
            ORDER BY hd.id DESC 
        """, MAX_WAIT_BILL, TABLE_NAME, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN, SessionUtils.isManager() ? " " : " AND id_tai_khoan = " + SessionLogin.getInstance().getData().getId());

        try {
            resultSet = JbdcHelper.query(query);
            while(resultSet.next()) {
                InuhaHoaDonModel model = buildData(resultSet);
		Optional<InuhaTaiKhoanModel> taiKhoan = taiKhoanRepository.getById(resultSet.getInt("id_tai_khoan"));
		if (taiKhoan.isPresent()) { 
                    model.setTaiKhoan(taiKhoan.get());
                }
		
		Optional<InuhaKhachHangModel> khachHang = khachHangRepository.getById(resultSet.getInt("id_khach_hang"));
                if (khachHang.isPresent()) { 
                    model.setKhachHang(khachHang.get());
                }
		
                list.add(model);
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
    public List<InuhaHoaDonModel> selectPage(FilterRequest request) throws SQLException {
        List<InuhaHoaDonModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            WITH TableCTE AS (
                SELECT
                    hd.*,
                    ROW_NUMBER() OVER (ORDER BY hd.id DESC) AS stt,
		    (SELECT ISNULL(SUM(gia_ban), 0) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) AS tong_tien_hang
                FROM %s AS hd
                WHERE hd.trang_thai_xoa != 1
            )
            SELECT *
            FROM TableCTE
            WHERE stt BETWEEN ? AND ?
        """, TABLE_NAME);

        int[] offset = FilterRequest.getOffset(request.getPage(), request.getSize());
        int start = offset[0];
        int limit = offset[1];

        Object[] args = new Object[] {
            start,
            limit
        };

        try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaHoaDonModel model = buildData(resultSet);
                Optional<InuhaTaiKhoanModel> taiKhoan = taiKhoanRepository.getById(resultSet.getInt("id_tai_khoan"));
                if (taiKhoan.isPresent()) { 
                    model.setTaiKhoan(taiKhoan.get());
                }
		
		Optional<InuhaKhachHangModel> khachHang = khachHangRepository.getById(resultSet.getInt("id_khach_hang"));
                if (khachHang.isPresent()) { 
                    model.setKhachHang(khachHang.get());
                }
		
		Optional<InuhaPhieuGiamGiaModel> phieuGiamGia = phieuGiamGiaRepository.getById(resultSet.getInt("id_phieu_giam_gia"));
                if (phieuGiamGia.isPresent()) { 
                    model.setPhieuGiamGia(phieuGiamGia.get());
                }
                list.add(model);
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
        int totalPages = 0;
        int totalRows = 0;

        String query = String.format("SELECT COUNT(*) FROM %s WHERE trang_thai_xoa != 1", TABLE_NAME);

        try {
            totalRows = (int) JbdcHelper.value(query);
            totalPages = (int) Math.ceil((double) totalRows / request.getSize());
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return totalPages;
    }
    
    public boolean isMaxHoaDonCho() throws SQLException {

        String query = String.format("SELECT COUNT(*) FROM %s WHERE trang_thai = %d AND id_tai_khoan = ?", TABLE_NAME, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN);

        try {
            return (int) JbdcHelper.value(query, SessionLogin.getInstance().getData().getId()) >= MAX_WAIT_BILL;
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
	
    private InuhaHoaDonModel buildData(ResultSet resultSet) throws SQLException { 
        return buildData(resultSet, true);
    }
    
    private InuhaHoaDonModel buildData(ResultSet resultSet, boolean addSTT) throws SQLException { 
        return InuhaHoaDonModel.builder()
            .id(resultSet.getInt("id"))
            .stt(addSTT ? resultSet.getInt("stt") : -1)
            .ma(resultSet.getString("ma"))
	    .tienGiam(resultSet.getDouble("tien_giam"))
	    .tienMat(resultSet.getDouble("tien_mat"))
	    .tienChuyenKhoan(resultSet.getDouble("tien_chuyen_khoan"))
	    .phuongThucThanhToan(resultSet.getInt("phuong_thuc_thanh_toan"))
	    .trangThai(resultSet.getInt("trang_thai"))
	    .trangThaiXoa(resultSet.getBoolean("trang_thai_xoa"))
            .ngayTao(resultSet.getString("ngay_tao"))
            .ngayCapNhat(resultSet.getString("ngay_cap_nhat"))
	    .tongTienHang(resultSet.getDouble("tong_tien_hang"))
	    .build();
    }
    
    public String getLastId() throws SQLException {
        String query = String.format("SELECT IDENT_CURRENT('%s') AS NextId", TABLE_NAME);
        try {
	    BigDecimal id = (BigDecimal) JbdcHelper.value(query);
            return String.valueOf(id.intValue());
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
}
