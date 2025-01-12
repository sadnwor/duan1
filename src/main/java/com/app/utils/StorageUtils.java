package com.app.utils;

import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author InuHa
 */
public class StorageUtils {

    public static final String FOLDER = "storages";

    public static final String FOLDER_AVATAR = "avatars";

    public static final String FOLDER_PRODUCT = "products";

    public static boolean checkDir(String path, boolean autoCreateIfNotExists) {
        File dir = new File(path);
        boolean resutl = dir.isDirectory();

        if (!resutl && autoCreateIfNotExists) {
            dir.mkdirs();
        }

        return resutl;
    }

    private static void initFolder() {
        checkDir(FOLDER, true);
        checkDir(FOLDER + "/" + FOLDER_AVATAR, true);
        checkDir(FOLDER + "/" + FOLDER_PRODUCT, true);
    }

    /**
     * Lấy ra data hình ảnh trong thư mục assets
     * @param path đường dẫn tệp tin (VD: avatar/logo.png)
     * @return javax.swing.ImageIcon
     */
    public static ImageIcon getImage(String path) throws NullPointerException {
        File file = new File(FOLDER + "/" + path);

        try {
            BufferedImage image = ImageIO.read(file);
            return new ImageIcon(image);
        } catch (IOException e) {
            throw new NullPointerException("Không tìm thấy hình ảnh: " + FOLDER + "/" + path);
        }
    }
    
    public static boolean deleteFile(String filePath) { 
        File file = new File(filePath);
	if (!file.exists()) {
	    return false;
	}
        return file.delete();
    }

    public static String uploadImage(ImageIcon imageIcon, String path, String fileName) {
        initFolder();

        path = path.replaceAll("^/+", "").replaceAll("/+$", "");

        try {
            Image image = imageIcon.getImage();

            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();

            String imageType = getImageFormat(bufferedImage);
            fileName += "." + imageType;

            File outputFile = new File(FOLDER + "/" + path + "/" + fileName);
            ImageIO.write(bufferedImage, imageType, outputFile);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String uploadFile(File file, String path, String fileName) {
        initFolder();

        path = path.replaceAll("^/+", "").replaceAll("/+$", "");

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(FOLDER + "/" + path + "/" + fileName)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String uploadAvatar(ImageIcon imageIcon, String fileName) {
        return uploadImage(imageIcon, FOLDER_AVATAR, fileName);
    }

    public static String uploadProduct(ImageIcon imageIcon, String fileName) {
        return uploadImage(imageIcon, FOLDER_PRODUCT, fileName);
    }

    public static ImageIcon getAvatar(String fileName) {
        return getImage(FOLDER_AVATAR + "/" + fileName);
    }

    public static ImageIcon getProduct(String fileName) {
        return getImage(FOLDER_PRODUCT + "/" + fileName);
    }
    
    public static String getUrlImageAvatar(String fileName) { 
        return FOLDER + "/" + FOLDER_AVATAR + "/" + fileName;
    }
	
    public static String getUrlImageProduct(String fileName) { 
        return FOLDER + "/" + FOLDER_PRODUCT + "/" + fileName;
    }

    private static String getImageFormat(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        byte[] header = new byte[8];
        inputStream.read(header, 0, 8);
        String formatName;
        if (header[0] == (byte) 'G' && header[1] == (byte) 'I' && header[2] == (byte) 'F') {
            formatName = "gif";
        } else if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
            formatName = "jpg";
        } else if (header[0] == (byte) 0x89 && header[1] == (byte) 'P' && header[2] == (byte) 'N' && header[3] == (byte) 'G') {
            formatName = "png";
        } else {
            throw new IOException("Không thể nhận diện định dạng hình ảnh.");
        }
        return formatName;
    }

}
