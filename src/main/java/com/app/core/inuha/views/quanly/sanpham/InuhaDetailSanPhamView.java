package com.app.core.inuha.views.quanly.sanpham;

import com.app.common.helper.ExcelHelper;
import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.helper.QrCodeHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.models.sanpham.InuhaMauSacModel;
import com.app.core.inuha.request.InuhaFilterSanPhamChiTietRequest;
import com.app.core.inuha.services.InuhaKichCoService;
import com.app.core.inuha.services.InuhaMauSacService;
import com.app.core.inuha.services.InuhaSanPhamChiTietService;
import com.app.core.inuha.views.quanly.InuhaSanPhamView;
import com.app.core.inuha.views.quanly.components.table.soluongton.InuhaSoLuongTonSanPhamTableCellRender;
import com.app.core.inuha.views.quanly.components.table.trangthai.InuhaTrangThaiSanPhamTableCellRender;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.ProductUtils;
import com.app.utils.QrCodeUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.TimeUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.picturebox.DefaultPictureBoxRender;
import com.app.views.UI.picturebox.PictureBox;
import com.app.views.UI.picturebox.SuperEllipse2D;
import com.app.views.UI.table.TableCustomUI;
import com.app.views.UI.table.celll.CheckBoxTableHeaderRenderer;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import com.app.views.UI.table.ITableActionEvent;
import com.app.views.UI.table.celll.TableActionCellEditor;
import com.app.views.UI.table.celll.TableActionCellRender;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import static java.time.Instant.now;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author inuHa
 */
public class InuhaDetailSanPhamView extends JPanel {

    private static InuhaDetailSanPhamView instance;
        
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    private final static InuhaSanPhamChiTietService sanPhamChiTietService = InuhaSanPhamChiTietService.getInstance();
        
    private final static InuhaKichCoService kichCoService = InuhaKichCoService.getInstance();
    
    private final static InuhaMauSacService mauSacService = InuhaMauSacService.getInstance();
    
    private List<InuhaKichCoModel> dataKichCo = new ArrayList<>();
    
    private List<InuhaMauSacModel> dataMauSac = new ArrayList<>();
    
    public Pagination pagination = new Pagination();
    
    private int sizePage = pagination.getLimitItem();
    
    private List<InuhaSanPhamChiTietModel> dataItems = new ArrayList<>();
    
    private InuhaSanPhamModel sanPham = null;
    
    private final LoadingDialog loading = new LoadingDialog();
    
    /**
     * Creates new form InuhaSanPhamView
     */
    
    public static InuhaDetailSanPhamView getInstance() { 
        return getInstance(null);
    }
    
    public static InuhaDetailSanPhamView getInstance(InuhaSanPhamModel sanPham) { 
        if (instance == null) { 
            instance = new InuhaDetailSanPhamView(sanPham);
        }
        return instance;
    }

    private boolean reLoad = true;
    
