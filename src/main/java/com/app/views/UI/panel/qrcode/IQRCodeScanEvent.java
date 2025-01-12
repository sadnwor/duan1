package com.app.views.UI.panel.qrcode;

import com.google.zxing.Result;

/**
 *
 * @author InuHa
 */
@FunctionalInterface
public interface IQRCodeScanEvent {
    
    void onScanning(Result result);
    
}
