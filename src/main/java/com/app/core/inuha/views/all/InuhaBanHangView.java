package com.app.core.inuha.views.all;

import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.helper.PdfHelper;
import com.app.common.helper.QrCodeHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.PhuongThucThanhToanConstant;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.common.models.invoice.InvoiceDataModel;
import com.app.core.common.models.invoice.InvoiceProduct;
import com.app.core.inuha.models.InuhaHoaDonChiTietModel;
import com.app.core.inuha.models.InuhaHoaDonModel;
import com.app.core.inuha.models.InuhaKhachHangModel;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaChatLieuModel;
import com.app.core.inuha.models.sanpham.InuhaDanhMucModel;
import com.app.core.inuha.models.sanpham.InuhaDeGiayModel;
import com.app.core.inuha.models.sanpham.InuhaKieuDangModel;
import com.app.core.inuha.models.sanpham.InuhaThuongHieuModel;
import com.app.core.inuha.models.sanpham.InuhaXuatXuModel;
import com.app.core.inuha.request.InuhaFilterSanPhamRequest;
import com.app.core.inuha.services.InuhaChatLieuService;
import com.app.core.inuha.services.InuhaDanhMucService;
import com.app.core.inuha.services.InuhaDeGiayService;
import com.app.core.inuha.services.InuhaHoaDonChiTietService;
import com.app.core.inuha.services.InuhaHoaDonService;
import com.app.core.inuha.services.InuhaKichCoService;
import com.app.core.inuha.services.InuhaKieuDangService;
import com.app.core.inuha.services.InuhaMauSacService;
import com.app.core.inuha.services.InuhaSanPhamChiTietService;
import com.app.core.inuha.services.InuhaSanPhamService;
import com.app.core.inuha.services.InuhaThuongHieuService;
import com.app.core.inuha.services.InuhaXuatXuService;
import com.app.core.inuha.views.all.banhang.InuhaAddGioHangView;
import com.app.core.inuha.views.all.banhang.InuhaConfirmHoaDonView;
import com.app.core.inuha.views.all.banhang.InuhaEditGioHangView;
import com.app.core.inuha.views.all.banhang.InuhaListKhachHangView;
import com.app.core.inuha.views.all.banhang.InuhaListPhieuGiamGiaView;
import com.app.core.inuha.views.quanly.components.table.soluongton.InuhaSoLuongTonSanPhamTableCellRender;
import com.app.utils.BillUtils;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.QrCodeUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.ThemeUtils;
import com.app.utils.TimeUtils;
import com.app.utils.VoucherUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.table.TableCustomUI;
import com.app.views.UI.table.celll.TableAlignCenterCellRender;
import com.app.views.UI.table.celll.TableImageCellRender;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import raven.popup.GlassPanePopup;

/**
 *
 * @author inuHa
 */
public class InuhaBanHangView extends javax.swing.JPanel {

    private static InuhaBanHangView instance;
        
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    private final static InuhaSanPhamService sanPhamService = InuhaSanPhamService.getInstance();
    
    private final static InuhaHoaDonService hoaDonService = InuhaHoaDonService.getInstance();
    
    private final static InuhaHoaDonChiTietService hoaDonChiTietService = InuhaHoaDonChiTietService.getInstance();
    
    private final static InuhaDanhMucService danhMucService = InuhaDanhMucService.getInstance();
    
    private final static InuhaThuongHieuService thuongHieuService = InuhaThuongHieuService.getInstance();
    
    private final static InuhaXuatXuService xuatXuService = InuhaXuatXuService.getInstance();
    
    private final static InuhaKieuDangService kieuDangService = InuhaKieuDangService.getInstance();
    
    private final static InuhaChatLieuService chatLieuService = InuhaChatLieuService.getInstance();
    
    private final static InuhaDeGiayService deGiayService = InuhaDeGiayService.getInstance();
    
    private final static InuhaKichCoService kichCoService = InuhaKichCoService.getInstance();
    
    private final static InuhaMauSacService mauSacService = InuhaMauSacService.getInstance();
    
    private List<InuhaDanhMucModel> dataDanhMuc = new ArrayList<>();
    
    private List<InuhaThuongHieuModel> dataThuongHieu = new ArrayList<>();
    
    private List<InuhaXuatXuModel> dataXuatXu = new ArrayList<>();
    
    private List<InuhaKieuDangModel> dataKieuDang = new ArrayList<>();
    
    private List<InuhaChatLieuModel> dataChatLieu = new ArrayList<>();
    
    private List<InuhaDeGiayModel> dataDeGiay = new ArrayList<>();
    
    private JTextField txtTuKhoa;
    
    public Pagination pagination = new Pagination();
	
    private int sizePage = pagination.getLimitItem();
    
    private List<InuhaHoaDonModel> dataItemsHoaDonCho = new ArrayList<>();
    
    private List<InuhaHoaDonChiTietModel> dataItemsGioHang = new ArrayList<>();
	
    private List<InuhaSanPhamModel> dataItemsSanPham = new ArrayList<>();
    
    private InuhaHoaDonModel currentHoaDon = null;
    
    private InuhaHoaDonChiTietModel currentHoaDonChiTiet = null;
    
    private InuhaPhieuGiamGiaModel currentPhieuGiamGia = null;
    
    private InuhaKhachHangModel currentKhachHang = null;
    
    private final LoadingDialog loading = new LoadingDialog();
    
    private boolean reLoad = true;
    
    private ChiTietThanhToan chiTietThanhToan = new ChiTietThanhToan();
    
