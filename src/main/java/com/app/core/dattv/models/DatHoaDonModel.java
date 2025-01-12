/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.models;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import lombok.Builder;

/**
 *
 * @author WIN
 */
@Builder
public class DatHoaDonModel {
   
        private int id;
        private int stt;
        private int id_tai_khoan;
        private int id_khach_hang;
        private int id_phieu_giam_gia;
        private String maHd;
        private int tienGiam;
        private boolean phuong_thuc_thanh_toan;
        private int trangThai;
        private String ngayTao;
        private String ngayCapnhat;
        private boolean trangThaixoa;

    public DatHoaDonModel() {
    }

    public DatHoaDonModel(int id, int stt, int id_tai_khoan, int id_khach_hang, int id_phieu_giam_gia, String maHd, int tienGiam, boolean phuong_thuc_thanh_toan, int trangThai, String ngayTao, String ngayCapnhat, boolean trangThaixoa) {
        this.id = id;
        this.stt = stt;
        this.id_tai_khoan = id_tai_khoan;
        this.id_khach_hang = id_khach_hang;
        this.id_phieu_giam_gia = id_phieu_giam_gia;
        this.maHd = maHd;
        this.tienGiam = tienGiam;
        this.phuong_thuc_thanh_toan = phuong_thuc_thanh_toan;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngayCapnhat = ngayCapnhat;
        this.trangThaixoa = trangThaixoa;
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

    public int getId_tai_khoan() {
        return id_tai_khoan;
    }

    public void setId_tai_khoan(int id_tai_khoan) {
        this.id_tai_khoan = id_tai_khoan;
    }

    public int getId_khach_hang() {
        return id_khach_hang;
    }

    public void setId_khach_hang(int id_khach_hang) {
        this.id_khach_hang = id_khach_hang;
    }

    public int getId_phieu_giam_gia() {
        return id_phieu_giam_gia;
    }

    public void setId_phieu_giam_gia(int id_phieu_giam_gia) {
        this.id_phieu_giam_gia = id_phieu_giam_gia;
    }

    public String getMaHd() {
        return maHd;
    }

    public void setMaHd(String maHd) {
        this.maHd = maHd;
    }

    public int getTienGiam() {
        return tienGiam;
    }

    public void setTienGiam(int tienGiam) {
        this.tienGiam = tienGiam;
    }

    public boolean isPhuong_thuc_thanh_toan() {
        return phuong_thuc_thanh_toan;
    }

    public void setPhuong_thuc_thanh_toan(boolean phuong_thuc_thanh_toan) {
        this.phuong_thuc_thanh_toan = phuong_thuc_thanh_toan;
    }

   

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getNgayCapnhat() {
        return ngayCapnhat;
    }

    public void setNgayCapnhat(String ngayCapnhat) {
        this.ngayCapnhat = ngayCapnhat;
    }

    public boolean isTrangThaixoa() {
        return trangThaixoa;
    }

    public void setTrangThaixoa(boolean trangThaixoa) {
        this.trangThaixoa = trangThaixoa;
    }

    
        
        
}
