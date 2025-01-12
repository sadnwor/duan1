package com.app.core.inuha.repositories;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.interfaces.IDAOinterface;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaChatLieuModel;
import com.app.core.inuha.models.sanpham.InuhaDanhMucModel;
import com.app.core.inuha.models.sanpham.InuhaDeGiayModel;
import com.app.core.inuha.models.sanpham.InuhaKieuDangModel;
import com.app.core.inuha.models.sanpham.InuhaThuongHieuModel;
import com.app.core.inuha.models.sanpham.InuhaXuatXuModel;
import com.app.core.inuha.request.InuhaFilterSanPhamRequest;
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
public class InuhaSanPhamRepository implements IDAOinterface<InuhaSanPhamModel, Integer> {
    
    private final static String TABLE_NAME = "SanPham";
    
    private static InuhaSanPhamRepository instance = null;
    
    public static InuhaSanPhamRepository getInstance() { 
	if (instance == null) { 
	    instance = new InuhaSanPhamRepository();
	}
	return instance;
    }
    
    private InuhaSanPhamRepository() { 
	
    }
    
    @Override
    public int insert(InuhaSanPhamModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            INSERT INTO %s(id_danh_muc, id_thuong_hieu, id_xuat_xu, id_kieu_dang, id_chat_lieu, id_de_giay, ma, ten, mo_ta, gia_nhap, gia_ban, hinh_anh, trang_thai, ngay_tao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getDanhMuc().getId(),
                model.getThuongHieu().getId(),
                model.getXuatXu().getId(),
                model.getKieuDang().getId(),
                model.getChatLieu().getId(),
                model.getDeGiay().getId(),
                model.getMa(),
                model.getTen(),
                model.getMoTa(),
		model.getGiaNhap(),
                model.getGiaBan(),
                model.getHinhAnh(),
                model.isTrangThai(),
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
    public int update(InuhaSanPhamModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            UPDATE %s SET
                id_danh_muc = ?,
                id_thuong_hieu = ?,
                id_xuat_xu = ?,
                id_kieu_dang = ?,
                id_chat_lieu = ?,
                id_de_giay = ?,
                ten = ?,
                mo_ta = ?,
                gia_nhap = ?,
                gia_ban = ?,
                hinh_anh = ?,
                trang_thai = ?,
                trang_thai_xoa = ?,
                ngay_cap_nhat = ?
            WHERE id = ?;
                                     
            UPDATE HoaDonChiTiet 
	    SET gia_nhap = ?, gia_ban = ?
            WHERE
                id_san_pham_chi_tiet IN (
		    SELECT id FROM SanPhamChiTiet WHERE id_san_pham = ?
		) AND
                id_hoa_don IN (
                    SELECT id FROM HoaDon WHERE trang_thai = %d
                )
        """, TABLE_NAME, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN);
        try {
            Object[] args = new Object[] {
                model.getDanhMuc().getId(),
                model.getThuongHieu().getId(),
                model.getXuatXu().getId(),
                model.getKieuDang().getId(),
                model.getChatLieu().getId(),
                model.getDeGiay().getId(),
                model.getTen(),
                model.getMoTa(),
		model.getGiaNhap(),
                model.getGiaBan(),
                model.getHinhAnh(),
                model.isTrangThai(),
                model.isTrangThaiXoa(),
                TimeUtils.currentDate(),
                model.getId(),
		
		model.getGiaNhap(),
		model.getGiaBan(),
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
            DELETE FROM HoaDonChiTiet WHERE id_san_pham_chi_tiet IN (SELECT id FROM SanPhamChiTiet WHERE id_san_pham = ?);
            DELETE FROM SanPhamChiTiet WHERE id_san_pham = ?;
            DELETE FROM %s WHERE id = ?
        """, TABLE_NAME);
        try {
            result = JbdcHelper.updateAndFlush(query, id, id, id);
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

    public boolean has(String name) throws SQLException {
        String query = String.format("SELECT TOP(1) 1 FROM %s WHERE ten LIKE ? AND trang_thai_xoa != 1", TABLE_NAME);
        try {
            return JbdcHelper.value(query, name) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
        
    public boolean hasUse(Integer id) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM HoaDonChitiet AS hdct
                JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
                JOIN HoaDon AS hd ON hd.id = hdct.id_hoa_don
            WHERE spct.id_san_pham = ? AND hd.trang_thai != %d
        """, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN);
        try {
            return JbdcHelper.value(query, id) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    public boolean has(InuhaSanPhamModel model) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                ten LIKE ? AND
                id != ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, model.getTen(), model.getId()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
        
    @Override
    public Optional<InuhaSanPhamModel> getById(Integer id) throws SQLException {
        ResultSet resultSet = null;
        InuhaSanPhamModel model = null;

        String query = String.format("""
            SELECT
                sp.*,
                dm.ten AS ten_danh_muc,
                dm.ngay_tao AS ngay_tao_danh_muc,
                dm.ngay_cap_nhat AS ngay_cap_nhat_danh_muc,
                th.ten AS ten_thuong_hieu,
                th.ngay_tao AS ngay_tao_thuong_hieu,
                th.ngay_cap_nhat AS ngay_cap_nhat_thuong_hieu,
                xx.ten AS ten_xuat_xu,
                xx.ngay_tao AS ngay_tao_xuat_xu,
                xx.ngay_cap_nhat AS ngay_cap_nhat_xuat_xu,
                kd.ten AS ten_kieu_dang,
                kd.ngay_tao AS ngay_tao_kieu_dang,
                kd.ngay_cap_nhat AS ngay_cap_nhat_kieu_dang,
                cl.ten AS ten_chat_lieu,
                cl.ngay_tao AS ngay_tao_chat_lieu,
                cl.ngay_cap_nhat AS ngay_cap_nhat_chat_lieu,
                dg.ten AS ten_de_giay,
                dg.ngay_tao AS ngay_tao_de_giay,
                dg.ngay_cap_nhat AS ngay_cap_nhat_de_giay,
                (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) AS so_luong
            FROM %s AS sp
                LEFT JOIN DanhMuc AS dm ON dm.id = sp.id_danh_muc
                LEFT JOIN ThuongHieu AS th ON th.id = sp.id_thuong_hieu
                LEFT JOIN XuatXu AS xx ON xx.id = sp.id_xuat_xu
                LEFT JOIN KieuDang AS kd ON kd.id = sp.id_kieu_dang
                LEFT JOIN ChatLieu AS cl ON cl.id = sp.id_chat_lieu
                LEFT JOIN DeGiay AS dg ON dg.id = sp.id_de_giay
            WHERE
                sp.id = ? AND
                sp.trang_thai_xoa != 1
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, id);
            while(resultSet.next()) {
                model = buildData(resultSet, false);
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
    public List<InuhaSanPhamModel> selectAll() throws SQLException {
        List<InuhaSanPhamModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            SELECT
                sp.*,
                ROW_NUMBER() OVER (ORDER BY sp.id DESC) AS stt,
                dm.ten AS ten_danh_muc,
                dm.ngay_tao AS ngay_tao_danh_muc,
                dm.ngay_cap_nhat AS ngay_cap_nhat_danh_muc,
                th.ten AS ten_thuong_hieu,
                th.ngay_tao AS ngay_tao_thuong_hieu,
                th.ngay_cap_nhat AS ngay_cap_nhat_thuong_hieu,
                xx.ten AS ten_xuat_xu,
                xx.ngay_tao AS ngay_tao_xuat_xu,
                xx.ngay_cap_nhat AS ngay_cap_nhat_xuat_xu,
                kd.ten AS ten_kieu_dang,
                kd.ngay_tao AS ngay_tao_kieu_dang,
                kd.ngay_cap_nhat AS ngay_cap_nhat_kieu_dang,
                cl.ten AS ten_chat_lieu,
                cl.ngay_tao AS ngay_tao_chat_lieu,
                cl.ngay_cap_nhat AS ngay_cap_nhat_chat_lieu,
                dg.ten AS ten_de_giay,
                dg.ngay_tao AS ngay_tao_de_giay,
                dg.ngay_cap_nhat AS ngay_cap_nhat_de_giay,
                (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) AS so_luong
            FROM %s AS sp
                LEFT JOIN DanhMuc AS dm ON dm.id = sp.id_danh_muc
                LEFT JOIN ThuongHieu AS th ON th.id = sp.id_thuong_hieu
                LEFT JOIN XuatXu AS xx ON xx.id = sp.id_xuat_xu
                LEFT JOIN KieuDang AS kd ON kd.id = sp.id_kieu_dang
                LEFT JOIN ChatLieu AS cl ON cl.id = sp.id_chat_lieu
                LEFT JOIN DeGiay AS dg ON dg.id = sp.id_de_giay
            WHERE
                sp.trang_thai_xoa != 1
            ORDER BY sp.id DESC 
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query);
            while(resultSet.next()) {
                InuhaSanPhamModel model = buildData(resultSet);
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



    public List<InuhaSanPhamModel> selectPage(FilterRequest request, boolean isBanHang) throws SQLException {
        
        InuhaFilterSanPhamRequest filter = (InuhaFilterSanPhamRequest) request;
        
        List<InuhaSanPhamModel> list = new ArrayList<>();
        ResultSet resultSet = null;
        
        String query = String.format("""
            WITH TableCTE AS (
                SELECT
                    sp.*,
                    ROW_NUMBER() OVER (ORDER BY sp.id DESC) AS stt,
                    dm.ten AS ten_danh_muc,
                    dm.ngay_tao AS ngay_tao_danh_muc,
                    dm.ngay_cap_nhat AS ngay_cap_nhat_danh_muc,
                    th.ten AS ten_thuong_hieu,
                    th.ngay_tao AS ngay_tao_thuong_hieu,
                    th.ngay_cap_nhat AS ngay_cap_nhat_thuong_hieu,
                    xx.ten AS ten_xuat_xu,
                    xx.ngay_tao AS ngay_tao_xuat_xu,
                    xx.ngay_cap_nhat AS ngay_cap_nhat_xuat_xu,
                    kd.ten AS ten_kieu_dang,
                    kd.ngay_tao AS ngay_tao_kieu_dang,
                    kd.ngay_cap_nhat AS ngay_cap_nhat_kieu_dang,
                    cl.ten AS ten_chat_lieu,
                    cl.ngay_tao AS ngay_tao_chat_lieu,
                    cl.ngay_cap_nhat AS ngay_cap_nhat_chat_lieu,
                    dg.ten AS ten_de_giay,
                    dg.ngay_tao AS ngay_tao_de_giay,
                    dg.ngay_cap_nhat AS ngay_cap_nhat_de_giay,
                    (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id %s) AS so_luong
                FROM %s AS sp
                    LEFT JOIN DanhMuc AS dm ON dm.id = sp.id_danh_muc
                    LEFT JOIN ThuongHieu AS th ON th.id = sp.id_thuong_hieu
                    LEFT JOIN XuatXu AS xx ON xx.id = sp.id_xuat_xu
                    LEFT JOIN KieuDang AS kd ON kd.id = sp.id_kieu_dang
                    LEFT JOIN ChatLieu AS cl ON cl.id = sp.id_chat_lieu
                    LEFT JOIN DeGiay AS dg ON dg.id = sp.id_de_giay
                WHERE
                    sp.trang_thai_xoa != 1 AND
                    (
                        (? IS NULL OR sp.ten LIKE ? OR sp.ma LIKE ?) AND
			(COALESCE(?, 0) < 1 OR dm.ten LIKE ?) AND
                        (COALESCE(?, 0) < 1 OR th.ten LIKE ?) AND
                        (COALESCE(?, 0) < 1 OR xx.ten LIKE ?) AND
			(COALESCE(?, 0) < 1 OR kd.ten LIKE ?) AND
			(COALESCE(?, 0) < 1 OR cl.ten LIKE ?) AND
			(COALESCE(?, 0) < 1 OR dg.ten LIKE ?) AND
                        (COALESCE(?, -1) < 0 OR (? = 1 AND (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) > 0) OR (? = 0 AND (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) < 1)) AND
                        (COALESCE(?, -1) < 0 OR sp.trang_thai = ?)
                    )
            )
            SELECT *
            FROM TableCTE
            WHERE stt BETWEEN ? AND ?
        """, isBanHang ? " AND trang_thai = 1 " : "", TABLE_NAME);

        int[] offset = FilterRequest.getOffset(filter.getPage(), filter.getSize());
        int start = offset[0];
        int limit = offset[1];

	
        Object[] args = new Object[] {
            filter.getKeyword(),
            String.format("%%%s%%", filter.getKeyword()),
            String.format("%%%s%%", filter.getKeyword()),
            filter.getDanhMuc().getValue(),
            filter.getDanhMuc().getText(),
            filter.getThuongHieu().getValue(),
            filter.getThuongHieu().getText(),
            filter.getXuatXu().getValue(),
            filter.getXuatXu().getText(),
	    filter.getKieuDang().getValue(),
            filter.getKieuDang().getText(),
	    filter.getChatLieu().getValue(),
            filter.getChatLieu().getText(),
	    filter.getDeGiay().getValue(),
            filter.getDeGiay().getText(),
	    filter.getSoLuong().getValue(),
	    filter.getSoLuong().getValue(),
	    filter.getSoLuong().getValue(),
            filter.getTrangThai().getValue(),
            filter.getTrangThai().getValue(),
            start,
            limit
        };	

        try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaSanPhamModel model = buildData(resultSet);
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
    public List<InuhaSanPhamModel> selectPage(FilterRequest request) throws SQLException {
	return selectPage(request, false);
    }

    @Override
    public int count(FilterRequest request) throws SQLException {
        
        InuhaFilterSanPhamRequest filter = (InuhaFilterSanPhamRequest) request;
        
        int totalPages = 0;
        int totalRows = 0;

        String query = String.format("""
	    SELECT 
		COUNT(*)
	    FROM %s AS sp
		LEFT JOIN DanhMuc AS dm ON dm.id = sp.id_danh_muc
		LEFT JOIN ThuongHieu AS th ON th.id = sp.id_thuong_hieu
		LEFT JOIN XuatXu AS xx ON xx.id = sp.id_xuat_xu
		LEFT JOIN KieuDang AS kd ON kd.id = sp.id_kieu_dang
		LEFT JOIN ChatLieu AS cl ON cl.id = sp.id_chat_lieu
		LEFT JOIN DeGiay AS dg ON dg.id = sp.id_de_giay
	    WHERE
		sp.trang_thai_xoa != 1 AND  
		(
		    (? IS NULL OR sp.ten LIKE ? OR sp.ma LIKE ?) AND
		    (COALESCE(?, 0) < 1 OR dm.ten LIKE ?) AND
		    (COALESCE(?, 0) < 1 OR th.ten LIKE ?) AND
		    (COALESCE(?, 0) < 1 OR xx.ten LIKE ?) AND
		    (COALESCE(?, 0) < 1 OR kd.ten LIKE ?) AND
		    (COALESCE(?, 0) < 1 OR cl.ten LIKE ?) AND
		    (COALESCE(?, 0) < 1 OR dg.ten LIKE ?) AND
		    (COALESCE(?, -1) < 0 OR (? = 1 AND (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) > 0) OR (? = 0 AND (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) < 1)) AND
		    (COALESCE(?, -1) < 0 OR sp.trang_thai = ?)
		)
        """, TABLE_NAME);

        Object[] args = new Object[] { 
            filter.getKeyword(),
            String.format("%%%s%%", filter.getKeyword()),
            String.format("%%%s%%", filter.getKeyword()),
            filter.getDanhMuc().getValue(),
            filter.getDanhMuc().getText(),
            filter.getThuongHieu().getValue(),
            filter.getThuongHieu().getText(),
            filter.getXuatXu().getValue(),
            filter.getXuatXu().getText(),
	    filter.getKieuDang().getValue(),
            filter.getKieuDang().getText(),
	    filter.getChatLieu().getValue(),
            filter.getChatLieu().getText(),
	    filter.getDeGiay().getValue(),
            filter.getDeGiay().getText(),
	    filter.getSoLuong().getValue(),
	    filter.getSoLuong().getValue(),
	    filter.getSoLuong().getValue(),
            filter.getTrangThai().getValue(),
            filter.getTrangThai().getValue()
        };
        
        try {
            totalRows = (int) JbdcHelper.value(query, args);
            totalPages = (int) Math.ceil((double) totalRows / request.getSize());
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return totalPages;
    }
    
    public Optional<InuhaSanPhamModel> getByCode(String code) throws SQLException {
        ResultSet resultSet = null;
        InuhaSanPhamModel model = null;

        String query = String.format("""
            SELECT
                sp.*,
                dm.ten AS ten_danh_muc,
                dm.ngay_tao AS ngay_tao_danh_muc,
                dm.ngay_cap_nhat AS ngay_cap_nhat_danh_muc,
                th.ten AS ten_thuong_hieu,
                th.ngay_tao AS ngay_tao_thuong_hieu,
                th.ngay_cap_nhat AS ngay_cap_nhat_thuong_hieu,
                xx.ten AS ten_xuat_xu,
                xx.ngay_tao AS ngay_tao_xuat_xu,
                xx.ngay_cap_nhat AS ngay_cap_nhat_xuat_xu,
                kd.ten AS ten_kieu_dang,
                kd.ngay_tao AS ngay_tao_kieu_dang,
                kd.ngay_cap_nhat AS ngay_cap_nhat_kieu_dang,
                cl.ten AS ten_chat_lieu,
                cl.ngay_tao AS ngay_tao_chat_lieu,
                cl.ngay_cap_nhat AS ngay_cap_nhat_chat_lieu,
                dg.ten AS ten_de_giay,
                dg.ngay_tao AS ngay_tao_de_giay,
                dg.ngay_cap_nhat AS ngay_cap_nhat_de_giay,
                (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) AS so_luong
            FROM %s AS sp
                LEFT JOIN DanhMuc AS dm ON dm.id = sp.id_danh_muc
                LEFT JOIN ThuongHieu AS th ON th.id = sp.id_thuong_hieu
                LEFT JOIN XuatXu AS xx ON xx.id = sp.id_xuat_xu
                LEFT JOIN KieuDang AS kd ON kd.id = sp.id_kieu_dang
                LEFT JOIN ChatLieu AS cl ON cl.id = sp.id_chat_lieu
                LEFT JOIN DeGiay AS dg ON dg.id = sp.id_de_giay
            WHERE
                sp.ma LIKE ? AND
                sp.trang_thai_xoa != 1
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, code);
            while(resultSet.next()) {
                model = buildData(resultSet, false);
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
	
    private InuhaSanPhamModel buildData(ResultSet resultSet) throws SQLException { 
        return buildData(resultSet, true);
    }
    
    private InuhaSanPhamModel buildData(ResultSet resultSet, boolean addSTT) throws SQLException { 
        InuhaDanhMucModel danhMuc = new InuhaDanhMucModel(resultSet.getInt("id_danh_muc"), -1, resultSet.getString("ten_danh_muc"), resultSet.getString("ngay_tao_danh_muc"), false, resultSet.getString("ngay_cap_nhat_danh_muc"));
        InuhaThuongHieuModel thuongHieu = new InuhaThuongHieuModel(resultSet.getInt("id_thuong_hieu"), -1, resultSet.getString("ten_thuong_hieu"), resultSet.getString("ngay_tao_thuong_hieu"), false, resultSet.getString("ngay_cap_nhat_thuong_hieu"));
        InuhaXuatXuModel xuatXu = new InuhaXuatXuModel(resultSet.getInt("id_xuat_xu"), -1, resultSet.getString("ten_xuat_xu"), resultSet.getString("ngay_tao_xuat_xu"), false, resultSet.getString("ngay_cap_nhat_xuat_xu"));
        InuhaKieuDangModel kieuDang = new InuhaKieuDangModel(resultSet.getInt("id_kieu_dang"), -1, resultSet.getString("ten_kieu_dang"), resultSet.getString("ngay_tao_kieu_dang"), false, resultSet.getString("ngay_cap_nhat_kieu_dang"));
        InuhaChatLieuModel chatLieu = new InuhaChatLieuModel(resultSet.getInt("id_chat_lieu"), -1, resultSet.getString("ten_chat_lieu"), resultSet.getString("ngay_tao_chat_lieu"), false, resultSet.getString("ngay_cap_nhat_chat_lieu"));
        InuhaDeGiayModel deGiay = new InuhaDeGiayModel(resultSet.getInt("id_de_giay"), -1, resultSet.getString("ten_de_giay"), resultSet.getString("ngay_tao_de_giay"), false, resultSet.getString("ngay_cap_nhat_de_giay"));
        
        return InuhaSanPhamModel.builder()
            .id(resultSet.getInt("id"))
            .stt(addSTT ? resultSet.getInt("stt") : -1)
            .ma(resultSet.getString("ma"))
            .ten(resultSet.getString("ten"))
            .moTa(resultSet.getString("mo_ta"))
	    .giaNhap(resultSet.getDouble("gia_nhap"))
            .giaBan(resultSet.getDouble("gia_ban"))
            .hinhAnh(resultSet.getString("hinh_anh"))
            .trangThai(resultSet.getBoolean("trang_thai"))
            .ngayTao(resultSet.getString("ngay_tao"))
            .ngayCapNhat(resultSet.getString("ngay_cap_nhat"))
            .trangThaiXoa(resultSet.getBoolean("trang_thai_xoa"))
            .soLuong(resultSet.getInt("so_luong"))
            .danhMuc(danhMuc)
            .thuongHieu(thuongHieu)
            .xuatXu(xuatXu)
            .kieuDang(kieuDang)
            .chatLieu(chatLieu)
            .deGiay(deGiay)
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
