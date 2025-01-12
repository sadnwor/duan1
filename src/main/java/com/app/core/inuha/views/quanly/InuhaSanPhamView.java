package com.app.core.inuha.views.quanly;

import com.app.Application;
import com.app.common.helper.ExcelHelper;
import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.helper.QrCodeHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaChatLieuModel;
import com.app.core.inuha.models.sanpham.InuhaDanhMucModel;
import com.app.core.inuha.models.sanpham.InuhaDeGiayModel;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.models.sanpham.InuhaKieuDangModel;
import com.app.core.inuha.models.sanpham.InuhaMauSacModel;
import com.app.core.inuha.models.sanpham.InuhaThuongHieuModel;
import com.app.core.inuha.models.sanpham.InuhaXuatXuModel;
import com.app.core.inuha.request.InuhaFilterSanPhamChiTietRequest;
import com.app.core.inuha.request.InuhaFilterSanPhamRequest;
import com.app.core.inuha.services.InuhaChatLieuService;
import com.app.core.inuha.services.InuhaDanhMucService;
import com.app.core.inuha.services.InuhaDeGiayService;
import com.app.core.inuha.services.InuhaKichCoService;
import com.app.core.inuha.services.InuhaKieuDangService;
import com.app.core.inuha.services.InuhaMauSacService;
import com.app.core.inuha.services.InuhaSanPhamChiTietService;
import com.app.core.inuha.services.InuhaSanPhamService;
import com.app.core.inuha.services.InuhaThuongHieuService;
import com.app.core.inuha.services.InuhaXuatXuService;
import com.app.core.inuha.views.quanly.components.table.soluongton.InuhaSoLuongTonSanPhamTableCellRender;
import com.app.core.inuha.views.quanly.components.table.trangthai.InuhaTrangThaiSanPhamTableCellRender;
import com.app.core.inuha.views.quanly.sanpham.InuhaAddSanPhamView;
import com.app.core.inuha.views.quanly.sanpham.InuhaDetailSanPhamChiTietView;
import com.app.core.inuha.views.quanly.sanpham.InuhaDetailSanPhamView;
import static com.app.core.inuha.views.quanly.sanpham.InuhaDetailSanPhamView.ID_MODAL_DEAIL;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.ProductUtils;
import com.app.utils.QrCodeUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.ThemeUtils;
import com.app.utils.TimeUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.panel.RoundPanel;
import com.app.views.UI.table.TableCustomUI;
import com.app.views.UI.table.celll.CheckBoxTableHeaderRenderer;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import com.app.views.UI.table.ITableActionEvent;
import com.app.views.UI.table.celll.TableActionCellEditor;
import com.app.views.UI.table.celll.TableActionCellRender;
import com.app.views.UI.table.celll.TableImageCellRender;
import com.google.zxing.WriterException;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import jnafilechooser.api.JnaFileChooser;

/**
 *
 * @author inuHa
 */
public class InuhaSanPhamView extends RoundPanel {

    private static InuhaSanPhamView instance;
        
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    private final static InuhaSanPhamService sanPhamService = InuhaSanPhamService.getInstance();
    
    private final static InuhaSanPhamChiTietService sanPhamChiTietService = InuhaSanPhamChiTietService.getInstance();
	
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
    
    private List<InuhaKichCoModel> dataKichCo = new ArrayList<>();
    
    private List<InuhaMauSacModel> dataMauSac = new ArrayList<>();
    
    private JTextField txtTuKhoa;
    
    private JTextField txtTuKhoa2;
    
    public Pagination pagination = new Pagination();
    
    public Pagination paginationSPCT = new Pagination();
    
    private int sizePage = pagination.getLimitItem();
    
    private List<InuhaSanPhamModel> dataItems = new ArrayList<>();
    
    private List<InuhaSanPhamChiTietModel> dataItemsSPCT = new ArrayList<>();
    
    private final LoadingDialog loading = new LoadingDialog();
    
    private boolean firstLoad = true;
    
    /**
     * Creates new form InuhaSanPhamView
     */
    
    public static InuhaSanPhamView getInstance() { 
        if (instance == null) { 
            instance = new InuhaSanPhamView();
        }
        return instance;
    }

