/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.lam.repositories;

import com.app.common.configs.DBConnect;
import com.app.common.helper.JbdcHelper;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.constants.TrangThaiPhieuGiamGiaConstant;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.request.InuhaFilterPhieuGiamGiaRequest;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.app.core.lam.models.LamKhachHangModels;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class LamKhachHangRepositories {

    public int count(String timKiem, FilterRequest request) {
	
        int totalPages = 0;
        int totalRows = 0;

        String sql = """
                        SELECT COUNT(*)
                        FROM [dbo].[KhachHang]
                        WHERE (? IS NULL OR [ho_ten] LIKE ? OR [sdt] LIKE ?) AND trang_thai_xoa !=1
                     """;
        
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql);) {
            
            ps.setObject(1, timKiem);
            ps.setObject(2, "%" + timKiem + "%");
            ps.setObject(3, "%" + timKiem + "%");
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                totalRows = rs.getInt(1);
                totalPages = (int) Math.ceil((double) totalRows / request.getSize());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalPages;
    }
    
    public ArrayList<LamKhachHangModels> getKH(String timKiem, FilterRequest request) {
        String sql = """
                     WITH TableCTE AS (
                        SELECT [id]
                           ,[ho_ten]
                           ,[sdt]
                           ,[gioi_tinh]
                           ,[dia_chi]
                           ,[ngay_tao]
                           ,[trang_thai_xoa]
                           ,(SELECT COUNT(*) FROM [dbo].[HoaDon] WHERE [id_khach_hang] = [dbo].[KhachHang].[id] AND [trang_thai] = ?) AS luot_mua
                           ,ROW_NUMBER() OVER (ORDER BY [dbo].[KhachHang].[id] DESC) AS stt
                        FROM [dbo].[KhachHang]
                        WHERE (? IS NULL OR [ho_ten] LIKE ? OR [sdt] LIKE ?) AND trang_thai_xoa !=1 
                     )
                     SELECT 
                        id,
                        ho_ten,
                        sdt,
                        gioi_tinh,
                        dia_chi,
                        ngay_tao,
                        trang_thai_xoa,
                        luot_mua,
                        stt
                        FROM TableCTE
                        WHERE stt BETWEEN ? AND ?
                     """;
        
        int[] offset = FilterRequest.getOffset(request.getPage(), request.getSize());
        int start = offset[0];
        int limit = offset[1];
        
        
        ArrayList<LamKhachHangModels> listRPKH = new ArrayList<>();
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql);) {
            
            ps.setObject(1, TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN);
            ps.setObject(2, timKiem);
            ps.setObject(3, "%" + timKiem + "%");
            ps.setObject(4, "%" + timKiem + "%");
            ps.setObject(5, start);
            ps.setObject(6, limit);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LamKhachHangModels kh = new LamKhachHangModels();
                kh.setIdKH(rs.getInt(1));
                kh.setTenKH(rs.getString(2));
                kh.setSoDienThoai(rs.getString(3));
                kh.setGioiTinh(rs.getBoolean(4));
                kh.setDiaChi(rs.getString(5));
                kh.setNgayTao(rs.getString(6));
                kh.setTrangThaiXoa(rs.getBoolean(7));
                kh.setSoLanMuaHang(rs.getInt(8));
                kh.setStt(rs.getInt(9));
                listRPKH.add(kh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listRPKH;
    }

    public boolean checkSdtThem(String sdt) {
        String sql = """
                     SELECT [id]
                       FROM [dbo].[KhachHang] 
                       WHERE [sdt] LIKE ?
                     """;
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setObject(1, sdt);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
       
    public boolean checkSdtSua(int id, String sdt) {
        String sql = """
                     SELECT [id]
                       FROM [dbo].[KhachHang] 
                       WHERE [sdt] LIKE ? AND [id] != ?
                     """;
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setObject(1, sdt);
            ps.setObject(2, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public boolean addKhachHang(LamKhachHangModels kh) {
        String sql = """
                 INSERT INTO [dbo].[KhachHang]
                            ([ho_ten]
                            ,[sdt]
                            ,[gioi_tinh]
                            ,[dia_chi]
                            ,[trang_thai_xoa])
                      VALUES
                            (?,?,?,?,?)
                 """;
        int check = 0;
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getSoDienThoai());
            ps.setBoolean(3, kh.isGioiTinh());
            ps.setString(4, kh.getDiaChi());
            ps.setBoolean(5, false);
            check = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check > 0;

    }

    public boolean updateKhachHang(LamKhachHangModels kh) {
        String sql = """
                     UPDATE [dbo].[KhachHang]
                        SET [ho_ten] = ?
                           ,[sdt] = ?
                           ,[gioi_tinh] = ?
                           ,[dia_chi] = ?
                           ,[trang_thai_xoa] = ?
                      WHERE  id = ?
                     """;
        int check = 0;
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getSoDienThoai());
            ps.setBoolean(3, kh.isGioiTinh());
            ps.setString(4, kh.getDiaChi());
            ps.setBoolean(5, kh.isTrangThaiXoa());
            ps.setInt(6, kh.getIdKH());
            check = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check > 0;
    }
    
    public boolean checkDelete(int id) {
        String sql = """
                     SELECT [id_khach_hang]
                       FROM [dbo].[HoaDon] 
                       WHERE [id_khach_hang] = ? AND [trang_thai] != ?
                     """;
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setObject(1, id);
            ps.setObject(2, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteKhachHang(int id) {
        String sql = """
                     UPDATE [dbo].[HoaDon] 
                        SET [id_khach_hang] = NULL 
                        WHERE [id_khach_hang] = ? AND [trang_thai] = ?;
                     
                     DELETE FROM [dbo].[KhachHang]
                           WHERE id = ?
                     """;
        int check = 0;
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setObject(1, id);
            ps.setObject(2, TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN);
            ps.setObject(3, id);
            
            check = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check > 0;
    }
}
