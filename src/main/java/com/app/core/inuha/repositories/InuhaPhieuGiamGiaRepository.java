package com.app.core.inuha.repositories;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.TrangThaiPhieuGiamGiaConstant;
import com.app.common.infrastructure.interfaces.IDAOinterface;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.request.InuhaFilterPhieuGiamGiaRequest;
import com.app.utils.TimeUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaPhieuGiamGiaRepository implements IDAOinterface<InuhaPhieuGiamGiaModel, Integer> {
    
    private final static String TABLE_NAME = "PhieuGiamGia";
    
    private static InuhaPhieuGiamGiaRepository instance = null;
    
    public static InuhaPhieuGiamGiaRepository getInstance() { 
	if (instance == null) { 
	    instance = new InuhaPhieuGiamGiaRepository();
	}
	return instance;
    }
    
    private InuhaPhieuGiamGiaRepository() {
    }
    	
    @Override
    public int insert(InuhaPhieuGiamGiaModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            INSERT INTO %s(ma, ten, so_luong, ngay_bat_dau, ngay_ket_thuc, giam_theo_phan_tram, gia_tri_giam, giam_toi_da, don_toi_thieu, ngay_tao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getMa(),
		model.getTen(),
		model.getSoLuong(),
		model.getNgayBatDau(),
		model.getNgayKetThuc(),
		model.isGiamTheoPhanTram(),
		model.getGiaTriGiam(),
		model.getGiamToiDa(),
		model.getDonToiThieu(),
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
    public int update(InuhaPhieuGiamGiaModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            UPDATE %s SET
                ma = ?,
                ten = ?,
                so_luong = ?,
                ngay_bat_dau = ?,
		ngay_ket_thuc = ?,
		giam_theo_phan_tram = ?,
		gia_tri_giam = ?,
		giam_toi_da = ?,
		don_toi_thieu = ?,
		ngay_cap_nhat = ?,
                trang_thai_xoa = ?
            WHERE id = ?
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getMa(),
		model.getTen(),
		model.getSoLuong(),
		model.getNgayBatDau(),
                model.getNgayKetThuc(),
                model.isGiamTheoPhanTram(),
		model.getGiaTriGiam(),
		model.getGiamToiDa(),
		model.getDonToiThieu(),
		TimeUtils.currentDate(),
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

    public boolean has(String ma) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1 
            FROM %s 
            WHERE ma LIKE ? AND trang_thai_xoa != 1 AND ngay_ket_thuc >= CONVERT(DATE, GETDATE())
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, ma) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
        
    public boolean hasUse(Integer id) throws SQLException {
        String query = "SELECT TOP(1) 1 FROM HoaDon WHERE id_phieu_giam_gia = ?";
        try {
            return JbdcHelper.value(query, id) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    public boolean has(InuhaPhieuGiamGiaModel model) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                ma LIKE ? AND
                id != ? AND
                trang_thai_xoa != 1 AND
                ngay_ket_thuc >= CONVERT(DATE, GETDATE()) 
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, model.getMa(), model.getId()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    @Override
    public Optional<InuhaPhieuGiamGiaModel> getById(Integer id) throws SQLException {
        ResultSet resultSet = null;
        InuhaPhieuGiamGiaModel model = null;

        String query = String.format("SELECT * FROM %s WHERE id = ? AND trang_thai_xoa != 1", TABLE_NAME);

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
    public List<InuhaPhieuGiamGiaModel> selectAll() throws SQLException {
        List<InuhaPhieuGiamGiaModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            SELECT 
                *,
                ROW_NUMBER() OVER (ORDER BY id DESC) AS stt
            FROM %s
            WHERE trang_thai_xoa != 1
            ORDER BY id DESC 
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query);
            while(resultSet.next()) {
                InuhaPhieuGiamGiaModel model = buildData(resultSet);
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
    public List<InuhaPhieuGiamGiaModel> selectPage(FilterRequest request) throws SQLException {
	InuhaFilterPhieuGiamGiaRequest filter = (InuhaFilterPhieuGiamGiaRequest) request;
        List<InuhaPhieuGiamGiaModel> list = new ArrayList<>();
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
                        (? IS NULL OR ma LIKE ? OR ten LIKE ?) AND
                        (COALESCE(?, NULL) IS NULL OR ngay_bat_dau >= ?) AND
			(COALESCE(?, NULL) IS NULL OR ngay_ket_thuc <= ?) AND
                        (
                            COALESCE(?, 0) < 1 OR
                            (? = %d AND ngay_bat_dau <= CONVERT(DATE, GETDATE()) AND ngay_ket_thuc >= CONVERT(DATE, GETDATE())) OR
                            (? = %d AND ngay_bat_dau > CONVERT(DATE, GETDATE())) OR 
                            (? = %d AND ngay_ket_thuc < CONVERT(DATE, GETDATE()))
                        )
		    )
            )
            SELECT *
            FROM TableCTE
            WHERE stt BETWEEN ? AND ?
        """, TABLE_NAME, TrangThaiPhieuGiamGiaConstant.DANG_DIEN_RA, TrangThaiPhieuGiamGiaConstant.SAP_DIEN_RA, TrangThaiPhieuGiamGiaConstant.DA_DIEN_RA);

        int[] offset = FilterRequest.getOffset(request.getPage(), request.getSize());
        int start = offset[0];
        int limit = offset[1];

        Object[] args = new Object[] {
	    filter.getKeyword(),
            String.format("%%%s%%", filter.getKeyword()),
            String.format("%%%s%%", filter.getKeyword()),
	    filter.getNgayBatDau(),
	    filter.getNgayBatDau(),
	    filter.getNgayKetThuc(),
	    filter.getNgayKetThuc(),
	    filter.getTrangThai().getValue(),
	    filter.getTrangThai().getValue(),
	    filter.getTrangThai().getValue(),
	    filter.getTrangThai().getValue(),
            start,
            limit
        };

        try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaPhieuGiamGiaModel model = buildData(resultSet);
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
	InuhaFilterPhieuGiamGiaRequest filter = (InuhaFilterPhieuGiamGiaRequest) request;
	
        int totalPages = 0;
        int totalRows = 0;

        String query = String.format("""
            SELECT COUNT(*)
            FROM %s
            WHERE
                trang_thai_xoa != 1 AND
		(
		    (? IS NULL OR ma LIKE ? OR ten LIKE ?) AND
		    (COALESCE(?, NULL) IS NULL OR ngay_bat_dau >= ?) AND
		    (COALESCE(?, NULL) IS NULL OR ngay_ket_thuc <= ?) AND
		    (
			COALESCE(?, 0) < 1 OR
			(? = %d AND ngay_bat_dau <= CONVERT(DATE, GETDATE()) AND ngay_ket_thuc >= CONVERT(DATE, GETDATE())) OR
			(? = %d AND ngay_bat_dau > CONVERT(DATE, GETDATE())) OR 
			(? = %d AND ngay_ket_thuc < CONVERT(DATE, GETDATE()))
		    )
		)
        """, TABLE_NAME, TrangThaiPhieuGiamGiaConstant.DANG_DIEN_RA, TrangThaiPhieuGiamGiaConstant.SAP_DIEN_RA, TrangThaiPhieuGiamGiaConstant.DA_DIEN_RA);

	
	Object[] args = new Object[] {
	    filter.getKeyword(),
            String.format("%%%s%%", filter.getKeyword()),
            String.format("%%%s%%", filter.getKeyword()),
	    filter.getNgayBatDau(),
	    filter.getNgayBatDau(),
	    filter.getNgayKetThuc(),
	    filter.getNgayKetThuc(),
	    filter.getTrangThai().getValue(),
	    filter.getTrangThai().getValue(),
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
    
    public Optional<InuhaPhieuGiamGiaModel> getByCode(String ma) throws SQLException {
        ResultSet resultSet = null;
        InuhaPhieuGiamGiaModel model = null;

        String query = String.format("""
            SELECT * 
            FROM %s 
            WHERE 
                ma LIKE ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, ma);
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
	
    private InuhaPhieuGiamGiaModel buildData(ResultSet resultSet) throws SQLException { 
        return buildData(resultSet, true);
    }
    
    private InuhaPhieuGiamGiaModel buildData(ResultSet resultSet, boolean addSTT) throws SQLException { 
        return InuhaPhieuGiamGiaModel.builder()
            .id(resultSet.getInt("id"))
            .stt(addSTT ? resultSet.getInt("stt") : -1)
            .ma(resultSet.getString("ma"))
	    .ten(resultSet.getString("ten"))
	    .soLuong(resultSet.getInt("so_luong"))
	    .ngayBatDau(resultSet.getString("ngay_bat_dau"))
	    .ngayKetThuc(resultSet.getString("ngay_ket_thuc"))
	    .giamTheoPhanTram(resultSet.getBoolean("giam_theo_phan_tram"))
	    .giaTriGiam(resultSet.getDouble("gia_tri_giam"))
	    .giamToiDa(resultSet.getDouble("giam_toi_da"))
	    .donToiThieu(resultSet.getDouble("don_toi_thieu"))
	    .ngayTao(resultSet.getString("ngay_tao"))
	    .ngayCapNhat(resultSet.getString("ngay_cap_nhat"))
	    .trangThaiXoa(resultSet.getBoolean("trang_thai_xoa"))
	    .build();
    }
    
}
