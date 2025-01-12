package com.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author InuHa
 */
public class ValidateUtils {

    public static final int MIN_LENGTH_USERNAME = 2;

    public static final int MAX_LENGTH_USERNAME = 25;

    public static final int MIN_LENGTH_PASSWORD = 3;

    public static final int MAX_LENGTH_PASSWORD = 50;


    /**
     * Kiểm tra email có hợp lệ hay không
     * @param email chuỗi muốn kiểm tra
     * @return true nếu hợp lệ và false nếu không hợp lệ
     */
    public static boolean isEmail(String email) {
        return Pattern.compile("[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                .matcher(email)
                .matches();
    }

    /**
     * Kiểm tra username có hợp lệ hay không
     * @param username chuỗi muốn kiểm tra
     * @return true nếu hợp lệ và false nếu không hợp lệ
     */
    public static boolean isUsername(String username) {
        username = username.trim();

        if (username.length() < MIN_LENGTH_USERNAME || username.length() > MAX_LENGTH_USERNAME) {
            return false;
        }

        return Pattern.compile("^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")
                .matcher(username)
                .matches();
    }

    /**
     * Kiểm tra định dạng password có hợp lệ hay không
     * @param password chuỗi muốn kiểm tra
     * @return true nếu hợp lệ và false nếu không hợp lệ
     */
    public static boolean isPassword(String password) {
        password = password.trim();
        return password.length() >= MIN_LENGTH_PASSWORD && password.length() <= MAX_LENGTH_PASSWORD;
    }

    /**
     * Kiểm tra chuỗi có toàn chữ số hay không
     * @param str chuỗi muốn kiểm tra
     * @return trả về true nếu chuỗi gồm toàn số và false nếu có chứa ký tự không phải là số
     */
    public static boolean isNumber(String str) {
        return Pattern.compile("^[0-9]+$")
                .matcher(str)
                .matches();
    }
    
    /**
     * Kiểm tra chuỗi có phải họ tên hay không
     * @param str chuỗi muốn kiểm tra
     * @return trả về true nếu đúng và false nếu sai
     */
    public static boolean isFullName(String str) {
        String regex = "^[\\p{L} ]+$";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    /**
     * Kiểm tra chuỗi có chứa ký tự đặc biệt hay không (trừ &, -, _)
     * @param str chuỗi muốn kiểm tra
     * @return trả về true nếu chuỗi chứ ký tự đặc biệt và false không chứa ký tự đặc biệt
     */
    public static boolean isSpecialCharacters(String str) {
        String regex = "^[\\p{L}\\p{N} _&-:]+$";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return !matcher.matches();
    }

    public static boolean isCodeVoucher(String str) {
        String regex = "^[a-zA-Z0-9_-]+$";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
