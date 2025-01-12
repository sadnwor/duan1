package com.app.core.inuha.views.quanly.sanpham;

import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaHoaDonChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaChatLieuModel;
import com.app.core.inuha.models.sanpham.InuhaDanhMucModel;
import com.app.core.inuha.models.sanpham.InuhaDeGiayModel;
import com.app.core.inuha.models.sanpham.InuhaKieuDangModel;
import com.app.core.inuha.models.sanpham.InuhaThuongHieuModel;
import com.app.core.inuha.models.sanpham.InuhaXuatXuModel;
import com.app.core.inuha.repositories.InuhaHoaDonChiTietRepository;
import com.app.core.inuha.services.InuhaChatLieuService;
import com.app.core.inuha.services.InuhaDanhMucService;
import com.app.core.inuha.services.InuhaDeGiayService;
import com.app.core.inuha.services.InuhaHoaDonChiTietService;
import com.app.core.inuha.services.InuhaKieuDangService;
import com.app.core.inuha.services.InuhaSanPhamService;
import com.app.core.inuha.services.InuhaThuongHieuService;
import com.app.core.inuha.services.InuhaXuatXuService;
import com.app.core.inuha.views.quanly.InuhaSanPhamView;
import com.app.core.inuha.views.quanly.sanpham.thuoctinhsanpham.chatlieu.InuhaListChatLieuView;
import com.app.core.inuha.views.quanly.sanpham.thuoctinhsanpham.danhmuc.InuhaListDanhMucView;
import com.app.core.inuha.views.quanly.sanpham.thuoctinhsanpham.degiay.InuhaListDeGiayView;
import com.app.core.inuha.views.quanly.sanpham.thuoctinhsanpham.thuonghieu.InuhaListThuongHieuView;
import com.app.core.inuha.views.quanly.sanpham.thuoctinhsanpham.xuatxu.InuhaListXuatXuView;
import com.app.core.inuha.views.quanly.sanpham.thuoctinhsanpham.kieudang.InuhaListKieuDangView;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.ProductUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.ValidateUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.picturebox.DefaultPictureBoxRender;
import com.app.views.UI.picturebox.PictureBox;
import com.app.views.UI.picturebox.SuperEllipse2D;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import jnafilechooser.api.JnaFileChooser;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

/**
 *
 * @author InuHa
 */
public class InuhaAddSanPhamView extends javax.swing.JPanel {

    private static InuhaAddSanPhamView instance;
    
    private final InuhaSanPhamService sanPhamService = InuhaSanPhamService.getInstance();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
        
    private final static InuhaDanhMucService danhMucService = InuhaDanhMucService.getInstance();
    
    private final static InuhaThuongHieuService thuongHieuService = InuhaThuongHieuService.getInstance();
    
    private final static InuhaXuatXuService xuatXuService = InuhaXuatXuService.getInstance();
    
    private final static InuhaKieuDangService kieuDangervice = InuhaKieuDangService.getInstance();
    
    private final static InuhaChatLieuService chatLieuService = InuhaChatLieuService.getInstance();
    
    private final static InuhaDeGiayService deGiayService = InuhaDeGiayService.getInstance();
    
    private List<InuhaDanhMucModel> dataDanhMuc = new ArrayList<>();
    
    private List<InuhaThuongHieuModel> dataThuongHieu = new ArrayList<>();
    
    private List<InuhaXuatXuModel> dataXuatXu = new ArrayList<>();
    
    private List<InuhaKieuDangModel> dataKieuDang = new ArrayList<>();
    
    private List<InuhaChatLieuModel> dataChatLieu = new ArrayList<>();
    
    private List<InuhaDeGiayModel> dataDeGiay = new ArrayList<>();
    
    private final Color currentColor;
    
    private final LoadingDialog loading = new LoadingDialog();
    
    public static InuhaAddSanPhamView getInstance() {
        if (instance == null) {
            instance = new InuhaAddSanPhamView();
        }
        return instance;
    }
    
    
    /**
     * Creates new form InuhThemSanPhamView
     */
    
    private InuhaSanPhamModel sanPham = null;
    