    public InuhaDetailSanPhamView(InuhaSanPhamModel sanPham) {
        initComponents();
        instance = this;
	
	if (sanPham == null) { 
	    return;
	}
	
        this.sanPham = sanPham;
        
        pnlQR.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	
	pnlList.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlInfo.setOpaque(false);

	
	lblTenSanPham.setForeground(ColorUtils.PRIMARY_COLOR);
	
	btnEdit.setIcon(ResourceUtils.getSVG("/svg/edit.svg", new Dimension(20, 20)));
	btnSaveQR.setIcon(ResourceUtils.getSVG("/svg/save.svg", new Dimension(20, 20)));
	btnScanQR.setIcon(ResourceUtils.getSVG("/svg/qr.svg", new Dimension(20, 20)));
	btnExport.setIcon(ResourceUtils.getSVG("/svg/export.svg", new Dimension(20, 20)));
	btnReset.setIcon(ResourceUtils.getSVG("/svg/reload.svg", new Dimension(20, 20)));
        
        btnThemSanPhamChiTiet.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        
        btnThemSanPhamChiTiet.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnThemSanPhamChiTiet.setForeground(Color.WHITE);
        
        cboTrangThai.removeAllItems();
        cboTrangThai.addItem(new ComboBoxItem<>("-- Tất cả trạng thái --", -1));
        cboTrangThai.addItem(new ComboBoxItem<>("Đang bán", 1));
        cboTrangThai.addItem(new ComboBoxItem<>("Ngừng bán", 0));
        
	cboSoLuong.removeAllItems();
        cboSoLuong.addItem(new ComboBoxItem<>("-- Tất cả số lượng --", -1));
        cboSoLuong.addItem(new ComboBoxItem<>("Còn hàng", 1));
        cboSoLuong.addItem(new ComboBoxItem<>("Hết hàng", 0));
	
        pictureBox.setImage(QrCodeHelper.getImage(QrCodeUtils.generateCodeSanPham(sanPham.getId())));
        pictureBox.setBoxFit(PictureBox.BoxFit.CONTAIN);
        pictureBox.setPictureBoxRender(new DefaultPictureBoxRender() {
            @Override
            public Shape render(Rectangle rec) {
                return new SuperEllipse2D(rec.x, rec.y, rec.width, rec.height, 12f).getShape();
            }
        });
        
        
        lblTenSanPham.setText(sanPham.getTen());
        lblTrangThai.setText(ProductUtils.getTrangThai(sanPham.isTrangThai()));
	lblGiaNhap.setText(CurrencyUtils.parseString(sanPham.getGiaNhap()));
        lblGiaBan.setText(CurrencyUtils.parseString(sanPham.getGiaBan()));
        lblDanhMuc.setText(sanPham.getDanhMuc().getTen());
        lblThuongHieu.setText(sanPham.getThuongHieu().getTen());
        lblXuatXu.setText(sanPham.getXuatXu().getTen());
        lblKieuDang.setText(sanPham.getKieuDang().getTen());
        lblChatLieu.setText(sanPham.getChatLieu().getTen());
        lblDeGiay.setText(sanPham.getDeGiay().getTen());
        
        lblTrangThai.setForeground(sanPham.isTrangThai() ? ColorUtils.SUCCESS_COLOR : ColorUtils.DANGER_COLOR);
//	lblTT.setForeground(ColorUtils.PRIMARY_COLOR);
//	lblGN.setForeground(ColorUtils.PRIMARY_COLOR);
//        lblGB.setForeground(ColorUtils.PRIMARY_COLOR);
//        lblDM.setForeground(ColorUtils.PRIMARY_COLOR);
//        lblTH.setForeground(ColorUtils.PRIMARY_COLOR);
//        lblXX.setForeground(ColorUtils.PRIMARY_COLOR);
//        lblKD.setForeground(ColorUtils.PRIMARY_COLOR);
//        lblCL.setForeground(ColorUtils.PRIMARY_COLOR);
//        lblDG.setForeground(ColorUtils.PRIMARY_COLOR);
        
	Dimension cboSize = new Dimension(150, 36);
	cboKichCo.setPreferredSize(cboSize);
	cboMauSac.setPreferredSize(cboSize);
	cboTrangThai.setPreferredSize(cboSize);
	cboSoLuong.setPreferredSize(cboSize);
	
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadDataKichCo();
                loadDataMauSac();

                setupTable(tblDanhSach);
                loadDataPage(1);
                setupPagination();
                return null;
            }

