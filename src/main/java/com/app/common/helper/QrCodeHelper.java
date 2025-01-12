package com.app.common.helper;

import com.app.Application;
import com.app.utils.QrCodeUtils;
import com.app.views.UI.panel.qrcode.IQRCodeScanEvent;
import com.app.views.UI.panel.qrcode.WebcamQRCodeScanPanel;
import static com.app.views.UI.panel.qrcode.WebcamQRCodeScanPanel.playSound;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import jnafilechooser.api.JnaFileChooser;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

/**
 *
 * @author InuHa
 */
public class QrCodeHelper {
    
    public static void showWebcam(IQRCodeScanEvent event) { 
        showWebcam(null, event);
    }
     
    public static void showWebcam(String title, IQRCodeScanEvent event) { 
        if (ModalDialog.isIdExist(QrCodeUtils.MODAL_SCAN_ID)) {
            return;
        }
        
        IQRCodeScanEvent callback = (Result result) -> {
            event.onScanning(result);
            closeWebcam();
        };

        ModalDialog.showModal(Application.app, new SimpleModalBorder(WebcamQRCodeScanPanel.getInstance(callback), title), QrCodeUtils.MODAL_SCAN_ID);
    }
    
    public static void initWebcam(JPanel panel, Dimension size, IQRCodeScanEvent event) { 
	panel.setLayout(new MigLayout("fill", String.format("[center, %s:%s]", size.getWidth(), size.getWidth()), String.format("[center, %s:%s]", size.getHeight(), size.getHeight())));
	ExecutorService executorService = Executors.newSingleThreadExecutor();
	executorService.submit(() -> { 
	    panel.add(WebcamQRCodeScanPanel.initPanel(event));
	    panel.revalidate();
	    panel.repaint();
	    executorService.shutdown();
	});

    }
	
    public static void closeWebcam() { 
        if (ModalDialog.isIdExist(QrCodeUtils.MODAL_SCAN_ID)) {
            ModalDialog.closeModal(QrCodeUtils.MODAL_SCAN_ID);
        }
        WebcamQRCodeScanPanel.dispose();
    }
    
    public static void save(String code, String fileName) {
        JnaFileChooser ch = new JnaFileChooser();
        ch.setMode(JnaFileChooser.Mode.Directories);
        boolean act = ch.showOpenDialog(Application.app);
        if (act) {
            File folder = ch.getSelectedFile();
            File file = new File(folder, fileName + "." + QrCodeUtils.IMAGE_FORMAT.toLowerCase());
            try {
                QrCodeUtils.generateQRCodeImage(code, file);
                MessageToast.success("Lưu QR Code thành công!");
            } catch (WriterException | IOException e) {
                e.printStackTrace();
                MessageToast.error("Không thể lưu QR Code!!!!");
            }
        }
    }
   
    
    public static ImageIcon getImage(String code) {
	try {
	    return QrCodeUtils.generateQRCodeImage(code);
	} catch (WriterException e) {
	    e.printStackTrace();
	}
	return null;
    }
    
}
