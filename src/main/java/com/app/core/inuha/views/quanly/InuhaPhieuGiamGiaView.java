package com.app.core.inuha.views.quanly;

import com.app.common.helper.JbdcHelper;
import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiPhieuGiamGiaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.request.InuhaFilterPhieuGiamGiaRequest;
import com.app.core.inuha.services.InuhaPhieuGiamGiaService;
import com.app.core.inuha.views.quanly.components.table.thuoctinhsanpham.InuhaThuocTinhTableActionCellEditor;
import com.app.core.inuha.views.quanly.components.table.thuoctinhsanpham.InuhaThuocTinhTableActionCellRender;
import com.app.core.inuha.views.quanly.components.table.phieugiamgia.InuhaTrangThaiPhieuGiamGiaTableCellRender;
import com.app.core.inuha.views.quanly.phieugiamgia.InuhaAddPhieuGiamGiaView;
import com.app.utils.ColorUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.ThemeUtils;
import com.app.utils.TimeUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.table.ITableActionEvent;
import com.app.views.UI.table.TableCustomUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
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
import raven.datetime.component.date.DatePicker;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

/**
 *
 * @author inuHa
 */
public class InuhaPhieuGiamGiaView extends javax.swing.JPanel {

    private static InuhaPhieuGiamGiaView instance;
        
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    private final InuhaPhieuGiamGiaService phieuGiamGiaService = InuhaPhieuGiamGiaService.getInstance();
    
    public Pagination pagination = new Pagination();
    
    private int sizePage = pagination.getLimitItem();
    
    private List<InuhaPhieuGiamGiaModel> dataItems = new ArrayList<>();
    
    public final static String MODAL_ID_CREATE = "modal_create_phieu_giam_gia";
    
    private JTextField txtTuKhoa;
    
    private DatePicker datePickerNgayBatDau = new DatePicker();
    
    private DatePicker datePickerNgayKetThuc = new DatePicker();
	
    private final LoadingDialog loading = new LoadingDialog();
    
    private boolean firstLoad = true;
    
    
    public static InuhaPhieuGiamGiaView getInstance() { 
        if (instance == null) { 
            instance = new InuhaPhieuGiamGiaView();
        }
	
        return instance;
    }
	
    /** Creates new form InuhaPhieuGiamGiaView */
    public InuhaPhieuGiamGiaView() {
	initComponents();
	instance = this;
	
	btnCreate.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnCreate.setForeground(Color.WHITE);
	btnCreate.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
	
	datePickerNgayBatDau.setEditor(txtNgayBatDau);
	datePickerNgayBatDau.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
	datePickerNgayBatDau.setCloseAfterSelected(true);
	
	datePickerNgayKetThuc.setEditor(txtNgayKetThuc);
	datePickerNgayKetThuc.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
	datePickerNgayKetThuc.setCloseAfterSelected(true);
	
	pnlSearchBox.setPlaceholder("Nhập tên hoặc mã giảm giá ...");
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
        cboTrangThai.addItem(new ComboBoxItem<>("Đang diễn ra", TrangThaiPhieuGiamGiaConstant.DANG_DIEN_RA));
        cboTrangThai.addItem(new ComboBoxItem<>("Sắp diễn ra", TrangThaiPhieuGiamGiaConstant.SAP_DIEN_RA));
	cboTrangThai.addItem(new ComboBoxItem<>("Đã diễn ra", TrangThaiPhieuGiamGiaConstant.DA_DIEN_RA));
		
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
                InuhaPhieuGiamGiaModel item = dataItems.get(row);
                showModalEdit(item);
            }

            @Override
            public void onDelete(int row) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
		
