/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.app.core.dattv.views;

import com.app.Application;
import com.app.common.helper.ExcelHelper;
import com.app.common.helper.MessageModal;
import com.app.common.helper.PdfHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.common.models.invoice.InvoiceDataModel;
import com.app.core.common.models.invoice.InvoiceProduct;
import com.app.core.dattv.models.DatHoaDonChiTietModel;
import com.app.core.dattv.repositoris.DatHoaDonChiTietRepository;
import com.app.core.dattv.request.DatHoaDonRequest;
import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.services.InuhaSanPhamChiTietService;
import com.app.core.inuha.views.quanly.sanpham.InuhaDetailSanPhamChiTietView;
import com.app.utils.BillUtils;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.TimeUtils;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.table.TableCustomUI;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

/**
 *
 * @author WIN
 */
public class DatHoaDonChiTietView extends javax.swing.JPanel {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    DatHoaDonChiTietRepository datHoaDonChiTietRepository=new DatHoaDonChiTietRepository();
    private final LoadingDialog loading = new LoadingDialog();
    DatHoaDonRequest datHoaDonRequest= null;

    private ArrayList<DatHoaDonChiTietModel> list = new ArrayList<>();
    private static DatHoaDonChiTietView instance;
    
    /**
     * Creates new form DatHoaDon
     */
    public DatHoaDonChiTietView(DatHoaDonRequest datHoaDonRequest) {
        initComponents();
        instance = this;
        this.datHoaDonRequest=datHoaDonRequest;

        list=datHoaDonChiTietRepository.loadDatHoaDonChiTietTable(datHoaDonRequest.getId());
        loadData(list);

        lblMaHd.setForeground(ColorUtils.PRIMARY_COLOR);
        switch (datHoaDonRequest.getTrangThai()) {
            case TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN -> lblTrangThai.setForeground(ColorUtils.WARNING_COLOR);
            case TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN -> lblTrangThai.setForeground(ColorUtils.SUCCESS_COLOR);
            case TrangThaiHoaDonConstant.STATUS_DA_HUY -> lblTrangThai.setForeground(ColorUtils.DANGER_COLOR);
        }
        
        btnInHoaDon.setBackground(ColorUtils.BUTTON_PRIMARY);
        setupTable(tblHoadonchitiet);

        double tienThua = 0;
        if (datHoaDonRequest.getTienMat() > 0 || datHoaDonRequest.getTienChuyenKhoan() > 0) {
            tienThua = (datHoaDonRequest.getTienMat() + datHoaDonRequest.getTienChuyenKhoan()) - datHoaDonRequest.getThanhTien();
        }

        String khachHang = datHoaDonRequest.getKhachHang();
        if (datHoaDonRequest.getSdt() != null && !datHoaDonRequest.getSdt().isEmpty()) {
            khachHang += " - " + datHoaDonRequest.getSdt();
        }

        lblMaHd.setText(datHoaDonRequest.getMaHd());
        lblNgayTao.setText(TimeUtils.date("dd-MM-yyyy HH:mm", datHoaDonRequest.getThoiGian()));
        lblTenkhachhang.setText(khachHang);
        lblTrangThai.setText(BillUtils.getTrangThai(datHoaDonRequest.getTrangThai()));
        lblPhuongThucThanhToan.setText(BillUtils.getPhuongThucThanhToan(datHoaDonRequest.getPhuongThucTT()));
        lblTienMat.setText(CurrencyUtils.parseString(datHoaDonRequest.getTienMat()));
        lblTienChuyenKhoan.setText(CurrencyUtils.parseString(datHoaDonRequest.getTienChuyenKhoan()));
        lblTienThua.setText(CurrencyUtils.parseString(tienThua));
        lblNhanvien.setText(datHoaDonRequest.getTenNv());
        lblTongtienhang.setText(CurrencyUtils.parseString(datHoaDonRequest.getTongTienhang()));
        lblGiamgia.setText(CurrencyUtils.parseString(datHoaDonRequest.getGiamGia()));
        lblThanhtoan.setText(CurrencyUtils.parseString(datHoaDonRequest.getThanhTien()));

    }
    
    private void setupTable(JTable table){
        TableCustomUI.apply(jScrollPane3, TableCustomUI.TableType.DEFAULT);
    }

    void loadData(ArrayList<DatHoaDonChiTietModel> list) {
        DefaultTableModel tableModel = (DefaultTableModel) tblHoadonchitiet.getModel();
        tableModel.setRowCount(0);

        int i = 1;
        for (DatHoaDonChiTietModel datHoaDonChiTietModel : list) {
           Object[] row={
               i++,
               datHoaDonChiTietModel.getMa(),
               datHoaDonChiTietModel.getTen(),
               datHoaDonChiTietModel.getKichCo(),
               datHoaDonChiTietModel.getMauSac(),
               CurrencyUtils.parseNumber(datHoaDonChiTietModel.getSoLuong()),
               CurrencyUtils.parseString(datHoaDonChiTietModel.getGiaBan()),
           };
           
            tableModel.addRow(row);
        }
    }

