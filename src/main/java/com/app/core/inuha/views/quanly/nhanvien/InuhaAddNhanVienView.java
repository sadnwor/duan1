package com.app.core.inuha.views.quanly.nhanvien;


import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.QrCodeHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.session.AvatarUpload;
import com.app.common.infrastructure.session.SessionLogin;
import com.app.core.inuha.models.InuhaHoaDonChiTietModel;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.repositories.InuhaHoaDonChiTietRepository;
import com.app.core.inuha.services.InuhaHoaDonChiTietService;
import com.app.core.inuha.services.InuhaPhieuGiamGiaService;
import com.app.core.inuha.services.InuhaTaiKhoanService;
import com.app.core.inuha.views.quanly.InuhaNhanVienView;
import com.app.core.inuha.views.quanly.InuhaSanPhamView;
import com.app.utils.ColorUtils;
import com.app.utils.NumberPhoneUtils;
import com.app.utils.ProductUtils;
import com.app.utils.QrCodeUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.SessionUtils;
import com.app.utils.ValidateUtils;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.picturebox.DefaultPictureBoxRender;
import com.app.views.UI.picturebox.PictureBox;
import com.app.views.UI.picturebox.SuperEllipse2D;
import com.app.views.UI.sidebarmenu.SidebarMenu;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import jnafilechooser.api.JnaFileChooser;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

/**
 *
 * @author inuHa
 */
public class InuhaAddNhanVienView extends javax.swing.JPanel {
    
    private final InuhaTaiKhoanService taiKhoanService = InuhaTaiKhoanService.getInstance();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    private Color currentColor;
        
    private InuhaTaiKhoanModel model;
    
    private final LoadingDialog loading = new LoadingDialog();
    
    /** Creates new form InuhaAddPhieuGiamGiaView */
    
    public InuhaAddNhanVienView(InuhaTaiKhoanModel model) {
	this();
	this.model = model;
	
	txtUsername.setText(model.getUsername());
	txtHoTen.setText(model.getHoTen());
	txtEmail.setText(model.getEmail());
	txtSoDienThoai.setText(model.getSdt());
	txtDiaChi.setText(model.getDiaChi());
	
	rdoNam.setSelected(model.isGioiTinh());
	rdoNu.setSelected(!model.isGioiTinh());
	
	rdoQuanLy.setSelected(model.isAdmin());
	rdoNhanVien.setSelected(!model.isAdmin());
	
	rdoHoatDong.setSelected(model.isTrangThai());
	rdoDaNghi.setSelected(!model.isTrangThai());

	pictureBox.setImage(SessionUtils.getAvatar(model));
	
	btnChangePassword.setVisible(true);
	btnScan.setVisible(false);
	btnSubmit.setText("Lưu thông tin");
    }
    
