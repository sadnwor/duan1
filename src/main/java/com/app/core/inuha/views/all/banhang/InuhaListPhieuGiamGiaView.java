package com.app.core.inuha.views.all.banhang;

import com.app.common.helper.JbdcHelper;
import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiPhieuGiamGiaConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.models.InuhaPhieuGiamGiaModel;
import com.app.core.inuha.request.InuhaFilterPhieuGiamGiaRequest;
import com.app.core.inuha.request.InuhaFilterPhieuGiamGiaRequest;
import com.app.core.inuha.services.InuhaPhieuGiamGiaService;
import com.app.core.inuha.services.InuhaPhieuGiamGiaService;
import com.app.core.inuha.views.all.InuhaBanHangView;
import com.app.core.inuha.views.all.InuhaBanHangView.ChiTietThanhToan;
import com.app.core.inuha.views.quanly.components.table.thuoctinhsanpham.InuhaThuocTinhTableActionCellEditor;
import com.app.core.inuha.views.quanly.components.table.thuoctinhsanpham.InuhaThuocTinhTableActionCellRender;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.table.TableCustomUI;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import com.app.views.UI.table.ITableActionEvent;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author InuHa
 */
public class InuhaListPhieuGiamGiaView extends javax.swing.JPanel {

    private static InuhaListPhieuGiamGiaView instance;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    private final InuhaPhieuGiamGiaService phieuGiamGiaService = InuhaPhieuGiamGiaService.getInstance();
    
    public Pagination pagination = new Pagination();
    
    private int sizePage = pagination.getLimitItem();
    
    private List<InuhaPhieuGiamGiaModel> dataItems = new ArrayList<>();
    
    public final static String MODAL_ID_CREATE = "modal_create_phieu_giam_gia";
            
    private final LoadingDialog loading = new LoadingDialog();
        
    public static InuhaListPhieuGiamGiaView getInstance() { 
        if (instance == null) { 
            instance = new InuhaListPhieuGiamGiaView(null);
        }
        return instance;
    }
    
    private ChiTietThanhToan chiTietThanhToan = null;
    
    private InuhaPhieuGiamGiaModel phieuGiamGia = null;
    
    /**
     * Creates new form InuhaQuanLyPhieuGiamGiaView
     */
    public InuhaListPhieuGiamGiaView(ChiTietThanhToan chiTietThanhToan) {
        initComponents();
        instance = this;
	this.chiTietThanhToan = chiTietThanhToan;
	

	txtMa.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã giảm giá...");
	txtMa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                    handleClickButtonAdd();
                }
            }
        });
	
        btnAdd.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        
        setupTable(tblDanhSach);
        loadDataPage(1);
        setupPagination();
    }

    private void setupTable(JTable table) { 
	TableCustomUI.apply(scrDanhSach, TableCustomUI.TableType.DEFAULT);
	TableCustomUI.resizeColumnHeader(table);
        pnlDanhSach.setBackground(ColorUtils.BACKGROUND_TABLE);
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
            
            model.setRowCount(0);
            
            InuhaFilterPhieuGiamGiaRequest request = new InuhaFilterPhieuGiamGiaRequest();
	    request.setTrangThai(new ComboBoxItem<Integer>("Đang diễn ra", TrangThaiPhieuGiamGiaConstant.DANG_DIEN_RA));
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

        btnAdd = new javax.swing.JButton();
        pnlDanhSach = new com.app.views.UI.panel.RoundPanel();
        scrDanhSach = new javax.swing.JScrollPane();
        tblDanhSach = new javax.swing.JTable();
        pnlPhanTrang = new javax.swing.JPanel();
        txtMa = new javax.swing.JTextField();

        btnAdd.setText("Áp dụng mã");
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
                "#", "Mã", "Tên", "Số lượng", "Kiểu giảm", "Giá trị giảm", "Giảm tối đa", "Hoá đơn tối thiểu", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
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
        }

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
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

        txtMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaActionPerformed(evt);
            }
        });

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
                        .addComponent(txtMa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAdd)))
                .addGap(41, 41, 41))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMa, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
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
	phieuGiamGia = dataItems.get(tblDanhSach.getSelectedRow());
	txtMa.setText(phieuGiamGia.getMa().trim());
	
	if (evt.getClickCount() > 1) {
	    handleClickButtonAdd();
	}
    }//GEN-LAST:event_tblDanhSachMouseClicked

    private void txtMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private com.app.views.UI.panel.RoundPanel pnlDanhSach;
    private javax.swing.JPanel pnlPhanTrang;
    private javax.swing.JScrollPane scrDanhSach;
    private javax.swing.JTable tblDanhSach;
    private javax.swing.JTextField txtMa;
    // End of variables declaration//GEN-END:variables

    
    private void handleClickButtonAdd() {
	String ma = txtMa.getText().trim();
	if (ma.isEmpty()) { 
	    MessageToast.error("Vui lòng nhập mã giảm giá");
	    return;
	}
	
	executorService.submit(() -> {
	    try {
		InuhaPhieuGiamGiaModel model = phieuGiamGiaService.getByCode(ma);
		if (chiTietThanhToan.getTongTienHang() < model.getDonToiThieu()) { 
		    throw new ServiceResponseException("Giá trị đơn hàng phải thấp nhất là " + CurrencyUtils.parseString(model.getDonToiThieu()) + " để có thể áp dụng");
		}
		InuhaBanHangView.getInstance().setVoucher(phieuGiamGia);
		ModalDialog.closeAllModal();
	    } catch (ServiceResponseException e) {
		MessageToast.error(e.getMessage());
	    } catch (Exception e) {
		MessageToast.error(ErrorConstant.DEFAULT_ERROR);
	    } finally {
		txtMa.requestFocus();
		loading.dispose();
	    }
	});
	loading.setVisible(true);
    }
    
}
