package com.app.common.infrastructure.session;

import com.app.Application;
import com.app.common.controller.ApplicationController;
import com.app.common.helper.MessageModal;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.services.InuhaTaiKhoanService;
import com.app.core.inuha.views.all.InuhaChangePasswordView;
import com.app.core.inuha.views.guest.LoginView;
import com.app.utils.QrCodeUtils;
import com.app.views.UI.dialog.LoadingDialog;
import java.util.concurrent.ExecutionException;
import lombok.Getter;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

/**
 *
 * @author InuHa
 */
public class SessionLogin {

    private static SessionLogin instance;

    private final InuhaTaiKhoanService nhanVienService = InuhaTaiKhoanService.getInstance();

    private String username = null;

    private String password = null;

    @Getter
    private InuhaTaiKhoanModel data = null;

    private SessionLogin() {
    }

    public static SessionLogin getInstance() {
        if (instance == null) {
            instance = new SessionLogin();
        }
        return instance;
    }

    public boolean isLogin() {
        return username != null && password != null;
    }

    public SessionLogin validSession() {
        if (!isLogin()) {
            return this;
        }

        try {
            this.data = nhanVienService.login(username, password);
        } catch (ServiceResponseException e) {
            clear();
            MessageModal.closeAll();
            MessageModal.warning("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại");
            ApplicationController.getInstance().show(new LoginView());
        }
        return this;
    }

    public void create(String username, String password, InuhaTaiKhoanModel data) {
        this.username = username;
        this.password = password;
        this.data = data;
    }

    public void clear() {
        this.username = null;
        this.password = null;
        this.data = null;
    }

    public void logout() {
	SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
	    @Override
	    protected Boolean doInBackground() {
		return MessageModal.confirmInfo("Bạn thực sự muốn đăng xuất?");
	    }

	    @Override
	    protected void done() {
		try {
		    if (get()) {
			LoadingDialog loading = new LoadingDialog();
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.submit(() -> {
			    clear();
			    ApplicationController.getInstance().show(new LoginView());
			    loading.dispose();
			    executorService.shutdown();
			});
			loading.setVisible(true);
		    }
		} catch (InterruptedException ex) {
		} catch (ExecutionException ex) {
		}
	    }
	};
	worker.execute();
		


    }

    public void changePassword() {
        if (ModalDialog.isIdExist("THAY_DOI_MAT_KHAU")) {
            return;
        }
        ModalDialog.showModal(Application.app, new SimpleModalBorder(new InuhaChangePasswordView(), "Thay đổi mật khẩu"), "THAY_DOI_MAT_KHAU");
    }
}
