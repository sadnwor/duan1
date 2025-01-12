/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.core.inuha.services.impl;

import com.app.common.infrastructure.interfaces.IServiceInterface;
import com.app.core.inuha.models.InuhaTaiKhoanModel;

/**
 *
 * @author inuHa
 */

public interface IInuhaNhanVienServiceInterface extends IServiceInterface<InuhaTaiKhoanModel, Integer> {

    InuhaTaiKhoanModel login(String username, String password);

    void requestForgotPassword(String email);

    void validOtp(String email, String otp);

    void changePassword(String email, String otp, String password, String confirmPassword);

    void changeAvatar(String avatar);

    void changePassword(String oldPassword, String newPassword, String confirmPassword);

}