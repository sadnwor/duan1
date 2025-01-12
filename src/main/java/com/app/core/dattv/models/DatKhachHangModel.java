/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.models;

/**
 *
 * @author WIN
 */
public class DatKhachHangModel {
    private int id;
    
    private String tenKhachHang;

    public DatKhachHangModel() {
    }

    public DatKhachHangModel(int id, String tenKhachHang) {
        this.id = id;
        this.tenKhachHang = tenKhachHang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    

    @Override
    public String toString() {
        return tenKhachHang.toString();
    }
    
    
}
