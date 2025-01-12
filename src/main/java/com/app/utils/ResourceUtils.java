/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.plaf.ColorUIResource;

/**
 *
 * @author inuHa
 */
public class ResourceUtils {

    public static final String DIR_ASSETS = "assets";

    /**
     * Lấy ra tệp tin trong thư mục resources
     * @param path địa chỉ tệp trong thư mục resources
     * @return trả về InputStream của tệp
     * @throws NullPointerException nếu path null
     */
    public static InputStream getDataFile(String path) throws NullPointerException {
        return ResourceUtils.class.getClassLoader().getResourceAsStream(path);
    }

    /**
     * Lấy ra địa chỉ URL của tệp tin trong thư mục resources
     * @param path đường dẫn tệp tin (VD: common/images/logo.png)
     * @return java.net.URL
     */
    public static URL getUrlFile(String path) {
        return getUrlFile(path, true);
    }

    public static URL getUrlFile(String path, boolean isJar) {
	path = path.replaceAll("^/+", "").replaceAll("/+$", "");
	
	if (!isJar) { 
	    
	    URL url = null;
	    try {
		url = ResourceUtils.class.getResource("/" + path);
	    } catch (NullPointerException e) {
		e.printStackTrace();
	    }
	    return url;
	}
	
        URL url = null;
        try {
            File jarFile = new File(ResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            
            if (jarFile.isFile()) {
                File resourceFile = new File(jarFile.getParentFile(), path);
                url = resourceFile.toURI().toURL();
            } else {
                url = ResourceUtils.class.getClassLoader().getResource(path);
            }
        } catch (URISyntaxException | NullPointerException | java.net.MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    
    /**
     * Lấy ra data hình ảnh trong thư mục resources
     * @param path đường dẫn tệp tin (VD: common/images/logo.png)
     * @return javax.swing.ImageIcon
     */
    public static ImageIcon getImage(String path) throws NullPointerException {
        try {
            return new ImageIcon(getUrlFile(path));
        } catch (NullPointerException e) {
            throw new NullPointerException("Không tìm thấy hình ảnh: " + path);
        }
    }

    /**
     * Lấy ra data hình ảnh trong thư mục resources/assets
     * @param path đường dẫn tệp tin (VD: images/logo.png)
     * @return javax.swing.ImageIcon
     */
    public static ImageIcon getImageAssets(String path) {
        return new ImageIcon(getUrlFile(DIR_ASSETS + "/" + path, false));
    }

    /**
     * Lấy ra data svg trong thư mục resources/assets
     * @param path đường dẫn tệp tin (VD: svg/logo.svg)
     * @return com.formdev.flatlaf.extras.FlatSVGIcon
     */
    public static FlatSVGIcon getSVG(String path) {
        return getSVG(path, null);
    }

    /**
     * Lấy ra data svg trong thư mục resources/assets
     * @param path đường dẫn tệp tin (VD: svg/logo.svg)
     * @param size tuỳ chỉnh kích thước
     * @return com.formdev.flatlaf.extras.FlatSVGIcon
     */
    public static FlatSVGIcon getSVG(String path, Dimension size) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        path = path.replaceAll("^/+", "").replaceAll("/+$", "");
        try {
            path = "/" + DIR_ASSETS + "/" + path;
            if (size != null) {
                return new FlatSVGIcon(path.replaceAll("^/+", ""), (int) size.getWidth(), (int) size.getHeight());
            }
            return new FlatSVGIcon(ResourceUtils.class.getResourceAsStream(path));
        } catch (IOException e) {
            return null;
        }
    }

}
