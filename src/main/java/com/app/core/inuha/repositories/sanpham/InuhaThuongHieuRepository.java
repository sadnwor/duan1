package com.app.core.inuha.repositories.sanpham;

import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.interfaces.IDAOinterface;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.sanpham.InuhaMauSacModel;
import com.app.core.inuha.models.sanpham.InuhaThuongHieuModel;
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
public class InuhaThuongHieuRepository implements IDAOinterface<InuhaThuongHieuModel, Integer> {
    
    private final static String TABLE_NAME = "ThuongHieu";
    
    private static InuhaThuongHieuRepository instance = null;
    
    public static InuhaThuongHieuRepository getInstance() { 
	if (instance == null) { 
	    instance = new InuhaThuongHieuRepository();
	}
	return instance;
    }
    
    private InuhaThuongHieuRepository() { 
	
    }
    
    @Override
    public int insert(InuhaThuongHieuModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            DELETE FROM %s WHERE trang_thai_xoa = 1 AND id NOT IN (SELECT DISTINCT id_thuong_hieu FROM SanPham);
            INSERT INTO %s(ten, ngay_tao)
            VALUES (?, ?)
        """, TABLE_NAME, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getTen(),
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
    public int update(InuhaThuongHieuModel model) throws SQLException {
        int result = 0;
        String query = String.format("""
            DELETE FROM %s WHERE trang_thai_xoa = 1 AND id NOT IN (SELECT DISTINCT id_thuong_hieu FROM SanPham);
            UPDATE %s SET
                ten = ?,
                trang_thai_xoa = ?,
                ngay_cap_nhat = ?
            WHERE id = ?
        """, TABLE_NAME, TABLE_NAME);
        try {
            Object[] args = new Object[] {
                model.getTen(),
                model.isTrangThaiXoa(),
                TimeUtils.currentDate(),
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
            DELETE FROM %s WHERE trang_thai_xoa = 1 AND id NOT IN (SELECT DISTINCT id_thuong_hieu FROM SanPham);
            DELETE FROM %s WHERE id = ?
        """, TABLE_NAME, TABLE_NAME);
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
        String query = "SELECT TOP(1) 1 FROM SanPham WHERE id_thuong_hieu = ?";
        try {
            return JbdcHelper.value(query, id) != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    public boolean has(InuhaThuongHieuModel model) throws SQLException {
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
    public Optional<InuhaThuongHieuModel> getById(Integer id) throws SQLException {
        ResultSet resultSet = null;
        InuhaThuongHieuModel model = null;

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
    public List<InuhaThuongHieuModel> selectAll() throws SQLException {
        List<InuhaThuongHieuModel> list = new ArrayList<>();
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
                InuhaThuongHieuModel model = buildData(resultSet);
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
    public List<InuhaThuongHieuModel> selectPage(FilterRequest request) throws SQLException {
        List<InuhaThuongHieuModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = String.format("""
            WITH TableCTE AS (
                SELECT
                    *,
                    ROW_NUMBER() OVER (ORDER BY id DESC) AS stt
                FROM %s
                WHERE trang_thai_xoa != 1
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
                InuhaThuongHieuModel model = buildData(resultSet);
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
    
    public Optional<InuhaThuongHieuModel> getByName(String name) throws SQLException {
        ResultSet resultSet = null;
        InuhaThuongHieuModel model = null;

        String query = String.format("SELECT * FROM %s WHERE ten LIKE ? AND trang_thai_xoa != 1", TABLE_NAME);

        try {
            resultSet = JbdcHelper.query(query, name);
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
    
    private InuhaThuongHieuModel buildData(ResultSet resultSet) throws SQLException { 
        return buildData(resultSet, true);
    }
    
    private InuhaThuongHieuModel buildData(ResultSet resultSet, boolean addSTT) throws SQLException { 
        return InuhaThuongHieuModel.builder()
            .id(resultSet.getInt("id"))
            .stt(addSTT ? resultSet.getInt("stt") : -1)
            .ten(resultSet.getString("ten"))
            .trangThaiXoa(resultSet.getBoolean("trang_thai_xoa"))
            .ngayTao(resultSet.getString("ngay_tao"))
            .ngayCapNhat(resultSet.getString("ngay_cap_nhat"))
            .build();
    }
    
}
