package com.app.core.inuha.services.impl;

import com.app.common.infrastructure.interfaces.IServiceInterface;
import com.app.core.inuha.models.InuhaHoaDonChiTietModel;

/**
 *
 * @author inuHa
 */
public interface IInuhaHoaDonChiTietServiceInterface extends IServiceInterface<InuhaHoaDonChiTietModel, Integer> {

    String getLastId();
    
}