    /** Creates new form BanHangView */
    public InuhaBanHangView() {
	initComponents();
	instance = this;
		
	handleClear();
		
	txtSoDienThoai.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Số điện thoại");
	txtTenKhachHang.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Khách lẻ");
	txtTienMat.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tiền mặt khách trả");
	txtTienChuyenKhoan.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tiền Khách chuyển khoản");
	
	lblTongThanhToan.setForeground(ColorUtils.PRIMARY_COLOR);
	txtTienMat.setFormatterFactory(CurrencyUtils.getDefaultFormat());
	txtTienChuyenKhoan.setFormatterFactory(CurrencyUtils.getDefaultFormat());
	
	txtTuKhoa = pnlSearchBox.getKeyword();
	txtTuKhoa.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo tên hoặc mã...");
	pnlHoaDon.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlSanPham.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlGioHang.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	btnSubmit.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnSubmit.setForeground(Color.WHITE);
	
	
	cboHinhThucThanhToan.removeAllItems();
	cboHinhThucThanhToan.addItem(new ComboBoxItem<Integer>("Thanh toán tiền mặt", PhuongThucThanhToanConstant.TIEN_MAT));
	cboHinhThucThanhToan.addItem(new ComboBoxItem<Integer>("Thanh toán chuyển khoản", PhuongThucThanhToanConstant.CHUYEN_KHOAN));
	cboHinhThucThanhToan.addItem(new ComboBoxItem<Integer>("Thanh toán tiền mặt và chuyển khoản", PhuongThucThanhToanConstant.KET_HOP));
	
	
	lblThongTinHoaDon.setIcon(ResourceUtils.getSVG("/svg/bill.svg", new Dimension(20, 20)));
	lblBill.setIcon(ResourceUtils.getSVG("/svg/bill.svg", new Dimension(20, 20)));
	lblProducts.setIcon(ResourceUtils.getSVG("/svg/shoes.svg", new Dimension(20, 20)));
	lblCart.setIcon(ResourceUtils.getSVG("/svg/cart.svg", new Dimension(20, 20)));
	btnReset.setIcon(ResourceUtils.getSVG("/svg/reload-n.svg", new Dimension(20, 20)));
	btnSubmit.setIcon(ResourceUtils.getSVG("/svg/paid.svg", new Dimension(24, 24)));
	btnCancel.setIcon(ResourceUtils.getSVG("/svg/times.svg", new Dimension(32, 32)));
	btnScanQr.setIcon(ResourceUtils.getSVG("/svg/qr.svg", new Dimension(20, 20)));
	btnRemoveSanPham.setIcon(ResourceUtils.getSVG("/svg/times.svg", new Dimension(20, 20)));
	
        if (ThemeUtils.isLight()) { 
            btnAddBill.setIcon(ResourceUtils.getSVG("/svg/plus-c.svg", new Dimension(20, 20)));
            
            btnScanQr.setBackground(ColorUtils.BUTTON_PRIMARY);
            btnScanQr.setForeground(Color.WHITE);
            
            btnReset.setBackground(ColorUtils.BUTTON_PRIMARY);
            
        } else  {
            btnAddBill.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        }
        
        btnSelectKhachHang.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnSelectKhachHang.setForeground(Color.WHITE);
        btnAddBill.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnAddBill.setForeground(Color.WHITE);

        
	pnlDanhSachSanPham.setRound(0, 0, 0, 0);
	pnlThongTinHoaDon.setMinimumSize(new Dimension(310, pnlThongTinHoaDon.getPreferredSize().height));
        
	txtTuKhoa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleSearch(null);
                }
            }
        });
	
	txtTienMat.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    handleClickButtonSubmit();
		    e.consume();
		}
	    }
	});
	
	txtTienChuyenKhoan.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    handleClickButtonSubmit();
		    e.consume();
		}
	    }
	});
	
	Dimension cboSize = new Dimension(100, 28);
	cboDanhMuc.setPreferredSize(cboSize);
	cboThuongHieu.setPreferredSize(cboSize);
	cboXuatXu.setPreferredSize(cboSize);
	cboKieuDang.setPreferredSize(cboSize);
	cboChatLieu.setPreferredSize(cboSize);
	cboDeGiay.setPreferredSize(cboSize);

	setupTableGioHang(tblDanhSachGioHang);
	setupTableHoaDon(tblDanhSachHoaDon);
	setupTableSanPham(tblDanhSachSanPham);
	setupPagination();
	
	DocumentListener documentListener = new DocumentListener() {
	    @Override
	    public void insertUpdate(DocumentEvent e) {
		updateTienTraLai();
	    }

	    @Override
	    public void removeUpdate(DocumentEvent e) {
		updateTienTraLai();
	    }

	    @Override
	    public void changedUpdate(DocumentEvent e) {
		updateTienTraLai();
	    }
	};
	
	txtTienMat.getDocument().addDocumentListener(documentListener);
	txtTienChuyenKhoan.getDocument().addDocumentListener(documentListener);
	
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadDataDanhMuc();
                loadDataThuongHieu();
                loadDataXuatXu();
                loadDataKieuDang();
                loadDataChatLieu();
                loadDataDeGiay();

                loadDataHoaDonCho();
                loadDataPageSanPham(1);
                return null;
            }
        };
        worker.execute();
    }

    public static InuhaBanHangView getInstance() { 
        return instance;
    }

    private void setupTableHoaDon(JTable table) { 
        pnlDanhSachHoaDon.setBackground(ColorUtils.BACKGROUND_TABLE);
        TableCustomUI.apply(scrDanhSachHoaDon, TableCustomUI.TableType.DEFAULT);
        TableCustomUI.resizeColumnHeader(table);
	
        table.setRowHeight(30);
    }

    private void setupTableGioHang(JTable table) { 
        pnlDanhSachGioHang.setBackground(ColorUtils.BACKGROUND_TABLE);
        TableCustomUI.apply(scrDanhSachGioHang, TableCustomUI.TableType.DEFAULT);
	TableCustomUI.resizeColumnHeader(table);
	table.getColumnModel().getColumn(1).setCellRenderer(new TableAlignCenterCellRender(table));
        table.getColumnModel().getColumn(0).setCellRenderer(new TableImageCellRender(table));
    }
	
    private void setupTableSanPham(JTable table) { 

        pnlDanhSachSanPham.setBackground(ColorUtils.BACKGROUND_TABLE);
        TableCustomUI.apply(scrDanhSachSanPham, TableCustomUI.TableType.DEFAULT);
        TableCustomUI.resizeColumnHeader(table);
	
	table.getColumnModel().getColumn(5).setCellRenderer(new TableAlignCenterCellRender(table));
	table.getColumnModel().getColumn(5).setCellRenderer(new InuhaSoLuongTonSanPhamTableCellRender(table));
        table.getColumnModel().getColumn(2).setCellRenderer(new TableImageCellRender(table));
    }
    
    public void loadDataHoaDonCho() { 
        try {
            DefaultTableModel model = (DefaultTableModel) tblDanhSachHoaDon.getModel();
            if (tblDanhSachHoaDon.isEditing()) {
                tblDanhSachHoaDon.getCellEditor().stopCellEditing();
            }
            
            model.setRowCount(0);
	    
            dataItemsHoaDonCho = hoaDonService.getAll();
            
            for(InuhaHoaDonModel m: dataItemsHoaDonCho) { 
                model.addRow(m.toDataRowBanHang());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public void loadDataGioHang() { 
        try {
            DefaultTableModel model = (DefaultTableModel) tblDanhSachGioHang.getModel();
            if (tblDanhSachGioHang.isEditing()) {
                tblDanhSachGioHang.getCellEditor().stopCellEditing();
            }
            
            model.setRowCount(0);
	    
            dataItemsGioHang = currentHoaDon == null ? new ArrayList<>() : hoaDonChiTietService.getAll(currentHoaDon.getId());
            
	    if (dataItemsGioHang.size() < 1) {
		btnRemoveSanPham.setEnabled(false);
	    }
	    
	    chiTietThanhToan.setTongTienHang(0);
            for(InuhaHoaDonChiTietModel m: dataItemsGioHang) { 
                model.addRow(m.toDataRowBanHang());
		chiTietThanhToan.setTongTienHang(chiTietThanhToan.getTongTienHang() + (m.getGiaBan() * m.getSoLuong()));
            }
	    lblTongTienHang.setText(CurrencyUtils.parseString(chiTietThanhToan.getTongTienHang()));
	    lblTongVoucherGiamGia.setText(CurrencyUtils.parseString(chiTietThanhToan.getTongGiamGia()));
	    lblTongThanhToan.setText(CurrencyUtils.parseString(chiTietThanhToan.getTongThanhToan()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public void loadDataPageSanPham() { 
        loadDataPageSanPham(pagination.getCurrentPage());
    }
	
    public void loadDataPageSanPham(int page) { 
        try {
            DefaultTableModel model = (DefaultTableModel) tblDanhSachSanPham.getModel();
            if (tblDanhSachSanPham.isEditing()) {
                tblDanhSachSanPham.getCellEditor().stopCellEditing();
            }
            
	    String keyword = txtTuKhoa.getText().trim();
            keyword = keyword.replaceAll("\\s+", " ");
	    
	    if (keyword.length() > 250) {
		MessageToast.warning("Từ khoá tìm kiếm chỉ được chứa tối đa 250 ký tự");
		return;
	    }
	    
            model.setRowCount(0);
        
            ComboBoxItem<Integer> danhMuc = (ComboBoxItem<Integer>) cboDanhMuc.getSelectedItem();
            ComboBoxItem<Integer> thuongHieu = (ComboBoxItem<Integer>) cboThuongHieu.getSelectedItem();
	    ComboBoxItem<Integer> xuatXu = (ComboBoxItem<Integer>) cboXuatXu.getSelectedItem();
	    ComboBoxItem<Integer> kieuDang = (ComboBoxItem<Integer>) cboKieuDang.getSelectedItem();
	    ComboBoxItem<Integer> chatLieu = (ComboBoxItem<Integer>) cboChatLieu.getSelectedItem();
	    ComboBoxItem<Integer> deGiay = (ComboBoxItem<Integer>) cboDeGiay.getSelectedItem();
	    ComboBoxItem<Integer> trangThai = new ComboBoxItem<>("Đang bán", 1);
	    
            InuhaFilterSanPhamRequest request = new InuhaFilterSanPhamRequest();
	    request.setKeyword(keyword);
	    request.setDanhMuc(danhMuc);
	    request.setThuongHieu(thuongHieu);
	    request.setXuatXu(xuatXu);
	    request.setKieuDang(kieuDang);
	    request.setChatLieu(chatLieu);
	    request.setDeGiay(deGiay);
	    request.setTrangThai(trangThai);
	    
            request.setSize(sizePage);
	    
            int totalPages = sanPhamService.getTotalPage(request);
            
            if (totalPages < page) { 
                page = totalPages;
            }
            
            request.setPage(page);

           
            dataItemsSanPham = sanPhamService.getPage(request, true);
            
            for(InuhaSanPhamModel m: dataItemsSanPham) { 
                model.addRow(m.toDataRowBanHang());
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
		    loadDataPageSanPham(1);
		    loading.dispose();
		});
                loading.setVisible(true);
            }

            @Override
            public void onClickPage(int page) {
		executorService.submit(() -> { 
		    loadDataPageSanPham(page);
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
    
	
    public void loadDataDanhMuc() { 
	reLoad = true;
        dataDanhMuc = danhMucService.getAll();
        cboDanhMuc.removeAllItems();
	
        cboDanhMuc.addItem(new ComboBoxItem<>("-- Tất cả --", -1));
        for(InuhaDanhMucModel m: dataDanhMuc) { 
            cboDanhMuc.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
	reLoad = false;
    }

    public void loadDataThuongHieu() { 
	reLoad = true;
        dataThuongHieu = thuongHieuService.getAll();
        cboThuongHieu.removeAllItems();
	
        cboThuongHieu.addItem(new ComboBoxItem<>("-- Tất cả --", -1));
        for(InuhaThuongHieuModel m: dataThuongHieu) { 
            cboThuongHieu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
	reLoad = false;
    }

    public void loadDataXuatXu() { 
	reLoad = true;
        dataXuatXu = xuatXuService.getAll();
        cboXuatXu.removeAllItems();
	
        cboXuatXu.addItem(new ComboBoxItem<>("-- Tất cả --", -1));
        for(InuhaXuatXuModel m: dataXuatXu) { 
            cboXuatXu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
	reLoad = false;
    }

    public void loadDataKieuDang() { 
	reLoad = true;
        dataKieuDang = kieuDangService.getAll();
        cboKieuDang.removeAllItems();
	
        cboKieuDang.addItem(new ComboBoxItem<>("-- Tất cả --", -1));
        for(InuhaKieuDangModel m: dataKieuDang) { 
            cboKieuDang.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
	reLoad = false;
    }
	
    public void loadDataChatLieu() { 
	reLoad = true;
        dataChatLieu = chatLieuService.getAll();
        cboChatLieu.removeAllItems();
	
        cboChatLieu.addItem(new ComboBoxItem<>("-- Tất cả --", -1));
        for(InuhaChatLieuModel m: dataChatLieu) { 
            cboChatLieu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
	reLoad = false;
    }
	    
    public void loadDataDeGiay() { 
	reLoad = true;
        dataDeGiay = deGiayService.getAll();
        cboDeGiay.removeAllItems();
	
        cboDeGiay.addItem(new ComboBoxItem<>("-- Tất cả --", -1));
        for(InuhaDeGiayModel m: dataDeGiay) { 
            cboDeGiay.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
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

        btnGroupHinhThucThanhToan = new javax.swing.ButtonGroup();
        pnlThongTinHoaDon = new com.app.views.UI.panel.RoundPanel();
        splitLine1 = new com.app.views.UI.label.SplitLine();
        btnSubmit = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtSoDienThoai = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTenKhachHang = new javax.swing.JTextField();
        btnClearKhachHang = new javax.swing.JButton();
        btnSelectKhachHang = new javax.swing.JButton();
        lblThongTinHoaDon = new javax.swing.JLabel();
        splitLine2 = new com.app.views.UI.label.SplitLine();
        jLabel15 = new javax.swing.JLabel();
        lblMaHoaDon = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblNgayTao = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblNguoiTao = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblTrangThai = new javax.swing.JLabel();
        splitLine7 = new com.app.views.UI.label.SplitLine();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnSelectVoucher = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        lblTongTienHang = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lblTongVoucherGiamGia = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblTongThanhToan = new javax.swing.JLabel();
        splitLine8 = new com.app.views.UI.label.SplitLine();
        cboHinhThucThanhToan = new javax.swing.JComboBox();
        txtTienMat = new javax.swing.JFormattedTextField();
        txtTienChuyenKhoan = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        lblTienTraLai = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        pnlSanPham = new com.app.views.UI.panel.RoundPanel();
        lblProducts = new javax.swing.JLabel();
        splitLine4 = new com.app.views.UI.label.SplitLine();
        pnlSearchBox = new com.app.views.UI.panel.SearchBox();
        cboDanhMuc = new javax.swing.JComboBox();
        cboThuongHieu = new javax.swing.JComboBox();
        cboXuatXu = new javax.swing.JComboBox();
        cboKieuDang = new javax.swing.JComboBox();
        cboChatLieu = new javax.swing.JComboBox();
        cboDeGiay = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        pnlDanhSachSanPham = new com.app.views.UI.panel.RoundPanel();
        scrDanhSachSanPham = new javax.swing.JScrollPane();
        tblDanhSachSanPham = new javax.swing.JTable();
        pnlPhanTrang = new javax.swing.JPanel();
        btnReset = new javax.swing.JButton();
        btnScanQr = new javax.swing.JButton();
        pnlHoaDon = new com.app.views.UI.panel.RoundPanel();
        lblBill = new javax.swing.JLabel();
        splitLine3 = new com.app.views.UI.label.SplitLine();
        btnAddBill = new javax.swing.JButton();
        pnlDanhSachHoaDon = new javax.swing.JPanel();
        scrDanhSachHoaDon = new javax.swing.JScrollPane();
        tblDanhSachHoaDon = new javax.swing.JTable();
        pnlGioHang = new com.app.views.UI.panel.RoundPanel();
        splitLine5 = new com.app.views.UI.label.SplitLine();
        lblCart = new javax.swing.JLabel();
        pnlDanhSachGioHang = new javax.swing.JPanel();
        scrDanhSachGioHang = new javax.swing.JScrollPane();
        tblDanhSachGioHang = new javax.swing.JTable();
        btnRemoveSanPham = new javax.swing.JButton();

        pnlThongTinHoaDon.setMaximumSize(new java.awt.Dimension(310, 32767));
        pnlThongTinHoaDon.setMinimumSize(new java.awt.Dimension(310, 0));

        javax.swing.GroupLayout splitLine1Layout = new javax.swing.GroupLayout(splitLine1);
        splitLine1.setLayout(splitLine1Layout);
        splitLine1Layout.setHorizontalGroup(
            splitLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine1Layout.setVerticalGroup(
            splitLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 11, Short.MAX_VALUE)
        );

        btnSubmit.setText("Thanh toán");
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

        jLabel12.setText("Mã hoá đơn:");

        txtSoDienThoai.setEditable(false);
        txtSoDienThoai.setFocusable(false);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Thông tin khách hàng:");

        txtTenKhachHang.setEditable(false);
        txtTenKhachHang.setFocusable(false);

        btnClearKhachHang.setText("Khách lẻ");
        btnClearKhachHang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClearKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearKhachHangActionPerformed(evt);
            }
        });

        btnSelectKhachHang.setText("Chọn");
        btnSelectKhachHang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelectKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectKhachHangActionPerformed(evt);
            }
        });

        lblThongTinHoaDon.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblThongTinHoaDon.setText("Thông tin hoá đơn");
        lblThongTinHoaDon.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        splitLine2.setForeground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout splitLine2Layout = new javax.swing.GroupLayout(splitLine2);
        splitLine2.setLayout(splitLine2Layout);
        splitLine2Layout.setHorizontalGroup(
            splitLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine2Layout.setVerticalGroup(
            splitLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Chi tiết hoá đơn:");

        lblMaHoaDon.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblMaHoaDon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMaHoaDon.setText("HD-00001");

        jLabel16.setText("Ngày tạo:");

        lblNgayTao.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNgayTao.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNgayTao.setText("29/07/2024");

        jLabel17.setText("Người tạo:");

        lblNguoiTao.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNguoiTao.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNguoiTao.setText("Admin");

        jLabel18.setText("Trạng thái:");

        lblTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTrangThai.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTrangThai.setText("Chưa thanh toán");

        javax.swing.GroupLayout splitLine7Layout = new javax.swing.GroupLayout(splitLine7);
        splitLine7.setLayout(splitLine7Layout);
        splitLine7Layout.setHorizontalGroup(
            splitLine7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine7Layout.setVerticalGroup(
            splitLine7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Chi tiết thanh toán:");

        jLabel20.setText("Voucher:");

        btnSelectVoucher.setText("Chọn hoặc nhập mã");
        btnSelectVoucher.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelectVoucher.setEnabled(false);
        btnSelectVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectVoucherActionPerformed(evt);
            }
        });

        jLabel14.setText("Tổng tiền hàng:");

        lblTongTienHang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTongTienHang.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTongTienHang.setText("đ1.000.000");

        jLabel21.setText("Tổng Voucher giảm giá:");

        lblTongVoucherGiamGia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTongVoucherGiamGia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTongVoucherGiamGia.setText("-đ100.000");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setText("Tổng thanh toán:");

        lblTongThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTongThanhToan.setForeground(new java.awt.Color(255, 0, 0));
        lblTongThanhToan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTongThanhToan.setText("đ900.000");

        javax.swing.GroupLayout splitLine8Layout = new javax.swing.GroupLayout(splitLine8);
        splitLine8.setLayout(splitLine8Layout);
        splitLine8Layout.setHorizontalGroup(
            splitLine8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine8Layout.setVerticalGroup(
            splitLine8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );

        cboHinhThucThanhToan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Thanh toán tiền mặt", "Thanh toán chuyển khoản", "Thanh toán tiền mặt và chuyển khoản" }));
        cboHinhThucThanhToan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboHinhThucThanhToanItemStateChanged(evt);
            }
        });

        jLabel23.setText("Tiền trả lại:");

        lblTienTraLai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTienTraLai.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTienTraLai.setText("-đ100.000");

        jLabel24.setText("Tiền mặt:");

        jLabel25.setText("Chuyển khoản:");

        javax.swing.GroupLayout pnlThongTinHoaDonLayout = new javax.swing.GroupLayout(pnlThongTinHoaDon);
        pnlThongTinHoaDon.setLayout(pnlThongTinHoaDonLayout);
        pnlThongTinHoaDonLayout.setHorizontalGroup(
            pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitLine1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(splitLine2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(splitLine7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addComponent(btnSelectVoucher, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblMaHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNgayTao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNguoiTao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTrangThai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(23, 23, 23))
            .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblThongTinHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlThongTinHoaDonLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(splitLine8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlThongTinHoaDonLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTienTraLai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(cboHinhThucThanhToan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTongThanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTongVoucherGiamGia, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
                    .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSelectKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(txtTenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnClearKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTongTienHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                        .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTienChuyenKhoan)
                            .addComponent(txtTienMat))))
                .addGap(20, 20, 20))
        );
        pnlThongTinHoaDonLayout.setVerticalGroup(
            pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinHoaDonLayout.createSequentialGroup()
                .addComponent(lblThongTinHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSelectKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSoDienThoai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTenKhachHang)
                    .addComponent(btnClearKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(splitLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMaHoaDon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNgayTao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNguoiTao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTrangThai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelectVoucher))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(splitLine7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTongTienHang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTongVoucherGiamGia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTongThanhToan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(splitLine8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboHinhThucThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTienMat, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTienChuyenKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTienTraLai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlThongTinHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        lblProducts.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblProducts.setText("Sản phẩm");
        lblProducts.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout splitLine4Layout = new javax.swing.GroupLayout(splitLine4);
        splitLine4.setLayout(splitLine4Layout);
        splitLine4Layout.setHorizontalGroup(
            splitLine4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine4Layout.setVerticalGroup(
            splitLine4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 11, Short.MAX_VALUE)
        );

        cboDanhMuc.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả --" }));
        cboDanhMuc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDanhMucItemStateChanged(evt);
            }
        });

        cboThuongHieu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả --" }));
        cboThuongHieu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboThuongHieuItemStateChanged(evt);
            }
        });

        cboXuatXu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả --" }));
        cboXuatXu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboXuatXuItemStateChanged(evt);
            }
        });

        cboKieuDang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả --" }));
        cboKieuDang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboKieuDangItemStateChanged(evt);
            }
        });

        cboChatLieu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả --" }));
        cboChatLieu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboChatLieuItemStateChanged(evt);
            }
        });

        cboDeGiay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả --" }));
        cboDeGiay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDeGiayItemStateChanged(evt);
            }
        });

        jLabel8.setText("Đế giày:");

        jLabel9.setText("Chất liệu:");

        jLabel4.setText("Kiểu dáng:");

        jLabel5.setText("Xuất xứ:");

        jLabel6.setText("Thương hiệu:");

        jLabel7.setText("Danh mục:");

        tblDanhSachSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Mã sản phẩm", "", "Tên sản phẩm", "Giá bán", "Số lượng tồn", "Danh mục", "Thương hiệu", "Xuất xứ", "Kiểu dáng", "Chất liệu", "Đế giày", "Mô tả"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSachSanPham.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachSanPham.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachSanPhamMouseClicked(evt);
            }
        });
        scrDanhSachSanPham.setViewportView(tblDanhSachSanPham);
        if (tblDanhSachSanPham.getColumnModel().getColumnCount() > 0) {
            tblDanhSachSanPham.getColumnModel().getColumn(12).setMinWidth(240);
        }

        javax.swing.GroupLayout pnlDanhSachSanPhamLayout = new javax.swing.GroupLayout(pnlDanhSachSanPham);
        pnlDanhSachSanPham.setLayout(pnlDanhSachSanPhamLayout);
        pnlDanhSachSanPhamLayout.setHorizontalGroup(
            pnlDanhSachSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSachSanPham)
        );
        pnlDanhSachSanPhamLayout.setVerticalGroup(
            pnlDanhSachSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDanhSachSanPhamLayout.createSequentialGroup()
                .addComponent(scrDanhSachSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

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

        btnReset.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReset.setMaximumSize(new java.awt.Dimension(79, 35));
        btnReset.setMinimumSize(new java.awt.Dimension(79, 35));
        btnReset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnResetMouseClicked(evt);
            }
        });
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnScanQr.setText("Quét QR");
        btnScanQr.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnScanQr.setMaximumSize(new java.awt.Dimension(79, 35));
        btnScanQr.setMinimumSize(new java.awt.Dimension(79, 35));
        btnScanQr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnScanQrMouseClicked(evt);
            }
        });
        btnScanQr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanQrActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSanPhamLayout = new javax.swing.GroupLayout(pnlSanPham);
        pnlSanPham.setLayout(pnlSanPhamLayout);
        pnlSanPhamLayout.setHorizontalGroup(
            pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitLine4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlDanhSachSanPham, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlSanPhamLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSanPhamLayout.createSequentialGroup()
                        .addComponent(pnlSearchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboDanhMuc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboThuongHieu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboXuatXu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlSanPhamLayout.createSequentialGroup()
                                .addComponent(cboKieuDang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlSanPhamLayout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboDeGiay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlSanPhamLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lblProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnScanQr, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlPhanTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        pnlSanPhamLayout.setVerticalGroup(
            pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSanPhamLayout.createSequentialGroup()
                .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScanQr, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitLine4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cboChatLieu, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboKieuDang, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboXuatXu, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboThuongHieu, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnReset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSearchBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(cboDeGiay)
                    .addComponent(cboDanhMuc, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(19, 19, 19)
                .addComponent(pnlDanhSachSanPham, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPhanTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        lblBill.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblBill.setText("Hoá đơn chờ");
        lblBill.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout splitLine3Layout = new javax.swing.GroupLayout(splitLine3);
        splitLine3.setLayout(splitLine3Layout);
        splitLine3Layout.setHorizontalGroup(
            splitLine3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine3Layout.setVerticalGroup(
            splitLine3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        btnAddBill.setText("Tạo hoá đơn");
        btnAddBill.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddBill.setMaximumSize(new java.awt.Dimension(79, 35));
        btnAddBill.setMinimumSize(new java.awt.Dimension(79, 35));
        btnAddBill.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddBillMouseClicked(evt);
            }
        });
        btnAddBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBillActionPerformed(evt);
            }
        });

        tblDanhSachHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Mã hoá đơn", "Người tạo", "Ngày tạo", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSachHoaDon.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachHoaDon.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachHoaDonMouseClicked(evt);
            }
        });
        scrDanhSachHoaDon.setViewportView(tblDanhSachHoaDon);
        if (tblDanhSachHoaDon.getColumnModel().getColumnCount() > 0) {
            tblDanhSachHoaDon.getColumnModel().getColumn(0).setMaxWidth(40);
            tblDanhSachHoaDon.getColumnModel().getColumn(3).setMinWidth(180);
        }

        javax.swing.GroupLayout pnlDanhSachHoaDonLayout = new javax.swing.GroupLayout(pnlDanhSachHoaDon);
        pnlDanhSachHoaDon.setLayout(pnlDanhSachHoaDonLayout);
        pnlDanhSachHoaDonLayout.setHorizontalGroup(
            pnlDanhSachHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSachHoaDon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        pnlDanhSachHoaDonLayout.setVerticalGroup(
            pnlDanhSachHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSachHoaDon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlHoaDonLayout = new javax.swing.GroupLayout(pnlHoaDon);
        pnlHoaDon.setLayout(pnlHoaDonLayout);
        pnlHoaDonLayout.setHorizontalGroup(
            pnlHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitLine3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlHoaDonLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblBill, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddBill, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addComponent(pnlDanhSachHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlHoaDonLayout.setVerticalGroup(
            pnlHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHoaDonLayout.createSequentialGroup()
                .addGroup(pnlHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBill, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddBill, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitLine3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlDanhSachHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout splitLine5Layout = new javax.swing.GroupLayout(splitLine5);
        splitLine5.setLayout(splitLine5Layout);
        splitLine5Layout.setHorizontalGroup(
            splitLine5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine5Layout.setVerticalGroup(
            splitLine5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        lblCart.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCart.setText("Giỏ hàng");
        lblCart.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        tblDanhSachGioHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Số lượng", "Đơn giá", "Sản phẩm", "Màu sắc", "Kích cỡ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSachGioHang.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachGioHang.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachGioHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachGioHangMouseClicked(evt);
            }
        });
        scrDanhSachGioHang.setViewportView(tblDanhSachGioHang);
        if (tblDanhSachGioHang.getColumnModel().getColumnCount() > 0) {
            tblDanhSachGioHang.getColumnModel().getColumn(0).setMinWidth(60);
            tblDanhSachGioHang.getColumnModel().getColumn(0).setMaxWidth(60);
            tblDanhSachGioHang.getColumnModel().getColumn(1).setMaxWidth(100);
            tblDanhSachGioHang.getColumnModel().getColumn(3).setMinWidth(150);
        }

        javax.swing.GroupLayout pnlDanhSachGioHangLayout = new javax.swing.GroupLayout(pnlDanhSachGioHang);
        pnlDanhSachGioHang.setLayout(pnlDanhSachGioHangLayout);
        pnlDanhSachGioHangLayout.setHorizontalGroup(
            pnlDanhSachGioHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSachGioHang)
        );
        pnlDanhSachGioHangLayout.setVerticalGroup(
            pnlDanhSachGioHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSachGioHang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
        );

        btnRemoveSanPham.setText("Xoá sản phẩm");
        btnRemoveSanPham.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveSanPham.setEnabled(false);
        btnRemoveSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSanPhamActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGioHangLayout = new javax.swing.GroupLayout(pnlGioHang);
        pnlGioHang.setLayout(pnlGioHangLayout);
        pnlGioHangLayout.setHorizontalGroup(
            pnlGioHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitLine5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGioHangLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblCart, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRemoveSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addComponent(pnlDanhSachGioHang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlGioHangLayout.setVerticalGroup(
            pnlGioHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGioHangLayout.createSequentialGroup()
                .addGroup(pnlGioHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCart, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoveSanPham))
                .addGap(7, 7, 7)
                .addComponent(splitLine5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlDanhSachGioHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlGioHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlSanPham, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(pnlThongTinHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlGioHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(pnlSanPham, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlThongTinHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboDanhMucItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDanhMucItemStateChanged
        // TODO add your handling code here:
	handleSearch(evt);
    }//GEN-LAST:event_cboDanhMucItemStateChanged

    private void cboThuongHieuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboThuongHieuItemStateChanged
        // TODO add your handling code here:
	handleSearch(evt);
    }//GEN-LAST:event_cboThuongHieuItemStateChanged

    private void cboXuatXuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboXuatXuItemStateChanged
        // TODO add your handling code here:
	handleSearch(evt);
    }//GEN-LAST:event_cboXuatXuItemStateChanged

    private void cboKieuDangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboKieuDangItemStateChanged
        // TODO add your handling code here:
	handleSearch(evt);
    }//GEN-LAST:event_cboKieuDangItemStateChanged

    private void cboChatLieuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboChatLieuItemStateChanged
        // TODO add your handling code here:
	handleSearch(evt);
    }//GEN-LAST:event_cboChatLieuItemStateChanged

    private void cboDeGiayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDeGiayItemStateChanged
        // TODO add your handling code here:
	handleSearch(evt);
    }//GEN-LAST:event_cboDeGiayItemStateChanged

    private void btnResetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnResetMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetMouseClicked

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
	handleClickButtonReset();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnAddBillMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddBillMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddBillMouseClicked

    private void btnAddBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBillActionPerformed
        // TODO add your handling code here:
	handleClickButtonCreateBill();
    }//GEN-LAST:event_btnAddBillActionPerformed

    private void btnScanQrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnScanQrMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnScanQrMouseClicked

    private void btnScanQrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanQrActionPerformed
        // TODO add your handling code here:
	handleClickButtonScanQr();
    }//GEN-LAST:event_btnScanQrActionPerformed

    private void tblDanhSachSanPhamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachSanPhamMouseClicked
        // TODO add your handling code here:
	handleClickDuplicateSanPham(evt);
    }//GEN-LAST:event_tblDanhSachSanPhamMouseClicked

    private void tblDanhSachHoaDonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachHoaDonMouseClicked
        // TODO add your handling code here:
	handleSelectBill(tblDanhSachHoaDon.getSelectedRow());
    }//GEN-LAST:event_tblDanhSachHoaDonMouseClicked

    private void cboHinhThucThanhToanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboHinhThucThanhToanItemStateChanged
        // TODO add your handling code here:
	if (evt.getStateChange() == ItemEvent.SELECTED) { 
	    handleSelectHinhThucThanhToan();
	}
    }//GEN-LAST:event_cboHinhThucThanhToanItemStateChanged

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
	handleClickButtonCancel();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSelectKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectKhachHangActionPerformed
        // TODO add your handling code here:
	handleClickButtonSelectKhachHang();
    }//GEN-LAST:event_btnSelectKhachHangActionPerformed

    private void btnClearKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearKhachHangActionPerformed
        // TODO add your handling code here:
	handleClickButtonClearKhachHang();
    }//GEN-LAST:event_btnClearKhachHangActionPerformed

    private void tblDanhSachGioHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachGioHangMouseClicked
        // TODO add your handling code here:
	handleClickRowGioHang(evt);
    }//GEN-LAST:event_tblDanhSachGioHangMouseClicked

    private void btnRemoveSanPhamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSanPhamActionPerformed
        // TODO add your handling code here:
	handleClickButtonRemoveItemCart();
    }//GEN-LAST:event_btnRemoveSanPhamActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
	handleClickButtonSubmit();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnSelectVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectVoucherActionPerformed
        // TODO add your handling code here:
	handleClickButtonSelectVoucher();
    }//GEN-LAST:event_btnSelectVoucherActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBill;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClearKhachHang;
    private javax.swing.ButtonGroup btnGroupHinhThucThanhToan;
    private javax.swing.JButton btnRemoveSanPham;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnScanQr;
    private javax.swing.JButton btnSelectKhachHang;
    private javax.swing.JButton btnSelectVoucher;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox cboChatLieu;
    private javax.swing.JComboBox cboDanhMuc;
    private javax.swing.JComboBox cboDeGiay;
    private javax.swing.JComboBox cboHinhThucThanhToan;
    private javax.swing.JComboBox cboKieuDang;
    private javax.swing.JComboBox cboThuongHieu;
    private javax.swing.JComboBox cboXuatXu;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblBill;
    private javax.swing.JLabel lblCart;
    private javax.swing.JLabel lblMaHoaDon;
    private javax.swing.JLabel lblNgayTao;
    private javax.swing.JLabel lblNguoiTao;
    private javax.swing.JLabel lblProducts;
    private javax.swing.JLabel lblThongTinHoaDon;
    private javax.swing.JLabel lblTienTraLai;
    private javax.swing.JLabel lblTongThanhToan;
    private javax.swing.JLabel lblTongTienHang;
    private javax.swing.JLabel lblTongVoucherGiamGia;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JPanel pnlDanhSachGioHang;
    private javax.swing.JPanel pnlDanhSachHoaDon;
    private com.app.views.UI.panel.RoundPanel pnlDanhSachSanPham;
    private com.app.views.UI.panel.RoundPanel pnlGioHang;
    private com.app.views.UI.panel.RoundPanel pnlHoaDon;
    private javax.swing.JPanel pnlPhanTrang;
    private com.app.views.UI.panel.RoundPanel pnlSanPham;
    private com.app.views.UI.panel.SearchBox pnlSearchBox;
    private com.app.views.UI.panel.RoundPanel pnlThongTinHoaDon;
    private javax.swing.JScrollPane scrDanhSachGioHang;
    private javax.swing.JScrollPane scrDanhSachHoaDon;
    private javax.swing.JScrollPane scrDanhSachSanPham;
    private com.app.views.UI.label.SplitLine splitLine1;
    private com.app.views.UI.label.SplitLine splitLine2;
    private com.app.views.UI.label.SplitLine splitLine3;
    private com.app.views.UI.label.SplitLine splitLine4;
    private com.app.views.UI.label.SplitLine splitLine5;
    private com.app.views.UI.label.SplitLine splitLine7;
    private com.app.views.UI.label.SplitLine splitLine8;
    private javax.swing.JTable tblDanhSachGioHang;
    private javax.swing.JTable tblDanhSachHoaDon;
    private javax.swing.JTable tblDanhSachSanPham;
    private javax.swing.JTextField txtSoDienThoai;
    private javax.swing.JTextField txtTenKhachHang;
    private javax.swing.JFormattedTextField txtTienChuyenKhoan;
    private javax.swing.JFormattedTextField txtTienMat;
    // End of variables declaration//GEN-END:variables

    private void handleSearch(ItemEvent evt) {
	
	if (reLoad || (evt != null && evt.getStateChange() != ItemEvent.SELECTED)) { 
            return;
        }
	
        executorService.submit(() -> {
            loadDataPageSanPham();
            loading.dispose();
        });
        loading.setVisible(true);
    }

    private void handleClickButtonReset() {
        executorService.submit(() -> {
            reLoad = true;
            txtTuKhoa.setText(null);
            cboDanhMuc.setSelectedIndex(0);
            cboThuongHieu.setSelectedIndex(0);
	    cboXuatXu.setSelectedIndex(0);
	    cboKieuDang.setSelectedIndex(0);
	    cboChatLieu.setSelectedIndex(0);
	    cboDeGiay.setSelectedIndex(0);
            reLoad = false;
            loadDataPageSanPham();
            loading.dispose();
        });
        loading.setVisible(true);
    }

    private void handleClickButtonCreateBill() {
	executorService.submit(() -> {
	    try {
		InuhaHoaDonModel model = new InuhaHoaDonModel();
		model.setMa(BillUtils.generateCodeHoaDon());
		hoaDonService.insert(model);
		loadDataHoaDonCho();
		handleSelectBill(0);
		MessageToast.success("Tạo mới hoá đơn thành công.");
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

    private void handleClickDuplicateSanPham(MouseEvent evt) {
	if (evt.getClickCount() < 2) { 
	    return;
	}
	
	if (currentHoaDon == null) { 
	    MessageToast.warning("Vui lòng chọn một hoá đơn trước!");
	    return;
	}
	
	InuhaSanPhamModel sanPham = dataItemsSanPham.get(tblDanhSachSanPham.getSelectedRow());
	if (sanPham.getSoLuong() < 1) { 
	    MessageToast.warning("Sản phẩm đã hết hàng. Vui lòng chọn sản phẩm khác!");
	    return;
	}
	
	showModalAddToCart(sanPham);
    }
    
    private void showModalAddToCart(InuhaSanPhamModel sanPham) { 
        if (ModalDialog.isIdExist("showModalAddToCart")) {
            return;
        }
	ModalDialog.closeAllModal();
	ModalDialog.showModal(this, new SimpleModalBorder(new InuhaAddGioHangView(sanPham), sanPham.getMa() + " - " + sanPham.getTen()), "showModalAddToCart");
    }

    private void showModalEditCart(InuhaHoaDonChiTietModel hoaDonChiTiet) { 
        if (ModalDialog.isIdExist("showModalEditCart")) {
            return;
        }
	ModalDialog.closeAllModal();
	ModalDialog.showModal(this, new SimpleModalBorder(new InuhaEditGioHangView(hoaDonChiTiet), hoaDonChiTiet.getSanPhamChiTiet().getMa() + " - " + hoaDonChiTiet.getSanPhamChiTiet().getSanPham().getTen()), "showModalEditCart");
    }
	
    private void handleSelectBill(int i) {
        clearVoucher();
        
	if (i < 0) {
	    handleClear();
	    return;
	}
	
	tblDanhSachHoaDon.setRowSelectionInterval(i, i);
	currentHoaDon = dataItemsHoaDonCho.get(i);
	currentKhachHang = dataItemsHoaDonCho.get(i).getKhachHang();
	
	executorService.submit(() -> {
	    loadDataGioHang();
	    loading.dispose();
	});
	loading.setVisible(true);
	
	
	InuhaKhachHangModel khachHang = currentHoaDon.getKhachHang();
	if (khachHang != null) { 
	    txtSoDienThoai.setText(khachHang.getSdt());
	    txtTenKhachHang.setText(khachHang.getHoTen());
	    btnSelectVoucher.setEnabled(true);
	} else {
	    txtSoDienThoai.setText(null);
	    txtTenKhachHang.setText(null);
	    btnSelectVoucher.setEnabled(false);
	}
	
	lblMaHoaDon.setText("#" + currentHoaDon.getMa());
	lblNgayTao.setText(TimeUtils.date("dd-MM-yyyy HH:mm", currentHoaDon.getNgayTao()));
	lblNguoiTao.setText(currentHoaDon.getTaiKhoan().getUsername());
	lblTrangThai.setText(BillUtils.getTrangThai(currentHoaDon.getTrangThai()));
	
	cboHinhThucThanhToan.setSelectedIndex(0);
	txtTienMat.setEnabled(true);
	
	btnSelectKhachHang.setEnabled(true);
	btnClearKhachHang.setEnabled(true);
	cboHinhThucThanhToan.setEnabled(true);
        cboHinhThucThanhToan.setSelectedIndex(currentHoaDon.getPhuongThucThanhToan());
        txtTienMat.setText(currentHoaDon.getTienMat() > 0 ? CurrencyUtils.parseTextField(currentHoaDon.getTienMat()) : null);
        txtTienChuyenKhoan.setText(currentHoaDon.getTienChuyenKhoan() > 0 ? CurrencyUtils.parseTextField(currentHoaDon.getTienChuyenKhoan()) : null);
        
	btnCancel.setEnabled(true);
	btnSubmit.setEnabled(true);
    }

    private void handleClear() {
	
	currentHoaDon = null;
	currentHoaDonChiTiet = null;
	currentPhieuGiamGia = null;
	currentKhachHang = null;
	dataItemsGioHang = new ArrayList<>();
	txtSoDienThoai.setText(null);
	txtTenKhachHang.setText(null);
	lblMaHoaDon.setText(null);
	lblNgayTao.setText(null);
	lblNguoiTao.setText(null);
	lblTrangThai.setText(null);
	lblTongTienHang.setText("đ0");
	lblTongThanhToan.setText("đ0");
	cboHinhThucThanhToan.setSelectedIndex(0);
	txtTienMat.setText(null);
	txtTienChuyenKhoan.setText(null);
	lblTienTraLai.setText("đ0");
	
	txtTienMat.setEnabled(false);
	txtTienChuyenKhoan.setEnabled(false);
	
	btnSelectKhachHang.setEnabled(false);
	btnClearKhachHang.setEnabled(false);
	cboHinhThucThanhToan.setEnabled(false);
	btnCancel.setEnabled(false);
	btnSubmit.setEnabled(false);
	
	btnRemoveSanPham.setEnabled(false);
	
	chiTietThanhToan.setTongTienHang(0);
	clearVoucher();
	loadDataGioHang();
    }

    private void handleSelectHinhThucThanhToan() {
	ComboBoxItem<Integer> hinhThucThanhToan = (ComboBoxItem<Integer>) cboHinhThucThanhToan.getSelectedItem();
	if (currentHoaDon == null) { 
	    txtTienMat.setEnabled(false);
	    txtTienChuyenKhoan.setEnabled(false);
	    return;
	}
	switch (hinhThucThanhToan.getValue()) {
	    case PhuongThucThanhToanConstant.CHUYEN_KHOAN -> {
		txtTienMat.setText(null);
		txtTienMat.setEnabled(false);
		txtTienChuyenKhoan.setEnabled(true);
                
                txtTienChuyenKhoan.requestFocus();
	    }
	    case PhuongThucThanhToanConstant.KET_HOP -> {
		txtTienMat.setEnabled(true);
		txtTienChuyenKhoan.setEnabled(true);
                
                txtTienMat.requestFocus();
	    }
	    default -> {
		txtTienMat.setEnabled(true);
		txtTienChuyenKhoan.setText(null);
		txtTienChuyenKhoan.setEnabled(false);
                txtTienMat.requestFocus();
	    }
	}
    }
    
   

    private void handleClickButtonCancel() {
	SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
	    @Override
	    protected Boolean doInBackground() throws Exception {
		return MessageModal.confirmWarning("Không thể hoàn tác. Bạn thực sự muốn huỷ đơn hàng này?");
	    }

	    @Override
	    protected void done() {
		try {
		    if (get()) {
			try {
			    currentHoaDon.setTrangThai(TrangThaiHoaDonConstant.STATUS_DA_HUY);
			    hoaDonService.update(currentHoaDon);
			    
			    for(InuhaHoaDonChiTietModel m: dataItemsGioHang) { 
				InuhaSanPhamChiTietModel sanPhamChiTiet = m.getSanPhamChiTiet();
				sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + m.getSoLuong());
				InuhaSanPhamChiTietService.getInstance().update(sanPhamChiTiet);
			    }
			    
			    loadDataHoaDonCho();
			    loadDataPageSanPham();
			    MessageToast.success("Huỷ thành công đơn hàng #" + currentHoaDon.getMa());
			    handleClear();
			} catch (ServiceResponseException e) {
			    e.printStackTrace();
			    MessageToast.error(e.getMessage());
			} catch (Exception e) {
			    e.printStackTrace();
			    MessageModal.error(ErrorConstant.DEFAULT_ERROR);
			} finally {
			    loading.dispose();
			}
		    }
		} catch (InterruptedException ex) {
		} catch (ExecutionException ex) {
		}
	    } 
	};
	worker.execute();
    }
    
    private void handleClickButtonSelectKhachHang() {
        if (ModalDialog.isIdExist("handleClickButtonSelectKhachHang")) {
            return;
        }
	ModalDialog.closeAllModal();
	ModalDialog.showModal(this, new SimpleModalBorder(new InuhaListKhachHangView(currentKhachHang), "Danh sách khách hàng"), "handleClickButtonSelectKhachHang");
    }

    public void setKhachHang(InuhaKhachHangModel khachHang) {
	if (khachHang == null) { 
	    handleClickButtonClearKhachHang();
	    return;
	}
	
	executorService.submit(() -> {
	    try {
		currentKhachHang = khachHang;
		
		dataItemsHoaDonCho.forEach(o -> {
		    if (o.getKhachHang() != null && o.getKhachHang().getId() == khachHang.getId()) { 
			o.setKhachHang(khachHang);
		    }
		});
		
		currentHoaDon.setKhachHang(currentKhachHang);
		hoaDonService.update(currentHoaDon);
		txtSoDienThoai.setText(khachHang.getSdt());
		txtTenKhachHang.setText(khachHang.getHoTen());
		btnSelectVoucher.setEnabled(true);
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
	
    private void handleClickButtonClearKhachHang() {
	executorService.submit(() -> {
	    try {
		clearVoucher();
		currentKhachHang = null;
		currentHoaDon.setKhachHang(null);
		currentHoaDon.setPhieuGiamGia(null);
		hoaDonService.update(currentHoaDon);
		txtSoDienThoai.setText(null);
		txtTenKhachHang.setText(null);
		btnSelectVoucher.setEnabled(false);
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

    public void setVoucher(InuhaPhieuGiamGiaModel phieuGiamGia) {
	if (phieuGiamGia == null) {
	    return;
	}
        
	double tongGiamGia = VoucherUtils.getTienGiam(chiTietThanhToan.getTongTienHang(), phieuGiamGia);
	lblTongVoucherGiamGia.setText("-" + CurrencyUtils.parseString(tongGiamGia));
	currentPhieuGiamGia = phieuGiamGia;
	btnSelectVoucher.setText(phieuGiamGia.getMa() + " - " + VoucherUtils.getTextGiaTriGiam(phieuGiamGia));
	chiTietThanhToan.setTongGiamGia(tongGiamGia);
	lblTongThanhToan.setText(CurrencyUtils.parseString(chiTietThanhToan.getTongThanhToan()));
        
	btnSelectVoucher.setEnabled(true);
        updateTienTraLai();
    }
	
    private void handleClickButtonScanQr() {
	if (currentHoaDon == null) {
	    MessageToast.warning("Vui lòng chọn một hoá đơn để sử dụng tính năng này!");
	    return;
	}
	
	QrCodeHelper.showWebcam(result -> { 
            executorService.submit(() -> {
                try {
                    String code = result.getText();

                    InuhaSanPhamModel sanPhamModel = null;
                    int id;
                    if ((id = QrCodeUtils.getIdSanPham(code)) > 0) {
                        sanPhamModel = sanPhamService.getById(id);
			if (sanPhamModel.getSoLuong() < 1 || !sanPhamModel.isTrangThai()) { 
			    MessageToast.warning("Sản phẩm đã hết hàng hoặc đã ngừng bán!");
			    return;
			}
			showModalAddToCart(sanPhamModel);
                    } else if((id = QrCodeUtils.getIdSanPhamChiTiet(code)) > 0) { 
			InuhaSanPhamChiTietModel sanPhamChiTietModel = InuhaSanPhamChiTietService.getInstance().getById(id);
			addToCart(sanPhamChiTietModel, 1);
                    } else { 
                        MessageToast.error("QRCode không hợp lệ!!!");
                        return;
                    }             
                } catch (ServiceResponseException e) {
                    MessageModal.error(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageModal.error(ErrorConstant.DEFAULT_ERROR);
                } finally { 
		    loading.dispose();
		}
            });
            loading.setVisible(true);
	});
    }

    public void addToCart(InuhaSanPhamChiTietModel sanPhamChiTietModel, int soLuong) {
	if (sanPhamChiTietModel.getSoLuong() < soLuong || !sanPhamChiTietModel.isTrangThai() || !sanPhamChiTietModel.getSanPham().isTrangThai()) { 
	    MessageToast.warning("Sản phẩm đã hết hàng hoặc đã ngừng bán!");
	    return;
	}
	executorService.submit(() -> {
	    try {
		InuhaHoaDonChiTietModel hoaDonChiTiet = new InuhaHoaDonChiTietModel();
		hoaDonChiTiet.setMa(BillUtils.generateCodeHoaDonChiTiet());
		hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTietModel);
		hoaDonChiTiet.setHoaDon(currentHoaDon);
		hoaDonChiTiet.setSoLuong(soLuong);
		hoaDonChiTietService.insert(hoaDonChiTiet);
		loadDataGioHang();
		loadDataPageSanPham();
		setVoucher(currentPhieuGiamGia);
                updateTienTraLai();
		MessageToast.success("Thêm vào giỏ hàng thành công!");
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

    public void editCart(InuhaHoaDonChiTietModel hoaDonChiTiet, int soLuongChenhLech) {
	if (soLuongChenhLech == 0) { 
	    return;
	}
	
	executorService.submit(() -> {
	    try {
		hoaDonChiTietService.update(hoaDonChiTiet, soLuongChenhLech);
		loadDataGioHang();
		loadDataPageSanPham();
		setVoucher(currentPhieuGiamGia);
		MessageToast.success("Cập nhật giỏ hàng thành công!");
		currentHoaDonChiTiet = null;
                updateTienTraLai();
                updateVoucher();
		btnRemoveSanPham.setEnabled(false);
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

	
    private void handleClickRowGioHang(MouseEvent evt) {
	int index = tblDanhSachGioHang.getSelectedRow();
	
	if (index < 0) {
	    currentHoaDonChiTiet = null;
	    btnRemoveSanPham.setEnabled(false);
	    return;
	} 
	
	currentHoaDonChiTiet = dataItemsGioHang.get(index);
	btnRemoveSanPham.setEnabled(true);
	
	if (evt.getClickCount() > 1) { 
	    showModalEditCart(currentHoaDonChiTiet);
	}
    }

    private void handleClickButtonRemoveItemCart() {
	if (currentHoaDonChiTiet == null) { 
	    return;
	}
	
	executorService.submit(() -> {
	    try {
		hoaDonChiTietService.delete(currentHoaDonChiTiet);

		loadDataPageSanPham();
		loadDataGioHang();
		setVoucher(currentPhieuGiamGia);
		MessageToast.success("Xoá thành công sản phẩm khỏi giỏ hàng!");
		currentHoaDonChiTiet = null;
                updateTienTraLai();
                updateVoucher();
		btnRemoveSanPham.setEnabled(false);
	    } catch (ServiceResponseException e) {
		e.printStackTrace();
		MessageToast.error(e.getMessage());
	    } catch (Exception e) {
		e.printStackTrace();
		MessageModal.error(ErrorConstant.DEFAULT_ERROR);
	    } finally {
		loading.dispose();
	    }
	});
	loading.setVisible(true);
    }

    private void handleClickButtonSubmit() {
        if (ModalDialog.isIdExist("handleClickButtonSubmit")) {
            return;
        }
        
	if (currentHoaDon == null) { 
	    MessageToast.warning("Vui lòng chọn một hoá đơn");
	    return;
	}
	
	if (dataItemsGioHang.size() < 1) { 
	    MessageToast.warning("Vui lòng thêm ít nhất một sản phẩm");
	    return;
	}
	
	String tienMat = txtTienMat.getText().trim();
	String tienChuyenKhoan = txtTienChuyenKhoan.getText().trim();
	
	int tienMatValue = 0;
	int tienChuyenKhoanValue = 0;
		
	ComboBoxItem<Integer> hinhThucThanhToan = (ComboBoxItem<Integer>) cboHinhThucThanhToan.getSelectedItem();
	
	if (hinhThucThanhToan.getValue() == PhuongThucThanhToanConstant.TIEN_MAT || hinhThucThanhToan.getValue() == PhuongThucThanhToanConstant.KET_HOP) { 
	    if (tienMat.isEmpty()) { 
		MessageToast.warning("Tiền mặt không được bỏ trống");
		txtTienMat.requestFocus();
		return;
	    }
	    try {
		tienMatValue = (int) CurrencyUtils.parseNumber(tienMat);
	    } catch (NumberFormatException e) { 
		MessageToast.warning("Tiền mặt vượt quá giới hạn cho phép");
		txtTienMat.requestFocus();
		return;
	    }
	}

	if (hinhThucThanhToan.getValue() == PhuongThucThanhToanConstant.CHUYEN_KHOAN || hinhThucThanhToan.getValue() == PhuongThucThanhToanConstant.KET_HOP) { 
	    if (tienChuyenKhoan.isEmpty()) { 
		MessageToast.warning("Tiền chuyển khoản không được bỏ trống");
		txtTienChuyenKhoan.requestFocus();
		return;
	    }
	    try {
		tienChuyenKhoanValue = (int) CurrencyUtils.parseNumber(tienChuyenKhoan);
	    } catch (NumberFormatException e) { 
		MessageToast.warning("Tiền chuyển khoản vượt quá giới hạn cho phép");
		txtTienChuyenKhoan.requestFocus();
		return;
	    }
	}
	
	if ((tienMatValue + tienChuyenKhoanValue) < chiTietThanhToan.getTongThanhToan()) { 
	    MessageToast.warning("Tiền khách trả phải lớn hơn hoặc bằng tổng thanh toán");
            
            if (hinhThucThanhToan.getValue() == PhuongThucThanhToanConstant.TIEN_MAT || hinhThucThanhToan.getValue() == PhuongThucThanhToanConstant.KET_HOP) { 
                txtTienMat.requestFocus();
            } else {
                txtTienChuyenKhoan.requestFocus();
            }
	    
	    return;
	}
	
	ModalDialog.closeAllModal();
	ModalDialog.showModal(this, new SimpleModalBorder(new InuhaConfirmHoaDonView(), null), "handleClickButtonSubmit");
    }
    
    public void submitSave(boolean printInvoice) { 
        SwingWorker<Void, Void> worker1 = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String tienMat = txtTienMat.getText().trim();
                    String tienChuyenKhoan = txtTienChuyenKhoan.getText().trim();
		    ComboBoxItem<Integer> hinhThucThanhToan = (ComboBoxItem<Integer>) cboHinhThucThanhToan.getSelectedItem();
		    
                    currentHoaDon.setTrangThai(TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN);
                    currentHoaDon.setPhieuGiamGia(currentPhieuGiamGia);
		    currentHoaDon.setPhuongThucThanhToan(hinhThucThanhToan.getValue());
                    currentHoaDon.setTienGiam(chiTietThanhToan.getTongGiamGia());
                    currentHoaDon.setTienMat(CurrencyUtils.parseNumber(tienMat));
                    currentHoaDon.setTienChuyenKhoan(CurrencyUtils.parseNumber(tienChuyenKhoan));
                    hoaDonService.update(currentHoaDon);

                    loadDataHoaDonCho();
                    loadDataGioHang();
                } catch (ServiceResponseException e) {
                    e.printStackTrace();
                    MessageToast.error(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageModal.error(ErrorConstant.DEFAULT_ERROR);
                } finally {
                    loading.dispose();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (printInvoice) {
                        InvoiceDataModel invoiceData = new InvoiceDataModel();
                        invoiceData.setId(currentHoaDon.getId());
                        invoiceData.setMaHoaDon(currentHoaDon.getMa());
                        invoiceData.setTenKhachHang(currentHoaDon.getKhachHang() != null ? currentHoaDon.getKhachHang().getHoTen() : "Khách lẻ");
                        invoiceData.setSoDienThoai(currentHoaDon.getKhachHang() != null ? currentHoaDon.getKhachHang().getSdt(): "");
                        invoiceData.setTaiKhoan(currentHoaDon.getTaiKhoan().getHoTen());
                        List<InvoiceProduct> products = new ArrayList<>();
                        for(InuhaHoaDonChiTietModel m: dataItemsGioHang) { 
                            products.add(new InvoiceProduct(m.getSanPhamChiTiet().getSanPham().getTen() + " - " + m.getSanPhamChiTiet().getKichCo().getTen() + " - " + m.getSanPhamChiTiet().getMauSac().getTen(), m.getSoLuong(), (float) m.getGiaBan()));
                        }
                        invoiceData.setHoaDonChiTiet(products);
                        invoiceData.setTongTienHang(chiTietThanhToan.getTongTienHang());
                        invoiceData.setTongTienGiam(chiTietThanhToan.getTongGiamGia());
                        invoiceData.setTienKhachTra(currentHoaDon.getTienMat() + currentHoaDon.getTienChuyenKhoan());
                        
                        SwingWorker<File, Void> worker2 = new SwingWorker<File, Void>() {
                            
                            @Override
                            protected File doInBackground() throws Exception {
                                return PdfHelper.selectFolder();
                            }

                            @Override
                            protected void done() {
                                try {
                                    File folder = get();
                                    if (folder != null) {
                                        File invoiceFile = PdfHelper.createInvoicePDF(invoiceData, folder);
                                        PdfHelper.openFile(invoiceFile);
                                    }
                                } catch (InterruptedException | ExecutionException ex) {
                                }

                            }
                            
                            
                        };
                        worker2.execute();
                        

 
                    }
                    MessageToast.success("Xác nhận thanh toán thành công đơn hàng #" + currentHoaDon.getMa());
                    handleClear();
                    ModalDialog.closeAllModal();
                } catch (ServiceResponseException e) {
                    e.printStackTrace();
                    MessageToast.error(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageModal.error(ErrorConstant.DEFAULT_ERROR);
                }
            }
        };
        worker1.execute();
        loading.setVisible(true);
        
    }
    
    private void updateTienTraLai() {
	int tienKhachTra = (int) CurrencyUtils.parseNumber(txtTienMat.getText().trim()) + (int) CurrencyUtils.parseNumber(txtTienChuyenKhoan.getText().trim());
	int tienTraLai = (int) (tienKhachTra - chiTietThanhToan.getTongThanhToan());
	if (tienTraLai > 0) {
	    lblTienTraLai.setText("-" + CurrencyUtils.parseString(tienTraLai));
	    lblTienTraLai.setForeground(ColorUtils.DANGER_COLOR);
	} else {
	    lblTienTraLai.setText(CurrencyUtils.parseString(0));
	    lblTienTraLai.setForeground(ColorUtils.PRIMARY_TEXT);
	}
    }

    private void handleClickButtonSelectVoucher() {
	if (currentPhieuGiamGia != null) { 
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return MessageModal.confirmWarning("Sẽ huỷ áp dụng mã giảm giá hiện tại. Vẫn sẽ tiếp tục?");
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            currentPhieuGiamGia = null;
                            clearVoucher();
                            showSelectVoucher();
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                    }
                }
  
            };
            worker.execute();
            return;
	}
        
	showSelectVoucher();
    }
    
    private void showSelectVoucher() {
        if (ModalDialog.isIdExist("showSelectVoucher")) {
            return;
        }
//	if (currentKhachHang == null || currentKhachHang.getSoLanMuaHang() < 1) {
//	    MessageToast.warning("Phiếu giảm giá chỉ áp dụng cho khách hàng đã từng mua hàng tại đây!");
//	    return;
//	}
	ModalDialog.closeAllModal();
	ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaListPhieuGiamGiaView(chiTietThanhToan), "Danh sách Voucher"), "showSelectVoucher");
    }

    private void clearVoucher() {
	currentPhieuGiamGia = null;
	btnSelectVoucher.setText("Chọn hoặc nhập mã");
	btnSelectVoucher.setEnabled(currentHoaDon != null && currentHoaDon.getKhachHang() != null);
	chiTietThanhToan.setTongGiamGia(0);
	lblTongVoucherGiamGia.setText("đ0");
	lblTongThanhToan.setText(CurrencyUtils.parseString(chiTietThanhToan.getTongThanhToan()));
        updateTienTraLai();
    }

    public void updateTienThanhToan() {
        if (instance == null) { 
            return;
        }
        
        if (currentHoaDon != null) { 
            
            
            String tienMat = txtTienMat.getText().trim();
            String tienChuyenKhoan = txtTienChuyenKhoan.getText().trim();
            
            double tienMatValue = 0;
            double tienChuyenKhoanValue = 0;
            
            try {
		tienMatValue = CurrencyUtils.parseNumber(tienMat);
	    } catch (NumberFormatException e) { 
		tienMatValue = currentHoaDon.getTienMat();
	    }
            
            try {
		tienChuyenKhoanValue = CurrencyUtils.parseNumber(tienChuyenKhoan);
	    } catch (NumberFormatException e) { 
		tienChuyenKhoanValue = currentHoaDon.getTienChuyenKhoan();
	    }
            
            ComboBoxItem<Integer> hinhThucChuyenKhoan = (ComboBoxItem<Integer>) cboHinhThucThanhToan.getSelectedItem();
            
            if (hinhThucChuyenKhoan.getValue() != currentHoaDon.getPhuongThucThanhToan() || tienMatValue != currentHoaDon.getTienMat() || tienChuyenKhoanValue != currentHoaDon.getTienChuyenKhoan()) { 
                
                currentHoaDon.setPhuongThucThanhToan(hinhThucChuyenKhoan.getValue());
                currentHoaDon.setTienMat(tienMatValue);
                currentHoaDon.setTienChuyenKhoan(tienChuyenKhoanValue);
                executorService.submit(() -> { 
                    try {
                        hoaDonService.update(currentHoaDon);
                    } catch (ServiceResponseException e) {
                        MessageToast.error(e.getMessage());
                    } catch (Exception e) {
                        MessageModal.error(ErrorConstant.DEFAULT_ERROR);
                    } finally {
                        loading.dispose();
                    }
                });
                loading.setVisible(true);
            }
            
        }
    }

    private void updateVoucher() {
        if (currentPhieuGiamGia == null) { 
            return;
        }
        
        if (currentPhieuGiamGia.getDonToiThieu() > chiTietThanhToan.getTongTienHang()) {
            clearVoucher();
            MessageModal.warning("Giá trị đơn hàng không đủ để áp dụng phiếu giảm giá hiện tại!!!");
        }
    }
    
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ChiTietThanhToan {
	
	private double tongTienHang = 0;
	
	private double tongGiamGia = 0;
	
	public double getTongThanhToan() {
	    return tongTienHang - tongGiamGia;
	}
	
    }
}
