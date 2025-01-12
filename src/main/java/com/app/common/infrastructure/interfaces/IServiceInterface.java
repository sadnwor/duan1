/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.common.infrastructure.interfaces;

import com.app.common.infrastructure.request.FilterRequest;

import java.util.List;
import java.util.Set;

/**
 *
 * @author inuHa
 */
public interface IServiceInterface<T, K> {

    T getById(K id);
    
    Integer insert(T model);

    boolean has(K id);

    void update(T model);
    
    void delete(K id);
    
    void deleteAll(List<K> ids);

    List<T> getAll();

    List<T> getPage(FilterRequest request);

    Integer getTotalPage(FilterRequest request);

}
