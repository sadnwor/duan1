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
public class LamLichSuModels {

    private int id;
    
    private int stt;

    private String maHD;

    private Double tongTien;

    private String ngayMua;
    
    private int trangThai;

    public LamLichSuModels() {
    }

    
    
    public LamLichSuModels(int id, String maHD, Double tongTien, String ngayMua, int trangThai) {
        this.id = id;
        this.maHD = maHD;
        this.tongTien = tongTien;
        this.ngayMua = ngayMua;
        this.trangThai = trangThai;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public String getNgayMua() {
        return ngayMua;
    }

    public void setNgayMua(String ngayMua) {
        this.ngayMua = ngayMua;
    }

    public int isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    

}
