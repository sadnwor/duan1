package com.app.core.inuha.repositories;

import com.app.common.helper.JbdcHelper;
import static com.app.common.infrastructure.constants.ChartConstant.TYPE_DAY;
import static com.app.common.infrastructure.constants.ChartConstant.TYPE_MONTH;
import static com.app.common.infrastructure.constants.ChartConstant.TYPE_YEAR;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.thongke.InuhaThongKeChartModel;
import com.app.core.inuha.models.thongke.InuhaThongKeChiTietModel;
import com.app.core.inuha.models.thongke.InuhaThongKeTongSoModel;
import com.app.core.inuha.models.thongke.InuhaThongKeSanPhamModel;
import com.app.core.inuha.request.InuhaFilterThongKeRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author InuHa
 */
public class InuhaThongKeRepository {
    
    
    private static InuhaThongKeRepository instance = null;
    
    public static InuhaThongKeRepository getInstance() { 
	if (instance == null) { 
	    instance = new InuhaThongKeRepository();
	}
	return instance;
    }
    
    private InuhaThongKeRepository() { 
	
    }
    
    public List<InuhaSanPhamModel> getListSanPham() throws SQLException { 
        List<InuhaSanPhamModel> list = new ArrayList<>();
        ResultSet resultSet = null;

        String query = """
            SELECT
                id,
                ten
            FROM SanPham
            WHERE
                trang_thai_xoa != 1
            ORDER BY ten ASC 
        """;

        try {
            resultSet = JbdcHelper.query(query);
            while(resultSet.next()) {
                InuhaSanPhamModel model = InuhaSanPhamModel.builder()
			.id(resultSet.getInt("id"))
			.ten(resultSet.getString("ten"))
			.build();
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
    
    public String getFirstDate() throws SQLException {
        String query = "SELECT COALESCE((SELECT TOP(1) ngay_tao FROM HoaDon ORDER BY ngay_tao ASC), GETDATE())";
        try {
	    java.sql.Timestamp date = (java.sql.Timestamp) JbdcHelper.value(query);
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }
    
    
    public List<InuhaThongKeChartModel> getDataChart(InuhaFilterThongKeRequest filter) throws SQLException {

	List<InuhaThongKeChartModel> list = new ArrayList<>();
	ResultSet resultSet = null;
	
	String queryRangeDate = "";
	
	queryRangeDate = switch (filter.getType()) {
	    case TYPE_YEAR -> "FORMAT(hd.ngay_tao, 'yyyy')";
	    case TYPE_MONTH -> "N'Tháng ' + FORMAT(hd.ngay_tao, 'MM')";
	    case TYPE_DAY -> "FORMAT(hd.ngay_tao, 'dd/MM')";
	    default -> "FORMAT(hd.ngay_tao, 'HH:00')";
	};
	
	String giam_gia = "hd.tien_giam";
	if (filter.getSanPham().getValue() > 0) { 
	    giam_gia = "(hd.tien_giam / (SELECT SUM(so_luong) FROM dbo.HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))";
	}
	
	String query = String.format("""
	    WITH HoaDonCTE AS (
		SELECT 
		    %s AS date_range,
		    (SUM(hdct.gia_ban * hdct.so_luong) - %s) AS doanh_thu,
		    (SUM((hdct.gia_ban - hdct.gia_nhap) * hdct.so_luong) - %s) AS loi_nhuan
		FROM dbo.HoaDon AS hd
		    JOIN HoaDonChiTiet AS hdct ON hdct.id_hoa_don = hd.id
                    JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
		WHERE 
		    hd.trang_thai = %d AND
		    hd.trang_thai_xoa != 1 AND
                    (COALESCE(?, 0) < 1 OR spct.id_san_pham = ?) AND
                    hd.ngay_tao >= ? AND
                    hd.ngay_tao <= ?
		GROUP BY hd.id, hd.ngay_tao, hd.tien_giam
	    )
	    SELECT 
		date_range,
		ISNULL(SUM(doanh_thu), 0) AS doanh_thu,
		ISNULL(SUM(loi_nhuan), 0) AS loi_nhuan
	    FROM HoaDonCTE 
	    GROUP BY date_range
	""", queryRangeDate, giam_gia, giam_gia, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN);
	
	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	Object[] args = new Object[] {
	    filter.getSanPham().getValue(),
	    filter.getSanPham().getValue(),
	    String.format("%s 00:00:00", filter.getStartDate().format(format)),
	    String.format("%s 23:59:59", filter.getEndDate().format(format))
	};
	
	try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaThongKeChartModel model = InuhaThongKeChartModel.builder()
		    .label(resultSet.getString("date_range"))
		    .doanhThu(resultSet.getDouble("doanh_thu"))
		    .loiNhuan(resultSet.getDouble("loi_nhuan"))
		    .build();
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
    
    public InuhaThongKeTongSoModel getTongSo(InuhaFilterThongKeRequest filter) throws SQLException {

	InuhaThongKeTongSoModel model = new InuhaThongKeTongSoModel();
	ResultSet resultSet = null;
	
	String giam_gia = "hd.tien_giam";
	if (filter.getSanPham().getValue() > 0) { 
	    giam_gia = "(hd.tien_giam / (SELECT SUM(so_luong) FROM dbo.HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))";
	}
	
	
	String query = String.format("""
	    WITH HoaDonCTE AS (
		SELECT 
                    (SUM(hdct.so_luong)) AS san_pham,
		    (SUM(hdct.gia_ban * hdct.so_luong) - %s) AS doanh_thu,
		    (SUM((hdct.gia_ban - hdct.gia_nhap) * hdct.so_luong) - %s) AS loi_nhuan
		FROM dbo.HoaDon AS hd
		    JOIN HoaDonChiTiet AS hdct ON hdct.id_hoa_don = hd.id
                    JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
		WHERE 
		    hd.trang_thai = %d AND
		    hd.trang_thai_xoa != 1 AND
                    (COALESCE(?, 0) < 1 OR spct.id_san_pham = ?) AND
                    hd.ngay_tao >= ? AND
                    hd.ngay_tao <= ?
		GROUP BY hd.id, hd.ngay_tao, hd.tien_giam
	    )
	    SELECT 
                ISNULL(SUM(san_pham), 0) AS tong_san_pham,
		ISNULL(SUM(doanh_thu), 0) AS tong_doanh_thu,
		ISNULL(SUM(loi_nhuan), 0) AS tong_loi_nhuan
	    FROM HoaDonCTE 
	""", giam_gia, giam_gia, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN);
	
	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	Object[] args = new Object[] {
	    filter.getSanPham().getValue(),
	    filter.getSanPham().getValue(),
	    String.format("%s 00:00:00", filter.getStartDate().format(format)),
	    String.format("%s 23:59:59", filter.getEndDate().format(format))
	};
	
	try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
		model.setTongSanPham(resultSet.getDouble("tong_san_pham"));
                model.setTongDoanhThu(resultSet.getDouble("tong_doanh_thu"));
		model.setTongLoiNhuan(resultSet.getDouble("tong_loi_nhuan"));
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        finally {
            JbdcHelper.close(resultSet);
        }

        return model;
    }
    
    public InuhaThongKeChiTietModel getDetail(InuhaFilterThongKeRequest filter) throws SQLException {

	InuhaThongKeChiTietModel model = new InuhaThongKeChiTietModel();
	ResultSet resultSet = null;
	
	
	String query = String.format("""
	    SELECT 
            	COUNT(*) AS tong_don,
            	SUM(CASE WHEN hd.trang_thai = %d THEN 1 ELSE 0 END) AS cho_thanh_toan,
            	SUM(CASE WHEN hd.trang_thai = %d THEN 1 ELSE 0 END) AS da_thanh_toan,
                SUM(CASE WHEN hd.trang_thai = %d THEN 1 ELSE 0 END) AS da_huy,
            	(SELECT COUNT(*) FROM KhachHang WHERE ngay_tao >= ? AND ngay_tao <= ?) AS khach_hang
            FROM HoaDon AS hd
                LEFT JOIN HoaDonChiTiet AS hdct ON hdct.id_hoa_don = hd.id
                LEFT JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
            WHERE 
                (COALESCE(?, 0) < 1 OR spct.id_san_pham = ?) AND
                hd.ngay_tao >= ? AND
                hd.ngay_tao <= ?
	""", TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN, TrangThaiHoaDonConstant.STATUS_DA_HUY);
	
	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	Object[] args = new Object[] {
	    String.format("%s 00:00:00", filter.getStartDate().format(format)),
	    String.format("%s 23:59:59", filter.getEndDate().format(format)),
	    filter.getSanPham().getValue(),
	    filter.getSanPham().getValue(),
	    String.format("%s 00:00:00", filter.getStartDate().format(format)),
	    String.format("%s 23:59:59", filter.getEndDate().format(format))
	};
	
	try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
		model.setTongHoaDon(resultSet.getInt("tong_don"));
                model.setChoThanhToan(resultSet.getInt("cho_thanh_toan"));
		model.setDaThanhToan(resultSet.getInt("da_thanh_toan"));
		model.setDaHuy(resultSet.getInt("da_huy"));
		model.setKhachHang(resultSet.getInt("khach_hang"));
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        finally {
            JbdcHelper.close(resultSet);
        }

        return model;
    }
	
    public List<InuhaThongKeSanPhamModel> selectAll(InuhaFilterThongKeRequest filter) throws SQLException {
                
        List<InuhaThongKeSanPhamModel> list = new ArrayList<>();
        ResultSet resultSet = null;
	
	String orderBy = "";
	switch(filter.getOrderBy().getValue()) {
	    case 1 -> {
		orderBy = "hd.doanh_thu";
	    }
	    case 2 -> {
		orderBy = "hd.loi_nhuan";
	    }
	    default -> {
		orderBy = "hd.so_luong";
	    }
	}
        
	String query = null;
	
	int idSanPham = filter.getSanPham().getValue();
	
	if (idSanPham < 1) {
	    query = String.format("""
		WITH HoaDonCTE AS (
		    SELECT 
			sp.id,
			SUM(hdct.so_luong) AS so_luong,
			(SUM(hdct.gia_ban * hdct.so_luong) - (hd.tien_giam / (SELECT SUM(so_luong) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))) AS doanh_thu,
			(SUM((hdct.gia_ban - hdct.gia_nhap) * hdct.so_luong) - (hd.tien_giam / (SELECT SUM(so_luong) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))) AS loi_nhuan
		    FROM HoaDon AS hd
			JOIN HoaDonChiTiet AS hdct ON hdct.id_hoa_don = hd.id
			JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
			JOIN SanPham AS sp ON sp.id = spct.id_san_pham
		    WHERE 
			hd.trang_thai = %d AND
			hd.trang_thai_xoa != 1 AND
			(COALESCE(?, 0) < 1 OR spct.id_san_pham = ?) AND
			hd.ngay_tao >= ? AND
			hd.ngay_tao <= ?
		    GROUP BY hd.id, hd.ngay_tao, hd.tien_giam, sp.id
		),
		HoaDonCTE2 AS (
		    SELECT 
			id,
			ISNULL(SUM(so_luong), 0) AS so_luong,
			ISNULL(SUM(doanh_thu), 0) AS doanh_thu,
			ISNULL(SUM(loi_nhuan), 0) AS loi_nhuan
		    FROM HoaDonCTE
		    GROUP BY id
		)
		SELECT 
		    ROW_NUMBER() OVER (ORDER BY %s DESC) AS stt,
		    sp.id,
		    sp.ma,
		    sp.ten,
		    hd.so_luong AS so_luong_ban,
		    hd.doanh_thu,
		    hd.loi_nhuan,
		    (SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) AS so_luong_ton
		FROM HoaDonCTE2 AS hd
		    JOIN SanPham AS sp ON sp.id = hd.id
	    """, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN, orderBy);
	} else {
	    query = String.format("""
		WITH HoaDonCTE AS (
		    SELECT 
			spct.id,
			SUM(hdct.so_luong) AS so_luong,
			(SUM(hdct.gia_ban * hdct.so_luong) - (hd.tien_giam / (SELECT SUM(so_luong) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))) AS doanh_thu,
			(SUM((hdct.gia_ban - hdct.gia_nhap) * hdct.so_luong) - (hd.tien_giam / (SELECT SUM(so_luong) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))) AS loi_nhuan
		    FROM HoaDon AS hd
			JOIN HoaDonChiTiet AS hdct ON hdct.id_hoa_don = hd.id
			JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
			JOIN SanPham AS sp ON sp.id = spct.id_san_pham
		    WHERE 
			hd.trang_thai = %d AND
			hd.trang_thai_xoa != 1 AND
			(COALESCE(?, 0) < 1 OR spct.id_san_pham = ?) AND
			hd.ngay_tao >= ? AND
			hd.ngay_tao <= ?
		    GROUP BY hd.id, hd.tien_giam, spct.id
		),
		HoaDonCTE2 AS (
		    SELECT 
			id,
			ISNULL(SUM(so_luong), 0) AS so_luong,
			ISNULL(SUM(doanh_thu), 0) AS doanh_thu,
			ISNULL(SUM(loi_nhuan), 0) AS loi_nhuan
		    FROM HoaDonCTE
		    GROUP BY id
		)
		SELECT 
		    ROW_NUMBER() OVER (ORDER BY %s DESC) AS stt,
		    spct.id,
		    spct.ma,
		    (sp.ten + ' - ' + kc.ten + ' - ' + ms.ten) AS ten,
		    hd.so_luong AS so_luong_ban,
		    hd.doanh_thu,
		    hd.loi_nhuan,
		    spct.so_luong AS so_luong_ton
		FROM HoaDonCTE2 AS hd
		    JOIN SanPhamChiTiet AS spct ON spct.id = hd.id
		    JOIN SanPham AS sp ON sp.id = spct.id_san_pham
		    JOIN KichCo AS kc ON kc.id = spct.id_kich_co
		    JOIN MauSac AS ms ON ms.id = spct.id_mau_sac
	    """, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN, orderBy);
	}

	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
        Object[] args = new Object[] {
	    idSanPham,
	    idSanPham,
	    String.format("%s 00:00:00", filter.getStartDate().format(format)),
	    String.format("%s 23:59:59", filter.getEndDate().format(format))
        };

        try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaThongKeSanPhamModel model = InuhaThongKeSanPhamModel.builder()
			.stt(resultSet.getInt("stt"))
			.id(resultSet.getInt("id"))
			.ma(resultSet.getString("ma"))
			.ten(resultSet.getString("ten"))
			.doanhThu(resultSet.getDouble("doanh_thu"))
			.loiNhuan(resultSet.getDouble("loi_nhuan"))
			.soLuongBan(resultSet.getInt("so_luong_ban"))
			.soLuongTon(resultSet.getInt("so_luong_ton"))
			.build();

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
	
    public List<InuhaThongKeSanPhamModel> selectPage(InuhaFilterThongKeRequest filter) throws SQLException {
                
        List<InuhaThongKeSanPhamModel> list = new ArrayList<>();
        ResultSet resultSet = null;
	
	String orderBy = "";
	switch(filter.getOrderBy().getValue()) {
	    case 1 -> {
		orderBy = "hd.doanh_thu";
	    }
	    case 2 -> {
		orderBy = "hd.loi_nhuan";
	    }
	    default -> {
		orderBy = "hd.so_luong";
	    }
	}
        

	String query = null;
	
	int idSanPham = filter.getSanPham().getValue();
	
	if (idSanPham < 1) {
	    query = String.format("""
		WITH HoaDonCTE AS (
		    SELECT 
			sp.id,
			SUM(hdct.so_luong) AS so_luong,
			(SUM(hdct.gia_ban * hdct.so_luong) - (hd.tien_giam / (SELECT SUM(so_luong) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))) AS doanh_thu,
			(SUM((hdct.gia_ban - hdct.gia_nhap) * hdct.so_luong) - (hd.tien_giam / (SELECT SUM(so_luong) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))) AS loi_nhuan
		    FROM HoaDon AS hd
			JOIN HoaDonChiTiet AS hdct ON hdct.id_hoa_don = hd.id
			JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
			JOIN SanPham AS sp ON sp.id = spct.id_san_pham
		    WHERE 
			hd.trang_thai = %d AND
			hd.trang_thai_xoa != 1 AND
			(COALESCE(?, 0) < 1 OR spct.id_san_pham = ?) AND
			hd.ngay_tao >= ? AND
			hd.ngay_tao <= ?
		    GROUP BY hd.id, hd.ngay_tao, hd.tien_giam, sp.id
		),
		HoaDonCTE2 AS (
		    SELECT 
			id,
			ISNULL(SUM(so_luong), 0) AS so_luong,
			ISNULL(SUM(doanh_thu), 0) AS doanh_thu,
			ISNULL(SUM(loi_nhuan), 0) AS loi_nhuan
		    FROM HoaDonCTE
		    GROUP BY id
		),
		TableCTE AS (
                    SELECT 
			ROW_NUMBER() OVER (ORDER BY %s DESC) AS stt,
			sp.id,
			sp.ma,
			sp.ten,
			hd.so_luong AS so_luong_ban,
			hd.doanh_thu,
			hd.loi_nhuan,
			(SELECT SUM(so_luong) FROM SanPhamChiTiet WHERE id_san_pham = sp.id) AS so_luong_ton
		    FROM HoaDonCTE2 AS hd
			JOIN SanPham AS sp ON sp.id = hd.id
                )
		SELECT *
		FROM TableCTE
		WHERE stt BETWEEN ? AND ?
	    """, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN, orderBy);
	} else {
	    query = String.format("""
		WITH HoaDonCTE AS (
		    SELECT 
			spct.id,
			SUM(hdct.so_luong) AS so_luong,
			(SUM(hdct.gia_ban * hdct.so_luong) - (hd.tien_giam / (SELECT SUM(so_luong) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))) AS doanh_thu,
			(SUM((hdct.gia_ban - hdct.gia_nhap) * hdct.so_luong) - (hd.tien_giam / (SELECT SUM(so_luong) FROM HoaDonChiTiet WHERE id_hoa_don = hd.id) * SUM(hdct.so_luong))) AS loi_nhuan
		    FROM HoaDon AS hd
			JOIN HoaDonChiTiet AS hdct ON hdct.id_hoa_don = hd.id
			JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
			JOIN SanPham AS sp ON sp.id = spct.id_san_pham
		    WHERE 
			hd.trang_thai = %d AND
			hd.trang_thai_xoa != 1 AND
			(COALESCE(?, 0) < 1 OR spct.id_san_pham = ?) AND
			hd.ngay_tao >= ? AND
			hd.ngay_tao <= ?
		    GROUP BY hd.id, hd.tien_giam, spct.id
		),
		HoaDonCTE2 AS (
		    SELECT 
			id,
			ISNULL(SUM(so_luong), 0) AS so_luong,
			ISNULL(SUM(doanh_thu), 0) AS doanh_thu,
			ISNULL(SUM(loi_nhuan), 0) AS loi_nhuan
		    FROM HoaDonCTE
		    GROUP BY id
		),
		TableCTE AS (
                    SELECT 
			ROW_NUMBER() OVER (ORDER BY %s DESC) AS stt,
			spct.id,
			spct.ma,
			(sp.ten + ' - ' + kc.ten + ' - ' + ms.ten) AS ten,
			hd.so_luong AS so_luong_ban,
			hd.doanh_thu,
			hd.loi_nhuan,
			spct.so_luong AS so_luong_ton
		    FROM HoaDonCTE2 AS hd
			JOIN SanPhamChiTiet AS spct ON spct.id = hd.id
			JOIN SanPham AS sp ON sp.id = spct.id_san_pham
                        JOIN KichCo AS kc ON kc.id = spct.id_kich_co
                        JOIN MauSac AS ms ON ms.id = spct.id_mau_sac
                )
		SELECT *
		FROM TableCTE
		WHERE stt BETWEEN ? AND ?
	    """, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN, orderBy);
	}

        int[] offset = FilterRequest.getOffset(filter.getPage(), filter.getSize());
        int start = offset[0];
        int limit = offset[1];

	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
        Object[] args = new Object[] {
	    idSanPham,
	    idSanPham,
	    String.format("%s 00:00:00", filter.getStartDate().format(format)),
	    String.format("%s 23:59:59", filter.getEndDate().format(format)),
            start,
            limit
        };

        try {
            resultSet = JbdcHelper.query(query, args);
            while(resultSet.next()) {
                InuhaThongKeSanPhamModel model = InuhaThongKeSanPhamModel.builder()
			.stt(resultSet.getInt("stt"))
			.id(resultSet.getInt("id"))
			.ma(resultSet.getString("ma"))
			.ten(resultSet.getString("ten"))
			.doanhThu(resultSet.getDouble("doanh_thu"))
			.loiNhuan(resultSet.getDouble("loi_nhuan"))
			.soLuongBan(resultSet.getInt("so_luong_ban"))
			.soLuongTon(resultSet.getInt("so_luong_ton"))
			.build();

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

    public int count(InuhaFilterThongKeRequest filter) throws SQLException {
                
        int totalPages = 0;
        int totalRows = 0;

	int idSanPham = filter.getSanPham().getValue();
	
        String query = String.format("""
	    SELECT 
		COUNT(DISTINCT %s)
	    FROM HoaDon AS hd
		JOIN HoaDonChiTiet AS hdct ON hdct.id_hoa_don = hd.id
		JOIN SanPhamChiTiet AS spct ON spct.id = hdct.id_san_pham_chi_tiet
		JOIN SanPham AS sp ON sp.id = spct.id_san_pham
	    WHERE 
		hd.trang_thai = %d AND
		hd.trang_thai_xoa != 1 AND
		    (COALESCE(?, 0) < 1 OR spct.id_san_pham = ?) AND
		hd.ngay_tao >= ? AND
		hd.ngay_tao <= ?
        """, idSanPham < 1 ? "sp.id" : "spct.id", TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN);

	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
        Object[] args = new Object[] {
	    idSanPham,
	    idSanPham,
	    String.format("%s 00:00:00", filter.getStartDate().format(format)),
	    String.format("%s 23:59:59", filter.getEndDate().format(format))
        };
	        
        try {
	    Object num = JbdcHelper.value(query, args);
            totalRows = num != null ? (int) num : 0;
	   		
            totalPages = (int) Math.ceil((double) totalRows / filter.getSize());
        } catch(Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return totalPages;
    }
}
