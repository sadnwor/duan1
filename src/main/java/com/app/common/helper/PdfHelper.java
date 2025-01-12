package com.app.common.helper;


import com.app.Application;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.common.models.invoice.InvoiceAddressDetails;
import com.app.core.common.models.invoice.InvoiceDataModel;
import com.app.core.common.models.invoice.InvoiceHeaderDetails;
import com.app.core.common.models.invoice.InvoiceProductTableHeader;
import com.app.core.common.services.InvoiceCreatorPdfService;
import com.app.utils.QrCodeUtils;
import com.app.utils.TimeUtils;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import jnafilechooser.api.JnaFileChooser;


/**
 *
 * @author inuHa
 */
public class PdfHelper {

    public static File selectFolder() {
	JnaFileChooser ch = new JnaFileChooser();
	ch.setMode(JnaFileChooser.Mode.Directories);
	boolean act = ch.showOpenDialog(Application.app);
	if (act) {
	    File folder = ch.getSelectedFile();
	    return folder;
	}
	return null;
    }
    
    public static File createInvoicePDF(InvoiceDataModel data, File path) {
	String fileName = data.getMaHoaDon() + "-" + TimeUtils.now("dd_MM_yyyy__hh_mm_a") + ".pdf";
	File pathFile = new File(path, fileName);
	try {
	    
	    InvoiceCreatorPdfService cepdf = new InvoiceCreatorPdfService(pathFile.getAbsolutePath());
	    
	    cepdf.createDocument();
	    
	    
	    InvoiceHeaderDetails header=new InvoiceHeaderDetails();
	    header.setInvoiceNo(data.getMaHoaDon()).setInvoiceDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).build();
	    cepdf.createHeader(header);
	    
	    InvoiceAddressDetails addressDetails=new InvoiceAddressDetails();
	    addressDetails
		    .setBillingStore(System.getProperty("APP_NAME"))
		    .setBillingUser(data.getTaiKhoan())
		    .setCustomerName(data.getTenKhachHang())
		    .setCustomerPhone(data.getSoDienThoai())
		    .build();
	    
	    cepdf.createAddress(addressDetails);
	    
	    InvoiceProductTableHeader productTableHeader=new InvoiceProductTableHeader();
	    
	    cepdf.createTableHeader(productTableHeader);
	    

	    cepdf.createProduct(data);
	    cepdf.createQrCode(QrCodeHelper.getImage(QrCodeUtils.generateCodeHoaDon(data.getId())));
	    cepdf.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    throw new ServiceResponseException("Không thể xuất hoá đơn");
	}
	return pathFile;
    }
    
    public static void openFile(File path) {
        try {

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();

                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(path);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
