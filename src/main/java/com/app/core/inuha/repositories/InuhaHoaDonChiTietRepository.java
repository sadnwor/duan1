package com.app.core.inuha.repositories;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.PhuongThucThanhToanConstant;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.interfaces.IDAOinterface;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.common.infrastructure.session.SessionLogin;
import com.app.core.inuha.models.InuhaHoaDonChiTietModel;
import com.app.core.inuha.models.InuhaHoaDonModel;
import com.app.core.inuha.models.InuhaKhachHangModel;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.request.InuhaFilterHoaDonChiTietRequest;
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
public class InuhaHoaDonChiTietRepository implements IDAOinterface<InuhaHoaDonChiTietModel, Integer> {
    
    private final static String TABLE_NAME = "HoaDonChiTiet";
    
    private final InuhaSanPhamChiTietRepository sanPhamChiTietRepository = InuhaSanPhamChiTietRepository.getInstance();
    
    private final InuhaHoaDonRepository hoaDonRepository = InuhaHoaDonRepository.getInstance();

    private static InuhaHoaDonChiTietRepository instance = null;
    
    public static InuhaHoaDonChiTietRepository getInstance() { 
	if (instance == null) { 
	    instance = new InuhaHoaDonChiTietRepository();
	}
	return instance;
    }
    
    private InuhaHoaDonChiTietRepository() { 
	
    }
    
