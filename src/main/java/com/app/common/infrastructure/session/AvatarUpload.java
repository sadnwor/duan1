package com.app.common.infrastructure.session;

import javax.swing.*;

/**
 *
 * @author InuHa
 */
public class AvatarUpload {

    private ImageIcon dataImage;

    private String fileName;

    public AvatarUpload() {
    }

    public AvatarUpload(ImageIcon dataImage, String fileName) {
        this.dataImage = dataImage;
        this.fileName = fileName;
    }

    public ImageIcon getDataImage() {
        return dataImage;
    }

    public void setDataImage(ImageIcon dataImage) {
        this.dataImage = dataImage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
