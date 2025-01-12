package com.app.core.inuha.views.quanly;

import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.helper.QrCodeHelper;
import com.app.common.infrastructure.constants.ChucVuConstant;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.GioiTinhConstant;
import com.app.common.infrastructure.constants.TrangThaiNhanVienConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.models.InuhaTaiKhoanModel;
import com.app.core.inuha.request.InuhaFilterTaiKhoanRequest;
import com.app.core.inuha.services.InuhaTaiKhoanService;
import com.app.core.inuha.views.quanly.components.table.nhanvien.InuhaTrangThaiNhanVienTableCellRender;
import com.app.core.inuha.views.quanly.components.table.thuoctinhsanpham.InuhaThuocTinhTableActionCellEditor;
import com.app.core.inuha.views.quanly.components.table.thuoctinhsanpham.InuhaThuocTinhTableActionCellRender;
import com.app.core.inuha.views.quanly.components.table.phieugiamgia.InuhaTrangThaiPhieuGiamGiaTableCellRender;
import com.app.core.inuha.views.quanly.nhanvien.InuhaAddNhanVienView;
import com.app.core.inuha.views.quanly.phieugiamgia.InuhaAddPhieuGiamGiaView;
import com.app.utils.ColorUtils;
import com.app.utils.QrCodeUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.ThemeUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.table.ITableActionEvent;
import com.app.views.UI.table.TableCustomUI;
import com.app.views.UI.table.celll.TableImageCellRender;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ItemEvent;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

/**
 *
 * @author inuHa
 */
public class InuhaNhanVienView extends javax.swing.JPanel {

    private static InuhaNhanVienView instance;
        
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    private final InuhaTaiKhoanService taiKhoanService = InuhaTaiKhoanService.getInstance();
    
    public Pagination pagination = new Pagination();
    
    private int sizePage = pagination.getLimitItem();
    
    private List<InuhaTaiKhoanModel> dataItems = new ArrayList<>();
    
    private final LoadingDialog loading = new LoadingDialog();
        
    private JTextField txtTuKhoa;
	
    private boolean firstLoad = true;
    
    public static InuhaNhanVienView getInstance() { 
        if (instance == null) { 
            instance = new InuhaNhanVienView();
        }
	
        return instance;
    }
	