    @Override
    public int insert(InuhaHoaDonChiTietModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            INSERT INTO %s(id_san_pham_chi_tiet, id_hoa_don, ma, gia_nhap, gia_ban, so_luong)
            VALUES (?, ?, ?, ?, ?, ?)
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
		model.getSanPhamChiTiet().getId(),
		model.getHoaDon().getId(),
		model.getMa(),
		model.getSanPhamChiTiet().getSanPham().getGiaNhap(),
		model.getSanPhamChiTiet().getSanPham().getGiaBan(),
		model.getSoLuong()
            };
            result = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return result;
    }

    @Override
    public int update(InuhaHoaDonChiTietModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            UPDATE %s SET
                so_luong = ?
            WHERE id = ?
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getSoLuong(),
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
        String query = String.format("SELECT TOP(1) 1 FROM %s WHERE id = ?", TABLE_NAME);
        try {
            return JbdcHelper.value(query, id) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
	
    @Override
    public Optional<InuhaHoaDonChiTietModel> getById(Integer id) throws SQLException {
        ResultSet resultSet = null;
        InuhaHoaDonChiTietModel model = null;

        String query = String.format("""
            SELECT * 
            FROM %s
            WHERE 
                id = ?
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, id);
            while(resultSet.next()) {
                model = buildData(resultSet, false);
                Optional<InuhaSanPhamChiTietModel> sanPhamChiTiet = sanPhamChiTietRepository.getById(resultSet.getInt("id_san_pham_chi_tiet"));
                if (sanPhamChiTiet.isPresent()) { 
                    model.setSanPhamChiTiet(sanPhamChiTiet.get());
                }
		
		Optional<InuhaHoaDonModel> hoaDon = hoaDonRepository.getById(resultSet.getInt("id_hoa_don"));
                if (hoaDon.isPresent()) { 
                    model.setHoaDon(hoaDon.get());
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
    public List<InuhaHoaDonChiTietModel> selectAll() throws SQLException {
        return null;
    }
    
    public List<InuhaHoaDonChiTietModel> selectAll(int idHoaDon) throws SQLException {
        List<InuhaHoaDonChiTietModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            SELECT 
                hdct.*,
                ROW_NUMBER() OVER (ORDER BY hdct.id DESC) AS stt
            FROM %s AS hdct
                JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
                JOIN SanPham AS sp ON sp.id = spct.id_san_pham
            WHERE 
                hdct.id_hoa_don = ? AND
                sp.trang_thai_xoa != 1 AND
                spct.trang_thai_xoa != 1
	    ORDER BY hdct.id DESC 
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, idHoaDon);
            while(resultSet.next()) {
                InuhaHoaDonChiTietModel model = buildData(resultSet);
                Optional<InuhaSanPhamChiTietModel> sanPhamChiTiet = sanPhamChiTietRepository.getById(resultSet.getInt("id_san_pham_chi_tiet"));
                if (sanPhamChiTiet.isPresent()) { 
                    model.setSanPhamChiTiet(sanPhamChiTiet.get());
                }
		
		Optional<InuhaHoaDonModel> hoaDon = hoaDonRepository.getById(resultSet.getInt("id_hoa_don"));
                if (hoaDon.isPresent()) { 
                    model.setHoaDon(hoaDon.get());
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

    public List<InuhaHoaDonChiTietModel> getAllIdsByIdSanPham(int idSanPham) throws SQLException {
        List<InuhaHoaDonChiTietModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            SELECT 
                hdct.*
            FROM %s AS hdct
		JOIN HoaDon AS hd ON hd.id = hdct.id_hoa_don
		JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
            WHERE spct.id_san_pham = ? AND hd.trang_thai = %d
        """, TABLE_NAME, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN);

        try {
            resultSet = JbdcHelper.query(query, idSanPham);
            while(resultSet.next()) {
                InuhaHoaDonChiTietModel model = buildData(resultSet, false);
                Optional<InuhaSanPhamChiTietModel> sanPhamChiTiet = sanPhamChiTietRepository.getById(resultSet.getInt("id_san_pham_chi_tiet"));
                if (sanPhamChiTiet.isPresent()) { 
                    model.setSanPhamChiTiet(sanPhamChiTiet.get());
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
	
    public List<InuhaHoaDonChiTietModel> getAllIdsByIdSanPhamChiTiet(int idSanPhamChiTiet) throws SQLException {
        List<InuhaHoaDonChiTietModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            SELECT 
                hdct.*
            FROM %s AS hdct
		JOIN HoaDon AS hd ON hd.id = hdct.id_hoa_don
		JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
            WHERE spct.id = ? AND hd.trang_thai = %d
        """, TABLE_NAME, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN);

        try {
            resultSet = JbdcHelper.query(query, idSanPhamChiTiet);
            while(resultSet.next()) {
                InuhaHoaDonChiTietModel model = buildData(resultSet, false);
                Optional<InuhaSanPhamChiTietModel> sanPhamChiTiet = sanPhamChiTietRepository.getById(resultSet.getInt("id_san_pham_chi_tiet"));
                if (sanPhamChiTiet.isPresent()) { 
                    model.setSanPhamChiTiet(sanPhamChiTiet.get());
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
    public List<InuhaHoaDonChiTietModel> selectPage(FilterRequest request) throws SQLException {
	InuhaFilterHoaDonChiTietRequest filter = (InuhaFilterHoaDonChiTietRequest) request;
        List<InuhaHoaDonChiTietModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            WITH TableCTE AS (
                SELECT
                    *,
                    ROW_NUMBER() OVER (ORDER BY id DESC) AS stt
                FROM %s
                WHERE id_hoa_don = ?
            )
            SELECT *
            FROM TableCTE
            WHERE stt BETWEEN ? AND ?
        """, TABLE_NAME);

        int[] offset = FilterRequest.getOffset(filter.getPage(), filter.getSize());
        int start = offset[0];
        int limit = offset[1];

        Object[] args = new Object[] {
	    filter.getIdHoaDon(),
            start,
            limit
        };

        try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaHoaDonChiTietModel model = buildData(resultSet);
                Optional<InuhaSanPhamChiTietModel> sanPhamChiTiet = sanPhamChiTietRepository.getById(resultSet.getInt("id_san_pham_chi_tiet"));
                if (sanPhamChiTiet.isPresent()) { 
                    model.setSanPhamChiTiet(sanPhamChiTiet.get());
                }
		
		Optional<InuhaHoaDonModel> hoaDon = hoaDonRepository.getById(resultSet.getInt("id_hoa_don"));
                if (hoaDon.isPresent()) { 
                    model.setHoaDon(hoaDon.get());
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
	InuhaFilterHoaDonChiTietRequest filter = (InuhaFilterHoaDonChiTietRequest) request;
        int totalPages = 0;
        int totalRows = 0;

        String query = String.format("""
            SELECT COUNT(*) 
            FROM %s
            WHERE id_hoa_don = ?
        """, TABLE_NAME);

        try {
            totalRows = (int) JbdcHelper.value(query, filter.getIdHoaDon());
            totalPages = (int) Math.ceil((double) totalRows / filter.getSize());
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return totalPages;
    }
    
    public int count() throws SQLException {
        String query = String.format("""
            SELECT COUNT(*) 
            FROM %s
        """, TABLE_NAME);
        try {
            return (int) JbdcHelper.value(query);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
	
    public Optional<InuhaHoaDonChiTietModel> getDuplicate(InuhaHoaDonChiTietModel hoaDonChiTiet) throws SQLException {
        ResultSet resultSet = null;
        InuhaHoaDonChiTietModel model = null;

        String query = String.format("""
            SELECT * 
            FROM %s
            WHERE 
                id_hoa_don = ? AND id_san_pham_chi_tiet = ?
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, hoaDonChiTiet.getHoaDon().getId(), hoaDonChiTiet.getSanPhamChiTiet().getId());
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
	
    private InuhaHoaDonChiTietModel buildData(ResultSet resultSet) throws SQLException { 
        return buildData(resultSet, true);
    }
    
    private InuhaHoaDonChiTietModel buildData(ResultSet resultSet, boolean addSTT) throws SQLException { 
        return InuhaHoaDonChiTietModel.builder()
            .id(resultSet.getInt("id"))
            .stt(addSTT ? resultSet.getInt("stt") : -1)
            .ma(resultSet.getString("ma"))
	    .giaNhap(resultSet.getDouble("gia_nhap"))
	    .giaBan(resultSet.getDouble("gia_ban"))
	    .soLuong(resultSet.getInt("so_luong"))
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
