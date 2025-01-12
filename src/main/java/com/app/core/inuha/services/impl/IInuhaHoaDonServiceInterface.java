package com.app.core.inuha.services.impl;

import com.app.common.infrastructure.interfaces.IServiceInterface;
import com.app.core.inuha.models.InuhaHoaDonModel;

/**
 *
 * @author inuHa
 */
public interface IInuhaHoaDonServiceInterface extends IServiceInterface<InuhaHoaDonModel, Integer> {

    String getLastId();
    
}