            @Override
            protected void done() {
                reLoad = false;
                loading.dispose();
            }
            
        };
        worker.execute();
	loading.setVisible(true);
    }
    
    private void setupTable(JTable table) { 
        
        ITableActionEvent event = new ITableActionEvent() {
            @Override
            public void onEdit(int row) {
                InuhaSanPhamChiTietModel item = dataItems.get(row);
                showEdit(item);
            }

            @Override
            public void onDelete(int row) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                InuhaSanPhamChiTietModel item = dataItems.get(row);
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return MessageModal.confirmWarning("Xoá sản phẩm chi tiết ", "Bạn thực sự muốn xoá sản phẩm chi tiết này?");
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                executorService.submit(() -> {
                                    try {
                                        sanPhamChiTietService.delete(item.getId());
                                        InuhaSanPhamView.getInstance().loadDataPage();
                                        InuhaSanPhamView.getInstance().loadDataPageSPCT();
                                        loadDataPage();
                                        MessageToast.success("Xoá thành công sản phẩm chi tiết");
                                    } catch (ServiceResponseException e) {
                                        MessageToast.error(e.getMessage());
                                    } catch (Exception e) {
                                        MessageModal.error(ErrorConstant.DEFAULT_ERROR);
                                    }  finally {
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

            @Override
            public void onView(int row) {
		InuhaSanPhamChiTietModel item = dataItems.get(row);
		handleClickButtomView(item);
            }
        };
        pnlDanhSach.setRound(0, 0, 0, 0);
        TableCustomUI.apply(scrDanhSach, TableCustomUI.TableType.DEFAULT);

        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
	table.getColumnModel().getColumn(5).setCellRenderer(new InuhaSoLuongTonSanPhamTableCellRender(table));
	table.getColumnModel().getColumn(6).setCellRenderer(new InuhaTrangThaiSanPhamTableCellRender(table));
        table.getColumnModel().getColumn(7).setCellRenderer(new TableActionCellRender(table));
        table.getColumnModel().getColumn(7).setCellEditor(new TableActionCellEditor(event));
    }
    

    public void loadDataPage() { 
        loadDataPage(pagination.getCurrentPage());
    }
    
    public void loadDataPage(int page) { 
        if (this.sanPham == null) {
            return;
        }
        
        try {
            DefaultTableModel model = (DefaultTableModel) tblDanhSach.getModel();
            if (tblDanhSach.isEditing()) {
                tblDanhSach.getCellEditor().stopCellEditing();
            }
            
            model.setRowCount(0);
            
            ComboBoxItem<Integer> kichCo = new ComboBoxItem<>();
            ComboBoxItem<Integer> mauSac = new ComboBoxItem<>();
            ComboBoxItem<Integer> trangThai = new ComboBoxItem<>();
	    ComboBoxItem<Integer> soLuong = new ComboBoxItem<>();
            
            try {
                kichCo = (ComboBoxItem<Integer>) cboKichCo.getSelectedItem();
                mauSac = (ComboBoxItem<Integer>) cboMauSac.getSelectedItem();
                trangThai = (ComboBoxItem<Integer>) cboTrangThai.getSelectedItem();
                soLuong = (ComboBoxItem<Integer>) cboSoLuong.getSelectedItem(); 
            } catch (Exception e) {
                
            }

            InuhaFilterSanPhamChiTietRequest request = new InuhaFilterSanPhamChiTietRequest();
	    request.setIdSanPham(this.sanPham.getId());
	    request.setKichCo(kichCo);
	    request.setMauSac(mauSac);
	    request.setTrangThai(trangThai);
	    request.setSoLuong(soLuong);
	    
            request.setSize(sizePage);
	    
            int totalPages = sanPhamChiTietService.getTotalPage(request);
            
            if (totalPages < page) { 
                page = totalPages;
            }
            
            request.setPage(page);
           
            dataItems = sanPhamChiTietService.getPage(request);
            
            for(InuhaSanPhamChiTietModel m: dataItems) { 
                model.addRow(m.toDataRowSanPhamChiTiet());
            }

            rerenderPagination(page, totalPages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    
    private void setupPagination() { 
        pagination.setPanel(pnlPhanTrang);
        pagination.setCallback(new Pagination.Callback() {
            @Override
            public void onChangeLimitItem(JComboBox<Integer> comboBox) {
                sizePage = (int) comboBox.getSelectedItem();
		executorService.submit(() -> { 
		    loadDataPage(1);
		    loading.dispose();
		});
                loading.setVisible(true);
            }

            @Override
            public void onClickPage(int page) {
		executorService.submit(() -> { 
		    loadDataPage(page);
		    loading.dispose();
		});
                loading.setVisible(true);
            }
        });
        pagination.render();
    }
        
    private void rerenderPagination(int currentPage, int totalPages) { 
	pagination.rerender(currentPage, totalPages);
    }
   
    public void loadDataKichCo() { 
        reLoad = true;
        dataKichCo = kichCoService.getAll();
        cboKichCo.removeAllItems();
        
        cboKichCo.addItem(new ComboBoxItem<>("-- Tất cả kích cỡ --", -1));
        for(InuhaKichCoModel m: dataKichCo) { 
            cboKichCo.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        reLoad = false;
    }
    
    public void loadDataMauSac() { 
        reLoad = true;
        dataMauSac = mauSacService.getAll();
        cboMauSac.removeAllItems();
        
        cboMauSac.addItem(new ComboBoxItem<>("-- Tất cả màu sắc --", -1));
        for(InuhaMauSacModel m: dataMauSac) { 
            cboMauSac.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        reLoad = false;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlInfo = new com.app.views.UI.panel.RoundPanel();
        lblTenSanPham = new javax.swing.JLabel();
        lblGB = new javax.swing.JLabel();
        lblGiaBan = new javax.swing.JLabel();
        lblTrangThai = new javax.swing.JLabel();
        lblTT = new javax.swing.JLabel();
        lblDM = new javax.swing.JLabel();
        lblDanhMuc = new javax.swing.JLabel();
        lblTH = new javax.swing.JLabel();
        lblThuongHieu = new javax.swing.JLabel();
        lblXX = new javax.swing.JLabel();
        lblXuatXu = new javax.swing.JLabel();
        lblKieuDang = new javax.swing.JLabel();
        lblKD = new javax.swing.JLabel();
        lblCL = new javax.swing.JLabel();
        lblChatLieu = new javax.swing.JLabel();
        lblDG = new javax.swing.JLabel();
        lblDeGiay = new javax.swing.JLabel();
        lblGiaNhap = new javax.swing.JLabel();
        lblGN = new javax.swing.JLabel();
        pnlQR = new com.app.views.UI.panel.RoundPanel();
        pictureBox = new com.app.views.UI.picturebox.PictureBox();
        btnScanQR = new javax.swing.JButton();
        btnSaveQR = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        pnlList = new com.app.views.UI.panel.RoundPanel();
        btnThemSanPhamChiTiet = new javax.swing.JButton();
        pnlPhanTrang = new javax.swing.JPanel();
        pnlDanhSach = new com.app.views.UI.panel.RoundPanel();
        scrDanhSach = new javax.swing.JScrollPane();
        tblDanhSach = new javax.swing.JTable();
        cboKichCo = new javax.swing.JComboBox();
        cboMauSac = new javax.swing.JComboBox();
        btnExport = new javax.swing.JButton();
        cboTrangThai = new javax.swing.JComboBox();
        btnReset = new javax.swing.JButton();
        cboSoLuong = new javax.swing.JComboBox();

        lblTenSanPham.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTenSanPham.setText("Thông tin sản phẩm");
        lblTenSanPham.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lblGB.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblGB.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblGB.setText("Giá bán:");

        lblGiaBan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblGiaBan.setText("100.000đ");

        lblTrangThai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTrangThai.setText("Đang bán");

        lblTT.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTT.setText("Trạng thái:");

        lblDM.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDM.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDM.setText("Danh mục:");

        lblDanhMuc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDanhMuc.setText("Thời trang");

        lblTH.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTH.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTH.setText("Thương hiệu:");

        lblThuongHieu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblThuongHieu.setText("Adidas");

        lblXX.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblXX.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblXX.setText("Xuất xứ:");

        lblXuatXu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblXuatXu.setText("Nhật bản");

        lblKieuDang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblKieuDang.setText("Nam");

        lblKD.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblKD.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblKD.setText("Kiểu dáng:");

        lblCL.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCL.setText("Chất liệu:");

        lblChatLieu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblChatLieu.setText("Real 1:1");

        lblDG.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDG.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDG.setText("Đế giày:");

        lblDeGiay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDeGiay.setText("Đế bằng");

        lblGiaNhap.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblGiaNhap.setText("100.000đ");

        lblGN.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblGN.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblGN.setText("Giá nhập:");

        btnScanQR.setText("Quét QR");
        btnScanQR.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnScanQR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanQRActionPerformed(evt);
            }
        });

        btnSaveQR.setText("Lưu QR Code");
        btnSaveQR.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSaveQR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveQRActionPerformed(evt);
            }
        });

        btnEdit.setText("Chỉnh sửa");
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlQRLayout = new javax.swing.GroupLayout(pnlQR);
        pnlQR.setLayout(pnlQRLayout);
        pnlQRLayout.setHorizontalGroup(
            pnlQRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQRLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pictureBox, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlQRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnScanQR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaveQR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        pnlQRLayout.setVerticalGroup(
            pnlQRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQRLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlQRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlQRLayout.createSequentialGroup()
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(btnSaveQR, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnScanQR, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pictureBox, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlInfoLayout = new javax.swing.GroupLayout(pnlInfo);
        pnlInfo.setLayout(pnlInfoLayout);
        pnlInfoLayout.setHorizontalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addComponent(pnlQR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lblTenSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 762, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDM, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTT, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTH))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblThuongHieu, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblDanhMuc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(lblTrangThai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblGB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblKD))
                            .addComponent(lblXX, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblXuatXu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblGiaBan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblKieuDang, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lblGN)
                                .addComponent(lblCL, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblDG, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDeGiay, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblChatLieu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblGiaNhap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnlInfoLayout.setVerticalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlQR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addComponent(lblTenSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTT)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addComponent(lblTrangThai)
                                .addGap(18, 18, 18)
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDM)
                                    .addComponent(lblXX, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblXuatXu, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblCL, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblChatLieu))
                                .addGap(18, 18, 18)
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblThuongHieu)
                                    .addComponent(lblTH)
                                    .addComponent(lblKD)
                                    .addComponent(lblKieuDang)
                                    .addComponent(lblDG)
                                    .addComponent(lblDeGiay)))
                            .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblGN)
                                .addComponent(lblGiaNhap))
                            .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblGB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblGiaBan)))
                        .addContainerGap())))
        );

        btnThemSanPhamChiTiet.setText("Thêm mới");
        btnThemSanPhamChiTiet.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThemSanPhamChiTiet.setMaximumSize(new java.awt.Dimension(50, 23));
        btnThemSanPhamChiTiet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemSanPhamChiTietActionPerformed(evt);
            }
        });

        pnlPhanTrang.setOpaque(false);

        javax.swing.GroupLayout pnlPhanTrangLayout = new javax.swing.GroupLayout(pnlPhanTrang);
        pnlPhanTrang.setLayout(pnlPhanTrangLayout);
        pnlPhanTrangLayout.setHorizontalGroup(
            pnlPhanTrangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPhanTrangLayout.setVerticalGroup(
            pnlPhanTrangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        tblDanhSach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "#", "Mã chi tiết", "Kích cỡ", "Màu sắc", "Số lượng", "Trạng thái", "Hành động"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSach.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSach.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSach.setShowGrid(true);
        tblDanhSach.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachMouseClicked(evt);
            }
        });
        scrDanhSach.setViewportView(tblDanhSach);
        if (tblDanhSach.getColumnModel().getColumnCount() > 0) {
            tblDanhSach.getColumnModel().getColumn(0).setMaxWidth(30);
            tblDanhSach.getColumnModel().getColumn(1).setMaxWidth(50);
            tblDanhSach.getColumnModel().getColumn(7).setMinWidth(120);
        }

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        );

        cboKichCo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả kích cỡ --" }));
        cboKichCo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboKichCoItemStateChanged(evt);
            }
        });

        cboMauSac.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả màu sắc --" }));
        cboMauSac.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboMauSacItemStateChanged(evt);
            }
        });

        btnExport.setText("Xuất Excel");
        btnExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả trạng thái --" }));
        cboTrangThai.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTrangThaiItemStateChanged(evt);
            }
        });

        btnReset.setText("Làm mới");
        btnReset.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        cboSoLuong.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả số lượng --" }));
        cboSoLuong.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboSoLuongItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlListLayout = new javax.swing.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDanhSach, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createSequentialGroup()
                        .addComponent(cboKichCo, 0, 180, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(cboMauSac, 0, 154, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(cboTrangThai, 0, 160, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReset)
                        .addGap(65, 65, 65)
                        .addComponent(btnExport)
                        .addGap(18, 18, 18)
                        .addComponent(btnThemSanPhamChiTiet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlPhanTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnThemSanPhamChiTiet, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboKichCo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(pnlPhanTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(pnlInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblDanhSachMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachMouseClicked
        // TODO add your handling code here:
	if (evt.getClickCount() > 1) {
            InuhaSanPhamChiTietModel item = dataItems.get(tblDanhSach.getSelectedRow());
            showEdit(item);
	    return;
	}
        List<Integer> columns = List.of(0, 7);
        if (SwingUtilities.isLeftMouseButton(evt)) { 
            int row = tblDanhSach.rowAtPoint(evt.getPoint());
            int col = tblDanhSach.columnAtPoint(evt.getPoint());
            boolean isSelected = (boolean) tblDanhSach.getValueAt(row, 0);
            if (!columns.contains(col)) { 
                tblDanhSach.setValueAt(!isSelected, row, 0);
            }
        }
    }//GEN-LAST:event_tblDanhSachMouseClicked

    private void btnThemSanPhamChiTietActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemSanPhamChiTietActionPerformed
        // TODO add your handling code here:
        handleClickButtonAdd();
    }//GEN-LAST:event_btnThemSanPhamChiTietActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        handleClickButtonReset();
    }//GEN-LAST:event_btnResetActionPerformed

    private void cboKichCoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboKichCoItemStateChanged
        // TODO add your handling code here:
        handleFilter(evt);
    }//GEN-LAST:event_cboKichCoItemStateChanged

    private void cboMauSacItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboMauSacItemStateChanged
        // TODO add your handling code here:
        handleFilter(evt);
    }//GEN-LAST:event_cboMauSacItemStateChanged

    private void cboTrangThaiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTrangThaiItemStateChanged
        // TODO add your handling code here:
        handleFilter(evt);
    }//GEN-LAST:event_cboTrangThaiItemStateChanged

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
	handleClickButtonExport();
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnSaveQRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveQRActionPerformed
        // TODO add your handling code here:
        handleClickButtonSaveQR();
    }//GEN-LAST:event_btnSaveQRActionPerformed

    private void btnScanQRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanQRActionPerformed
        // TODO add your handling code here:
        handleClickButtonScanQrCode();
    }//GEN-LAST:event_btnScanQRActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:h
	handleClickButtonEdit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void cboSoLuongItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboSoLuongItemStateChanged
        // TODO add your handling code here:
	handleFilter(evt);
    }//GEN-LAST:event_cboSoLuongItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSaveQR;
    private javax.swing.JButton btnScanQR;
    private javax.swing.JButton btnThemSanPhamChiTiet;
    private javax.swing.JComboBox cboKichCo;
    private javax.swing.JComboBox cboMauSac;
    private javax.swing.JComboBox cboSoLuong;
    private javax.swing.JComboBox cboTrangThai;
    private javax.swing.JLabel lblCL;
    private javax.swing.JLabel lblChatLieu;
    private javax.swing.JLabel lblDG;
    private javax.swing.JLabel lblDM;
    private javax.swing.JLabel lblDanhMuc;
    private javax.swing.JLabel lblDeGiay;
    private javax.swing.JLabel lblGB;
    private javax.swing.JLabel lblGN;
    private javax.swing.JLabel lblGiaBan;
    private javax.swing.JLabel lblGiaNhap;
    private javax.swing.JLabel lblKD;
    private javax.swing.JLabel lblKieuDang;
    private javax.swing.JLabel lblTH;
    private javax.swing.JLabel lblTT;
    private javax.swing.JLabel lblTenSanPham;
    private javax.swing.JLabel lblThuongHieu;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JLabel lblXX;
    private javax.swing.JLabel lblXuatXu;
    private com.app.views.UI.picturebox.PictureBox pictureBox;
    private com.app.views.UI.panel.RoundPanel pnlDanhSach;
    private com.app.views.UI.panel.RoundPanel pnlInfo;
    private com.app.views.UI.panel.RoundPanel pnlList;
    private javax.swing.JPanel pnlPhanTrang;
    private com.app.views.UI.panel.RoundPanel pnlQR;
    private javax.swing.JScrollPane scrDanhSach;
    private javax.swing.JTable tblDanhSach;
    // End of variables declaration//GEN-END:variables

    private void handleClickButtonExport() {
        String fileName = sanPham.getMa() + "-" + TimeUtils.now("dd_MM_yyyy__hh_mm_a");
	String[] headers = new String[] {
	    "STT",
	    "Mã chi tiết",
	    "Mã sản phẩm",
	    "Tên sản phẩm",
	    "Kích cỡ",
	    "Màu sắc",
	    "Số lượng",
	    "Trạng thái"
	};

	executorService.submit(() -> { 
	    List<InuhaSanPhamChiTietModel> items = findSelectedItems(tblDanhSach);
	    try {
		if (items.isEmpty()) { 
		    items = sanPhamChiTietService.getAll(sanPham.getId());
		}
		
		
		List<String[]> rows = new ArrayList<>();
		int i = 1;
		for(InuhaSanPhamChiTietModel item: items) { 
		    rows.add(new String[] { 
			String.valueOf(i++),
			item.getMa(),
			item.getSanPham().getMa(),
			item.getSanPham().getTen(),
			item.getKichCo().getTen(),
			item.getMauSac().getTen(),
			CurrencyUtils.parseTextField(item.getSoLuong()),
			ProductUtils.getTrangThai(item.getTrangThai())
		    });
		}
				
		ExcelHelper.writeFile(fileName, headers, rows);
	    } catch (ServiceResponseException e) {
		e.printStackTrace();
		MessageModal.error(e.getMessage());
	    } catch (Exception e) {
		e.printStackTrace();
		MessageModal.error(ErrorConstant.DEFAULT_ERROR);
	    } finally {
                loading.dispose();
            }
	});
	loading.setVisible(true);
    }
    
    private List<InuhaSanPhamChiTietModel> findSelectedItems(JTable table) {
        List<InuhaSanPhamChiTietModel> data = new ArrayList<>();
        for (int row = 0; row < table.getRowCount(); row++) {
            Boolean value = (Boolean) table.getValueAt(row, 0);
            if (Boolean.TRUE.equals(value)) {
                data.add(dataItems.get(row));
            }
        }
        return data;
    }

    private void handleClickButtonAdd() {
        if (ModalDialog.isIdExist(ID_MODAL_ADD)) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaAddSanPhamChiTietView(this.sanPham), "Thêm sản phẩm chi tiết"), ID_MODAL_ADD);
    }
    
    private void handleFilter(ItemEvent evt) {
        if (reLoad || (evt != null && evt.getStateChange() != ItemEvent.SELECTED)) { 
            return;
        }
	
        executorService.submit(() -> {
            loadDataPage();
            loading.dispose();
        });
        loading.setVisible(true);
    }
    
    private void handleClickButtonReset() {
        reLoad = true;
        executorService.submit(() -> {
            cboKichCo.setSelectedIndex(0);
            cboMauSac.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
	    cboSoLuong.setSelectedIndex(0);
            loadDataPage();
            loading.dispose();
            reLoad = false;
        });
        loading.setVisible(true);
    }
    
    private void handleClickButtonScanQrCode() {
        QrCodeHelper.showWebcam("Tìm kiếm bằng QR", result -> {

            executorService.submit(() -> {
                try {
                    String code = result.getText();

                    InuhaSanPhamChiTietModel sanPhamChiTietModel = null;
                    int id;
                    if ((id = QrCodeUtils.getIdSanPhamChiTiet(code)) > 0) {
                        sanPhamChiTietModel = sanPhamChiTietService.getById(id);
                    } else { 
                        loading.dispose();
                        MessageToast.error("QRCode không hợp lệ!!!");
                        return;
                    }
                    loading.dispose();
                    showDetail(sanPhamChiTietModel);                
                } catch (ServiceResponseException e) {
                    loading.dispose();
                    MessageModal.error(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    loading.dispose();
                    MessageModal.error(ErrorConstant.DEFAULT_ERROR);
                }
            });
            loading.setVisible(true);
        });

    }
    
    public final static String ID_MODAL_ADD = "add-sanphamchitiet";
    
    public final static String ID_MODAL_DEAIL = "detail-sanphamchitiet";

    private void showEdit(InuhaSanPhamChiTietModel item) {
        if (ModalDialog.isIdExist(ID_MODAL_ADD)) {
            return;
        }
        ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaAddSanPhamChiTietView(this.sanPham, item), "Chỉnh sửa chi tiết sản phẩm"), ID_MODAL_ADD);
    }

    private void showDetail(InuhaSanPhamChiTietModel item) {
        if (ModalDialog.isIdExist(ID_MODAL_DEAIL)) {
            return;
        }
        ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaDetailSanPhamChiTietView(item), null), ID_MODAL_DEAIL);
    }
	
    private void handleClickButtonSaveQR() {
        QrCodeHelper.save(QrCodeUtils.generateCodeSanPham(this.sanPham.getId()), this.sanPham.getMa());
    }
    
    private void handleClickButtomView(InuhaSanPhamChiTietModel item) {
        if (ModalDialog.isIdExist("handleClickButtomView")) {
            return;
        }
	ModalDialog.showModal(this, new SimpleModalBorder(new InuhaDetailSanPhamChiTietView(item), null), "handleClickButtomView");
    }

    private void handleClickButtonEdit() {
        if (ModalDialog.isIdExist("handleClickButtonEdit")) {
            return;
        }
	ModalDialog.closeAllModal();
	ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaAddSanPhamView(sanPham), "Chỉnh sửa sản phẩm"), "handleClickButtonEdit");
    }
}
