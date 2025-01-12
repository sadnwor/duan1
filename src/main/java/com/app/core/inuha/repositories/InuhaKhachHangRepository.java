package com.app.core.inuha.repositories;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.interfaces.IDAOinterface;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaKhachHangModel;
import com.app.core.inuha.request.InuhaFilterKhachHangRequest;
import com.app.utils.NumberPhoneUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author InuHa
 */
public class InuhaKhachHangRepository implements IDAOinterface<InuhaKhachHangModel, Integer> {
    
    private final static String TABLE_NAME = "KhachHang";
    
    private static InuhaKhachHangRepository instance = null;
    
    public static InuhaKhachHangRepository getInstance() { 
	if (instance == null) { 
	    instance = new InuhaKhachHangRepository();
	}
	return instance;
    }
    
    private InuhaKhachHangRepository() {
    }
    	
    @Override
    public int insert(InuhaKhachHangModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            INSERT INTO %s(ho_ten, sdt, gioi_tinh, dia_chi)
            VALUES (?, ?, ?, ?)
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getHoTen(),
		NumberPhoneUtils.formatPhoneNumber(model.getSdt()),
		model.isGioiTinh(),
		model.getDiaChi()
            };
            result = JbdcHelper.updateAndFlush(query, args);
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }

        return result;
    }

    @Override
    public int update(InuhaKhachHangModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            UPDATE %s SET
                ho_ten = ?,
                sdt = ?,
                gioi_tinh = ?,
                dia_chi = ?,
                trang_thai_xoa = ?
            WHERE id = ?
        """, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getHoTen(),
		NumberPhoneUtils.formatPhoneNumber(model.getSdt()),
		model.isGioiTinh(),
		model.getDiaChi(),
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
        String query = String.format("""
            UPDATE HoaDon SET id_khach_hang = NULL WHERE id_khach_hang = ? AND trang_thai = ?;
            DELETE FROM %s WHERE id = ?;
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

    public boolean has(String sdt) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1 
            FROM %s 
            WHERE sdt LIKE ? AND trang_thai_xoa != 1
        """, TABLE_NAME);
	try {
            return JbdcHelper.value(query, NumberPhoneUtils.formatPhoneNumber(sdt)) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
        
    public boolean hasUse(Integer id) throws SQLException {
        String query = String.format("SELECT TOP(1) 1 FROM HoaDon WHERE id_khach_hang = ? AND trang_thai != %d", TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN);
        try {
            return JbdcHelper.value(query, id) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    public boolean has(InuhaKhachHangModel model) throws SQLException {
        String query = String.format("""
            SELECT TOP(1) 1
            FROM %s
            WHERE
                sdt LIKE ? AND
                id != ? AND
                trang_thai_xoa != 1
        """, TABLE_NAME);
        try {
            return JbdcHelper.value(query, NumberPhoneUtils.formatPhoneNumber(model.getSdt()), model.getId()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    @Override
    public Optional<InuhaKhachHangModel> getById(Integer id) throws SQLException {
        ResultSet resultSet = null;
        InuhaKhachHangModel model = null;

        String query = String.format("""
            SELECT 
                kh.*,
                (SELECT COUNT(*) FROM HoaDon WHERE id_khach_hang = kh.id AND trang_thai = %d) AS luot_mua
            FROM %s AS kh
            WHERE kh.id = ? AND kh.trang_thai_xoa != 1
        """, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN, TABLE_NAME);

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
    public List<InuhaKhachHangModel> selectAll() throws SQLException {
        List<InuhaKhachHangModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            SELECT 
                kh.*,
                ROW_NUMBER() OVER (ORDER BY kh.id DESC) AS stt,
                (SELECT COUNT(*) FROM HoaDon WHERE id_khach_hang = kh.id AND trang_thai = %d) AS luot_mua
            FROM %s AS kh
            WHERE kh.trang_thai_xoa != 1
            ORDER BY kh.id DESC 
        """, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN, TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query);
            while(resultSet.next()) {
                InuhaKhachHangModel model = buildData(resultSet);
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
    public List<InuhaKhachHangModel> selectPage(FilterRequest request) throws SQLException {
	InuhaFilterKhachHangRequest filter = (InuhaFilterKhachHangRequest) request;
        List<InuhaKhachHangModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            WITH TableCTE AS (
                SELECT
                    kh.*,
                    ROW_NUMBER() OVER (ORDER BY kh.id DESC) AS stt,
                    (SELECT COUNT(*) FROM HoaDon WHERE id_khach_hang = kh.id AND trang_thai = %d) AS luot_mua
                FROM %s AS kh
                WHERE 
                    kh.trang_thai_xoa != 1 AND
                    (
			(? IS NULL OR kh.ho_ten LIKE ? OR kh.sdt LIKE ?) AND
			(COALESCE(?, -1) < 0 OR kh.gioi_tinh = ?)
		    )
            )
            SELECT *
            FROM TableCTE
            WHERE stt BETWEEN ? AND ?
        """, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN, TABLE_NAME);

        int[] offset = FilterRequest.getOffset(request.getPage(), request.getSize());
        int start = offset[0];
        int limit = offset[1];

        Object[] args = new Object[] {
	    filter.getKeyword(),
            String.format("%%%s%%", filter.getKeyword()),
            String.format("%%%s%%", filter.getKeyword()),
	    filter.getGioiTinh().getValue(),
	    filter.getGioiTinh().getValue(),
            start,
            limit
        };

        try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaKhachHangModel model = buildData(resultSet);
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
	InuhaFilterKhachHangRequest filter = (InuhaFilterKhachHangRequest) request;
        int totalPages = 0;
        int totalRows = 0;

        String query = String.format("""
            SELECT COUNT(*) 
            FROM %s 
            WHERE 
                trang_thai_xoa != 1 AND
		(
		    (? IS NULL OR ho_ten LIKE ? OR sdt LIKE ?) AND
		    (COALESCE(?, -1) < 0 OR gioi_tinh = ?)
		)
                                     
        """, TABLE_NAME);

	Object[] args = new Object[] { 
	    filter.getKeyword(),
            String.format("%%%s%%", filter.getKeyword()),
            String.format("%%%s%%", filter.getKeyword()),
	    filter.getGioiTinh().getValue(),
	    filter.getGioiTinh().getValue()
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
    
    private InuhaKhachHangModel buildData(ResultSet resultSet) throws SQLException { 
        return buildData(resultSet, true);
    }
    
    private InuhaKhachHangModel buildData(ResultSet resultSet, boolean addSTT) throws SQLException { 
        return InuhaKhachHangModel.builder()
            .id(resultSet.getInt("id"))
            .stt(addSTT ? resultSet.getInt("stt") : -1)
            .hoTen(resultSet.getString("ho_ten"))
	    .sdt(resultSet.getString("sdt"))
	    .soLanMuaHang(resultSet.getInt("luot_mua"))
	    .gioiTinh(resultSet.getBoolean("gioi_tinh"))
	    .diaChi(resultSet.getString("dia_chi"))
	    .trangThaiXoa(resultSet.getBoolean("trang_thai_xoa"))
            .build();
    }
    
}
