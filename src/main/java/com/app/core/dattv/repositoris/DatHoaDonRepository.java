/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.repositoris;

import com.app.common.configs.DBConnect;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.interfaces.IDAOinterface;
import com.app.core.dattv.models.DatHoaDonModel;
import com.app.core.dattv.request.DatHoaDonRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.request.FilterRequest;
import java.util.Date;
import java.util.Optional;

/**
 *
 * @author WIN
 */
public class DatHoaDonRepository {
    private static DatHoaDonRepository instance = null;
    
    public static DatHoaDonRepository getInstance() { 
	if (instance == null) { 
	    instance = new DatHoaDonRepository();
	}
	return instance;
    }

    public DatHoaDonRepository() {
    }

    public ArrayList<DatHoaDonRequest> getAll() {
        //SUA LAI CAU QUERY
        String sql = """
                    SELECT    
                            hd.ma,
                            hd.ngay_tao,
                            ISNULL(kh.ho_ten, N'Khách hàng lẻ') AS ho_ten,
                            ISNULL(SUM(hdct.gia_ban * so_luong), 0) AS tong_gia_ban,
                            hd.tien_giam,
                            ISNULL((SUM(hdct.gia_ban * so_luong) - hd.tien_giam), 0) AS tong_sau_giam,
                            hd.trang_thai,
                            hd.trang_thai_xoa,
                            hd.phuong_thuc_thanh_toan,
                            hd.id,
                            tk.tai_khoan as tennv,
                            hd.tien_mat,
                            hd.tien_chuyen_khoan,
                            ISNULL(kh.sdt, '') AS sdt
                          FROM 
                              HoaDon hd
                          LEFT JOIN 
                              HoaDonChiTiet hdct ON hd.id = hdct.id_hoa_don
                          LEFT JOIN 
                              KhachHang kh ON hd.id_khach_hang = kh.id
                          INNER JOIN 
                              TaiKhoan tk ON hd.id_tai_khoan = tk.id 
                          GROUP BY 
                            hd.ma,
                            hd.ngay_tao,
                            kh.ho_ten,
                            kh.sdt,
                            hd.tien_giam,
                            hd.trang_thai,
                            hd.trang_thai_xoa,
                            hd.phuong_thuc_thanh_toan,
                            hd.id,
                            hd.tien_mat,
                            hd.tien_chuyen_khoan,
                            tk.tai_khoan
                          ORDER BY 
                              hd.ngay_cap_nhat DESC;
                     """;
        ArrayList<DatHoaDonRequest> lists = new ArrayList<>();
        try (Connection con = DBConnect.getInstance().getConnect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DatHoaDonRequest datHoaDonRequest=new DatHoaDonRequest();

                datHoaDonRequest.setMaHd(rs.getString(1));
                datHoaDonRequest.setThoiGian(rs.getString(2));
                datHoaDonRequest.setKhachHang(rs.getString(3));
                datHoaDonRequest.setTongTienhang(rs.getDouble(4));
                datHoaDonRequest.setGiamGia(rs.getDouble(5));
                datHoaDonRequest.setThanhTien(rs.getDouble(6));
                datHoaDonRequest.setTrangThai(rs.getInt(7));
                datHoaDonRequest.setTrangThaixoa(rs.getBoolean(8));
                datHoaDonRequest.setPhuongThucTT(rs.getInt(9));
                datHoaDonRequest.setId(rs.getInt(10));
                datHoaDonRequest.setTenNv(rs.getString(11));
                datHoaDonRequest.setTienMat(rs.getDouble(12));
                datHoaDonRequest.setTienChuyenKhoan(rs.getDouble(13));
                datHoaDonRequest.setSdt(rs.getString(14));
                
                lists.add(datHoaDonRequest);

            }
        } catch (Exception e) {
            e.printStackTrace(System.out); // nem loi khi xay ra
        }
        return lists;
    }

    public Optional<DatHoaDonRequest> getById(Integer id) {
        ResultSet rs = null;
        DatHoaDonRequest model = null;

        String sql = ("""
                        SELECT
                            hd.ma,
                             hd.ngay_tao,
                             ISNULL(kh.ho_ten, N'Khách hàng lẻ') AS ho_ten,
                             ISNULL(SUM(hdct.gia_ban * so_luong), 0) AS tong_gia_ban,
                             hd.tien_giam,
                             ISNULL((SUM(hdct.gia_ban * so_luong) - hd.tien_giam), 0) AS tong_sau_giam,
                             hd.trang_thai,
                             hd.trang_thai_xoa,
                             hd.phuong_thuc_thanh_toan,
                             hd.id,
                             tk.tai_khoan as tennv,
                             hd.tien_mat,
                             hd.tien_chuyen_khoan,
                             ISNULL(kh.sdt, '') AS sdt
                        FROM 
                            HoaDon hd
                        LEFT JOIN 
                            HoaDonChiTiet hdct ON hd.id = hdct.id_hoa_don
                        LEFT JOIN 
                            KhachHang kh ON hd.id_khach_hang = kh.id
                        INNER JOIN 
                            TaiKhoan tk ON hd.id_tai_khoan = tk.id 
                        WHERE 
                            hd.trang_thai_xoa != 1 AND
                            hd.id=?
                        GROUP BY 
                            hd.ma,
                             hd.ngay_tao,
                             kh.ho_ten,
                             kh.sdt,
                             hd.tien_giam,
                             hd.trang_thai,
                             hd.trang_thai_xoa,
                             hd.phuong_thuc_thanh_toan,
                             hd.id,
                             hd.tien_mat,
                             hd.tien_chuyen_khoan,
                             tk.tai_khoan
                              
        """);
        try {
            Connection con = DBConnect.getInstance().getConnect();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setObject(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                model = new DatHoaDonRequest();
                
                model.setMaHd(rs.getString(1));
                model.setThoiGian(rs.getString(2));
                model.setKhachHang(rs.getString(3));
                model.setTongTienhang(rs.getDouble(4));
                model.setGiamGia(rs.getDouble(5));
                model.setThanhTien(rs.getDouble(6));
                model.setTrangThai(rs.getInt(7));
                model.setTrangThaixoa(rs.getBoolean(8));
                model.setPhuongThucTT(rs.getInt(9));
                model.setId(rs.getInt(10));
                model.setTenNv(rs.getString(11));
                model.setTienMat(rs.getDouble(12));
                model.setTienChuyenKhoan(rs.getDouble(13));
                model.setSdt(rs.getString(14));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(model);
    }
           
    public ArrayList<DatHoaDonRequest> locDataPage(String tuKhoa, int phuongThucTT, int trangThai, String username, String startDate, String endDate, FilterRequest request) {
        
        String sql = """
                WITH CTE_HoaDon AS (
                     SELECT
                         hd.ma,
                         hd.ngay_tao,
                         ISNULL(kh.ho_ten, N'Khách hàng lẻ') AS ho_ten,
                         ISNULL(SUM(hdct.gia_ban * so_luong), 0) AS tong_gia_ban,
                         hd.tien_giam,
                         ISNULL((SUM(hdct.gia_ban * so_luong) - hd.tien_giam), 0) AS tong_sau_giam,
                         hd.trang_thai,
                         hd.trang_thai_xoa,
                         hd.phuong_thuc_thanh_toan,
                         hd.id,
                         tk.tai_khoan as tennv,
                         ROW_NUMBER() OVER (ORDER BY hd.ngay_cap_nhat DESC) AS stt,
                         hd.tien_mat,
                         hd.tien_chuyen_khoan,
                        ISNULL(kh.sdt, '') AS sdt
                     FROM 
                         HoaDon hd
                     LEFT JOIN 
                         HoaDonChiTiet hdct ON hd.id = hdct.id_hoa_don
                     LEFT JOIN 
                         KhachHang kh ON hd.id_khach_hang = kh.id
                     INNER JOIN 
                         TaiKhoan tk ON hd.id_tai_khoan = tk.id 
                    Where 
                          hd.trang_thai_xoa != 1
                           AND (? IS NULL OR hd.ma LIKE ?)
                           AND (? IS NULL OR hd.ngay_tao >= ?)
                           AND (? IS NULL OR hd.ngay_tao <= ?)
                           AND (COALESCE(?, -1) < 0 OR hd.phuong_thuc_thanh_toan = ?)
                           AND (COALESCE(?, -1) < 0 OR hd.trang_thai = ?)
                           AND (? IS NULL OR tk.tai_khoan LIKE ?)
                     GROUP BY 
                         hd.ma,
                         hd.ngay_tao,
                         hd.ngay_cap_nhat,
                         kh.ho_ten,
                         kh.sdt,
                         hd.tien_giam,
                         hd.trang_thai,
                         hd.trang_thai_xoa,
                         hd.phuong_thuc_thanh_toan,
                         hd.id,
                         hd.tien_mat,
                         hd.tien_chuyen_khoan,
                         tk.tai_khoan
                 )
                 SELECT 
                     stt,
                     ma,
                     ngay_tao,
                     ho_ten,
                     tong_gia_ban,
                     tien_giam,
                     tong_sau_giam,
                     trang_thai,
                     trang_thai_xoa,
                     phuong_thuc_thanh_toan,
                     id,
                     tennv,
                     tien_mat,
                     tien_chuyen_khoan,
                     sdt
                 FROM 
                     CTE_HoaDon
                 WHERE 
                     stt BETWEEN ? AND ?
                     """;
        
        int[] offset = FilterRequest.getOffset(request.getPage(), request.getSize());
        int start = offset[0];
        int limit = offset[1];
        
        
        ArrayList<DatHoaDonRequest> lists = new ArrayList<>();
        try (Connection con = DBConnect.getInstance().getConnect();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
        String thoiGianBatDau = startDate != null ? startDate + " 00:00:00" : null;
        String thoiGianKetThuc = endDate != null ? endDate + " 23:59:59" : null;

        ps.setObject(1, tuKhoa); // từ khoá
        ps.setObject(2, "%" + tuKhoa + "%"); // từ khoá
        ps.setObject(3, startDate); // Ngày bắt đầu
        ps.setObject(4, thoiGianBatDau); // Ngày bắt đầu tính từ 00h00p
        ps.setObject(5, endDate);   // Ngày kết thúc
        ps.setObject(6, thoiGianKetThuc);   // Ngày kết thúc tính đến 23:59:59
        ps.setObject(7, phuongThucTT); // Phương thức thanh toán
        ps.setObject(8, phuongThucTT); // Phương thức thanh toán
        ps.setObject(9, trangThai); // Trạng thái
        ps.setObject(10, trangThai); // Trạng thái
        ps.setObject(11, username); // tên tài khoản
        ps.setObject(12, username); // tên tài khoản
        ps.setObject(13, start); // bắt đầu từ stt
        ps.setObject(14, limit); // lấy đến số stt
            
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DatHoaDonRequest datHoaDonRequest=new DatHoaDonRequest();
                
                datHoaDonRequest.setStt(rs.getInt(1));
                datHoaDonRequest.setMaHd(rs.getString(2));
                datHoaDonRequest.setThoiGian(rs.getString(3));
                datHoaDonRequest.setKhachHang(rs.getString(4));
                datHoaDonRequest.setTongTienhang(rs.getDouble(5));
                datHoaDonRequest.setGiamGia(rs.getDouble(6));
                datHoaDonRequest.setThanhTien(rs.getDouble(7));
                datHoaDonRequest.setTrangThai(rs.getInt(8));
                datHoaDonRequest.setTrangThaixoa(rs.getBoolean(9));
                datHoaDonRequest.setPhuongThucTT(rs.getInt(10));
                datHoaDonRequest.setId(rs.getInt(11));
                datHoaDonRequest.setTenNv(rs.getString(12));
                datHoaDonRequest.setTienMat(rs.getDouble(13));
                datHoaDonRequest.setTienChuyenKhoan(rs.getDouble(14));
                datHoaDonRequest.setSdt(rs.getString(15));
                lists.add(datHoaDonRequest);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out); // nem loi khi xay ra 
        }
        return lists;
    }

    public int count(String tuKhoa, int phuongThucTT, int trangThai, String username, String startDate, String endDate, FilterRequest request) {
        
        int totalPages = 0;
        int totalRows = 0;
        
        String sql = """
                     SELECT
                         count(*)
                     FROM 
                         HoaDon hd
                         INNER JOIN TaiKhoan tk ON hd.id_tai_khoan = tk.id 
                    Where 
                          hd.trang_thai_xoa != 1
                           AND (? IS NULL OR hd.ma LIKE ?)
                           AND (? IS NULL OR hd.ngay_tao >= ?)
                           AND (? IS NULL OR hd.ngay_tao <= ?)
                           AND (COALESCE(?, -1) < 0 OR hd.phuong_thuc_thanh_toan = ?)
                           AND (COALESCE(?, -1) < 0 OR hd.trang_thai = ?)
                           AND (? IS NULL OR tk.tai_khoan LIKE ?)
                     """;
        

        try (Connection con = DBConnect.getInstance().getConnect();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
        String thoiGianBatDau = startDate != null ? startDate + " 00:00:00" : null;
        String thoiGianKetThuc = endDate != null ? endDate + " 23:59:59" : null;
        
        ps.setObject(1, tuKhoa); // từ khoá
        ps.setObject(2, "%" + tuKhoa + "%"); // từ khoá
        ps.setObject(3, startDate); // Ngày bắt đầu
        ps.setObject(4, thoiGianBatDau); // Ngày bắt đầu tính từ 00h00p
        ps.setObject(5, endDate);   // Ngày kết thúc
        ps.setObject(6, thoiGianKetThuc);   // Ngày kết thúc tính đến 23:59:59
        ps.setObject(7, phuongThucTT); // Phương thức thanh toán
        ps.setObject(8, phuongThucTT); // Phương thức thanh toán
        ps.setObject(9, trangThai); // Trạng thái
        ps.setObject(10, trangThai); // Trạng thái
        ps.setObject(11, username); // tên tài khoản
        ps.setObject(12, username); // tên tài khoản
            
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalRows = rs.getInt(1);
                totalPages = (int) Math.ceil((double) totalRows / request.getSize());
            }
        } catch (Exception e) {
            e.printStackTrace(System.out); // nem loi khi xay ra 
        }
        return totalPages;
    }

}



    