    public InuhaSanPhamView() {
        initComponents();
        instance = this;
	pnlSearchBox.setPlaceholder("Nhập tên hoặc mã sản phẩm ...");
        txtTuKhoa = pnlSearchBox.getKeyword();
        
	
	pnlSearchBox2.setPlaceholder("Nhập tên hoặc mã sản phẩm ...");
        txtTuKhoa2 = pnlSearchBox2.getKeyword();
	
        txtTuKhoa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleClickButtonSearch();
                }
            }
        });
        
	txtTuKhoa2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleClickButtonSeachChiTiet();
                }
            }
        });
        
        if (ThemeUtils.isLight()) { 
            btnSearch.setBackground(ColorUtils.BUTTON_PRIMARY);
            btnSearch.setForeground(Color.WHITE);
            btnSearch2.setBackground(ColorUtils.BUTTON_PRIMARY);
            btnSearch2.setForeground(Color.WHITE);
        }
		
        pnlContainer.setOpaque(false);
	pnlContainer.setRound(0, 0, 0, 0);
	pnlFilter.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlFilter2.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlList.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlList2.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlDanhSachChiTiet.setRound(0, 0, 0, 0);
	pnlDanhSach.setRound(0, 0, 0, 0);
	
        lblFilter.setIcon(ResourceUtils.getSVG("/svg/filter.svg", new Dimension(20, 20)));
	lblFilter2.setIcon(ResourceUtils.getSVG("/svg/filter.svg", new Dimension(20, 20)));
        lblList.setIcon(ResourceUtils.getSVG("/svg/list.svg", new Dimension(20, 20)));
	lblList2.setIcon(ResourceUtils.getSVG("/svg/list.svg", new Dimension(20, 20)));
        btnThemSanPham.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnXoaSanPham.setIcon(ResourceUtils.getSVG("/svg/trash.svg", new Dimension(20, 20)));
        btnSearch.setIcon(ResourceUtils.getSVG("/svg/search.svg", new Dimension(20, 20)));
        btnSearch2.setIcon(ResourceUtils.getSVG("/svg/search.svg", new Dimension(20, 20)));
	btnScanQR.setIcon(ResourceUtils.getSVG("/svg/qr.svg", new Dimension(20, 20)));
	btnScanQRChiTiet.setIcon(ResourceUtils.getSVG("/svg/qr.svg", new Dimension(20, 20)));
	btnExport.setIcon(ResourceUtils.getSVG("/svg/export.svg", new Dimension(20, 20)));
	btnImport.setIcon(ResourceUtils.getSVG("/svg/import.svg", new Dimension(20, 20)));
	btnSaveAllQR.setIcon(ResourceUtils.getSVG("/svg/save.svg", new Dimension(20, 20)));
	
        btnClear.setBackground(ColorUtils.BUTTON_GRAY);
	btnClear2.setBackground(ColorUtils.BUTTON_GRAY);
        btnThemSanPham.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnThemSanPham.setForeground(Color.WHITE);
        
        cboTrangThai.removeAllItems();
        cboTrangThai.addItem(new ComboBoxItem<>("-- Tất cả trạng thái --", -1));
        cboTrangThai.addItem(new ComboBoxItem<>("Đang bán", 1));
        cboTrangThai.addItem(new ComboBoxItem<>("Ngừng bán", 0));
        
	cboTrangThai2.removeAllItems();
        cboTrangThai2.addItem(new ComboBoxItem<>("-- Tất cả trạng thái --", -1));
        cboTrangThai2.addItem(new ComboBoxItem<>("Đang bán", 1));
        cboTrangThai2.addItem(new ComboBoxItem<>("Ngừng bán", 0));
	
	cboSoLuong.removeAllItems();
        cboSoLuong.addItem(new ComboBoxItem<>("-- Tất cả số lượng --", -1));
        cboSoLuong.addItem(new ComboBoxItem<>("Còn hàng", 1));
        cboSoLuong.addItem(new ComboBoxItem<>("Hết hàng", 0));
	
	cboSoLuong2.removeAllItems();
        cboSoLuong2.addItem(new ComboBoxItem<>("-- Tất cả số lượng --", -1));
        cboSoLuong2.addItem(new ComboBoxItem<>("Còn hàng", 1));
        cboSoLuong2.addItem(new ComboBoxItem<>("Hết hàng", 0));
	
	Dimension cboSize = new Dimension(150, 36);
	cboSoLuong.setPreferredSize(cboSize);
	cboSoLuong2.setPreferredSize(cboSize);
	cboDanhMuc.setPreferredSize(cboSize);
	cboThuongHieu.setPreferredSize(cboSize);
	cboTrangThai.setPreferredSize(cboSize);
	cboDanhMuc2.setPreferredSize(cboSize);
	cboThuongHieu2.setPreferredSize(cboSize);
	cboTrangThai2.setPreferredSize(cboSize);
	cboXuatXu.setPreferredSize(cboSize);
	cboKieuDang.setPreferredSize(cboSize);
	cboChatLieu.setPreferredSize(cboSize);
	cboDeGiay.setPreferredSize(cboSize);
	cboKichCo.setPreferredSize(cboSize);
	cboMauSac.setPreferredSize(cboSize);
	
        setupTable(tblDanhSach);
	setupTableSPCT(tblDanhSachChiTiet);
        setupPagination();
	setupPaginationSPCT();
	
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadDataDanhMuc();
                loadDataThuongHieu();
                loadDataXuatXu();
                loadDataKieuDang();
                loadDataChatLieu();
                loadDataDeGiay();
                loadDataKichCo();
                loadDataMauSac();

                loadDataPage(1);
                loadDataPageSPCT(1);
                firstLoad = false;
                return null;
            }
        };
        worker.execute();
        
    }
    
    private void setupTable(JTable table) { 
        
        ITableActionEvent event = new ITableActionEvent() {
            @Override
            public void onEdit(int row) {
                InuhaSanPhamModel item = dataItems.get(row);
                showEditSanPham(item);
            }

            @Override
            public void onDelete(int row) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                InuhaSanPhamModel item = dataItems.get(row);
                
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
		    @Override
		    protected Boolean doInBackground() throws Exception {
			return MessageModal.confirmWarning("Xoá: " + item.getTen(), "Bạn thực sự muốn xoá sản phẩm này?");
		    }

		    @Override
		    protected void done() {
			try {
			    if (get()) {
				executorService.submit(() -> {
				    try {
					sanPhamService.delete(item.getId());
					loadDataPage();
					loadDataPageSPCT();
					MessageToast.success("Xoá thành công sản phẩm: " + item.getTen());
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
			} catch (InterruptedException ex) {
			} catch (ExecutionException ex) {
			}
		    }
		    
		};
		worker.execute();
            }

            @Override
            public void onView(int row) {
                InuhaSanPhamModel item = dataItems.get(row);
                showDetailSanPham(item);
            }

        };
        
        pnlDanhSach.setBackground(ColorUtils.BACKGROUND_TABLE);
        TableCustomUI.apply(scrDanhSach, TableCustomUI.TableType.DEFAULT);
	
        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
        table.getColumnModel().getColumn(3).setCellRenderer(new TableImageCellRender(table));
	table.getColumnModel().getColumn(7).setCellRenderer(new InuhaSoLuongTonSanPhamTableCellRender(table));
	table.getColumnModel().getColumn(9).setCellRenderer(new InuhaTrangThaiSanPhamTableCellRender(table));
        table.getColumnModel().getColumn(10).setCellRenderer(new TableActionCellRender(table));
        table.getColumnModel().getColumn(10).setCellEditor(new TableActionCellEditor(event));
    }
    
    private void setupTableSPCT(JTable table) { 
        
        pnlDanhSachChiTiet.setBackground(ColorUtils.BACKGROUND_TABLE);
        TableCustomUI.apply(scrDanhSachChiTiet, TableCustomUI.TableType.DEFAULT);
        TableCustomUI.resizeColumnHeader(table);
	
        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
        table.getColumnModel().getColumn(4).setCellRenderer(new TableImageCellRender(table));
	table.getColumnModel().getColumn(6).setCellRenderer(new InuhaSoLuongTonSanPhamTableCellRender(table));
	table.getColumnModel().getColumn(17).setCellRenderer(new InuhaTrangThaiSanPhamTableCellRender(table));
    }
    
    public void loadDataPage() { 
        loadDataPage(pagination.getCurrentPage());
    }
    
    public void loadDataPageSPCT() { 
        loadDataPageSPCT(paginationSPCT.getCurrentPage());
    }
	
    public void loadDataPage(int page) { 
        try {
            DefaultTableModel model = (DefaultTableModel) tblDanhSach.getModel();
            if (tblDanhSach.isEditing()) {
                tblDanhSach.getCellEditor().stopCellEditing();
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
	    ComboBoxItem<Integer> soLuong = (ComboBoxItem<Integer>) cboSoLuong.getSelectedItem();
            ComboBoxItem<Integer> trangThai = (ComboBoxItem<Integer>) cboTrangThai.getSelectedItem();

            InuhaFilterSanPhamRequest request = new InuhaFilterSanPhamRequest();
	    request.setKeyword(keyword);
	    request.setDanhMuc(danhMuc);
	    request.setThuongHieu(thuongHieu);
	    request.setSoLuong(soLuong);
	    request.setTrangThai(trangThai);
            request.setSize(sizePage);
	    
            int totalPages = sanPhamService.getTotalPage(request);
            
            if (totalPages < page) { 
                page = totalPages;
            }
            
            request.setPage(page);

           
            dataItems = sanPhamService.getPage(request);
            
            for(InuhaSanPhamModel m: dataItems) { 
                model.addRow(m.toDataRowSanPham());
            }

            rerenderPagination(page, totalPages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public void loadDataPageSPCT(int page) { 
        try {
            DefaultTableModel model = (DefaultTableModel) tblDanhSachChiTiet.getModel();
            if (tblDanhSachChiTiet.isEditing()) {
                tblDanhSachChiTiet.getCellEditor().stopCellEditing();
            }
            
	    String keyword = txtTuKhoa2.getText().trim();
            keyword = keyword.replaceAll("\\s+", " ");
        
	    if (keyword.length() > 250) {
		MessageToast.warning("Từ khoá tìm kiếm chỉ được chứa tối đa 250 ký tự");
		return;
	    }
	    
            model.setRowCount(0);
		    
            ComboBoxItem<Integer> danhMuc = (ComboBoxItem<Integer>) cboDanhMuc2.getSelectedItem();
            ComboBoxItem<Integer> thuongHieu = (ComboBoxItem<Integer>) cboThuongHieu2.getSelectedItem();
	    ComboBoxItem<Integer> xuatXu = (ComboBoxItem<Integer>) cboXuatXu.getSelectedItem();
	    ComboBoxItem<Integer> kieuDang = (ComboBoxItem<Integer>) cboKieuDang.getSelectedItem();
	    ComboBoxItem<Integer> chatLieu = (ComboBoxItem<Integer>) cboChatLieu.getSelectedItem();
	    ComboBoxItem<Integer> deGiay = (ComboBoxItem<Integer>) cboDeGiay.getSelectedItem();
	    ComboBoxItem<Integer> kichCo = (ComboBoxItem<Integer>) cboKichCo.getSelectedItem();
	    ComboBoxItem<Integer> mauSac = (ComboBoxItem<Integer>) cboMauSac.getSelectedItem();
	    ComboBoxItem<Integer> soLuong = (ComboBoxItem<Integer>) cboSoLuong2.getSelectedItem();
            ComboBoxItem<Integer> trangThai = (ComboBoxItem<Integer>) cboTrangThai2.getSelectedItem();

            InuhaFilterSanPhamChiTietRequest request = new InuhaFilterSanPhamChiTietRequest();
	    request.setKeyword(keyword);
	    request.setDanhMuc(danhMuc);
	    request.setThuongHieu(thuongHieu);
	    request.setXuatXu(xuatXu);
	    request.setKieuDang(kieuDang);
	    request.setChatLieu(chatLieu);
	    request.setDeGiay(deGiay);
	    request.setKichCo(kichCo);
	    request.setMauSac(mauSac);
	    request.setSoLuong(soLuong);
	    request.setTrangThai(trangThai);
            request.setSize(sizePage);
	    
            int totalPages = sanPhamChiTietService.getTotalPage(request);
            
            if (totalPages < page) { 
                page = totalPages;
            }
            
            request.setPage(page);

           
            dataItemsSPCT = sanPhamChiTietService.getPage(request);
            
            for(InuhaSanPhamChiTietModel m: dataItemsSPCT) { 
                model.addRow(m.toDataRowAllSanPhamChiTiet());
            }

            rerenderPaginationSPCT(page, totalPages);
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
     
    private void setupPaginationSPCT() { 
        paginationSPCT.setPanel(pnlPhanTrangChiTiet);
        paginationSPCT.setCallback(new Pagination.Callback() {
            @Override
            public void onChangeLimitItem(JComboBox<Integer> comboBox) {
                sizePage = (int) comboBox.getSelectedItem();
		executorService.submit(() -> { 
		    loadDataPageSPCT(1);
		    loading.dispose();
		});
                loading.setVisible(true);
            }

            @Override
            public void onClickPage(int page) {
		executorService.submit(() -> { 
		    loadDataPageSPCT(page);
		    loading.dispose();
		});
                loading.setVisible(true);
            }
        });
        paginationSPCT.render();
    }
	
    private void rerenderPagination(int currentPage, int totalPages) { 
	pagination.rerender(currentPage, totalPages);
    }
    
    private void rerenderPaginationSPCT(int currentPage, int totalPages) { 
	paginationSPCT.rerender(currentPage, totalPages);
    }
    
    public void loadDataDanhMuc() { 
        firstLoad = true;
        dataDanhMuc = danhMucService.getAll();
        cboDanhMuc.removeAllItems();
        cboDanhMuc2.removeAllItems();
	
        cboDanhMuc.addItem(new ComboBoxItem<>("-- Tất cả danh mục --", -1));
	cboDanhMuc2.addItem(new ComboBoxItem<>("-- Tất cả danh mục --", -1));
        for(InuhaDanhMucModel m: dataDanhMuc) { 
            cboDanhMuc.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
	    cboDanhMuc2.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        firstLoad = false;
    }

    public void loadDataThuongHieu() { 
        firstLoad = true;
        dataThuongHieu = thuongHieuService.getAll();
        cboThuongHieu.removeAllItems();
        cboThuongHieu2.removeAllItems();
	
        cboThuongHieu.addItem(new ComboBoxItem<>("-- Tất cả thương hiệu --", -1));
	cboThuongHieu2.addItem(new ComboBoxItem<>("-- Tất cả thương hiệu --", -1));
        for(InuhaThuongHieuModel m: dataThuongHieu) { 
            cboThuongHieu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
	    cboThuongHieu2.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        firstLoad = false;
    }

    public void loadDataXuatXu() {
        firstLoad = true;
        dataXuatXu = xuatXuService.getAll();
        cboXuatXu.removeAllItems();
	
        cboXuatXu.addItem(new ComboBoxItem<>("-- Tất cả xuất xứ --", -1));
        for(InuhaXuatXuModel m: dataXuatXu) { 
            cboXuatXu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        firstLoad = false;
    }

    public void loadDataKieuDang() { 
        firstLoad = true;
        dataKieuDang = kieuDangService.getAll();
        cboKieuDang.removeAllItems();
	
        cboKieuDang.addItem(new ComboBoxItem<>("-- Tất cả kiểu dáng --", -1));
        for(InuhaKieuDangModel m: dataKieuDang) { 
            cboKieuDang.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        firstLoad = false;
    }
	
    public void loadDataChatLieu() { 
        firstLoad = true;
        dataChatLieu = chatLieuService.getAll();
        cboChatLieu.removeAllItems();
	
        cboChatLieu.addItem(new ComboBoxItem<>("-- Tất cả chất liệu --", -1));
        for(InuhaChatLieuModel m: dataChatLieu) { 
            cboChatLieu.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        firstLoad = false;
    }
	    
    public void loadDataDeGiay() { 
        firstLoad = true;
        dataDeGiay = deGiayService.getAll();
        cboDeGiay.removeAllItems();
	
        cboDeGiay.addItem(new ComboBoxItem<>("-- Tất cả đế giày --", -1));
        for(InuhaDeGiayModel m: dataDeGiay) { 
            cboDeGiay.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        firstLoad = false;
    }
		
    public void loadDataKichCo() { 
        firstLoad = true;
        dataKichCo = kichCoService.getAll();
        cboKichCo.removeAllItems();
	
        cboKichCo.addItem(new ComboBoxItem<>("-- Tất cả kích cỡ --", -1));
        for(InuhaKichCoModel m: dataKichCo) { 
            cboKichCo.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        firstLoad = false;
    }
		    
    public void loadDataMauSac() { 
        firstLoad = true;
        dataMauSac = mauSacService.getAll();
        cboMauSac.removeAllItems();
	
        cboMauSac.addItem(new ComboBoxItem<>("-- Tất cả màu sắc --", -1));
        for(InuhaMauSacModel m: dataMauSac) { 
            cboMauSac.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
        firstLoad = false;
    }
			
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContainer = new com.app.views.UI.panel.RoundPanel();
        tbpTab = new javax.swing.JTabbedPane();
        pnlDanhSachSanPham = new javax.swing.JPanel();
        pnlFilter = new com.app.views.UI.panel.RoundPanel();
        pnlSearchBox = new com.app.views.UI.panel.SearchBox();
        cboThuongHieu = new javax.swing.JComboBox();
        cboTrangThai = new javax.swing.JComboBox();
        btnClear = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        lblFilter = new javax.swing.JLabel();
        splitLine1 = new com.app.views.UI.label.SplitLine();
        cboDanhMuc = new javax.swing.JComboBox();
        cboSoLuong = new javax.swing.JComboBox();
        pnlList = new com.app.views.UI.panel.RoundPanel();
        lblList = new javax.swing.JLabel();
        btnThemSanPham = new javax.swing.JButton();
        splitLine2 = new com.app.views.UI.label.SplitLine();
        btnXoaSanPham = new javax.swing.JButton();
        pnlPhanTrang = new javax.swing.JPanel();
        pnlDanhSach = new com.app.views.UI.panel.RoundPanel();
        scrDanhSach = new javax.swing.JScrollPane();
        tblDanhSach = new javax.swing.JTable();
        btnScanQR = new javax.swing.JButton();
        pnlDanhSachSanPhamChiTiet = new javax.swing.JPanel();
        pnlList2 = new com.app.views.UI.panel.RoundPanel();
        lblList2 = new javax.swing.JLabel();
        splitLine3 = new com.app.views.UI.label.SplitLine();
        btnSaveAllQR = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        pnlDanhSachChiTiet = new com.app.views.UI.panel.RoundPanel();
        scrDanhSachChiTiet = new javax.swing.JScrollPane();
        tblDanhSachChiTiet = new javax.swing.JTable();
        pnlPhanTrangChiTiet = new javax.swing.JPanel();
        btnScanQRChiTiet = new javax.swing.JButton();
        pnlFilter2 = new com.app.views.UI.panel.RoundPanel();
        lblFilter2 = new javax.swing.JLabel();
        splitLine4 = new com.app.views.UI.label.SplitLine();
        cboDanhMuc2 = new javax.swing.JComboBox();
        cboThuongHieu2 = new javax.swing.JComboBox();
        cboTrangThai2 = new javax.swing.JComboBox();
        cboXuatXu = new javax.swing.JComboBox();
        cboKieuDang = new javax.swing.JComboBox();
        cboChatLieu = new javax.swing.JComboBox();
        cboDeGiay = new javax.swing.JComboBox();
        cboKichCo = new javax.swing.JComboBox();
        cboMauSac = new javax.swing.JComboBox();
        pnlSearchBox2 = new com.app.views.UI.panel.SearchBox();
        btnSearch2 = new javax.swing.JButton();
        btnClear2 = new javax.swing.JButton();
        cboSoLuong2 = new javax.swing.JComboBox();

        pnlDanhSachSanPham.setOpaque(false);

        pnlSearchBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pnlSearchBoxKeyPressed(evt);
            }
        });

        cboThuongHieu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả thương hiệu --" }));
        cboThuongHieu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboThuongHieuItemStateChanged(evt);
            }
        });

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả trạng thái --" }));
        cboTrangThai.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTrangThaiItemStateChanged(evt);
            }
        });

        btnClear.setText("Huỷ lọc");
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnSearch.setText("Tìm kiếm");
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        lblFilter.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblFilter.setText("Bộ lọc");

        javax.swing.GroupLayout splitLine1Layout = new javax.swing.GroupLayout(splitLine1);
        splitLine1.setLayout(splitLine1Layout);
        splitLine1Layout.setHorizontalGroup(
            splitLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1141, Short.MAX_VALUE)
        );
        splitLine1Layout.setVerticalGroup(
            splitLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );

        cboDanhMuc.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả danh mục --" }));
        cboDanhMuc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDanhMucItemStateChanged(evt);
            }
        });

        cboSoLuong.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả số lượng --" }));
        cboSoLuong.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboSoLuongItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addComponent(pnlSearchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboDanhMuc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboThuongHieu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboSoLuong, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboTrangThai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClear)))
                .addGap(76, 76, 76))
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitLine1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlFilterLayout.setVerticalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(lblFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSearchBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cboDanhMuc, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cboThuongHieu, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(20, 20, 20))
        );

        lblList.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblList.setText("Danh sách sản phẩm");

        btnThemSanPham.setText("Thêm sản phẩm");
        btnThemSanPham.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThemSanPham.setMaximumSize(new java.awt.Dimension(50, 23));
        btnThemSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemSanPhamActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout splitLine2Layout = new javax.swing.GroupLayout(splitLine2);
        splitLine2.setLayout(splitLine2Layout);
        splitLine2Layout.setHorizontalGroup(
            splitLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine2Layout.setVerticalGroup(
            splitLine2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        btnXoaSanPham.setText("Xoá");
        btnXoaSanPham.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXoaSanPham.setMaximumSize(new java.awt.Dimension(50, 23));
        btnXoaSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaSanPhamActionPerformed(evt);
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
                "", "#", "Mã", "", "Tên", "Danh mục", "Thương hiệu", "Số lượng tồn", "Giá bán", "Trạng thái", "Hành động"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSach.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
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
            tblDanhSach.getColumnModel().getColumn(2).setMinWidth(50);
            tblDanhSach.getColumnModel().getColumn(3).setMinWidth(60);
            tblDanhSach.getColumnModel().getColumn(3).setMaxWidth(60);
            tblDanhSach.getColumnModel().getColumn(4).setMinWidth(100);
            tblDanhSach.getColumnModel().getColumn(10).setMinWidth(120);
        }

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1165, Short.MAX_VALUE)
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
        );

        btnScanQR.setText("Quét QR");
        btnScanQR.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnScanQR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanQRActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlListLayout = new javax.swing.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDanhSach, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createSequentialGroup()
                        .addComponent(lblList, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnXoaSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnScanQR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnThemSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlPhanTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
            .addComponent(splitLine2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblList, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThemSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoaSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScanQR, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(pnlPhanTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout pnlDanhSachSanPhamLayout = new javax.swing.GroupLayout(pnlDanhSachSanPham);
        pnlDanhSachSanPham.setLayout(pnlDanhSachSanPhamLayout);
        pnlDanhSachSanPhamLayout.setHorizontalGroup(
            pnlDanhSachSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlDanhSachSanPhamLayout.setVerticalGroup(
            pnlDanhSachSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachSanPhamLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pnlFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbpTab.addTab("Danh sách sản phẩm", pnlDanhSachSanPham);

        pnlDanhSachSanPhamChiTiet.setOpaque(false);

        lblList2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblList2.setText("Danh sách chi tiết sản phẩm");

        javax.swing.GroupLayout splitLine3Layout = new javax.swing.GroupLayout(splitLine3);
        splitLine3.setLayout(splitLine3Layout);
        splitLine3Layout.setHorizontalGroup(
            splitLine3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine3Layout.setVerticalGroup(
            splitLine3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 4, Short.MAX_VALUE)
        );

        btnSaveAllQR.setText("Tải danh sách QR Code");
        btnSaveAllQR.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSaveAllQR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAllQRActionPerformed(evt);
            }
        });

        btnImport.setText("Nhập Excel");
        btnImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        btnExport.setText("Xuất Excel");
        btnExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        tblDanhSachChiTiet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "#", "Mã chi tiết", "Mã sản phẩm", "", "Tên sản phẩm", "Số lượng tồn", "Danh mục", "Thương hiệu", "Xuất xứ", "Kiểu dáng", "Chất liệu", "Đế giày", "Kích cỡ", "Màu sắc", "Giá nhập", "Giá bán", "Trạng thái"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSachChiTiet.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDanhSachChiTiet.setAutoscrolls(false);
        tblDanhSachChiTiet.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachChiTiet.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachChiTiet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachChiTietMouseClicked(evt);
            }
        });
        scrDanhSachChiTiet.setViewportView(tblDanhSachChiTiet);
        if (tblDanhSachChiTiet.getColumnModel().getColumnCount() > 0) {
            tblDanhSachChiTiet.getColumnModel().getColumn(0).setMaxWidth(50);
            tblDanhSachChiTiet.getColumnModel().getColumn(4).setMinWidth(60);
            tblDanhSachChiTiet.getColumnModel().getColumn(4).setMaxWidth(60);
        }

        javax.swing.GroupLayout pnlDanhSachChiTietLayout = new javax.swing.GroupLayout(pnlDanhSachChiTiet);
        pnlDanhSachChiTiet.setLayout(pnlDanhSachChiTietLayout);
        pnlDanhSachChiTietLayout.setHorizontalGroup(
            pnlDanhSachChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSachChiTiet, javax.swing.GroupLayout.DEFAULT_SIZE, 1165, Short.MAX_VALUE)
        );
        pnlDanhSachChiTietLayout.setVerticalGroup(
            pnlDanhSachChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDanhSachChiTietLayout.createSequentialGroup()
                .addComponent(scrDanhSachChiTiet, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlPhanTrangChiTietLayout = new javax.swing.GroupLayout(pnlPhanTrangChiTiet);
        pnlPhanTrangChiTiet.setLayout(pnlPhanTrangChiTietLayout);
        pnlPhanTrangChiTietLayout.setHorizontalGroup(
            pnlPhanTrangChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPhanTrangChiTietLayout.setVerticalGroup(
            pnlPhanTrangChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        btnScanQRChiTiet.setText("Quét QR");
        btnScanQRChiTiet.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnScanQRChiTiet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanQRChiTietActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlList2Layout = new javax.swing.GroupLayout(pnlList2);
        pnlList2.setLayout(pnlList2Layout);
        pnlList2Layout.setHorizontalGroup(
            pnlList2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitLine3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlDanhSachChiTiet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlList2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlList2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlList2Layout.createSequentialGroup()
                        .addComponent(lblList2, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnScanQRChiTiet)
                        .addGap(18, 18, 18)
                        .addComponent(btnExport)
                        .addGap(18, 18, 18)
                        .addComponent(btnImport)
                        .addGap(18, 18, 18)
                        .addComponent(btnSaveAllQR))
                    .addComponent(pnlPhanTrangChiTiet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        pnlList2Layout.setVerticalGroup(
            pnlList2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlList2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlList2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlList2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnScanQRChiTiet, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblList2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveAllQR, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitLine3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlDanhSachChiTiet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPhanTrangChiTiet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblFilter2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblFilter2.setText("Bộ lọc");

        javax.swing.GroupLayout splitLine4Layout = new javax.swing.GroupLayout(splitLine4);
        splitLine4.setLayout(splitLine4Layout);
        splitLine4Layout.setHorizontalGroup(
            splitLine4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine4Layout.setVerticalGroup(
            splitLine4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );

        cboDanhMuc2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả danh mục --" }));
        cboDanhMuc2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDanhMuc2ItemStateChanged(evt);
            }
        });

        cboThuongHieu2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả thương hiệu --" }));
        cboThuongHieu2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboThuongHieu2ItemStateChanged(evt);
            }
        });
        cboThuongHieu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboThuongHieu2ActionPerformed(evt);
            }
        });

        cboTrangThai2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả trạng thái --" }));
        cboTrangThai2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTrangThai2ItemStateChanged(evt);
            }
        });

        cboXuatXu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả xuất xứ --" }));
        cboXuatXu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboXuatXuItemStateChanged(evt);
            }
        });
        cboXuatXu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboXuatXuActionPerformed(evt);
            }
        });

        cboKieuDang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả kiểu dáng --" }));
        cboKieuDang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboKieuDangItemStateChanged(evt);
            }
        });
        cboKieuDang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboKieuDangActionPerformed(evt);
            }
        });

        cboChatLieu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả chất liệu --" }));
        cboChatLieu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboChatLieuItemStateChanged(evt);
            }
        });

        cboDeGiay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả đế giày --" }));
        cboDeGiay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDeGiayItemStateChanged(evt);
            }
        });

        cboKichCo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả kích cỡ --" }));
        cboKichCo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboKichCoItemStateChanged(evt);
            }
        });
        cboKichCo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboKichCoActionPerformed(evt);
            }
        });

        cboMauSac.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả màu sắc --" }));
        cboMauSac.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboMauSacItemStateChanged(evt);
            }
        });
        cboMauSac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMauSacActionPerformed(evt);
            }
        });

        pnlSearchBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pnlSearchBox2KeyPressed(evt);
            }
        });

        btnSearch2.setText("Tìm kiếm");
        btnSearch2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearch2ActionPerformed(evt);
            }
        });

        btnClear2.setText("Huỷ lọc");
        btnClear2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClear2ActionPerformed(evt);
            }
        });

        cboSoLuong2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Tất cả số lượng --" }));
        cboSoLuong2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboSoLuong2ItemStateChanged(evt);
            }
        });
        cboSoLuong2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSoLuong2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFilter2Layout = new javax.swing.GroupLayout(pnlFilter2);
        pnlFilter2.setLayout(pnlFilter2Layout);
        pnlFilter2Layout.setHorizontalGroup(
            pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitLine4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlFilter2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSearchBox2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                    .addGroup(pnlFilter2Layout.createSequentialGroup()
                        .addComponent(lblFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlFilter2Layout.createSequentialGroup()
                        .addComponent(cboKieuDang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(cboSoLuong2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilter2Layout.createSequentialGroup()
                        .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboTrangThai2, 0, 185, Short.MAX_VALUE)
                            .addComponent(cboChatLieu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboDanhMuc2, 0, 186, Short.MAX_VALUE)
                            .addComponent(cboDeGiay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlFilter2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboThuongHieu2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboKichCo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboXuatXu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboMauSac, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        pnlFilter2Layout.setVerticalGroup(
            pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilter2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(lblFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(splitLine4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboDanhMuc2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboThuongHieu2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboTrangThai2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboXuatXu, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlSearchBox2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboKichCo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboKieuDang, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboDeGiay, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSoLuong2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFilter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout pnlDanhSachSanPhamChiTietLayout = new javax.swing.GroupLayout(pnlDanhSachSanPhamChiTiet);
        pnlDanhSachSanPhamChiTiet.setLayout(pnlDanhSachSanPhamChiTietLayout);
        pnlDanhSachSanPhamChiTietLayout.setHorizontalGroup(
            pnlDanhSachSanPhamChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFilter2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlList2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlDanhSachSanPhamChiTietLayout.setVerticalGroup(
            pnlDanhSachSanPhamChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachSanPhamChiTietLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pnlFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlList2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbpTab.addTab("Danh sách sản phẩm chi tiết", pnlDanhSachSanPhamChiTiet);

        javax.swing.GroupLayout pnlContainerLayout = new javax.swing.GroupLayout(pnlContainer);
        pnlContainer.setLayout(pnlContainerLayout);
        pnlContainerLayout.setHorizontalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContainerLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(tbpTab)
                .addGap(16, 16, 16))
        );
        pnlContainerLayout.setVerticalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbpTab)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblDanhSachMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachMouseClicked
        // TODO add your handling code here:
	handleClickRowTable(evt);
    }//GEN-LAST:event_tblDanhSachMouseClicked

    private void btnXoaSanPhamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaSanPhamActionPerformed
        // TODO add your handling code here:
        handleClickButtonDelete();
    }//GEN-LAST:event_btnXoaSanPhamActionPerformed

    private void btnThemSanPhamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemSanPhamActionPerformed
        // TODO add your handling code here:
        handleClickButtonAdd();
    }//GEN-LAST:event_btnThemSanPhamActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        handleClickButtonSearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        handleClickButtonClear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void pnlSearchBoxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pnlSearchBoxKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pnlSearchBoxKeyPressed

    private void btnScanQRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanQRActionPerformed
        // TODO add your handling code here:
        handleClickButtonScanQrCode();
    }//GEN-LAST:event_btnScanQRActionPerformed

    private void btnSaveAllQRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveAllQRActionPerformed
        // TODO add your handling code here:
	handleClickButtonSaveAllQR();
    }//GEN-LAST:event_btnSaveAllQRActionPerformed

    private void cboThuongHieu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboThuongHieu2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboThuongHieu2ActionPerformed

    private void cboXuatXuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboXuatXuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboXuatXuActionPerformed

    private void cboKieuDangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboKieuDangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboKieuDangActionPerformed

    private void cboKichCoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboKichCoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboKichCoActionPerformed

    private void cboMauSacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMauSacActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMauSacActionPerformed

    private void pnlSearchBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pnlSearchBox2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pnlSearchBox2KeyPressed

    private void btnClear2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClear2ActionPerformed
        // TODO add your handling code here:
	handleClickButtonClearChiTiet();
    }//GEN-LAST:event_btnClear2ActionPerformed

    private void btnSearch2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearch2ActionPerformed
        // TODO add your handling code here:
	handleClickButtonSeachChiTiet();
    }//GEN-LAST:event_btnSearch2ActionPerformed

    private void tblDanhSachChiTietMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachChiTietMouseClicked
        // TODO add your handling code here:
	handleClickRowTableChiTiet(evt);
    }//GEN-LAST:event_tblDanhSachChiTietMouseClicked

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
	handleClickXuatExcel();
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnScanQRChiTietActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanQRChiTietActionPerformed
        // TODO add your handling code here:
	handleClickButtonScanQrCodeChiTiet();
    }//GEN-LAST:event_btnScanQRChiTietActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
	handleClickButtonImport();
    }//GEN-LAST:event_btnImportActionPerformed

    private void cboSoLuong2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSoLuong2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboSoLuong2ActionPerformed

    private void cboDanhMucItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDanhMucItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSearch();
	}
    }//GEN-LAST:event_cboDanhMucItemStateChanged

    private void cboThuongHieuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboThuongHieuItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSearch();
	}
    }//GEN-LAST:event_cboThuongHieuItemStateChanged

    private void cboSoLuongItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboSoLuongItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSearch();
	}
    }//GEN-LAST:event_cboSoLuongItemStateChanged

    private void cboTrangThaiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTrangThaiItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSearch();
	}
    }//GEN-LAST:event_cboTrangThaiItemStateChanged

    private void cboTrangThai2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTrangThai2ItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboTrangThai2ItemStateChanged

    private void cboDanhMuc2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDanhMuc2ItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboDanhMuc2ItemStateChanged

    private void cboThuongHieu2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboThuongHieu2ItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboThuongHieu2ItemStateChanged

    private void cboXuatXuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboXuatXuItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboXuatXuItemStateChanged

    private void cboMauSacItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboMauSacItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboMauSacItemStateChanged

    private void cboKichCoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboKichCoItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboKichCoItemStateChanged

    private void cboDeGiayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDeGiayItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboDeGiayItemStateChanged

    private void cboChatLieuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboChatLieuItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboChatLieuItemStateChanged

    private void cboSoLuong2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboSoLuong2ItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboSoLuong2ItemStateChanged

    private void cboKieuDangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboKieuDangItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSeachChiTiet();
	}
    }//GEN-LAST:event_cboKieuDangItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClear2;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnSaveAllQR;
    private javax.swing.JButton btnScanQR;
    private javax.swing.JButton btnScanQRChiTiet;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearch2;
    private javax.swing.JButton btnThemSanPham;
    private javax.swing.JButton btnXoaSanPham;
    private javax.swing.JComboBox cboChatLieu;
    private javax.swing.JComboBox cboDanhMuc;
    private javax.swing.JComboBox cboDanhMuc2;
    private javax.swing.JComboBox cboDeGiay;
    private javax.swing.JComboBox cboKichCo;
    private javax.swing.JComboBox cboKieuDang;
    private javax.swing.JComboBox cboMauSac;
    private javax.swing.JComboBox cboSoLuong;
    private javax.swing.JComboBox cboSoLuong2;
    private javax.swing.JComboBox cboThuongHieu;
    private javax.swing.JComboBox cboThuongHieu2;
    private javax.swing.JComboBox cboTrangThai;
    private javax.swing.JComboBox cboTrangThai2;
    private javax.swing.JComboBox cboXuatXu;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblFilter2;
    private javax.swing.JLabel lblList;
    private javax.swing.JLabel lblList2;
    private com.app.views.UI.panel.RoundPanel pnlContainer;
    private com.app.views.UI.panel.RoundPanel pnlDanhSach;
    private com.app.views.UI.panel.RoundPanel pnlDanhSachChiTiet;
    private javax.swing.JPanel pnlDanhSachSanPham;
    private javax.swing.JPanel pnlDanhSachSanPhamChiTiet;
    private com.app.views.UI.panel.RoundPanel pnlFilter;
    private com.app.views.UI.panel.RoundPanel pnlFilter2;
    private com.app.views.UI.panel.RoundPanel pnlList;
    private com.app.views.UI.panel.RoundPanel pnlList2;
    private javax.swing.JPanel pnlPhanTrang;
    private javax.swing.JPanel pnlPhanTrangChiTiet;
    private com.app.views.UI.panel.SearchBox pnlSearchBox;
    private com.app.views.UI.panel.SearchBox pnlSearchBox2;
    private javax.swing.JScrollPane scrDanhSach;
    private javax.swing.JScrollPane scrDanhSachChiTiet;
    private com.app.views.UI.label.SplitLine splitLine1;
    private com.app.views.UI.label.SplitLine splitLine2;
    private com.app.views.UI.label.SplitLine splitLine3;
    private com.app.views.UI.label.SplitLine splitLine4;
    private javax.swing.JTable tblDanhSach;
    private javax.swing.JTable tblDanhSachChiTiet;
    private javax.swing.JTabbedPane tbpTab;
    // End of variables declaration//GEN-END:variables

    private void handleClickButtonDelete() {
        List<Integer> ids = findSelectedSanPhamIds(tblDanhSach);
        if (ids.isEmpty()) { 
            MessageModal.error("Vui lòng chọn ít nhất một sản phẩm muốn xoá!!!");
            return;
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return MessageModal.confirmError("Xoá sản phẩm đã chọn ", "Bạn thực sự muốn xoá những sản phẩm này?");
            }

            @Override
            protected void done() {
                try {
                    if(get()) {
                        executorService.submit(() -> {
                            try {
                                sanPhamService.deleteAll(ids);
                                loadDataPage();
                                MessageToast.success("Xoá thành công " + ids.size() + " sản phẩm đã chọn");
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
                } catch (Exception e) {
                }
            }
            
            
        };
        worker.execute();
    }
    
    private List<Integer> findSelectedSanPhamIds(JTable table) {
        List<Integer> ids = new ArrayList<>();
        for (int row = 0; row < table.getRowCount(); row++) {
            Boolean value = (Boolean) table.getValueAt(row, 0);
            if (Boolean.TRUE.equals(value)) {
                ids.add(dataItems.get(row).getId());
            }
        }
        return ids;
    }

    private List<InuhaSanPhamChiTietModel> findSelectedSanPhamChiTietIds(JTable table) {
        List<InuhaSanPhamChiTietModel> rows = new ArrayList<>();
        for (int row = 0; row < table.getRowCount(); row++) {
            Boolean value = (Boolean) table.getValueAt(row, 0);
            if (Boolean.TRUE.equals(value)) {
                rows.add(dataItemsSPCT.get(row));
            }
        }
        return rows;
    }
	
    private void handleClickButtonAdd() {
        if (ModalDialog.isIdExist("handleClickButtonAdd")) {
            return;
        }
	ModalDialog.closeAllModal();
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaAddSanPhamView(), "Thêm sản phẩm"), "handleClickButtonAdd");
    }

    public void handleClickButtonSearch() {
        executorService.submit(() -> {
            loadDataPage();
            loading.dispose();
        });
        loading.setVisible(true);
    }

    private void handleClickButtonSeachChiTiet() {
        executorService.submit(() -> {
            loadDataPageSPCT();
            loading.dispose();
        });
        loading.setVisible(true);
    }

    private void handleClickButtonClearChiTiet() {
        executorService.submit(() -> {
            firstLoad = true;
            txtTuKhoa2.setText(null);
            cboDanhMuc2.setSelectedIndex(0);
            cboThuongHieu2.setSelectedIndex(0);
            cboTrangThai2.setSelectedIndex(0);
	    cboXuatXu.setSelectedIndex(0);
	    cboKieuDang.setSelectedIndex(0);
	    cboChatLieu.setSelectedIndex(0);
	    cboDeGiay.setSelectedIndex(0);
	    cboKichCo.setSelectedIndex(0);
	    cboMauSac.setSelectedIndex(0);
	    cboSoLuong2.setSelectedIndex(0);
            firstLoad = false;
            loadDataPageSPCT();
            loading.dispose();
        });
        loading.setVisible(true);
    }
    
    public void handleClickButtonClear() {
        executorService.submit(() -> {
            firstLoad = true;
            txtTuKhoa.setText(null);
            cboDanhMuc.setSelectedIndex(0);
            cboThuongHieu.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
	    cboSoLuong.setSelectedIndex(0);
            firstLoad = false;
            loadDataPage();
            loading.dispose();
        });
        loading.setVisible(true);
    }

    private void handleClickButtonScanQrCode() {
        QrCodeHelper.showWebcam("Tìm kiếm sản phẩm bằng QR", result -> {

            executorService.submit(() -> {
                try {
                    String code = result.getText();

                    InuhaSanPhamModel sanPhamModel = null;
                    int id;
                    if ((id = QrCodeUtils.getIdSanPham(code)) > 0) {
                        sanPhamModel = sanPhamService.getById(id);
                    } else if((id = QrCodeUtils.getIdSanPhamChiTiet(code)) > 0) { 
			InuhaSanPhamChiTietModel sanPhamChiTietModel = sanPhamChiTietService.getById(id);
			sanPhamModel = sanPhamChiTietModel.getSanPham();
                    } else { 
                        
                        MessageToast.error("QRCode không hợp lệ!!!");
                        return;
                    }
                    showEditSanPham(sanPhamModel);                
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

    private void showEditSanPham(InuhaSanPhamModel item) {
        if (ModalDialog.isIdExist("showEditSanPham")) {
            return;
        }
	ModalDialog.closeAllModal();
        ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaAddSanPhamView(item), "Chỉnh sửa sản phẩm"), "showEditSanPham");
    }
    
    private void showDetailSanPham(InuhaSanPhamModel item) {
        if (ModalDialog.isIdExist("showDetailSanPham")) {
            return;
        }
	ModalDialog.closeAllModal();
        ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaDetailSanPhamView(item), null), "showDetailSanPham");
    }

    private void showDetailChiTiet(InuhaSanPhamChiTietModel item) {
        if (ModalDialog.isIdExist(ID_MODAL_DEAIL)) {
            return;
        }
	ModalDialog.closeAllModal();
        ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaDetailSanPhamChiTietView(item), null), ID_MODAL_DEAIL);
    }
    
    private void handleClickRowTable(MouseEvent evt) {
	if (evt.getClickCount() > 1) { 
	    InuhaSanPhamModel sanPhamModel = dataItems.get(tblDanhSach.getSelectedRow());
	    showDetailSanPham(sanPhamModel);
	    return;
	}
	
        List<Integer> columns = List.of(0, 10);
        if (SwingUtilities.isLeftMouseButton(evt)) { 
            int row = tblDanhSach.rowAtPoint(evt.getPoint());
            int col = tblDanhSach.columnAtPoint(evt.getPoint());
            boolean isSelected = (boolean) tblDanhSach.getValueAt(row, 0);
            if (!columns.contains(col)) { 
                tblDanhSach.setValueAt(!isSelected, row, 0);
            }
        }
    }
    
    private void handleClickRowTableChiTiet(MouseEvent evt) {
	if (evt.getClickCount() > 1) { 
	    InuhaSanPhamChiTietModel sanPhamChiTietModel = dataItemsSPCT.get(tblDanhSachChiTiet.getSelectedRow());
	    showDetailChiTiet(sanPhamChiTietModel);
	    return;
	}
	
        List<Integer> columns = List.of(0);
        if (SwingUtilities.isLeftMouseButton(evt)) { 
            int row = tblDanhSachChiTiet.rowAtPoint(evt.getPoint());
            int col = tblDanhSachChiTiet.columnAtPoint(evt.getPoint());
            boolean isSelected = (boolean) tblDanhSachChiTiet.getValueAt(row, 0);
            if (!columns.contains(col)) { 
                tblDanhSachChiTiet.setValueAt(!isSelected, row, 0);
            }
        }
    }

    private void handleClickXuatExcel() {
	String fileName = "SanPhamChiTiet-" + TimeUtils.now("dd_MM_yyyy__hh_mm_a");
	String[] headers = new String[] {
	    "STT",
	    "Mã sản phẩm",
	    "Mã chi tiết",
	    "Tên sản phẩm",
	    "Danh mục",
	    "Thương hiệu",
	    "Xuất xứ",
	    "Kiểu dáng",
	    "Chất liệu",
	    "Đế giày",
	    "Kích cỡ",
	    "Màu sắc",
	    "Giá nhập",
	    "Giá bán",
	    "Số lượng",
	    "Trạng thái"
	};

	executorService.submit(() -> { 
	    List<InuhaSanPhamChiTietModel> items = findSelectedSanPhamChiTietIds(tblDanhSachChiTiet);
	    try {
		if (items.isEmpty()) { 
		    items = sanPhamChiTietService.getAll();
		}
		
		List<String[]> rows = new ArrayList<>();
		int i = 1;
		for(InuhaSanPhamChiTietModel item: items) { 
		    rows.add(new String[] { 
			String.valueOf(i++),
			item.getSanPham().getMa(),
			item.getMa(),
			item.getSanPham().getTen(),
			item.getSanPham().getDanhMuc().getTen(),
			item.getSanPham().getThuongHieu().getTen(),
			item.getSanPham().getXuatXu().getTen(),
			item.getSanPham().getKieuDang().getTen(),
			item.getSanPham().getChatLieu().getTen(),
			item.getSanPham().getDeGiay().getTen(),
			item.getKichCo().getTen(),
			item.getMauSac().getTen(),
			CurrencyUtils.parseString(item.getSanPham().getGiaNhap()),
			CurrencyUtils.parseString(item.getSanPham().getGiaBan()),
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

    private void handleClickButtonSaveAllQR() {
        JnaFileChooser ch = new JnaFileChooser();
        ch.setMode(JnaFileChooser.Mode.Directories);
        boolean act = ch.showOpenDialog(Application.app);
        if (act) {
            File folder = ch.getSelectedFile();
            File dir = new File(folder, "SanPhamChiTiet-QRCode-" + TimeUtils.now("dd_MM_yyyy__hh_mm_a"));
	    boolean checkExists = dir.isDirectory();

	    if (!checkExists) {
		dir.mkdirs();
	    }
	    
	    executorService.submit(() -> { 
		List<InuhaSanPhamChiTietModel> items = findSelectedSanPhamChiTietIds(tblDanhSachChiTiet);
		try {
		    if (items.isEmpty()) { 
			items = sanPhamChiTietService.getAll();
		    }

		    int results = 0;
		    for(InuhaSanPhamChiTietModel item: items) { 
			try {
			    File fileName = new File(dir, item.getMa() + "." + QrCodeUtils.IMAGE_FORMAT.toLowerCase());
			    QrCodeUtils.generateQRCodeImage(QrCodeUtils.generateCodeSanPhamChiTiet(item.getId()), fileName);
			    results++;
			} catch (WriterException | IOException e) {
			    e.printStackTrace();
			}
		    }
		    MessageToast.success("Lưu danh sách QR Code thành công!!!");
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
    }

    private void handleClickButtonScanQrCodeChiTiet() {
	QrCodeHelper.showWebcam("Tìm kiếm sản phẩm chi tiết", (result) -> { 
	    String code = result.getText();
	    executorService.submit(() -> { 
		try {
		    int id = QrCodeUtils.getIdSanPhamChiTiet(code);
		    if (id > 0) { 
			InuhaSanPhamChiTietModel model = sanPhamChiTietService.getById(id);
			showDetailChiTiet(model);
		    } else { 
			MessageToast.error("Mã QR không hợp lệ!!!");
		    }
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
	});
    }

    private void handleClickButtonImport() {
	File fileExcel = ExcelHelper.selectFile();
	if (fileExcel == null) {
	    return;
	}
	
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return MessageModal.confirmWarning("Cảnh báo", "Dữ liệu chưa tồn tại thì sẽ được thêm vào còn dữ liệu đã tồn tại sẽ được cập nhật?");
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        executorService.submit(() -> {
                            List<String[]> rows = new ArrayList<>();
                            try {
                                rows = ExcelHelper.readFile(fileExcel, false);
                            } catch (Exception e) { 
                                e.printStackTrace();
                                loading.dispose();
                            }
                            int results = 0;

                            List<String> keywords = Arrays.asList("Mã sản phẩm", "Mã chi tiết", "Kích cỡ", "Màu sắc", "Số lượng", "Trạng thái");
                            String[] headers = rows.get(0);
                            Map<String, Integer> keywordPositions = new HashMap<>();
                            for (int i = 0; i < headers.length; i++) {
                                keywordPositions.put(headers[i], i);
                            }

                            List<Integer> positions = keywords.stream()
                                .map(keyword -> keywordPositions.getOrDefault(keyword, -1))
                                .collect(Collectors.toList());

                            int posSanPham = positions.get(0);
                            int posChiTiet = positions.get(1);
                            int posKichCo = positions.get(2);
                            int posMauSac = positions.get(3);
                            int posSoLuong = positions.get(4);
                            int posTrangThai = positions.get(5);

                            for(String[] row : rows) { 
                                try {
                                    String maSanPham = posSanPham != -1 && posSanPham < row.length ? row[posSanPham] : null;
                                    String maChiTiet = posChiTiet != -1 && posChiTiet < row.length ? row[posChiTiet] : null;
                                    String tenKichCo = posKichCo != -1 && posKichCo < row.length ? row[posKichCo] : null;
                                    String tenMauSac = posMauSac != -1 && posMauSac < row.length ? row[posMauSac] : null;
                                    Integer soLuong = posSoLuong != -1 && posSoLuong < row.length ? (int) CurrencyUtils.parseNumber(row[posSoLuong]) : null;
                                    Boolean trangThai = posTrangThai != -1 && posTrangThai < row.length ? row[posTrangThai].equalsIgnoreCase("Đang bán") : null;

                                    if ((maSanPham == null && maChiTiet == null) || tenKichCo == null || tenMauSac == null || soLuong == null || trangThai == null) { 
                                        continue;
                                    }

                                    InuhaKichCoModel kichCo = kichCoService.insertByExcel(tenKichCo);
                                    InuhaMauSacModel mauSac = mauSacService.insertByExcel(tenMauSac);

                                    InuhaSanPhamModel sanPham = new InuhaSanPhamModel();
                                    sanPham.setMa(maSanPham);

                                    InuhaSanPhamChiTietModel sanPhamChiTiet = new InuhaSanPhamChiTietModel();
                                    sanPhamChiTiet.setMa(maChiTiet);
                                    sanPhamChiTiet.setSoLuong(soLuong);
                                    sanPhamChiTiet.setTrangThai(trangThai);
                                    sanPhamChiTiet.setKichCo(kichCo);
                                    sanPhamChiTiet.setMauSac(mauSac);
                                    sanPhamChiTiet.setSanPham(sanPham);

                                    if (sanPhamChiTietService.insertByExcel(sanPhamChiTiet)) {
                                        results++;
                                    }
                                } catch (Exception e) { 
                                }
                            }


                            if (results > 0) { 
                                MessageToast.success(results + " hàng dữ liệu chịu tác động");
                                loadDataPageSPCT(1);
                            } else {
                                MessageToast.warning("Không có hàng dữ liệu nào chịu tác động");
                            }
                            loading.dispose();
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
