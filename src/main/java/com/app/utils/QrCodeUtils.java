package com.app.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;


/**
 *
 * @author InuHa
 */
public class QrCodeUtils {
    
    public final static String MODAL_SCAN_ID = "modal-webcam-scan";
    
    public final static String PREFIX_CODE = "I-SHOES";
        
    public final static String SEPERATOR = "|";
    
    public final static String TYPE_SANPHAM = "product";
    
    public final static String TYPE_HOADON = "bill";
    
    public final static String TYPE_SANPHAMCHITIET = "productdetail";
    
    private static final int WIDTH = 600;
    
    private static final int HEIGHT = 600;
    
    public static final String IMAGE_FORMAT = "PNG";
    
    
    public static String generateCodeSanPham(int id) { 
        return PREFIX_CODE + SEPERATOR + TYPE_SANPHAM + SEPERATOR + id;
    }

    public static String generateCodeSanPhamChiTiet(int id) { 
        return PREFIX_CODE + SEPERATOR + TYPE_SANPHAMCHITIET + SEPERATOR + id;
    }
    
    public static String generateCodeHoaDon(int id) { 
        return PREFIX_CODE + SEPERATOR + TYPE_HOADON + SEPERATOR + id;
    }
	
    public static int getIdSanPham(String code) { 
        String regex = String.format("^%s\\%s%s\\%s(\\d+)$", PREFIX_CODE, SEPERATOR, TYPE_SANPHAM, SEPERATOR);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        if (matcher.matches()) { 
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
        
    public static int getIdSanPhamChiTiet(String code) { 
        String regex = String.format("^%s\\%s%s\\%s(\\d+)$", PREFIX_CODE, SEPERATOR, TYPE_SANPHAMCHITIET, SEPERATOR);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        if (matcher.matches()) { 
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
     
    public static int getIdHoaDon(String code) { 
        String regex = String.format("^%s\\%s%s\\%s(\\d+)$", PREFIX_CODE, SEPERATOR, TYPE_HOADON, SEPERATOR);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        if (matcher.matches()) { 
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
	
    public static String[] getDataCanCuocCongDan(String code) { 
        String regex = "^\\d{12}\\|\\d{9}\\|([\\p{L}\\s]+)\\|\\d{8}\\|(Nam|Nữ)\\|([\\p{L}\\s,]+)\\|\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        if (matcher.matches()) { 
            String[] result = new String[matcher.groupCount()];
            for (int i = 1; i <= matcher.groupCount(); i++) {
                result[i - 1] = matcher.group(i);
            }
            return result;
        }
        return null;
    }
        
    public static void generateQRCodeImage(String text, File file) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
        MatrixToImageWriter.writeToPath(bitMatrix, IMAGE_FORMAT, file.toPath());
    }
    
    public static ImageIcon generateQRCodeImage(String text) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);

        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return new ImageIcon(bufferedImage);
    }
	
}