    public InuhaAddNhanVienView() {
	initComponents();
	
	currentColor = lblUsername.getForeground();
	
	txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Phải từ " + ValidateUtils.MIN_LENGTH_USERNAME + " đến " + ValidateUtils.MAX_LENGTH_USERNAME + " ký tự");
	txtSoDienThoai.setFormatterFactory(NumberPhoneUtils.getDefaultFormat());
	
	pictureBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
	pictureBox.setImage(ResourceUtils.getImageAssets("/images/noavatar.png"));
	pictureBox.setBoxFit(PictureBox.BoxFit.CONTAIN);
        pictureBox.setPictureBoxRender(new DefaultPictureBoxRender() {
            @Override
            public Shape render(Rectangle rec) {
                return new SuperEllipse2D(rec.x, rec.y, rec.width, rec.height, 12f).getShape();
            }
        });
	
	btnScan.setIcon(ResourceUtils.getSVG("/svg/qr.svg", new Dimension(20, 20)));

		
	KeyAdapter eventSubmit = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleClickButtonSubmit();
                }
            }
        };
		
	txtUsername.addKeyListener(eventSubmit);
	txtHoTen.addKeyListener(eventSubmit);
	txtEmail.addKeyListener(eventSubmit);
	txtSoDienThoai.addKeyListener(eventSubmit);

	btnChangePassword.setVisible(false);
	btnSubmit.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnSubmit.setForeground(Color.WHITE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdoGroupGioiTinh = new javax.swing.ButtonGroup();
        rdoGroupChucVu = new javax.swing.ButtonGroup();
        rdoGroupTrangThai = new javax.swing.ButtonGroup();
        lblUsername = new javax.swing.JLabel();
        btnSubmit = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnChangePassword = new javax.swing.JButton();
        txtUsername = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        lblHoTen = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        lblSoDienThoai = new javax.swing.JLabel();
        txtSoDienThoai = new javax.swing.JFormattedTextField();
        lblDiaChi = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDiaChi = new javax.swing.JTextArea();
        pictureBox = new com.app.views.UI.picturebox.PictureBox();
        jLabel1 = new javax.swing.JLabel();
        rdoNam = new javax.swing.JRadioButton();
        rdoNu = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        rdoQuanLy = new javax.swing.JRadioButton();
        rdoNhanVien = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        rdoHoatDong = new javax.swing.JRadioButton();
        rdoDaNghi = new javax.swing.JRadioButton();
        btnScan = new javax.swing.JButton();

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblUsername.setText("Tên tài khoản:");

        btnSubmit.setText("Thêm nhân viên");
        btnSubmit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnCancel.setText("Huỷ");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnChangePassword.setText("Đổi mật khẩu");
        btnChangePassword.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangePasswordActionPerformed(evt);
            }
        });

        lblEmail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEmail.setText("Email:");

        lblHoTen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHoTen.setText("Họ và tên:");

        lblSoDienThoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSoDienThoai.setText("Số điện thoại:");

        lblDiaChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDiaChi.setText("Địa chỉ:");

        txtDiaChi.setColumns(20);
        txtDiaChi.setRows(5);
        jScrollPane1.setViewportView(txtDiaChi);

        pictureBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pictureBoxMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Giới tính:");

        rdoGroupGioiTinh.add(rdoNam);
        rdoNam.setSelected(true);
        rdoNam.setText("Nam");

        rdoGroupGioiTinh.add(rdoNu);
        rdoNu.setText("Nữ");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Chức vụ:");

        rdoGroupChucVu.add(rdoQuanLy);
        rdoQuanLy.setText("Quản lý");

        rdoGroupChucVu.add(rdoNhanVien);
        rdoNhanVien.setSelected(true);
        rdoNhanVien.setText("Nhân viên");
        rdoNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoNhanVienActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Trạng thái:");

        rdoGroupTrangThai.add(rdoHoatDong);
        rdoHoatDong.setSelected(true);
        rdoHoatDong.setText("Hoạt động");

        rdoGroupTrangThai.add(rdoDaNghi);
        rdoDaNghi.setText("Đã nghỉ");
        rdoDaNghi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoDaNghiActionPerformed(evt);
            }
        });

        btnScan.setText("Quét QR");
        btnScan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 27, Short.MAX_VALUE)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnScan, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChangePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lblSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtSoDienThoai)
                                        .addComponent(txtEmail)
                                        .addComponent(txtUsername)
                                        .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(rdoNam, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(rdoNu)))
                                .addGap(36, 36, 36))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(rdoQuanLy, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(rdoNhanVien))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(rdoHoatDong, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(rdoDaNghi)))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pictureBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(40, 40, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblUsername)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblHoTen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(lblDiaChi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblSoDienThoai)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pictureBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoNam)
                            .addComponent(rdoNu))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoQuanLy)
                            .addComponent(rdoNhanVien))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoHoatDong)
                            .addComponent(rdoDaNghi))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChangePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
	handleClickButtonSubmit();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
	ModalDialog.closeAllModal();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangePasswordActionPerformed
        // TODO add your handling code here:
        if (ModalDialog.isIdExist("btnChangePasswordActionPerformed")) {
            return;
        }
	ModalDialog.closeAllModal();
	ModalDialog.showModal(this, new SimpleModalBorder(new InuhaChangePasswordNhanVienView(model), "Thay đổi mật khẩu"), "btnChangePasswordActionPerformed");
    }//GEN-LAST:event_btnChangePasswordActionPerformed

    private void rdoNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoNhanVienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoNhanVienActionPerformed

    private void rdoDaNghiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoDaNghiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoDaNghiActionPerformed

    private void pictureBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pictureBoxMouseClicked
        // TODO add your handling code here:
	handleUploadAvatar(evt);
    }//GEN-LAST:event_pictureBoxMouseClicked

    private void btnScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanActionPerformed
        // TODO add your handling code here:
	handleScanQR();
    }//GEN-LAST:event_btnScanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnChangePassword;
    private javax.swing.JButton btnScan;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDiaChi;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblHoTen;
    private javax.swing.JLabel lblSoDienThoai;
    private javax.swing.JLabel lblUsername;
    private com.app.views.UI.picturebox.PictureBox pictureBox;
    private javax.swing.JRadioButton rdoDaNghi;
    private javax.swing.ButtonGroup rdoGroupChucVu;
    private javax.swing.ButtonGroup rdoGroupGioiTinh;
    private javax.swing.ButtonGroup rdoGroupTrangThai;
    private javax.swing.JRadioButton rdoHoatDong;
    private javax.swing.JRadioButton rdoNam;
    private javax.swing.JRadioButton rdoNhanVien;
    private javax.swing.JRadioButton rdoNu;
    private javax.swing.JRadioButton rdoQuanLy;
    private javax.swing.JTextArea txtDiaChi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JFormattedTextField txtSoDienThoai;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

    private void handleClickButtonSubmit() {
        String username = txtUsername.getText().trim();
        username = username.replaceAll("\\s+"," ");
	
        String hoTen = txtHoTen.getText().trim();
        hoTen = hoTen.replaceAll("\\s+"," ");
	
	String email = txtEmail.getText().trim();
	email = email.replaceAll("\\s+"," ");

	String sdt = txtSoDienThoai.getText().trim();
	sdt = sdt.replaceAll("\\s+"," ");
	sdt = NumberPhoneUtils.formatPhoneNumber(sdt);
	
	String diaChi = txtDiaChi.getText().trim();
	diaChi = diaChi.replaceAll("\\s+"," ");
	
	boolean gioiTinh = rdoNam.isSelected();
	
	boolean adm = rdoQuanLy.isSelected();
	
	boolean trangThai = rdoHoatDong.isSelected();
	
	String selectHinhAnh = (String) pictureBox.getClientProperty("path-image");
	

	lblUsername.setForeground(ColorUtils.DANGER_COLOR);
        if (username.isEmpty()) { 
            MessageToast.error("Tên tài khoản không được bỏ trống");
            return;
        }

	if (username.length() < ValidateUtils.MIN_LENGTH_USERNAME) { 
            MessageToast.error("Tên tài khoản phải có ít nhất " + ValidateUtils.MIN_LENGTH_USERNAME + " ký tự");
            return;
        }
		
        if (username.length() > ValidateUtils.MAX_LENGTH_USERNAME) { 
            MessageToast.error("Tên tài khoản không được vượt quá " + ValidateUtils.MAX_LENGTH_USERNAME + " ký tự");
            return;
        }
	
	if (!ValidateUtils.isUsername(username)) {
            MessageToast.error("Tên tài khoản chứa ký tự không hợp lệ");
            return;
	}
	
        lblUsername.setForeground(currentColor);
	
        lblHoTen.setForeground(ColorUtils.DANGER_COLOR);
        if (hoTen.isEmpty()) { 
            MessageToast.error("Họ tên không được bỏ trống");
            return;
        }
        if (hoTen.length() > 250) { 
            MessageToast.error("Họ tên không được vượt quá 250 ký tự");
            return;
        }
        if (!ValidateUtils.isFullName(hoTen)) {
            MessageToast.error("Tên nhân viên không được chứa ký tự đặc biệt");
            return;
        }
        lblHoTen.setForeground(currentColor);
	
	lblEmail.setForeground(ColorUtils.DANGER_COLOR);
        if (email.isEmpty()) { 
            MessageToast.error("Email không được bỏ trống");
            return;
        }
	
	if (!ValidateUtils.isEmail(email)) {
            MessageToast.error("Định dạng email không chính xác");
            return;
	}
	
        if (username.length() > 250) { 
            MessageToast.error("Email không được vượt quá 250 ký tự");
            return;
        }
        lblEmail.setForeground(currentColor);
	
	lblDiaChi.setForeground(ColorUtils.DANGER_COLOR);
        if (hoTen.length() > 2000) { 
            MessageToast.error("Địa chỉ không được vượt quá 2000 ký tự");
            return;
        }
        lblDiaChi.setForeground(currentColor);
	
	lblSoDienThoai.setForeground(ColorUtils.DANGER_COLOR);
	if (NumberPhoneUtils.formatPhoneNumber(sdt).length() < 10) { 
            MessageToast.error("Số điện thoại không hợp lệ");
            return;
        }
	lblSoDienThoai.setForeground(currentColor);
	
	
	boolean isEdited = this.model != null;
		
	InuhaTaiKhoanModel taiKhoan = new InuhaTaiKhoanModel();
	taiKhoan.setUsername(username);
        taiKhoan.setPassword(SessionUtils.generatePassword(8));
        taiKhoan.setEmail(email);
	taiKhoan.setHoTen(hoTen);
	taiKhoan.setSdt(sdt);
	taiKhoan.setDiaChi(diaChi);
	taiKhoan.setGioiTinh(gioiTinh);
	taiKhoan.setAdmin(adm);
	taiKhoan.setTrangThai(trangThai);
	
	if (isEdited) { 
            taiKhoan.setId(model.getId());
	    taiKhoan.setPassword(model.getPassword());
	    taiKhoan.setAvatar(model.getAvatar());
            taiKhoan.setTrangThaiXoa(model.isTrangThaiXoa());
        }
		
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return MessageModal.confirmInfo(isEdited ? "Lưu lại thông tin nhân viên?" : "Thêm mới nhân viên này?");
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        executorService.submit(() -> {

                            try {
                                if (!isEdited) {
                                    taiKhoan.setId(taiKhoanService.getLastId());
                                }

                                if (selectHinhAnh != null && !selectHinhAnh.isEmpty()) {
                                    AvatarUpload avatarUpload = SessionUtils.uploadAvatar(taiKhoan, selectHinhAnh);
                                    MessageToast.clearAll();

                                    if (avatarUpload.getFileName() == null) {
                                        loading.dispose();
                                        MessageToast.error("Không thể upload hình ảnh!");
                                        return;
                                    }

                                    taiKhoan.setAvatar(avatarUpload.getFileName());			    
                                }

                                if (!isEdited) { 
                                    taiKhoanService.insert(taiKhoan);
                                    MessageToast.success("Thêm mới nhân viên thành công.");
                                    InuhaNhanVienView.getInstance().loadDataPage(1);
                                } else {
                                    taiKhoanService.update(taiKhoan);
                                    if (taiKhoan.getId() == SessionLogin.getInstance().getData().getId()) { 
                                        SidebarMenu.getInstance().setAvatar(SessionUtils.getAvatar(taiKhoan));
                                    }
                                    MessageToast.success("Lưu thông tin nhân viên thành công.");
                                    InuhaNhanVienView.getInstance().loadDataPage();
                                }

                                ModalDialog.closeAllModal();
                            } catch (ServiceResponseException e) {
                                MessageToast.error(e.getMessage());
                            } catch (Exception e) {
                                MessageToast.error(ErrorConstant.DEFAULT_ERROR);
                            } finally {
                                loading.dispose();
                            }
                        });
                        loading.setVisible(true);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                }
            }
            
        };
        worker.execute();
    }

    private void handleUploadAvatar(MouseEvent evt) {
	if (!SwingUtilities.isLeftMouseButton(evt)) {
	    return;
	}
	
        JnaFileChooser ch = new JnaFileChooser();
        ch.addFilter("Hình ảnh", "png", "jpg", "jpeg");
        boolean act = ch.showOpenDialog(SwingUtilities.getWindowAncestor(this));
        if (act) {
            File file = ch.getSelectedFile();

            executorService.submit(() -> {
                try {
                    ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
                    Image image = imageIcon.getImage().getScaledInstance(pictureBox.getWidth(), pictureBox.getHeight(), Image.SCALE_SMOOTH);
                    
                    pictureBox.setImage(new ImageIcon(image));
                    pictureBox.putClientProperty("path-image", file.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MessageToast.error(ErrorConstant.DEFAULT_ERROR);
                }  finally {
                    loading.dispose();
                }
            });
            loading.setVisible(true);

        }
    }
    
    private void handleScanQR() {
	QrCodeHelper.showWebcam((result) -> {
	    String code = result.getText();
	    String[] data = QrCodeUtils.getDataCanCuocCongDan(code);
	    if (data == null) {
		MessageToast.warning("Mã QR không hợp lệ!!!");
		return;
	    }
	    
	    boolean isGioiTinh = data[1].equalsIgnoreCase("Nam");

	    txtHoTen.setText(data[0]);
	    txtDiaChi.setText(data[2]);
	    rdoNam.setSelected(isGioiTinh);
	    rdoNu.setSelected(!isGioiTinh);
	});
    }
}
