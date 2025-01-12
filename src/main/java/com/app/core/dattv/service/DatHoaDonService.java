/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.core.dattv.service;


import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.dattv.repositoris.DatHoaDonRepository;
import com.app.core.dattv.request.DatHoaDonRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author WIN
 */
public class DatHoaDonService {
    
    private final DatHoaDonRepository repository = DatHoaDonRepository.getInstance();
    
    private static DatHoaDonService instance = null;
    
    public static DatHoaDonService getInstance() { 
	if (instance == null) { 
	    instance = new DatHoaDonService();
	}
	return instance;
    }


    public DatHoaDonRequest getById(Integer id) {
        try {
            Optional<DatHoaDonRequest> find = repository.getById(id);
            if (find.isEmpty()) { 
                throw new SQLException();
            }
            return find.get();
        } catch (SQLException ex) {
            throw new ServiceResponseException("Không tìm thấy sản phẩm");
        }
    }
    
}
