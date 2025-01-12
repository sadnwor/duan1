/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.repositoris;

import com.app.common.configs.DBConnect;
import com.app.core.dattv.models.DatKhachHangModel;
import com.app.core.dattv.models.DatNhanvienModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author WIN
 */
public class DatNhanVienRepository {
       public ArrayList<DatNhanvienModel> getAll() {
        String sql = """
                 select Taikhoan.id,TaiKhoan.tai_khoan from TaiKhoan
                     """;
        ArrayList<DatNhanvienModel> lists = new ArrayList<>();
        try (Connection con = DBConnect.getInstance().getConnect();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DatNhanvienModel  datNhanvienModel=new DatNhanvienModel();
                        datNhanvienModel.setId(rs.getInt(1));
                        datNhanvienModel.setTenNV(rs.getString(2));
                        
                        lists.add(datNhanvienModel);
             
            }
        } catch (Exception e) {
            e.printStackTrace(System.out); // nem loi khi xay ra 
        }
        return lists;
    }
       
}
