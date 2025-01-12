package com.app.core.inuha.services.impl;

import com.app.common.infrastructure.interfaces.IServiceInterface;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author inuHa
 */
public interface IInuhaSanPhamChiTietServiceInterface extends IServiceInterface<InuhaSanPhamChiTietModel, Integer> {

    String getLastId();
    
}
