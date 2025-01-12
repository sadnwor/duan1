/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.lam.models;

import com.app.utils.TimeUtils;
import java.time.LocalDateTime;


/**
 *
 * @author Admin
 */

public class LamKhachHangModels {

    private int idKH;
    
    private int stt;

    private String tenKH;

    private String soDienThoai;

    private boolean gioiTinh;

    private String diaChi;
    
    private int soLanMuaHang;
    
    private String ngayTao;

    private boolean trangThaiXoa;

    public LamKhachHangModels() {
    }

    public LamKhachHangModels(int idKH, String tenKH, String soDienThoai, boolean gioiTinh, String diaChi, boolean trangThaiXoa) {
        this.idKH = idKH;
        this.tenKH = tenKH;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.ngayTao = TimeUtils.currentDateTime();
        this.trangThaiXoa = trangThaiXoa;
    }

    public int getIdKH() {
        return idKH;
    }

    public void setIdKH(int idKH) {
        this.idKH = idKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public boolean isTrangThaiXoa() {
        return trangThaiXoa;
    }

    public void setTrangThaiXoa(boolean trangThaiXoa) {
        this.trangThaiXoa = trangThaiXoa;
    }

    public int getSoLanMuaHang() {
        return soLanMuaHang;
    }

    public void setSoLanMuaHang(int soLanMuaHang) {
        this.soLanMuaHang = soLanMuaHang;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    
    
    @Override
    public String toString() {
        return "KhachHangModels{" + "idKH=" + idKH + ", tenKH=" + tenKH + ", soDienThoai=" + soDienThoai + ", gioiTinh=" + gioiTinh + ", diaChi=" + diaChi + ", ngayTao=" + ngayTao + ", trangThaiXoa=" + trangThaiXoa + '}';
    }
    
}
