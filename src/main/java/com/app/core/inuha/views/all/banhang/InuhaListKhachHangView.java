package com.app.core.inuha.views.all.banhang;

import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.inuha.models.InuhaKhachHangModel;
import com.app.core.inuha.request.InuhaFilterKhachHangRequest;
import com.app.core.inuha.services.InuhaKhachHangService;
import com.app.core.inuha.views.all.InuhaBanHangView;
import com.app.core.inuha.views.quanly.components.table.khachhang.InuhaKhachHangTableActionCellEditor;
import com.app.core.inuha.views.quanly.components.table.khachhang.InuhaKhachHangTableActionCellRender;
import com.app.core.inuha.views.quanly.components.table.thuoctinhsanpham.InuhaThuocTinhTableActionCellEditor;
import com.app.core.inuha.views.quanly.components.table.thuoctinhsanpham.InuhaThuocTinhTableActionCellRender;
import com.app.utils.ColorUtils;
import com.app.utils.ResourceUtils;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.table.TableCustomUI;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import com.app.views.UI.table.ITableActionEvent;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 *
 * @author InuHa
 */
public class InuhaListKhachHangView extends javax.swing.JPanel {

    private static InuhaListKhachHangView instance;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    private final InuhaKhachHangService khachHangService = InuhaKhachHangService.getInstance();
    
    public Pagination pagination = new Pagination();
    
    private int sizePage = pagination.getLimitItem();
    
    private List<InuhaKhachHangModel> dataItems = new ArrayList<>();
    
    public final static String MODAL_ID_CREATE = "modal_create_khach_hang";
            
    private JTextField txtTuKhoa;
    
    private final LoadingDialog loading = new LoadingDialog();
        
    public static InuhaListKhachHangView getInstance() { 
        if (instance == null) { 
            instance = new InuhaListKhachHangView(null);
        }
        return instance;
    }
    
    private InuhaKhachHangModel khachHang;
    
    /**
     * Creates new form InuhaQuanLyKhachHangView
     */
    public InuhaListKhachHangView(InuhaKhachHangModel khachHang) {
        initComponents();
        instance = this;
	this.khachHang = khachHang;
		
	pnlSearchBox.setPlaceholder("Nhập tên hoặc số điện thoại ...");
	txtTuKhoa = pnlSearchBox.getKeyword();

	txtTuKhoa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleClickButtonSearch();
                }
            }
        });
	
        btnAdd.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        
        setupTable(tblDanhSach);
        loadDataPage(1);
        setupPagination();
    }

    private void setupTable(JTable table) { 
        
        ITableActionEvent event = new ITableActionEvent() {
            @Override
            public void onEdit(int row) {
                InuhaKhachHangModel item = dataItems.get(row);
                if (ModalDialog.isIdExist(MODAL_ID_CREATE)) {
                    return;
                }
                ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaAddKhachHangView(item), null), MODAL_ID_CREATE);
            }

            @Override
            public void onDelete(int row) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                InuhaKhachHangModel item = dataItems.get(row);
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return MessageModal.confirmWarning("Xoá: " + item.getHoTen(), "Bạn thực sự muốn xoá khách hàng này?");
                    }

                    @Override
                    protected void done() {
                        try {
                            if(get()) {
                                executorService.submit(() -> {
                                    try {
                                        khachHangService.delete(item.getId());
                                        int currentId = khachHang == null ? -1 : khachHang.getId();
                                        if (item.getId() == currentId) {
                                            InuhaBanHangView.getInstance().setKhachHang(null);
                                        }
                                        loadDataPage();
                                        MessageToast.success("Xoá thành công khách hàng: " + item.getHoTen());
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
            }
        };
        
        TableCustomUI.apply(scrDanhSach, TableCustomUI.TableType.DEFAULT);
	TableCustomUI.resizeColumnHeader(table);
        pnlDanhSach.setBackground(ColorUtils.BACKGROUND_TABLE);
        
        table.getColumnModel().getColumn(1).setCellRenderer(new InuhaKhachHangTableActionCellRender(table));
        table.getColumnModel().getColumn(1).setCellEditor(new InuhaKhachHangTableActionCellEditor(event));
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
            
	    
            InuhaFilterKhachHangRequest request = new InuhaFilterKhachHangRequest();
	    request.setKeyword(keyword);
            request.setSize(sizePage);
	    
            int totalPages = khachHangService.getTotalPage(request);
            if (totalPages < page) { 
                page = totalPages;
            }
            
            request.setPage(page);

           
            dataItems = khachHangService.getPage(request);
            
            for(InuhaKhachHangModel m: dataItems) { 
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

        btnAdd = new javax.swing.JButton();
        pnlDanhSach = new com.app.views.UI.panel.RoundPanel();
        scrDanhSach = new javax.swing.JScrollPane();
        tblDanhSach = new javax.swing.JTable();
        pnlPhanTrang = new javax.swing.JPanel();
        pnlSearchBox = new com.app.views.UI.panel.SearchBox();

        btnAdd.setText("Khách hàng mới");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        tblDanhSach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "", "Số điện thoại", "Tên khách hàng", "Lượt mua hàng", "Giới tính", "Địa chỉ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false, false
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
            tblDanhSach.getColumnModel().getColumn(0).setMaxWidth(50);
            tblDanhSach.getColumnModel().getColumn(1).setMinWidth(100);
            tblDanhSach.getColumnModel().getColumn(2).setMinWidth(100);
            tblDanhSach.getColumnModel().getColumn(3).setMinWidth(150);
            tblDanhSach.getColumnModel().getColumn(6).setMinWidth(200);
        }

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrDanhSach, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlPhanTrang.setOpaque(false);

        javax.swing.GroupLayout pnlPhanTrangLayout = new javax.swing.GroupLayout(pnlPhanTrang);
        pnlPhanTrang.setLayout(pnlPhanTrangLayout);
        pnlPhanTrangLayout.setHorizontalGroup(
            pnlPhanTrangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPhanTrangLayout.setVerticalGroup(
            pnlPhanTrangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlPhanTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSearchBox, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAdd)))
                .addGap(41, 41, 41))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSearchBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(pnlDanhSach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlPhanTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        handleClickButtonAdd();
    }//GEN-LAST:event_btnAddActionPerformed

    private void tblDanhSachMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachMouseClicked
        // TODO add your handling code here:
	if (evt.getClickCount() > 1) { 
	    ModalDialog.closeAllModal();
	    InuhaBanHangView.getInstance().setKhachHang(dataItems.get(tblDanhSach.getSelectedRow()));
	}
    }//GEN-LAST:event_tblDanhSachMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private com.app.views.UI.panel.RoundPanel pnlDanhSach;
    private javax.swing.JPanel pnlPhanTrang;
    private com.app.views.UI.panel.SearchBox pnlSearchBox;
    private javax.swing.JScrollPane scrDanhSach;
    private javax.swing.JTable tblDanhSach;
    // End of variables declaration//GEN-END:variables

    
    private void handleClickButtonAdd() {
        if (ModalDialog.isIdExist(MODAL_ID_CREATE)) {
            return;
        }
        ModalDialog.showModal(this, new SimpleModalBorder(new InuhaAddKhachHangView(), null), MODAL_ID_CREATE);
    }
    
    private void handleClickButtonSearch() {
	executorService.submit(() -> {
	    loadDataPage();
	    loading.dispose();
	});
	loading.setVisible(true);
    }
}
