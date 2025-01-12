/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.lam.repositories;

import com.app.common.configs.DBConnect;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.lam.models.LamLichSuModels;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class LamLichSuRepositories {

    public int count(int id_khach_hang, FilterRequest request) {
	
        int totalPages = 0;
        int totalRows = 0;

        String sql = """
                        SELECT COUNT(*)
                        FROM [dbo].[HoaDon]
                        WHERE [dbo].[HoaDon].[id_khach_hang] = ?
                     """;
        
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql);) {
            
            ps.setObject(1, id_khach_hang);
            
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
    
    public ArrayList<LamLichSuModels> getLS(int id_khach_hang, FilterRequest request) {
        String sql = """
            WITH TableCTE AS (
                 SELECT HoaDon.id
                       ,HoaDon.ma
                       ,((SELECT SUM(HoaDonChiTiet.gia_ban * HoaDonChiTiet.so_luong) FROM HoaDonChiTiet WHERE HoaDonChiTiet.id_hoa_don = HoaDon.id) - HoaDon.tien_giam) AS tong_tien
                       ,HoaDon.ngay_tao
                       ,HoaDon.trang_thai
                       ,ROW_NUMBER() OVER (ORDER BY HoaDon.id DESC) AS stt
                   FROM HoaDon
                  WHERE HoaDon.trang_thai_xoa != 1 AND HoaDon.id_khach_hang = ?
            )
            SELECT 
                id,
                ma,
                tong_tien,
                ngay_tao,
                trang_thai,
                stt
            FROM TableCTE
            WHERE stt BETWEEN ? AND ?
                 """;
        
        int[] offset = FilterRequest.getOffset(request.getPage(), request.getSize());
        int start = offset[0];
        int limit = offset[1];
        
        ArrayList<LamLichSuModels> listRPLS = new ArrayList<>();
        try (Connection con = DBConnect.getInstance().getConnect();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setObject(1, id_khach_hang);
            ps.setObject(2, start);
            ps.setObject(3, limit);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LamLichSuModels ls = new LamLichSuModels();
                ls.setId(rs.getInt(1));
                ls.setMaHD(rs.getString(2));
                ls.setTongTien(rs.getDouble(3));
                ls.setNgayMua(rs.getString(4));
                ls.setTrangThai(rs.getInt(5));
                ls.setStt(rs.getInt(6));
                listRPLS.add(ls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listRPLS;
    }


}
