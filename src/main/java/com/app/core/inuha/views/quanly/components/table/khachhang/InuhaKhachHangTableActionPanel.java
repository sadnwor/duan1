package com.app.core.inuha.views.quanly.components.table.khachhang;

import com.app.utils.SessionUtils;
import com.app.views.UI.table.TableActionPanel;

/**
 *
 * @author InuHa
 */
public class InuhaKhachHangTableActionPanel extends TableActionPanel {
    
    public InuhaKhachHangTableActionPanel() { 
        super();
        hideView();
	if (SessionUtils.isStaff()) { 
	    hideDelete();
	}
    }
    
}
