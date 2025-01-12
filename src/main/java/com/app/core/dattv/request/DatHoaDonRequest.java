/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.request;

import java.util.Date;



/**
 *
 * @author WIN
 */

public class DatHoaDonRequest {

    private int stt;
    private int id;
    private String maHd;
    private String thoiGian;
    private String khachHang;
    private double tongTienhang;
    private double giamGia;
    private double thanhTien;
    private double tienMat;
    private double tienChuyenKhoan;
    private int trangThai;
    private int phuongThucTT;
    private boolean trangThaixoa;
    private String tenNv;
    private String sdt;

    public DatHoaDonRequest() {
    }

    public DatHoaDonRequest(int stt, int id, String maHd, String thoiGian, String khachHang, double tongTienhang, double giamGia, double thanhTien, double tienMat, double tienChuyenKhoan, int trangThai, int phuongThucTT, boolean trangThaixoa, String tenNv, String sdt) {
        this.stt = stt;
        this.id = id;
        this.maHd = maHd;
        this.thoiGian = thoiGian;
        this.khachHang = khachHang;
        this.tongTienhang = tongTienhang;
        this.giamGia = giamGia;
        this.thanhTien = thanhTien;
        this.tienMat = tienMat;
        this.tienChuyenKhoan = tienChuyenKhoan;
        this.trangThai = trangThai;
        this.phuongThucTT = phuongThucTT;
        this.trangThaixoa = trangThaixoa;
        this.tenNv = tenNv;
        this.sdt = sdt;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaHd() {
        return maHd;
    }

    public void setMaHd(String maHd) {
        this.maHd = maHd;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(String khachHang) {
        this.khachHang = khachHang;
    }

    public double getTongTienhang() {
        return tongTienhang;
    }

    public void setTongTienhang(double tongTienhang) {
        this.tongTienhang = tongTienhang;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public double getTienMat() {
        return tienMat;
    }

    public void setTienMat(double tienMat) {
        this.tienMat = tienMat;
    }

    public double getTienChuyenKhoan() {
        return tienChuyenKhoan;
    }

    public void setTienChuyenKhoan(double tienChuyenKhoan) {
        this.tienChuyenKhoan = tienChuyenKhoan;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public int getPhuongThucTT() {
        return phuongThucTT;
    }

    public void setPhuongThucTT(int phuongThucTT) {
        this.phuongThucTT = phuongThucTT;
    }

    public boolean isTrangThaixoa() {
        return trangThaixoa;
    }

    public void setTrangThaixoa(boolean trangThaixoa) {
        this.trangThaixoa = trangThaixoa;
    }

    public String getTenNv() {
        return tenNv;
    }

    public void setTenNv(String tenNv) {
        this.tenNv = tenNv;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

   
    
}
