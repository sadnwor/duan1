package com.app.core.inuha.services.impl;

import com.app.common.infrastructure.interfaces.IServiceInterface;
import com.app.core.inuha.models.InuhaSanPhamModel;

/**
 *
 * @author inuHa
 */
public interface IInuhaSanPhamServiceInterface extends IServiceInterface<InuhaSanPhamModel, Integer> {

    String getLastId();
    
}
