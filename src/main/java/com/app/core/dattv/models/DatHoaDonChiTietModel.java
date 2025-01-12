/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.models;


/**
 *
 * @author WIN
 */
public class DatHoaDonChiTietModel {
    private int id;
    private int idSanPhamChiTiet;
    private String ma;
    private String ten;
    private String kichCo;
    private String mauSac;
    private double giaBan;
    private int soLuong;

    public DatHoaDonChiTietModel() {
    }

    public DatHoaDonChiTietModel(int id, int idSanPhamChiTiet, String ma, String ten, String kichCo, String mauSac, double giaBan, int soLuong) {
        this.id = id;
        this.idSanPhamChiTiet = idSanPhamChiTiet;
        this.ma = ma;
        this.ten = ten;
        this.kichCo = kichCo;
        this.mauSac = mauSac;
        this.giaBan = giaBan;
        this.soLuong = soLuong;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSanPhamChiTiet() {
        return idSanPhamChiTiet;
    }

    public void setIdSanPhamChiTiet(int idSanPhamChiTiet) {
        this.idSanPhamChiTiet = idSanPhamChiTiet;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getKichCo() {
        return kichCo;
    }

    public void setKichCo(String kichCo) {
        this.kichCo = kichCo;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    
    
    
}
