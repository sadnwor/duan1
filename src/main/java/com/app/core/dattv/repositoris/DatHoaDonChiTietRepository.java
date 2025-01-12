/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.repositoris;

import com.app.common.configs.DBConnect;
import com.app.core.dattv.models.DatHoaDonChiTietModel;
import com.app.core.dattv.request.DatHoaDonRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author WIN
 */
public class DatHoaDonChiTietRepository {

    DatHoaDonRequest datHoaDonRequest = null;

    public ArrayList<DatHoaDonChiTietModel> loadDatHoaDonChiTietTable(int idHoaDon) {
        //SUA LAI CAU QUERY

        String sql = """
        SELECT
             hdct.id,
             spct.ma,
             sp.ten,
             kc.ten,
             ms.ten,
             hdct.so_luong,
             hdct.gia_ban,
             hdct.id_san_pham_chi_tiet
         FROM HoaDonChiTiet hdct
            INNER JOIN HoaDon hd ON hd.id = hdct.id_hoa_don
            INNER JOIN SanPhamChiTiet spct ON hdct.id_san_pham_chi_tiet = spct.id
            INNER JOIN SanPham sp ON spct.id_san_pham = sp.id
            INNER JOIN KichCo kc ON spct.id_kich_co = kc.id
            INNER JOIN MauSac ms ON spct.id_mau_sac = ms.id
         WHERE
             hd.trang_thai_xoa != 1 and hdct.id_hoa_don = ?
         ORDER BY
             hdct.id DESC;	 
                     """;
        ArrayList<DatHoaDonChiTietModel> lists = new ArrayList<>();
        try (Connection con = DBConnect.getInstance().getConnect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, idHoaDon);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                DatHoaDonChiTietModel datHoaDonChiTietModel = new DatHoaDonChiTietModel();

                datHoaDonChiTietModel.setId(rs.getInt(1));
                datHoaDonChiTietModel.setMa(rs.getString(2));
                datHoaDonChiTietModel.setTen(rs.getString(3));
                datHoaDonChiTietModel.setKichCo(rs.getString(4));
                datHoaDonChiTietModel.setMauSac(rs.getString(5));
                datHoaDonChiTietModel.setSoLuong(rs.getInt(6));
                datHoaDonChiTietModel.setGiaBan(rs.getDouble(7));
                datHoaDonChiTietModel.setIdSanPhamChiTiet(rs.getInt(8));
                lists.add(datHoaDonChiTietModel);

            }
        } catch (Exception e) {
            e.printStackTrace(System.out); // nem loi khi xay ra 
        }
        return lists;
    }

}
