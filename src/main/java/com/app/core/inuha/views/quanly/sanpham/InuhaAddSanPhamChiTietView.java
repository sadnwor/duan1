package com.app.core.inuha.views.quanly.sanpham;

import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaHoaDonChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.models.sanpham.InuhaMauSacModel;
import com.app.core.inuha.repositories.InuhaHoaDonChiTietRepository;
import com.app.core.inuha.services.InuhaHoaDonChiTietService;
import com.app.core.inuha.services.InuhaKichCoService;
import com.app.core.inuha.services.InuhaMauSacService;
import com.app.core.inuha.services.InuhaSanPhamChiTietService;
import com.app.core.inuha.views.quanly.InuhaSanPhamView;
import static com.app.core.inuha.views.quanly.sanpham.InuhaDetailSanPhamChiTietView.getInstance;
import com.app.core.inuha.views.quanly.sanpham.thuoctinhsanpham.kichco.InuhaListKichCoView;
import com.app.core.inuha.views.quanly.sanpham.thuoctinhsanpham.mausac.InuhaListMauSacView;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.ProductUtils;
import com.app.utils.ResourceUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingWorker;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

/**
 *
 * @author InuHa
 */
public class InuhaAddSanPhamChiTietView extends javax.swing.JPanel {
    
    private final int MAX = 99999999;

    private static InuhaAddSanPhamChiTietView instance;
    
    private final InuhaSanPhamChiTietService sanPhamChiTietService = InuhaSanPhamChiTietService.getInstance();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
       
    private final static InuhaKichCoService kichCoService = InuhaKichCoService.getInstance();
    
    private final static InuhaMauSacService mauSacService = InuhaMauSacService.getInstance();
    
    private List<InuhaKichCoModel> dataKichCo = new ArrayList<>();
    
    private List<InuhaMauSacModel> dataMauSac = new ArrayList<>();
    
    private Color currentColor;
    
    private final LoadingDialog loading = new LoadingDialog();
    
    public static InuhaAddSanPhamChiTietView getInstance() { 
        return getInstance(null);
    }
    
    public static InuhaAddSanPhamChiTietView getInstance(InuhaSanPhamModel sanPham) {
        if (instance == null) {
            instance = new InuhaAddSanPhamChiTietView(sanPham);
        }
        return instance;
    }
    
    
    /**
     * Creates new form InuhThemSanPhamView
     */
    
    private InuhaSanPhamModel sanPham = null;
    private InuhaSanPhamChiTietModel sanPhamChiTiet = null;
    
    public InuhaAddSanPhamChiTietView(InuhaSanPhamModel sanPham, InuhaSanPhamChiTietModel sanPhamChiTiet) {
        this(sanPham);

        this.sanPhamChiTiet = sanPhamChiTiet;
        

        txtSoLuong.setText(CurrencyUtils.parseTextField(sanPhamChiTiet.getSoLuong()));
        rdoDangBan.setSelected(sanPhamChiTiet.isTrangThai());
        rdoNgungBan.setSelected(!sanPhamChiTiet.isTrangThai());
        
        checkDataExists(cboKichCo, new ComboBoxItem<>(sanPhamChiTiet.getKichCo().getTen(), sanPhamChiTiet.getKichCo().getId()));
        checkDataExists(cboMauSac, new ComboBoxItem<>(sanPhamChiTiet.getMauSac().getTen(), sanPhamChiTiet.getMauSac().getId()));
                
        btnSubmit.setText("Lưu lại");
    }
    