    public InuhaAddSanPhamView(InuhaSanPhamModel sanPham) {
        this();
        this.sanPham = sanPham;
       
        txtTen.setText(sanPham.getTen());
	txtGiaNhap.setText(CurrencyUtils.parseTextField(sanPham.getGiaNhap()));
        txtGiaBan.setText(CurrencyUtils.parseTextField(sanPham.getGiaBan()));
        rdoDangBan.setSelected(sanPham.isTrangThai());
        rdoNgungBan.setSelected(!sanPham.isTrangThai());
        txtMoTa.setText(sanPham.getMoTa());
        
        checkDataExists(cboDanhMuc, new ComboBoxItem<>(sanPham.getDanhMuc().getTen(), sanPham.getDanhMuc().getId()));
        checkDataExists(cboThuongHieu, new ComboBoxItem<>(sanPham.getThuongHieu().getTen(), sanPham.getThuongHieu().getId()));
        checkDataExists(cboXuatXu, new ComboBoxItem<>(sanPham.getXuatXu().getTen(), sanPham.getXuatXu().getId()));
        checkDataExists(cboKieuDang, new ComboBoxItem<>(sanPham.getKieuDang().getTen(), sanPham.getKieuDang().getId()));
        checkDataExists(cboChatLieu, new ComboBoxItem<>(sanPham.getChatLieu().getTen(), sanPham.getChatLieu().getId()));
        checkDataExists(cboDeGiay, new ComboBoxItem<>(sanPham.getDeGiay().getTen(), sanPham.getDeGiay().getId()));
        
        pictureBox.setImage(ProductUtils.getImage(sanPham.getHinhAnh()));
        pictureBox.putClientProperty("path-image", ProductUtils.getUrlImageProduct(sanPham.getHinhAnh()));
        
        btnSubmit.setText("Lưu lại");
        btnDetail.setVisible(true);
    }
    
