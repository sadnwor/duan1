package com.app.utils;

import com.app.common.helper.MailerHelper;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.common.infrastructure.session.AvatarUpload;
import com.app.common.infrastructure.session.SessionLogin;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.services.InuhaTaiKhoanService;
import static com.app.utils.ProductUtils.getUrlImageProduct;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 *
 * @author inuHa
 */
public class SessionUtils {

    private static final String AVATAR_NAME_FORMAT = "user_%d";

    public static final int MAX_WIDTH_AVATAR_UPLOAD = 184;

    public static final int MAX_HEIGHT_AVATAR_UPLOAD = 184;

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    private static final String DIGITS = "0123456789";
    
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:',.<>?/";

    private static final String ALL_CHARS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARS;

    private static final SecureRandom random = new SecureRandom();

    public static int getInsertId() {
        int lastId = InuhaTaiKhoanService.getInstance().getLastId();
        if (lastId == 1) { 
            if (InuhaTaiKhoanService.getInstance().count(new FilterRequest()) < 1) {
                lastId--;
            }
        }
        return ++lastId;
    }
    
    public static String generatePassword(int length) {
        if (length < ValidateUtils.MIN_LENGTH_PASSWORD) {
            throw new IllegalArgumentException("Mật khẩu phải lớn hơn hoặc bằng " + ValidateUtils.MIN_LENGTH_PASSWORD + " ký tự");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALL_CHARS.length());
            password.append(ALL_CHARS.charAt(index));
        }

        return password.toString();
    }
    
    public static String generateCode(int min, int max) {
        return Integer.toString((int) ((Math.random() * (max - min)) + min));
    }

    public static boolean sendOtp(String otp, String email) {
        String htmlContent = "";
        try {
            InputStream inputStream = ResourceUtils.getDataFile("templates/mail/forgot-password.html");
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                htmlContent = stringBuilder.toString();
                inputStream.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        htmlContent = htmlContent.replaceAll("\\{\\{CODE\\}\\}", otp);

        MailerHelper mailerHelper = new MailerHelper();

        return mailerHelper.send(email, "Quên mật khẩu", htmlContent);
    }

    public static boolean sendPassword(String password, String email) {
        String htmlContent = "";
        try {
            InputStream inputStream = ResourceUtils.getDataFile("templates/mail/send-password.html");
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                htmlContent = stringBuilder.toString();
                inputStream.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        htmlContent = htmlContent.replaceAll("\\{\\{PASSWORD\\}\\}", password);

        MailerHelper mailerHelper = new MailerHelper();

        return mailerHelper.send(email, "Thông tin tài khoản mới", htmlContent);
    }
	
    public static AvatarUpload uploadAvatar(InuhaTaiKhoanModel user, String pathImage) {
        ImageIcon resizeImage = ComponentUtils.resizeImage(new ImageIcon(pathImage), MAX_WIDTH_AVATAR_UPLOAD, MAX_HEIGHT_AVATAR_UPLOAD);
        String fileName = StorageUtils.uploadAvatar(resizeImage, String.format(AVATAR_NAME_FORMAT, user.getId()));
        return new AvatarUpload(resizeImage, fileName);
    }

    public static ImageIcon getAvatar(InuhaTaiKhoanModel user) {
	if (user == null) {
	    return null;
	}
        String avatar = user.getAvatar();

        if (avatar != null && !avatar.trim().isEmpty()) {
            try {
                return StorageUtils.getAvatar(user.getAvatar());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResourceUtils.getImageAssets("images/noavatar.png");
    }

    public static String getUrlImageAvatar(String image) { 
        return StorageUtils.getUrlImageAvatar(image);
    }
	
    
    public static String getGioiTinh(boolean gioiTinh) { 
        return gioiTinh ? "Nam" : "Nữ";
    }
    
    public static String getChucVu(boolean adm) { 
        return adm ? "Quản lý" : "Nhân viên";
    }
	
    public static String getTrangThai(boolean trangThai) { 
        return trangThai ? "Hoạt động" : "Nghỉ việc";
    }
	
    public static boolean isManager() {
	InuhaTaiKhoanModel data = SessionLogin.getInstance().getData();
        return data != null && data.isAdmin();
    }

    public static boolean isStaff() {
        return !isManager();
    }

    public static boolean removeImageAvatar(String image) { 
        return StorageUtils.deleteFile(getUrlImageAvatar(image));
    }
    
}