    public InuhaAddSanPhamChiTietView(InuhaSanPhamModel sanPham) {
        instance = this;
        this.sanPham = sanPham;
        initComponents();
	
	if (sanPham == null) { 
	    return;
	}
		
        currentColor = lblSoLuong.getForeground();
        btnSubmit.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnSubmit.setForeground(Color.WHITE);
        
        txtSoLuong.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tối đa " + MAX);
        
        btnCmdKichCo.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnCmdMauSac.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        
        txtSoLuong.setFormatterFactory(CurrencyUtils.getDefaultFormat());
        
        cboKichCo.setModel(new DefaultComboBoxModel<ComboBoxItem<Integer>>());
        cboMauSac.setModel(new DefaultComboBoxModel<ComboBoxItem<Integer>>());
        
	Dimension cboSize = new Dimension(150, 36);
	cboMauSac.setPreferredSize(cboSize);
	cboKichCo.setPreferredSize(cboSize);
	
	KeyAdapter eventEnter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleClickButtonSubmit();
                }
            }
        };
	txtSoLuong.addKeyListener(eventEnter);
		
        executorService.submit(() -> {
            loadDataKichCo();
            loadDataMauSac();
            loading.dispose();
        });
        loading.setVisible(true);
    }
    
    private void checkDataExists(JComboBox comboBox, ComboBoxItem<Integer> item) { 
        boolean exists = false;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                exists = true;
                break;
            }
        }
        
        
        
        if (!exists) { 
	    item.setText(item.getText() + " (đã xoá)");
            comboBox.addItem(item);
        }
        
        comboBox.setSelectedItem(item);
    }
    
    public void loadDataKichCo() {
        loadDataKichCo(false);
    }
    public void loadDataKichCo(boolean  checkDelete) { 
        dataKichCo = kichCoService.getAll();
        cboKichCo.removeAllItems();
       
        if (dataKichCo.isEmpty()) { 
            cboKichCo.addItem(new ComboBoxItem<>("-- Chưa có mục nào --", -1));
            cboKichCo.setEnabled(false);
            return;
        }
        
        cboKichCo.setEnabled(true);
        for(InuhaKichCoModel m: dataKichCo) { 
            cboKichCo.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        
        if (checkDelete && sanPhamChiTiet != null) { 
            checkDataExists(cboKichCo, new ComboBoxItem<>(sanPhamChiTiet.getKichCo().getTen(), sanPhamChiTiet.getKichCo().getId()));
        }
    }

    public void loadDataMauSac() { 
        loadDataMauSac(false);
    }
    public void loadDataMauSac(boolean checkDelete) { 
        dataMauSac = mauSacService.getAll();
        cboMauSac.removeAllItems();
        
        if (dataMauSac.isEmpty()) { 
            cboMauSac.addItem(new ComboBoxItem<>("-- Chưa có mục nào --", -1));
            cboMauSac.setEnabled(false);
            return;
        }
        
        cboMauSac.setEnabled(true);
        
        for(InuhaMauSacModel m: dataMauSac) { 
            cboMauSac.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        
        if (checkDelete && sanPhamChiTiet != null) {
            checkDataExists(cboMauSac, new ComboBoxItem<>(sanPhamChiTiet.getMauSac().getTen(), sanPhamChiTiet.getMauSac().getId()));
        }
    }
            
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGroupTrangThai = new javax.swing.ButtonGroup();
        roundPanel1 = new com.app.views.UI.panel.RoundPanel();
        lblTrangThai = new javax.swing.JLabel();
        rdoDangBan = new javax.swing.JRadioButton();
        rdoNgungBan = new javax.swing.JRadioButton();
        lblKichCo = new javax.swing.JLabel();
        lblMauSac = new javax.swing.JLabel();
        cboMauSac = new javax.swing.JComboBox();
        cboKichCo = new javax.swing.JComboBox();
        btnCmdKichCo = new javax.swing.JButton();
        btnCmdMauSac = new javax.swing.JButton();
        lblSoLuong = new javax.swing.JLabel();
        txtSoLuong = new javax.swing.JFormattedTextField();
        btnSubmit = new javax.swing.JButton();

        lblTrangThai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTrangThai.setText("Trạng thái:");

        btnGroupTrangThai.add(rdoDangBan);
        rdoDangBan.setSelected(true);
        rdoDangBan.setText("Đang bán");

        btnGroupTrangThai.add(rdoNgungBan);
        rdoNgungBan.setText("Ngừng bán");

        lblKichCo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKichCo.setText("Kích cỡ:");

        lblMauSac.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMauSac.setText("Màu sắc:");

        cboMauSac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMauSacActionPerformed(evt);
            }
        });

        cboKichCo.setMaximumSize(new java.awt.Dimension(250, 250));
        cboKichCo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboKichCoActionPerformed(evt);
            }
        });

        btnCmdKichCo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCmdKichCo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdKichCoActionPerformed(evt);
            }
        });

        btnCmdMauSac.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCmdMauSac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdMauSacActionPerformed(evt);
            }
        });

        lblSoLuong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSoLuong.setText("Số lượng tồn:");

        btnSubmit.setText("Thêm ngay");
        btnSubmit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundPanel1Layout = new javax.swing.GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblMauSac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(roundPanel1Layout.createSequentialGroup()
                                .addComponent(cboMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnCmdMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(roundPanel1Layout.createSequentialGroup()
                                .addComponent(cboKichCo, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnCmdKichCo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundPanel1Layout.createSequentialGroup()
                                .addGap(126, 126, 126)
                                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblKichCo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addComponent(lblTrangThai)
                        .addGap(18, 18, 18)
                        .addComponent(rdoDangBan)
                        .addGap(18, 18, 18)
                        .addComponent(rdoNgungBan)))
                .addGap(20, 20, 20))
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addComponent(lblKichCo)
                        .addGap(10, 10, 10)
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboKichCo, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                            .addComponent(btnCmdKichCo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addComponent(lblMauSac)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCmdMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(lblSoLuong)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTrangThai)
                    .addComponent(rdoDangBan)
                    .addComponent(rdoNgungBan))
                .addGap(40, 40, 40)
                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
        handleClickButtonSubmit();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnCmdMauSacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdMauSacActionPerformed
        // TODO add your handling code here:
        handleClickButtonThuongHieu();
    }//GEN-LAST:event_btnCmdMauSacActionPerformed

    private void btnCmdKichCoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdKichCoActionPerformed
        // TODO add your handling code here:
        handleClickButtonDanhMuc();
    }//GEN-LAST:event_btnCmdKichCoActionPerformed

    private void cboKichCoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboKichCoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboKichCoActionPerformed

    private void cboMauSacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMauSacActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMauSacActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCmdKichCo;
    private javax.swing.JButton btnCmdMauSac;
    private javax.swing.ButtonGroup btnGroupTrangThai;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox cboKichCo;
    private javax.swing.JComboBox cboMauSac;
    private javax.swing.JLabel lblKichCo;
    private javax.swing.JLabel lblMauSac;
    private javax.swing.JLabel lblSoLuong;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JRadioButton rdoDangBan;
    private javax.swing.JRadioButton rdoNgungBan;
    private com.app.views.UI.panel.RoundPanel roundPanel1;
    private javax.swing.JFormattedTextField txtSoLuong;
    // End of variables declaration//GEN-END:variables

    private void handleClickButtonDanhMuc() {
        if (ModalDialog.isIdExist("handleClickButtonDanhMuc")) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListKichCoView(), "Kích cỡ sản phẩm"), "handleClickButtonDanhMuc");
    }

    private void handleClickButtonThuongHieu() {
        if (ModalDialog.isIdExist("handleClickButtonThuongHieu")) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListMauSacView(), "Màu sắc sản phẩm"), "handleClickButtonThuongHieu");
    }

    private void handleClickButtonSubmit() {
        String soLuong = txtSoLuong.getText().trim();
        boolean trangThai = rdoDangBan.isSelected();
        ComboBoxItem<Integer> kichCo = (ComboBoxItem<Integer>) cboKichCo.getSelectedItem();
        ComboBoxItem<Integer> mauSac = (ComboBoxItem<Integer>) cboMauSac.getSelectedItem();

        lblSoLuong.setForeground(ColorUtils.DANGER_COLOR);
        if (soLuong.isEmpty()) { 
            MessageToast.error("Vui lòng nhập số lượng tồn");
            txtSoLuong.requestFocus();
            return;
        }
	try {
	    if (CurrencyUtils.parseNumber(soLuong) >= MAX) { 
		throw new NumberFormatException();
	    }
	} catch (NumberFormatException e) { 
	    MessageToast.error("Số lượng tồn phải nhỏ hơn " + MAX);
	    txtSoLuong.requestFocus();
	    return;
	}
        lblSoLuong.setForeground(currentColor);
        
        lblKichCo.setForeground(ColorUtils.DANGER_COLOR);
        if (kichCo.getValue() < 0) { 
            MessageToast.error("Vui lòng chọn một kích cỡ");
            cboKichCo.requestFocus();
            return;
        }
        lblKichCo.setForeground(currentColor);
        
        lblMauSac.setForeground(ColorUtils.DANGER_COLOR);
        if (mauSac.getValue() < 0) { 
            MessageToast.error("Vui lòng chọn một màu sắc");
            cboMauSac.requestFocus();
            return;
        }
        lblMauSac.setForeground(currentColor);
        
        
        boolean isEdited = this.sanPhamChiTiet != null;
                

        InuhaKichCoModel kichCoModel = new InuhaKichCoModel();
        kichCoModel.setId(kichCo.getValue());
	kichCoModel.setTen(kichCo.getText());
        
        InuhaMauSacModel mauSacModel = new InuhaMauSacModel();
        mauSacModel.setId(mauSac.getValue());
	mauSacModel.setTen(mauSacModel.getTen());
        
	String ma = !isEdited ? ProductUtils.generateCodeSanPhamChiTiet(): sanPhamChiTiet.getMa();
        InuhaSanPhamChiTietModel model = new InuhaSanPhamChiTietModel();
	model.setMa(ma);
        model.setSoLuong(Integer.parseInt(String.valueOf(CurrencyUtils.parseNumber(soLuong))));
        model.setTrangThai(trangThai);
        model.setKichCo(kichCoModel);
        model.setMauSac(mauSacModel);
        model.setSanPham(this.sanPham);
        
        if (isEdited) { 
            model.setId(sanPhamChiTiet.getId());
            model.setNgayTao(sanPhamChiTiet.getNgayTao());
            model.setTrangThaiXoa(sanPhamChiTiet.isTrangThaiXoa());
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return MessageModal.confirmInfo(isEdited ? "Lưu lại thông tin sản phẩm chi tiết?" : "Thêm mới sản phẩm chi tiết này?");
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        executorService.submit(() -> {

                            try {
                                if (!isEdited) { 

                                    sanPhamChiTietService.insert(model);
                                    MessageToast.success("Thêm mới sản phẩm chi tiết thành công.");
                                    InuhaDetailSanPhamView.getInstance().loadDataPage(1);
                                } else {
                                    sanPhamChiTietService.update(model);

                                    if (sanPhamChiTiet.isTrangThai() != trangThai && !trangThai) { 
                                        List<InuhaHoaDonChiTietModel> sanPhamCho = InuhaHoaDonChiTietRepository.getInstance().getAllIdsByIdSanPhamChiTiet(sanPhamChiTiet.getId());
                                        for(InuhaHoaDonChiTietModel m: sanPhamCho) { 
                                            InuhaHoaDonChiTietService.getInstance().delete(m);
                                        }
                                    }

                                    MessageToast.success("Lưu thông tin sản phẩm chi tiết thành công.");
                                    InuhaDetailSanPhamView.getInstance(sanPham).loadDataPage();
                                    InuhaDetailSanPhamChiTietView.getInstance().updateView(model);
                                }
                                InuhaSanPhamView.getInstance().loadDataPage();
                                InuhaSanPhamView.getInstance().loadDataPageSPCT();

                                ModalDialog.closeModal(InuhaDetailSanPhamView.ID_MODAL_ADD);
                            } catch (ServiceResponseException e) {
                                MessageToast.error(e.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
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

}