    public InuhaAddSanPhamView() {
        instance = this;
        initComponents();
        currentColor = lblTen.getForeground();
        
        roundPanel3.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
        
        btnSubmit.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnSubmit.setForeground(Color.WHITE);
        
        txtTen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tối đa 250 ký tự...");
        txtGiaNhap.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "VNĐ");
	txtGiaBan.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "VNĐ");
	
        btnDetail.setVisible(false);
        
        btnCmdDanhMuc.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnCmdThuongHieu.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnCmdXuatXu.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnCmdKieuDang.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnCmdDeGiay.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnCmdChatLieu.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnUploadImage.setIcon(ResourceUtils.getSVG("/svg/file.svg", new Dimension(20, 20)));
        
        txtGiaNhap.setFormatterFactory(CurrencyUtils.getDefaultFormat());
        txtGiaBan.setFormatterFactory(CurrencyUtils.getDefaultFormat());
		
        cboDanhMuc.setModel(new DefaultComboBoxModel<ComboBoxItem<Integer>>());
        cboThuongHieu.setModel(new DefaultComboBoxModel<ComboBoxItem<Integer>>());
        cboXuatXu.setModel(new DefaultComboBoxModel<ComboBoxItem<Integer>>());
        cboKieuDang.setModel(new DefaultComboBoxModel<ComboBoxItem<Integer>>());
        cboChatLieu.setModel(new DefaultComboBoxModel<ComboBoxItem<Integer>>());
        cboDeGiay.setModel(new DefaultComboBoxModel<ComboBoxItem<Integer>>());
        
        pictureBox.setBoxFit(PictureBox.BoxFit.CONTAIN);
        pictureBox.setPictureBoxRender(new DefaultPictureBoxRender() {
            @Override
            public Shape render(Rectangle rec) {
                return new SuperEllipse2D(rec.x, rec.y, rec.width, rec.height, 8f).getShape();
            }
        });
	
	KeyAdapter eventEnter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleClickButtonSubmit();
                }
            }
        };
	
	txtTen.addKeyListener(eventEnter);
	txtGiaNhap.addKeyListener(eventEnter);
	txtGiaBan.addKeyListener(eventEnter);
	txtMoTa.addKeyListener(eventEnter);
	
	Dimension cboSize = new Dimension(150, 36);
	cboDanhMuc.setPreferredSize(cboSize);
        cboThuongHieu.setPreferredSize(cboSize);
	cboXuatXu.setPreferredSize(cboSize);
	cboKieuDang.setPreferredSize(cboSize);
	cboChatLieu.setPreferredSize(cboSize);
	cboDeGiay.setPreferredSize(cboSize);
	
        executorService.submit(() -> {
            loadDataDanhMuc();
            loadDataThuongHieu();
            loadDataXuatXu();
            loadDataKieuDang();
            loadDataChatLieu();
            loadDataDeGiay();
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
    
    public void loadDataDanhMuc() {
        loadDataDanhMuc(false);
    }
    public void loadDataDanhMuc(boolean checkDelete) { 
        dataDanhMuc = danhMucService.getAll();
        cboDanhMuc.removeAllItems();
       
        if (dataDanhMuc.isEmpty()) { 
            cboDanhMuc.addItem(new ComboBoxItem<>("-- Chưa có mục nào --", -1));
            cboDanhMuc.setEnabled(false);
            return;
        }
        
        cboDanhMuc.setEnabled(true);
        for(InuhaDanhMucModel m: dataDanhMuc) { 
            cboDanhMuc.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        
        if (checkDelete && sanPham != null) { 
            checkDataExists(cboDanhMuc, new ComboBoxItem<>(sanPham.getDanhMuc().getTen(), sanPham.getDanhMuc().getId()));
        }
    }

    public void loadDataThuongHieu() {
        loadDataThuongHieu(false);
    }
    public void loadDataThuongHieu(boolean checkDelete) { 
        dataThuongHieu = thuongHieuService.getAll();
        cboThuongHieu.removeAllItems();
        
        if (dataThuongHieu.isEmpty()) { 
            cboThuongHieu.addItem(new ComboBoxItem<>("-- Chưa có mục nào --", -1));
            cboThuongHieu.setEnabled(false);
            return;
        }
        
        cboThuongHieu.setEnabled(true);
        
        for(InuhaThuongHieuModel m: dataThuongHieu) { 
            cboThuongHieu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        
        if (checkDelete && sanPham != null) { 
            checkDataExists(cboThuongHieu, new ComboBoxItem<>(sanPham.getThuongHieu().getTen(), sanPham.getThuongHieu().getId()));
        }
    }
    
    public void loadDataXuatXu() {
        loadDataXuatXu(false);
    }
    public void loadDataXuatXu(boolean checkDelete) { 
        dataXuatXu = xuatXuService.getAll();
        cboXuatXu.removeAllItems();
        
        if (dataXuatXu.isEmpty()) { 
            cboXuatXu.addItem(new ComboBoxItem<>("-- Chưa có mục nào --", -1));
            cboXuatXu.setEnabled(false);
            return;
        }
        
        cboXuatXu.setEnabled(true);
        
        for(InuhaXuatXuModel m: dataXuatXu) { 
            cboXuatXu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        
        if (checkDelete && sanPham != null) { 
            checkDataExists(cboXuatXu, new ComboBoxItem<>(sanPham.getXuatXu().getTen(), sanPham.getXuatXu().getId()));
        }
    }
    
    public void loadDataKieuDang() {
        loadDataKieuDang(false);
    }
    public void loadDataKieuDang(boolean checkDelete) { 
        dataKieuDang = kieuDangervice.getAll();
        cboKieuDang.removeAllItems();
        
        if (dataKieuDang.isEmpty()) { 
            cboKieuDang.addItem(new ComboBoxItem<>("-- Chưa có mục nào --", -1));
            cboKieuDang.setEnabled(false);
            return;
        }
        
        cboKieuDang.setEnabled(true);
        
        for(InuhaKieuDangModel m: dataKieuDang) { 
            cboKieuDang.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        
        if (checkDelete && sanPham != null) { 
            checkDataExists(cboKieuDang, new ComboBoxItem<>(sanPham.getKieuDang().getTen(), sanPham.getKieuDang().getId()));
        }
    }
    
    public void loadDataChatLieu() {
        loadDataChatLieu(false);
    }
    public void loadDataChatLieu(boolean checkDelete) { 
        dataChatLieu = chatLieuService.getAll();
        cboChatLieu.removeAllItems();
        
        if (dataChatLieu.isEmpty()) { 
            cboChatLieu.addItem(new ComboBoxItem<>("-- Chưa có mục nào --", -1));
            cboChatLieu.setEnabled(false);
            return;
        }
        
        cboChatLieu.setEnabled(true);
        
        for(InuhaChatLieuModel m: dataChatLieu) { 
            cboChatLieu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        
        if (checkDelete && sanPham != null) { 
            checkDataExists(cboChatLieu, new ComboBoxItem<>(sanPham.getChatLieu().getTen(), sanPham.getChatLieu().getId()));
        }
    }
    
    public void loadDataDeGiay() {
        loadDataDeGiay(false);
    }
    public void loadDataDeGiay(boolean checkDelete) { 
        dataDeGiay = deGiayService.getAll();
        cboDeGiay.removeAllItems();
        
        if (dataDeGiay.isEmpty()) { 
            cboDeGiay.addItem(new ComboBoxItem<>("-- Chưa có mục nào --", -1));
            cboDeGiay.setEnabled(false);
            return;
        }
        
        cboDeGiay.setEnabled(true);
        
        for(InuhaDeGiayModel m: dataDeGiay) { 
            cboDeGiay.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        
        if (checkDelete && sanPham != null) {
            checkDataExists(cboDeGiay, new ComboBoxItem<>(sanPham.getDeGiay().getTen(), sanPham.getDeGiay().getId()));
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
        lblTen = new javax.swing.JLabel();
        txtTen = new javax.swing.JTextField();
        lblGiaBan = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        rdoDangBan = new javax.swing.JRadioButton();
        rdoNgungBan = new javax.swing.JRadioButton();
        lblMoTa = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMoTa = new javax.swing.JTextArea();
        txtGiaBan = new javax.swing.JFormattedTextField();
        txtGiaNhap = new javax.swing.JFormattedTextField();
        lblGiaNhap = new javax.swing.JLabel();
        roundPanel2 = new com.app.views.UI.panel.RoundPanel();
        lblDeGiay = new javax.swing.JLabel();
        cboDeGiay = new javax.swing.JComboBox();
        btnCmdDeGiay = new javax.swing.JButton();
        roundPanel3 = new com.app.views.UI.panel.RoundPanel();
        lblHinhAnh = new javax.swing.JLabel();
        splitLine3 = new com.app.views.UI.label.SplitLine();
        btnUploadImage = new javax.swing.JButton();
        pictureBox = new com.app.views.UI.picturebox.PictureBox();
        btnSubmit = new javax.swing.JButton();
        roundPanel4 = new com.app.views.UI.panel.RoundPanel();
        lblDanhMuc = new javax.swing.JLabel();
        cboDanhMuc = new javax.swing.JComboBox();
        lblThuongHieu = new javax.swing.JLabel();
        cboThuongHieu = new javax.swing.JComboBox();
        cboXuatXu = new javax.swing.JComboBox();
        lblXuatXu = new javax.swing.JLabel();
        cboKieuDang = new javax.swing.JComboBox();
        lblKieuDang = new javax.swing.JLabel();
        lblChatLieu = new javax.swing.JLabel();
        cboChatLieu = new javax.swing.JComboBox();
        btnCmdDanhMuc = new javax.swing.JButton();
        btnCmdXuatXu = new javax.swing.JButton();
        btnCmdKieuDang = new javax.swing.JButton();
        btnCmdChatLieu = new javax.swing.JButton();
        btnCmdThuongHieu = new javax.swing.JButton();
        btnDetail = new javax.swing.JButton();

        lblTen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTen.setText("Tên sản phẩm:");

        lblGiaBan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGiaBan.setText("Giá bán:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Trạng thái:");

        btnGroupTrangThai.add(rdoDangBan);
        rdoDangBan.setSelected(true);
        rdoDangBan.setText("Đang bán");

        btnGroupTrangThai.add(rdoNgungBan);
        rdoNgungBan.setText("Ngừng bán");

        lblMoTa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMoTa.setText("Mô tả:");

        txtMoTa.setColumns(20);
        txtMoTa.setRows(5);
        jScrollPane1.setViewportView(txtMoTa);

        txtGiaBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGiaBanActionPerformed(evt);
            }
        });

        lblGiaNhap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGiaNhap.setText("Giá nhập:");

        javax.swing.GroupLayout roundPanel1Layout = new javax.swing.GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addComponent(lblTen)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdoDangBan, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rdoNgungBan))
                            .addComponent(lblMoTa, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel1Layout.createSequentialGroup()
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTen, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundPanel1Layout.createSequentialGroup()
                                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(roundPanel1Layout.createSequentialGroup()
                                        .addComponent(lblGiaNhap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(124, 124, 124))
                                    .addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblGiaBan)
                                    .addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(10, 10, 10))))
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblTen)
                .addGap(10, 10, 10)
                .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGiaBan)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addComponent(lblGiaNhap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(rdoDangBan)
                    .addComponent(rdoNgungBan))
                .addGap(18, 18, 18)
                .addComponent(lblMoTa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblDeGiay.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDeGiay.setText("Đế giày:");

        cboDeGiay.setMaximumSize(new java.awt.Dimension(100, 40));
        cboDeGiay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDeGiayActionPerformed(evt);
            }
        });

        btnCmdDeGiay.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCmdDeGiay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdDeGiayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundPanel2Layout = new javax.swing.GroupLayout(roundPanel2);
        roundPanel2.setLayout(roundPanel2Layout);
        roundPanel2Layout.setHorizontalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel2Layout.createSequentialGroup()
                        .addComponent(cboDeGiay, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCmdDeGiay, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblDeGiay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        roundPanel2Layout.setVerticalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblDeGiay)
                .addGap(10, 10, 10)
                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboDeGiay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCmdDeGiay, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        lblHinhAnh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHinhAnh.setText("Hình ảnh");

        javax.swing.GroupLayout splitLine3Layout = new javax.swing.GroupLayout(splitLine3);
        splitLine3.setLayout(splitLine3Layout);
        splitLine3Layout.setHorizontalGroup(
            splitLine3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine3Layout.setVerticalGroup(
            splitLine3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        btnUploadImage.setText("Chọn ảnh");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundPanel3Layout = new javax.swing.GroupLayout(roundPanel3);
        roundPanel3.setLayout(roundPanel3Layout);
        roundPanel3Layout.setHorizontalGroup(
            roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel3Layout.createSequentialGroup()
                .addGroup(roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(splitLine3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(roundPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblHinhAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)))
                .addContainerGap())
            .addGroup(roundPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(pictureBox, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );
        roundPanel3Layout.setVerticalGroup(
            roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblHinhAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(splitLine3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pictureBox, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUploadImage)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        btnSubmit.setText("Thêm ngay");
        btnSubmit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        lblDanhMuc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDanhMuc.setText("Danh mục:");

        cboDanhMuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDanhMucActionPerformed(evt);
            }
        });

        lblThuongHieu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblThuongHieu.setText("Thương hiệu:");

        cboThuongHieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboThuongHieuActionPerformed(evt);
            }
        });

        cboXuatXu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboXuatXuActionPerformed(evt);
            }
        });

        lblXuatXu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblXuatXu.setText("Xuất xứ:");

        cboKieuDang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboKieuDangActionPerformed(evt);
            }
        });

        lblKieuDang.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKieuDang.setText("Kiểu dáng:");

        lblChatLieu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblChatLieu.setText("Chất liệu:");

        cboChatLieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboChatLieuActionPerformed(evt);
            }
        });

        btnCmdDanhMuc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCmdDanhMuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdDanhMucActionPerformed(evt);
            }
        });

        btnCmdXuatXu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCmdXuatXu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdXuatXuActionPerformed(evt);
            }
        });

        btnCmdKieuDang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCmdKieuDang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdKieuDangActionPerformed(evt);
            }
        });

        btnCmdChatLieu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCmdChatLieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdChatLieuActionPerformed(evt);
            }
        });

        btnCmdThuongHieu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCmdThuongHieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdThuongHieuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundPanel4Layout = new javax.swing.GroupLayout(roundPanel4);
        roundPanel4.setLayout(roundPanel4Layout);
        roundPanel4Layout.setHorizontalGroup(
            roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblChatLieu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDanhMuc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblThuongHieu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblXuatXu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblKieuDang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundPanel4Layout.createSequentialGroup()
                        .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(roundPanel4Layout.createSequentialGroup()
                                .addComponent(cboXuatXu, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCmdXuatXu, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel4Layout.createSequentialGroup()
                                    .addComponent(cboChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnCmdChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(roundPanel4Layout.createSequentialGroup()
                                    .addComponent(cboKieuDang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnCmdKieuDang, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(roundPanel4Layout.createSequentialGroup()
                                    .addComponent(cboDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnCmdDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(roundPanel4Layout.createSequentialGroup()
                                .addComponent(cboThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCmdThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        roundPanel4Layout.setVerticalGroup(
            roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblDanhMuc)
                .addGap(10, 10, 10)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCmdDanhMuc, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(cboDanhMuc))
                .addGap(18, 18, 18)
                .addComponent(lblThuongHieu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCmdThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblXuatXu)
                .addGap(10, 10, 10)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboXuatXu, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(btnCmdXuatXu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(lblKieuDang)
                .addGap(10, 10, 10)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboKieuDang, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(btnCmdKieuDang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(lblChatLieu)
                .addGap(10, 10, 10)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboChatLieu, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(btnCmdChatLieu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        btnDetail.setText("Chi tiết sản phẩm");
        btnDetail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(roundPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(roundPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(roundPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roundPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(40, 40, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(roundPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(roundPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(roundPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(btnDetail, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboDanhMucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDanhMucActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboDanhMucActionPerformed

    private void cboThuongHieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboThuongHieuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboThuongHieuActionPerformed

    private void cboXuatXuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboXuatXuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboXuatXuActionPerformed

    private void cboKieuDangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboKieuDangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboKieuDangActionPerformed

    private void cboDeGiayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDeGiayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboDeGiayActionPerformed

    private void cboChatLieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboChatLieuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboChatLieuActionPerformed

    private void btnCmdDanhMucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdDanhMucActionPerformed
        // TODO add your handling code here:
        handleClickButtonDanhMuc();
    }//GEN-LAST:event_btnCmdDanhMucActionPerformed

    private void btnCmdThuongHieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdThuongHieuActionPerformed
        // TODO add your handling code here:
        handleClickButtonThuongHieu();
    }//GEN-LAST:event_btnCmdThuongHieuActionPerformed

    private void btnCmdXuatXuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdXuatXuActionPerformed
        // TODO add your handling code here:
        handleClickButtonXuatXu();
    }//GEN-LAST:event_btnCmdXuatXuActionPerformed

    private void btnCmdKieuDangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdKieuDangActionPerformed
        // TODO add your handling code here:
        handleClickButtonKieuDang();
    }//GEN-LAST:event_btnCmdKieuDangActionPerformed

    private void btnCmdChatLieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdChatLieuActionPerformed
        // TODO add your handling code here:
        handleClickButtonChatLieu();
    }//GEN-LAST:event_btnCmdChatLieuActionPerformed

    private void btnCmdDeGiayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdDeGiayActionPerformed
        // TODO add your handling code here:
        handleClickButtonDeGiay();
    }//GEN-LAST:event_btnCmdDeGiayActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        // TODO add your handling code here:
        handleClickButtonImage();
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
        handleClickButtonSubmit();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailActionPerformed
        // TODO add your handling code here:
        handleClickButtonDetail();
    }//GEN-LAST:event_btnDetailActionPerformed

    private void txtGiaBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGiaBanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGiaBanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCmdChatLieu;
    private javax.swing.JButton btnCmdDanhMuc;
    private javax.swing.JButton btnCmdDeGiay;
    private javax.swing.JButton btnCmdKieuDang;
    private javax.swing.JButton btnCmdThuongHieu;
    private javax.swing.JButton btnCmdXuatXu;
    private javax.swing.JButton btnDetail;
    private javax.swing.ButtonGroup btnGroupTrangThai;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JComboBox cboChatLieu;
    private javax.swing.JComboBox cboDanhMuc;
    private javax.swing.JComboBox cboDeGiay;
    private javax.swing.JComboBox cboKieuDang;
    private javax.swing.JComboBox cboThuongHieu;
    private javax.swing.JComboBox cboXuatXu;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblChatLieu;
    private javax.swing.JLabel lblDanhMuc;
    private javax.swing.JLabel lblDeGiay;
    private javax.swing.JLabel lblGiaBan;
    private javax.swing.JLabel lblGiaNhap;
    private javax.swing.JLabel lblHinhAnh;
    private javax.swing.JLabel lblKieuDang;
    private javax.swing.JLabel lblMoTa;
    private javax.swing.JLabel lblTen;
    private javax.swing.JLabel lblThuongHieu;
    private javax.swing.JLabel lblXuatXu;
    private com.app.views.UI.picturebox.PictureBox pictureBox;
    private javax.swing.JRadioButton rdoDangBan;
    private javax.swing.JRadioButton rdoNgungBan;
    private com.app.views.UI.panel.RoundPanel roundPanel1;
    private com.app.views.UI.panel.RoundPanel roundPanel2;
    private com.app.views.UI.panel.RoundPanel roundPanel3;
    private com.app.views.UI.panel.RoundPanel roundPanel4;
    private com.app.views.UI.label.SplitLine splitLine3;
    private javax.swing.JFormattedTextField txtGiaBan;
    private javax.swing.JFormattedTextField txtGiaNhap;
    private javax.swing.JTextArea txtMoTa;
    private javax.swing.JTextField txtTen;
    // End of variables declaration//GEN-END:variables

    private void handleClickButtonDanhMuc() {
        if (ModalDialog.isIdExist("handleClickButtonDanhMuc")) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListDanhMucView(), "Danh mục sản phẩm"), "handleClickButtonDanhMuc");
    }

    private void handleClickButtonThuongHieu() {
        if (ModalDialog.isIdExist("handleClickButtonThuongHieu")) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListThuongHieuView(), "Thương hiệu sản phẩm"), "handleClickButtonThuongHieu");
    }

    private void handleClickButtonXuatXu() {
        if (ModalDialog.isIdExist("handleClickButtonXuatXu")) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListXuatXuView(), "Xuất xứ sản phẩm"), "handleClickButtonXuatXu");
    }

    private void handleClickButtonKieuDang() {
        if (ModalDialog.isIdExist("handleClickButtonKieuDang")) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListKieuDangView(), "Kiểu dáng sản phẩm"), "handleClickButtonKieuDang");
    }

    private void handleClickButtonChatLieu() {
        if (ModalDialog.isIdExist("handleClickButtonChatLieu")) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListChatLieuView(), "Chất liệu sản phẩm"), "handleClickButtonChatLieu");
    }

    private void handleClickButtonDeGiay() {
        if (ModalDialog.isIdExist("handleClickButtonDeGiay")) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListDeGiayView(), "Đế giày sản phẩm"), "handleClickButtonDeGiay");
    }

    private void handleClickButtonImage() {
        JnaFileChooser ch = new JnaFileChooser();
        ch.addFilter("Hình ảnh", "png", "jpg", "jpeg");
        boolean act = ch.showOpenDialog(SwingUtilities.getWindowAncestor(this));
        if (act) {
            File file = ch.getSelectedFile();


            executorService.submit(() -> {
                try {
                    ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
                    Image image = imageIcon.getImage().getScaledInstance(pictureBox.getWidth(), pictureBox.getHeight(), Image.SCALE_SMOOTH);
                    loading.dispose();
                    pictureBox.setImage(new ImageIcon(image));
                    pictureBox.putClientProperty("path-image", file.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    loading.dispose();
                    MessageToast.error(ErrorConstant.DEFAULT_ERROR);
                } 
            });
            loading.setVisible(true);

        }

    }

    private void handleClickButtonSubmit() {
        String ten = txtTen.getText().trim();
	String giaNhap = txtGiaNhap.getText().trim();
        String giaBan = txtGiaBan.getText().trim();
        boolean trangThai = rdoDangBan.isSelected();
        String moTa = txtMoTa.getText().trim();
        ComboBoxItem<Integer> danhMuc = (ComboBoxItem<Integer>) cboDanhMuc.getSelectedItem();
        ComboBoxItem<Integer> thuongHieu = (ComboBoxItem<Integer>) cboThuongHieu.getSelectedItem();
        ComboBoxItem<Integer> xuatXu = (ComboBoxItem<Integer>) cboXuatXu.getSelectedItem();
        ComboBoxItem<Integer> kieuDang = (ComboBoxItem<Integer>) cboKieuDang.getSelectedItem();
        ComboBoxItem<Integer> chatLieu = (ComboBoxItem<Integer>) cboChatLieu.getSelectedItem();
        ComboBoxItem<Integer> deGiay = (ComboBoxItem<Integer>) cboDeGiay.getSelectedItem();
        String selectHinhAnh = (String) pictureBox.getClientProperty("path-image");
                
        ten = ten.replaceAll("\\s+"," ");
        
        lblTen.setForeground(ColorUtils.DANGER_COLOR);
        if (ten.isEmpty()) { 
            MessageToast.error("Tên sản phẩm không được bỏ trống");
            txtTen.requestFocus();
            return;
        }
        if (ten.length() > 250) { 
            MessageToast.error("Tên sản phẩm không được vượt quá 250 ký tự");
            txtTen.requestFocus();
            return;
        }
        
        if (ValidateUtils.isSpecialCharacters(ten)) {
            MessageToast.error("Tên sản phẩm không được chứa ký tự đặc biệt");
            return;
        }
        
        lblTen.setForeground(currentColor);
	
        lblGiaNhap.setForeground(ColorUtils.DANGER_COLOR);
        if (giaNhap.isEmpty()) { 
            MessageToast.error("Giá nhập không hợp lệ");
            txtGiaNhap.requestFocus();
            return;
        }
	try {
	    CurrencyUtils.parseNumber(giaNhap);
	} catch (NumberFormatException e) { 
	    MessageToast.error("Giá nhập vượt quá giới hạn cho phép");
	    txtGiaNhap.requestFocus();
	    return;
	}
        lblGiaNhap.setForeground(currentColor);
	
        lblGiaBan.setForeground(ColorUtils.DANGER_COLOR);

	try {
	    if (giaBan.isEmpty() || CurrencyUtils.parseNumber(giaBan) < 1) { 
		MessageToast.error("Giá bán không hợp lệ");
		txtGiaBan.requestFocus();
		return;
	    }
            
            if (CurrencyUtils.parseNumber(giaBan) <= CurrencyUtils.parseNumber(giaNhap)) { 
		MessageToast.error("Giá bán phải lớn hơn giá nhập");
		txtGiaBan.requestFocus();
		return;
	    }
	} catch (NumberFormatException e) { 
	    MessageToast.error("Giá bán vượt quá giới hạn cho phép");
	    txtGiaBan.requestFocus();
	    return;
	}
        lblGiaBan.setForeground(currentColor);

        lblMoTa.setForeground(ColorUtils.DANGER_COLOR);
        if (moTa.length() > 2000) { 
            MessageToast.error("Mô tả không được vượt quá 2000 ký tự");
            txtMoTa.requestFocus();
            return;
        }
        lblMoTa.setForeground(currentColor);
        
        lblDanhMuc.setForeground(ColorUtils.DANGER_COLOR);
        if (danhMuc.getValue() < 0) { 
            MessageToast.error("Vui lòng chọn một danh mục");
            cboDanhMuc.requestFocus();
            return;
        }
        lblDanhMuc.setForeground(currentColor);
        
        lblThuongHieu.setForeground(ColorUtils.DANGER_COLOR);
        if (thuongHieu.getValue() < 0) { 
            MessageToast.error("Vui lòng chọn một thương hiệu");
            cboThuongHieu.requestFocus();
            return;
        }
        lblThuongHieu.setForeground(currentColor);
        
        lblXuatXu.setForeground(ColorUtils.DANGER_COLOR);
        if (xuatXu.getValue() < 0) { 
            MessageToast.error("Vui lòng chọn một xuất xứ");
            cboXuatXu.requestFocus();
            return;
        }
        lblXuatXu.setForeground(currentColor);
        
        lblKieuDang.setForeground(ColorUtils.DANGER_COLOR);
        if (kieuDang.getValue() < 0) { 
            MessageToast.error("Vui lòng chọn một kiểu dáng");
            cboKieuDang.requestFocus();
            return;
        }
        lblKieuDang.setForeground(currentColor);
        
        lblChatLieu.setForeground(ColorUtils.DANGER_COLOR);
        if (chatLieu.getValue() < 0) { 
            MessageToast.error("Vui lòng chọn một chất liệu");
            cboChatLieu.requestFocus();
            return;
        }
        lblChatLieu.setForeground(currentColor);
        
        lblDeGiay.setForeground(ColorUtils.DANGER_COLOR);
        if (deGiay.getValue() < 0) { 
            MessageToast.error("Vui lòng chọn một đế giày");
            cboDeGiay.requestFocus();
            return;
        }
        lblDeGiay.setForeground(currentColor);
        
        lblHinhAnh.setForeground(ColorUtils.DANGER_COLOR);
        if (selectHinhAnh == null || selectHinhAnh.isEmpty()) { 
            MessageToast.error("Vui lòng chọn một hình ảnh");
            btnUploadImage.requestFocus();
            return;
        }
        lblHinhAnh.setForeground(currentColor);
        
        boolean isEdited = this.sanPham != null;
                
        String ma = !isEdited ? ProductUtils.generateCodeSanPham() : sanPham.getMa();

        InuhaDanhMucModel danhMucModel = new InuhaDanhMucModel();
        danhMucModel.setId(danhMuc.getValue());
        
        InuhaThuongHieuModel thuongHieuModel = new InuhaThuongHieuModel();
        thuongHieuModel.setId(thuongHieu.getValue());
        
        InuhaXuatXuModel xuatXuModel = new InuhaXuatXuModel();
        xuatXuModel.setId(xuatXu.getValue());
        
        InuhaKieuDangModel kieuDangModel = new InuhaKieuDangModel();
        kieuDangModel.setId(kieuDang.getValue());
        
        InuhaChatLieuModel chatLieuModel = new InuhaChatLieuModel();
        chatLieuModel.setId(chatLieu.getValue());
        
        InuhaDeGiayModel deGiayModel = new InuhaDeGiayModel();
        deGiayModel.setId(deGiay.getValue());
        
        InuhaSanPhamModel model = new InuhaSanPhamModel();
        model.setMa(ma);
        model.setTen(ten);
	model.setGiaNhap(Double.parseDouble(String.valueOf(CurrencyUtils.parseNumber(giaNhap))));
        model.setGiaBan(Double.parseDouble(String.valueOf(CurrencyUtils.parseNumber(giaBan))));
        model.setTrangThai(trangThai);
        model.setMoTa(moTa);
        model.setDanhMuc(danhMucModel);
        model.setThuongHieu(thuongHieuModel);
        model.setXuatXu(xuatXuModel);
        model.setKieuDang(kieuDangModel);
        model.setChatLieu(chatLieuModel);
        model.setDeGiay(deGiayModel);
        
        if (isEdited) { 
            model.setId(sanPham.getId());
            model.setNgayTao(sanPham.getNgayTao());
            model.setTrangThaiXoa(sanPham.isTrangThaiXoa());
            model.setHinhAnh(sanPham.getHinhAnh());
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return MessageModal.confirmInfo(isEdited ? "Lưu lại thông tin sản phẩm?" : "Thêm mới sản phẩm này?");
            }

            @Override
            protected void done() {
                
                try {
                    if (get()) {
                        executorService.submit(() -> {
                            
                            
                            String pathImage = ProductUtils.uploadImage(ma, selectHinhAnh);
                            MessageToast.clearAll();
                            
                            if (pathImage == null) {
                                loading.dispose();
                                MessageToast.error("Không thể upload hình ảnh!");
                                return;
                            }
                            
                            model.setHinhAnh(pathImage);
                            
                            try {
                                if (!isEdited) {
                                    sanPhamService.insert(model);
                                    MessageToast.success("Thêm mới sản phẩm thành công.");
                                    InuhaSanPhamView.getInstance().loadDataPage(1);
                                    InuhaSanPhamView.getInstance().loadDataPageSPCT(1);
                                } else {
                                    sanPhamService.update(model);
                                    if (sanPham.isTrangThai() != trangThai && !trangThai) {
                                        List<InuhaHoaDonChiTietModel> sanPhamCho = InuhaHoaDonChiTietRepository.getInstance().getAllIdsByIdSanPham(sanPham.getId());
                                        for(InuhaHoaDonChiTietModel m: sanPhamCho) {
                                            InuhaHoaDonChiTietService.getInstance().delete(m);
                                        }
                                    }
                                    MessageToast.success("Lưu thông tin sản phẩm thành công.");
                                    InuhaSanPhamView.getInstance().loadDataPage();
                                    InuhaSanPhamView.getInstance().loadDataPageSPCT();
                                }
                                
                                ModalDialog.closeAllModal();
                            } catch (ServiceResponseException e) {
                                e.printStackTrace();
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

    private void handleClickButtonDetail() {
        if (ModalDialog.isIdExist("handleClickButtonDetail")) {
            return;
        }
        ModalDialog.closeAllModal();
        ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaDetailSanPhamView(this.sanPham), "Chi tiết sản phẩm"), "handleClickButtonDetail");
    }
    
}
