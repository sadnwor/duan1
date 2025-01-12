/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.models;

/**
 *
 * @author WIN
 */
public class DatNhanvienModel {
    private int id;
    
    private String tenNV;

    public DatNhanvienModel() {
    }

    public DatNhanvienModel(int id, String tenNV) {
        this.id = id;
        this.tenNV = tenNV;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    @Override
    public String toString() {
        return tenNV.toString();
    }
    
    
    
    
}