    public void inDanhSach(){
        String fileName = "HoaDon_" + datHoaDonRequest.getMaHd() + "-" + TimeUtils.now("dd_MM_yyyy__hh_mm_a");
	String[] headers = new String[] {
	    "STT",
	    "Mã sản phẩm",
	    "Tên sản phẩm",
	    "Kích cỡ",
	    "Màu sắc",
	    "Số lượng",
	    "Đơn giá"
	};

	executorService.submit(() -> {

	    try {


		List<String[]> rows = new ArrayList<>();
                int i = 1;
		for(DatHoaDonChiTietModel item: list) {
		    rows.add(new String[]{
                        String.valueOf(i++),
                        item.getMa(),
                        item.getTen(),
                        item.getKichCo(),
                        item.getMauSac(),
                        String.valueOf(item.getSoLuong()),
                        String.valueOf(item.getGiaBan())
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

    private void showDetailSanPham(InuhaSanPhamChiTietModel item) {
        if (ModalDialog.isIdExist("showDetailSanPham")) {
            return;
        }
        ModalDialog.showModal(instance, new SimpleModalBorder(new InuhaDetailSanPhamChiTietView(item), null), "showDetailSanPham");
    }
    
   
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        roundPanel1 = new com.app.views.UI.panel.RoundPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        roundPanel2 = new com.app.views.UI.panel.RoundPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblHoadonchitiet = new javax.swing.JTable();
        roundPanel4 = new com.app.views.UI.panel.RoundPanel();
        btnInHoaDon = new javax.swing.JButton();
        btnXuat = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblTongtienhang = new javax.swing.JLabel();
        lblThanhtoan = new javax.swing.JLabel();
        lblGiamgia = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblTienMat = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblTienChuyenKhoan = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTienThua = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblMaHd = new javax.swing.JLabel();
        lblNgayTao = new javax.swing.JLabel();
        lblTenkhachhang = new javax.swing.JLabel();
        lblNhanvien = new javax.swing.JLabel();
        lblTrangThai = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblPhuongThucThanhToan = new javax.swing.JLabel();

        tblHoadonchitiet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Mã", "Tên", "Kích cỡ", "Màu sắc", "Số lượng", "Đơn giá"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHoadonchitiet.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblHoadonchitiet.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblHoadonchitiet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoadonchitietMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblHoadonchitiet);
        if (tblHoadonchitiet.getColumnModel().getColumnCount() > 0) {
            tblHoadonchitiet.getColumnModel().getColumn(0).setMaxWidth(60);
        }

        btnInHoaDon.setText("In hoá đơn");
        btnInHoaDon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInHoaDonActionPerformed(evt);
            }
        });

        btnXuat.setText("Xuất Excel");
        btnXuat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Tổng tiền hàng :");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Tổng giảm giá:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Tổng thanh toán:");

        lblTongtienhang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTongtienhang.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTongtienhang.setText("jLabel11");

        lblThanhtoan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblThanhtoan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblThanhtoan.setText("jLabel11");

        lblGiamgia.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblGiamgia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGiamgia.setText("jLabel11");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Tiền mặt khách trả:");

        lblTienMat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTienMat.setText("jLabel11");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("Tiền khách chuyển khoản:");

        lblTienChuyenKhoan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTienChuyenKhoan.setText("jLabel11");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Tiền trả lại khách:");

        lblTienThua.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTienThua.setText("jLabel11");

        javax.swing.GroupLayout roundPanel4Layout = new javax.swing.GroupLayout(roundPanel4);
        roundPanel4.setLayout(roundPanel4Layout);
        roundPanel4Layout.setHorizontalGroup(
            roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel4Layout.createSequentialGroup()
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundPanel4Layout.createSequentialGroup()
                        .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(lblTienChuyenKhoan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(roundPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(lblTienThua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(roundPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(lblTienMat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblGiamgia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTongtienhang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblThanhtoan)))
                    .addGroup(roundPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnInHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        roundPanel4Layout.setVerticalGroup(
            roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTongtienhang)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTienMat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGiamgia)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTienChuyenKhoan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblThanhtoan)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTienThua))
                .addGap(18, 18, 18)
                .addGroup(roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnInHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(btnXuat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        jLabel5.setText("Mã hóa đơn :");

        jLabel6.setText("Ngày tạo:");

        jLabel7.setText("Khách hàng :");

        jLabel8.setText("Trạng thái :");

        jLabel9.setText("Người tạo:");

        lblMaHd.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblMaHd.setText("lblMaHd");

        lblNgayTao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNgayTao.setText("jLabel11");

        lblTenkhachhang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTenkhachhang.setText("jLabel11");

        lblNhanvien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNhanvien.setText("jLabel11");

        lblTrangThai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTrangThai.setText("jLabel10");

        jLabel10.setText("Phương thức thanh toán:");

        lblPhuongThucThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPhuongThucThanhToan.setText("jLabel11");

        javax.swing.GroupLayout roundPanel2Layout = new javax.swing.GroupLayout(roundPanel2);
        roundPanel2.setLayout(roundPanel2Layout);
        roundPanel2Layout.setHorizontalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(roundPanel2Layout.createSequentialGroup()
                        .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNgayTao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTenkhachhang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMaHd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblPhuongThucThanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(roundPanel2Layout.createSequentialGroup()
                                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTrangThai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(roundPanel2Layout.createSequentialGroup()
                                        .addComponent(lblNhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))))
                    .addComponent(roundPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        roundPanel2Layout.setVerticalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblMaHd)
                    .addComponent(jLabel5)
                    .addComponent(lblTrangThai))
                .addGap(20, 20, 20)
                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblNgayTao)
                    .addComponent(jLabel9)
                    .addComponent(lblNhanvien))
                .addGap(20, 20, 20)
                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblTenkhachhang)
                    .addComponent(jLabel10)
                    .addComponent(lblPhuongThucThanhToan))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roundPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Hóa đơn chi tiết", roundPanel2);