    /** Creates new form InuhaPhieuGiamGiaView */
    public InuhaNhanVienView() {
	initComponents();
	instance = this;
	
	btnCreate.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnCreate.setForeground(Color.WHITE);
	btnCreate.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));

	pnlSearchBox.setPlaceholder("Nhập tên, tài khoản hoặc email ...");
        txtTuKhoa = pnlSearchBox.getKeyword();
	
	btnSearch.setIcon(ResourceUtils.getSVG("/svg/search.svg", new Dimension(20, 20)));
	btnClear.setBackground(ColorUtils.BUTTON_GRAY);
	
	lblFilter.setIcon(ResourceUtils.getSVG("/svg/filter.svg", new Dimension(20, 20)));
	lblDanhSach.setIcon(ResourceUtils.getSVG("/svg/list.svg", new Dimension(20, 20)));
	
	pnlFilter.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	
	txtTuKhoa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleClickButtonSearch();
                }
            }
        });
        
        if (ThemeUtils.isLight()) { 
            btnSearch.setBackground(ColorUtils.BUTTON_PRIMARY);
            btnSearch.setForeground(Color.WHITE);
        }
	
	cboTrangThai.removeAllItems();
        cboTrangThai.addItem(new ComboBoxItem<>("-- Tất cả trạng thái --", -1));
        cboTrangThai.addItem(new ComboBoxItem<>("Đang hoạt động", TrangThaiNhanVienConstant.DANG_LAM));
        cboTrangThai.addItem(new ComboBoxItem<>("Đã nghỉ việc", TrangThaiNhanVienConstant.DA_NGHI));
	
	cboChucVu.removeAllItems();
        cboChucVu.addItem(new ComboBoxItem<>("-- Tất cả chức vụ --", -1));
        cboChucVu.addItem(new ComboBoxItem<>("Quản lý", ChucVuConstant.QUAN_LY));
        cboChucVu.addItem(new ComboBoxItem<>("Nhân viên", ChucVuConstant.NHAN_VIEN));

	cboGioiTinh.removeAllItems();
        cboGioiTinh.addItem(new ComboBoxItem<>("-- Tất cả giới tính --", -1));
        cboGioiTinh.addItem(new ComboBoxItem<>("Nam", GioiTinhConstant.NAM));
        cboGioiTinh.addItem(new ComboBoxItem<>("Nữ", GioiTinhConstant.NU));
	
	Dimension cboSize = new Dimension(150, 36);
	cboTrangThai.setPreferredSize(cboSize);
	
	setupTable(tblDanhSach);
        setupPagination();
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadDataPage(1);
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
                InuhaTaiKhoanModel item = dataItems.get(row);
		showEdit(item);
            }

            @Override
            public void onDelete(int row) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
		
                InuhaTaiKhoanModel item = dataItems.get(row);
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return MessageModal.confirmWarning("Xoá: " + item.getUsername(), "Không thể hoàn tác. Bạn thực sự muốn xoá nhân viên này?");
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                executorService.submit(() -> {
                                    try {
                                        taiKhoanService.delete(item.getId());
                                        loadDataPage();
                                        MessageToast.success("Xoá thành công nhân viên: " + item.getUsername());
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
                        } catch (InterruptedException | ExecutionException ex) {
                        }
                    }
                    
                };
                worker.execute();
            }

            @Override
            public void onView(int row) {
            }
        };
		
	pnlList.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlDanhSach.setBackground(ColorUtils.BACKGROUND_TABLE);
        TableCustomUI.apply(scrDanhSach, TableCustomUI.TableType.DEFAULT);

        tblDanhSach.setBackground(ColorUtils.BACKGROUND_TABLE);
        tblDanhSach.getTableHeader().setBackground(ColorUtils.BACKGROUND_TABLE);
	
	table.getColumnModel().getColumn(1).setCellRenderer(new TableImageCellRender(table));
	table.getColumnModel().getColumn(9).setCellRenderer(new InuhaTrangThaiNhanVienTableCellRender(table));
	table.getColumnModel().getColumn(11).setCellRenderer(new InuhaThuocTinhTableActionCellRender(table));
        table.getColumnModel().getColumn(11).setCellEditor(new InuhaThuocTinhTableActionCellEditor(event));
    }
    
    public void loadDataPage() { 
        loadDataPage(pagination.getCurrentPage());
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
            
	    ComboBoxItem<Integer> trangThai = (ComboBoxItem<Integer>) cboTrangThai.getSelectedItem();
	    ComboBoxItem<Integer> chucVu = (ComboBoxItem<Integer>) cboChucVu.getSelectedItem();
	    ComboBoxItem<Integer> gioiTinh = (ComboBoxItem<Integer>) cboGioiTinh.getSelectedItem();
		    
            InuhaFilterTaiKhoanRequest request = new InuhaFilterTaiKhoanRequest();
	    request.setKeyword(keyword);
	    request.setTrangThai(trangThai);
	    request.setChucVu(chucVu);
	    request.setGioiTinh(gioiTinh);
            request.setSize(sizePage);
	    
            int totalPages = taiKhoanService.getTotalPage(request);
            if (totalPages < page) { 
                page = totalPages;
            }
            
            request.setPage(page);

           
            dataItems = taiKhoanService.getPage(request);
            
            for(InuhaTaiKhoanModel m: dataItems) { 
                model.addRow(m.toDataRow());
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
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFilter = new com.app.views.UI.panel.RoundPanel();
        lblFilter = new javax.swing.JLabel();
        splitLine2 = new com.app.views.UI.label.SplitLine();
        pnlSearchBox = new com.app.views.UI.panel.SearchBox();
        cboTrangThai = new javax.swing.JComboBox();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        cboGioiTinh = new javax.swing.JComboBox();
        cboChucVu = new javax.swing.JComboBox();
        pnlList = new com.app.views.UI.panel.RoundPanel();
        lblDanhSach = new javax.swing.JLabel();
        splitLine1 = new com.app.views.UI.label.SplitLine();
        pnlDanhSach = new javax.swing.JPanel();
        scrDanhSach = new javax.swing.JScrollPane();
        tblDanhSach = new javax.swing.JTable();
        pnlPhanTrang = new javax.swing.JPanel();
        btnCreate = new javax.swing.JButton();

        lblFilter.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblFilter.setText("Bộ lọc");

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

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTrangThai.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTrangThaiItemStateChanged(evt);
            }
        });

        btnSearch.setText("Tìm kiếm");
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnClear.setText("Huỷ");
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        cboGioiTinh.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboGioiTinh.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboGioiTinhItemStateChanged(evt);
            }
        });

        cboChucVu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboChucVu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboChucVuItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitLine2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addComponent(lblFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(20, 20, 20))
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addComponent(pnlSearchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(79, Short.MAX_VALUE))))
        );
        pnlFilterLayout.setVerticalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilterLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboChucVu)
                            .addComponent(cboGioiTinh)
                            .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(pnlSearchBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTrangThai))
                .addGap(20, 20, 20))
        );

        lblDanhSach.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblDanhSach.setText("Danh sách nhân viên");

        javax.swing.GroupLayout splitLine1Layout = new javax.swing.GroupLayout(splitLine1);
        splitLine1.setLayout(splitLine1Layout);
        splitLine1Layout.setHorizontalGroup(
            splitLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine1Layout.setVerticalGroup(
            splitLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        tblDanhSach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "", "Tài khoản", "Email", "Họ tên", "Số điện thoại", "Giới tính", "Địa chỉ", "Chức vụ", "Trạng thái", "Ngày tạo", "Hành động"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSach.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSach.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDanhSach.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachMouseClicked(evt);
            }
        });
        scrDanhSach.setViewportView(tblDanhSach);
        if (tblDanhSach.getColumnModel().getColumnCount() > 0) {
            tblDanhSach.getColumnModel().getColumn(0).setMaxWidth(40);
            tblDanhSach.getColumnModel().getColumn(1).setMinWidth(60);
            tblDanhSach.getColumnModel().getColumn(1).setMaxWidth(60);
            tblDanhSach.getColumnModel().getColumn(2).setMinWidth(100);
            tblDanhSach.getColumnModel().getColumn(3).setMinWidth(100);
        }

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach)
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
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

        btnCreate.setText("Thêm nhân viên mới");
        btnCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlListLayout = new javax.swing.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblDanhSach, javax.swing.GroupLayout.PREFERRED_SIZE, 736, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
            .addGroup(pnlListLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(pnlPhanTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
            .addComponent(splitLine1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlDanhSach, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCreate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDanhSach, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(splitLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(pnlPhanTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pnlFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
	handleClickButtonAdd();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
	handleClickButtonSearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
	handleClickButtonClear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void cboTrangThaiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTrangThaiItemStateChanged
        // TODO add your handling code here:
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSearch();
	}
    }//GEN-LAST:event_cboTrangThaiItemStateChanged

    private void cboGioiTinhItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboGioiTinhItemStateChanged
        // TODO add your handling code here:
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSearch();
	}
    }//GEN-LAST:event_cboGioiTinhItemStateChanged

    private void cboChucVuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboChucVuItemStateChanged
        // TODO add your handling code here:
	if (evt.getStateChange() == ItemEvent.SELECTED) {
	    handleClickButtonSearch();
	}
    }//GEN-LAST:event_cboChucVuItemStateChanged

    private void tblDanhSachMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachMouseClicked
        // TODO add your handling code here:
	if (evt.getClickCount() > 1) { 
	    showEdit(dataItems.get(tblDanhSach.getSelectedRow()));
	}
    }//GEN-LAST:event_tblDanhSachMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cboChucVu;
    private javax.swing.JComboBox cboGioiTinh;
    private javax.swing.JComboBox cboTrangThai;
    private javax.swing.JLabel lblDanhSach;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JPanel pnlDanhSach;
    private com.app.views.UI.panel.RoundPanel pnlFilter;
    private com.app.views.UI.panel.RoundPanel pnlList;
    private javax.swing.JPanel pnlPhanTrang;
    private com.app.views.UI.panel.SearchBox pnlSearchBox;
    private javax.swing.JScrollPane scrDanhSach;
    private com.app.views.UI.label.SplitLine splitLine1;
    private com.app.views.UI.label.SplitLine splitLine2;
    private javax.swing.JTable tblDanhSach;
    // End of variables declaration//GEN-END:variables

    private void handleClickButtonClear() {
        executorService.submit(() -> {
            firstLoad = true;
            txtTuKhoa.setText(null);
            cboTrangThai.setSelectedIndex(0);
	    cboGioiTinh.setSelectedIndex(0);
	    cboChucVu.setSelectedIndex(0);
            firstLoad = false;
            loadDataPage();
            loading.dispose();
        });
        loading.setVisible(true);
    }
	
    private void handleClickButtonSearch() {
	if (firstLoad) {
	    return;
	}
        executorService.submit(() -> {
            loadDataPage();
            loading.dispose();
        });
        loading.setVisible(true);
    }

    private void handleClickButtonAdd() {
        if (ModalDialog.isIdExist("handleClickButtonAdd")) {
            return;
        }
	ModalDialog.closeAllModal();
	ModalDialog.showModal(this, new SimpleModalBorder(new InuhaAddNhanVienView(), "Thêm nhân viên"), "handleClickButtonAdd");
    }
    
    private void showEdit(InuhaTaiKhoanModel item) { 
        if (ModalDialog.isIdExist("showEdit")) {
            return;
        }
	ModalDialog.closeAllModal();
	ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaAddNhanVienView(item), "Chỉnh sửa thông tin"), "showEdit");
    }


}