                InuhaPhieuGiamGiaModel item = dataItems.get(row);
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return MessageModal.confirmWarning("Xoá: " + item.getMa(), "Không thể hoàn tác. Bạn thực sự muốn xoá phiếu giảm giá này?");
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                executorService.submit(() -> {
                                    try {
                                        phieuGiamGiaService.delete(item.getId());
                                        loadDataPage();
                                        MessageToast.success("Xoá thành công phiếu giảm giá: " + item.getMa());
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
	
	table.getColumnModel().getColumn(10).setCellRenderer(new InuhaTrangThaiPhieuGiamGiaTableCellRender(table));
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
	    String ngayBatDau = null;
	    String ngayKetThuc = null;
	    
	    
	    if (datePickerNgayBatDau.getSelectedDate()!= null) {
		ngayBatDau = TimeUtils.date("yyyy-MM-dd", datePickerNgayBatDau.getSelectedDate());
	    }

	    if (datePickerNgayKetThuc.getSelectedDate()!= null) {
		ngayKetThuc = TimeUtils.date("yyyy-MM-dd", datePickerNgayKetThuc.getSelectedDate());
	    }
		    
            InuhaFilterPhieuGiamGiaRequest request = new InuhaFilterPhieuGiamGiaRequest();
	    request.setKeyword(keyword);
	    request.setTrangThai(trangThai);
	    request.setNgayBatDau(ngayBatDau);
	    request.setNgayKetThuc(ngayKetThuc);
            request.setSize(sizePage);
	    
            int totalPages = phieuGiamGiaService.getTotalPage(request);
            if (totalPages < page) { 
                page = totalPages;
            }
            
            request.setPage(page);

           
            dataItems = phieuGiamGiaService.getPage(request);
            
            for(InuhaPhieuGiamGiaModel m: dataItems) { 
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
        txtNgayBatDau = new javax.swing.JFormattedTextField();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        txtNgayKetThuc = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
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

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("tới ");

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
                        .addGap(18, 18, 18)
                        .addComponent(txtNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(70, Short.MAX_VALUE))))
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
                        .addComponent(txtNgayKetThuc))
                    .addComponent(pnlSearchBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTrangThai)
                    .addComponent(txtNgayBatDau)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );

        lblDanhSach.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblDanhSach.setText("Danh sách phiếu giảm giá");

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
                "#", "Mã", "Tên", "Số lượng", "Kiểu giảm", "Giá trị giảm", "Giảm tối đa", "Đơn tối thiểu", "Ngày bắt đầu", "ngày kết thúc", "Trạng thái", "Hành động"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true, true
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
            tblDanhSach.getColumnModel().getColumn(1).setMinWidth(100);
            tblDanhSach.getColumnModel().getColumn(2).setMinWidth(100);
        }

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach)
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
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

        btnCreate.setText("Tạo mới phiếu giảm giá");
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
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDanhSach, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreate))
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

    private void tblDanhSachMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() > 1) { 
            showModalEdit(dataItems.get(tblDanhSach.getSelectedRow()));
        }
    }//GEN-LAST:event_tblDanhSachMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cboTrangThai;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JFormattedTextField txtNgayBatDau;
    private javax.swing.JFormattedTextField txtNgayKetThuc;
    // End of variables declaration//GEN-END:variables

    private void handleClickButtonClear() {
        executorService.submit(() -> {
            txtTuKhoa.setText(null);
            cboTrangThai.setSelectedIndex(0);
	    datePickerNgayBatDau.clearSelectedDate();
	    datePickerNgayKetThuc.clearSelectedDate();
	    txtNgayBatDau.setText("");
	    txtNgayKetThuc.setText("");
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
	ModalDialog.showModal(this, new SimpleModalBorder(new InuhaAddPhieuGiamGiaView(), "Thêm mới"), "handleClickButtonAdd");
    }
    
    private void showModalEdit(InuhaPhieuGiamGiaModel item) {
        if (ModalDialog.isIdExist("showModalEdit")) {
            return;
        }
        ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaAddPhieuGiamGiaView(item), "Chỉnh sửa"), "showModalEdit");
    }
}