        javax.swing.GroupLayout roundPanel1Layout = new javax.swing.GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addGap(0, 0, 0))
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(roundPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatActionPerformed
        // TODO add your handling code here:
        inDanhSach();
    }//GEN-LAST:event_btnXuatActionPerformed

    private void btnInHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInHoaDonActionPerformed
        // TODO add your handling code here:
        inHoaDon();
    }//GEN-LAST:event_btnInHoaDonActionPerformed

    private void tblHoadonchitietMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoadonchitietMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() > 1) { 
            executorService.submit(() -> {
                try {
                    DatHoaDonChiTietModel hoaDonChiTiet = list.get(tblHoadonchitiet.getSelectedRow());
                    InuhaSanPhamChiTietModel sanPhamChiTiet = InuhaSanPhamChiTietService.getInstance().getById(hoaDonChiTiet.getIdSanPhamChiTiet());
		    if (sanPhamChiTiet == null || sanPhamChiTiet.getSanPham() == null) { 
			MessageModal.error("Sản phẩm không tồn tại hoặc đã bị xoá!");
			return;
		    }
                    showDetailSanPham(sanPhamChiTiet);
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
	}
    }//GEN-LAST:event_tblHoadonchitietMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInHoaDon;
    private javax.swing.JButton btnXuat;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblGiamgia;
    private javax.swing.JLabel lblMaHd;
    private javax.swing.JLabel lblNgayTao;
    private javax.swing.JLabel lblNhanvien;
    private javax.swing.JLabel lblPhuongThucThanhToan;
    private javax.swing.JLabel lblTenkhachhang;
    private javax.swing.JLabel lblThanhtoan;
    private javax.swing.JLabel lblTienChuyenKhoan;
    private javax.swing.JLabel lblTienMat;
    private javax.swing.JLabel lblTienThua;
    private javax.swing.JLabel lblTongtienhang;
    private javax.swing.JLabel lblTrangThai;
    private com.app.views.UI.panel.RoundPanel roundPanel1;
    private com.app.views.UI.panel.RoundPanel roundPanel2;
    private com.app.views.UI.panel.RoundPanel roundPanel4;
    private javax.swing.JTable tblHoadonchitiet;
    // End of variables declaration//GEN-END:variables

    private void inHoaDon() {
        InvoiceDataModel invoiceData = new InvoiceDataModel();
        invoiceData.setId(datHoaDonRequest.getId());
        invoiceData.setMaHoaDon(datHoaDonRequest.getMaHd());
        invoiceData.setTenKhachHang(datHoaDonRequest.getKhachHang());
        invoiceData.setSoDienThoai(datHoaDonRequest.getSdt());
        invoiceData.setTaiKhoan(datHoaDonRequest.getTenNv());
        List<InvoiceProduct> products = new ArrayList<>();
        for(DatHoaDonChiTietModel m: list) {
            products.add(new InvoiceProduct(m.getTen()+ " - " + m.getKichCo()+ " - " + m.getMauSac(), m.getSoLuong(), (float) m.getGiaBan()));
        }
        invoiceData.setHoaDonChiTiet(products);
        invoiceData.setTongTienHang(datHoaDonRequest.getTongTienhang());
        invoiceData.setTongTienGiam(datHoaDonRequest.getGiamGia());
        invoiceData.setTienKhachTra(datHoaDonRequest.getTienMat() + datHoaDonRequest.getTienChuyenKhoan());

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
}
