/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.app.core.lam.views;

import com.app.Application;
import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.lam.models.LamKhachHangModels;
import com.app.core.lam.models.LamLichSuModels;
import com.app.core.lam.repositories.LamKhachHangRepositories;
import com.app.core.lam.repositories.LamLichSuRepositories;
import com.app.utils.BillUtils;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.NumberPhoneUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.SessionUtils;
import com.app.utils.TimeUtils;
import com.app.utils.ValidateUtils;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.table.TableCustomUI;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public final class LamKhachHangView extends javax.swing.JPanel {

    private final LamKhachHangRepositories repo = new LamKhachHangRepositories();

    private DefaultTableModel dtm = new DefaultTableModel();

    private ArrayList<LamKhachHangModels> listKH = new ArrayList<>();

    private final LamLichSuRepositories repoLS = new LamLichSuRepositories();

    private DefaultTableModel dtmLS = new DefaultTableModel();

    private ArrayList<LamLichSuModels> listLS = new ArrayList<>();

    private LoadingDialog loading = new LoadingDialog();
    
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    public Pagination pagination = new Pagination();
    
    private int sizePage = pagination.getLimitItem();
    
    public Pagination paginationLs = new Pagination();
    
    private int sizePageLs = paginationLs.getLimitItem();
    
    private void setupPagination() { 
        pagination.setPanel(pnlPhanTrang);
        pagination.setCallback(new Pagination.Callback() {
            @Override
            public void onChangeLimitItem(JComboBox<Integer> comboBox) {
                sizePage = (int) comboBox.getSelectedItem();
		executorService.submit(() -> { 
		    showData(1);
		    loading.dispose();
		});
                loading.setVisible(true);
            }

            @Override
            public void onClickPage(int page) {
		executorService.submit(() -> { 
		    showData(page);
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
    
    private void setupPaginationLs() { 
        paginationLs.setPanel(pnlPhanTrangLs);
        paginationLs.setCallback(new Pagination.Callback() {
            @Override
            public void onChangeLimitItem(JComboBox<Integer> comboBox) {
                sizePageLs = (int) comboBox.getSelectedItem();
		executorService.submit(() -> { 
		    showDataLS(1);
		    loading.dispose();
		});
                loading.setVisible(true);
            }

            @Override
            public void onClickPage(int page) {
		executorService.submit(() -> { 
		    showDataLS(page);
		    loading.dispose();
		});
                loading.setVisible(true);
            }
        });
        paginationLs.render();
    }
    
    private void rerenderPaginationLs(int currentPage, int totalPages) {
	paginationLs.rerender(currentPage, totalPages);
    }
    
    public void detailTable(int row) {

        LamKhachHangModels kh = listKH.get(row);

        txtTenKH.setText(kh.getTenKH());

        txtSoDienThoai.setText(kh.getSoDienThoai());

        if (kh.isGioiTinh()) {
            rbnNam.setSelected(true);
        } else {
            rbnNu.setSelected(true);
        }

        txtDiaChi.setText(kh.getDiaChi());

    }

    public void showData() {
        showData(pagination.getCurrentPage());
    }
    
    public void showData(int page) {
        String timKiem = txtTimKiemKH.getText().trim();
        timKiem = timKiem.replaceAll("\\s+", " ");
	    
        if (timKiem.length() > 250) {
            MessageToast.warning("Từ khoá tìm kiếm chỉ được chứa tối đa 250 ký tự");
            return;
        }
        
        FilterRequest request = new FilterRequest();
        request.setSize(sizePage);

        int totalPages = repo.count(timKiem, request);
        if (totalPages < page) { 
            page = totalPages;
        }

        request.setPage(page);
        listKH = repo.getKH(timKiem, request);
        
        dtm.setRowCount(0);
        try {
            for (LamKhachHangModels kh : listKH) {
                dtm.addRow(new Object[]{
                    kh.getStt(), kh.getTenKH(), kh.getSoDienThoai(), kh.isGioiTinh() ? "Nam" : "Nữ", kh.getDiaChi(), kh.getSoLanMuaHang(), TimeUtils.date("dd-MM-yyyy HH:mm", kh.getNgayTao())
                });
            }
        } catch (Exception e) { 
            e.printStackTrace();
        }
        
        rerenderPagination(page, totalPages);
    }

    public void showDataLS() {
        showDataLS(paginationLs.getCurrentPage());
    }
    
    public void showDataLS(int page) {
        int row = tblKhachHang.getSelectedRow();
        if (row < 0) { 
            return;
        }
        
        LamKhachHangModels kh = listKH.get(row);
        
        FilterRequest request = new FilterRequest();
        request.setSize(sizePageLs);

        int totalPages = repoLS.count(kh.getIdKH(), request);
        if (totalPages < page) { 
            page = totalPages;
        }

        request.setPage(page);
        listLS = repoLS.getLS(kh.getIdKH(), request);
        
        dtmLS.setRowCount(0);
        try {
            for (LamLichSuModels ls : listLS) {
                
                dtmLS.addRow(new Object[]{
                    ls.getStt(), ls.getMaHD(), CurrencyUtils.parseString(ls.getTongTien()), TimeUtils.date("dd-MM-yyyy HH:mm", ls.getNgayMua()), BillUtils.getTrangThai(ls.isTrangThai())
                });
            }
        } catch (Exception e) { 
            e.printStackTrace();
        }
        
        rerenderPaginationLs(page, totalPages);
    }
    
    public LamKhachHangModels getFormData() {
        String tenKH = txtTenKH.getText().trim();
        String soDienThoai = NumberPhoneUtils.formatPhoneNumber(txtSoDienThoai.getText().trim());
        boolean gioiTinh = rbnNam.isSelected();
        String diaChi = txtDiaChi.getText().trim();
        String ngayTao = TimeUtils.currentDateTime();
        LamKhachHangModels kh = new LamKhachHangModels();
        kh.setTenKH(tenKH);
        kh.setSoDienThoai(soDienThoai);
        kh.setGioiTinh(gioiTinh);
        kh.setDiaChi(diaChi);
        kh.setNgayTao(ngayTao);
        return kh;
    }

    private void clearFormData() {
        txtTimKiemKH.setText("");
        
        txtTenKH.setText("");
        txtSoDienThoai.setText("");
        rbnNam.setSelected(true);
        txtDiaChi.setText("");
        
        btnThem.setEnabled(true);
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        
        tblKhachHang.clearSelection();
        tblKhachHang.getSelectionModel().clearSelection();
        tblLichSuMuaHang.clearSelection();
        tblLichSuMuaHang.getSelectionModel().clearSelection();
        
        dtmLS.setRowCount(0);
        rerenderPaginationLs(0, 0);
    }
    
    public LamKhachHangView() {
        initComponents();
        
        btnThem.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnThem.setForeground(Color.WHITE);
        
        lblTHongTin.setIcon(ResourceUtils.getSVG("/svg/info.svg", new Dimension(20, 20)));
        lblDanhSachKhachHang.setIcon(ResourceUtils.getSVG("/svg/list.svg", new Dimension(20, 20)));
        lblDonHangDaMua.setIcon(ResourceUtils.getSVG("/svg/list.svg", new Dimension(20, 20)));
                
                
        TableCustomUI.apply(scrKhachHang, TableCustomUI.TableType.DEFAULT);
        TableCustomUI.apply(scrLichSuMuaHang, TableCustomUI.TableType.DEFAULT);
        
        btnThem.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
        btnSua.setIcon(ResourceUtils.getSVG("/svg/edit.svg", new Dimension(20, 20)));
        btnXoa.setIcon(ResourceUtils.getSVG("/svg/trash.svg", new Dimension(20, 20)));
        btnLamMoi.setIcon(ResourceUtils.getSVG("/svg/reload.svg", new Dimension(20, 20)));

        dtm = (DefaultTableModel) tblKhachHang.getModel();
        dtmLS = (DefaultTableModel) tblLichSuMuaHang.getModel();
        
        txtTimKiemKH.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm theo tên hoặc số điện thoại");

        txtSoDienThoai.setFormatterFactory(NumberPhoneUtils.getDefaultFormat());

        if (!SessionUtils.isManager()) {
            btnXoa.setVisible(false);
            revalidate();
            repaint();
        }
        
        setupPagination();
        setupPaginationLs();
        showData();
    }

    private boolean isCheckSdtThem(String soDienThoai) {
        return repo.checkSdtThem(soDienThoai);
    }

    private boolean isCheckSdtSua(int id, String soDienThoai) {
        return repo.checkSdtSua(id, soDienThoai);
    }
    

    
    /**
     * Creates new form KhachHangView
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        roundPanel1 = new com.app.views.UI.panel.RoundPanel();
        txtTimKiemKH = new javax.swing.JFormattedTextField();
        btnSearch = new javax.swing.JButton();
        scrKhachHang = new javax.swing.JScrollPane();
        tblKhachHang = new javax.swing.JTable();
        btnLamMoi = new javax.swing.JButton();
        lblDanhSachKhachHang = new javax.swing.JLabel();
        pnlPhanTrang = new javax.swing.JPanel();
        roundPanel2 = new com.app.views.UI.panel.RoundPanel();
        scrLichSuMuaHang = new javax.swing.JScrollPane();
        tblLichSuMuaHang = new javax.swing.JTable();
        lblDonHangDaMua = new javax.swing.JLabel();
        pnlPhanTrangLs = new javax.swing.JPanel();
        roundPanel3 = new com.app.views.UI.panel.RoundPanel();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDiaChi = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        lblTHongTin = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtTenKH = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtSoDienThoai = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        rbnNam = new javax.swing.JRadioButton();
        rbnNu = new javax.swing.JRadioButton();

        jPanel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        roundPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSearch.setText("Tìm");
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        tblKhachHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Tên khách hàng", "Số điện thoại", "Giới tính", "Địa chỉ", "Số lần mua hàng", "Ngày tạo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblKhachHang.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblKhachHang.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKhachHangMouseClicked(evt);
            }
        });
        scrKhachHang.setViewportView(tblKhachHang);
        if (tblKhachHang.getColumnModel().getColumnCount() > 0) {
            tblKhachHang.getColumnModel().getColumn(0).setMinWidth(40);
            tblKhachHang.getColumnModel().getColumn(0).setMaxWidth(40);
        }

        btnLamMoi.setText("Làm Mới");
        btnLamMoi.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        lblDanhSachKhachHang.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblDanhSachKhachHang.setText("Danh sách khách hàng");

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

        javax.swing.GroupLayout roundPanel1Layout = new javax.swing.GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrKhachHang)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addComponent(lblDanhSachKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTimKiemKH, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLamMoi))
                    .addComponent(pnlPhanTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTimKiemKH, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDanhSachKhachHang))
                .addGap(15, 15, 15)
                .addComponent(scrKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPhanTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        roundPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblLichSuMuaHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Mã hóa đơn", "Tổng tiền", "Ngày mua hàng", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLichSuMuaHang.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLichSuMuaHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLichSuMuaHangMouseClicked(evt);
            }
        });
        scrLichSuMuaHang.setViewportView(tblLichSuMuaHang);
        if (tblLichSuMuaHang.getColumnModel().getColumnCount() > 0) {
            tblLichSuMuaHang.getColumnModel().getColumn(0).setMinWidth(40);
            tblLichSuMuaHang.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblLichSuMuaHang.getColumnModel().getColumn(0).setMaxWidth(40);
        }

        lblDonHangDaMua.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblDonHangDaMua.setText("Đơn hàng đã mua");

        javax.swing.GroupLayout pnlPhanTrangLsLayout = new javax.swing.GroupLayout(pnlPhanTrangLs);
        pnlPhanTrangLs.setLayout(pnlPhanTrangLsLayout);
        pnlPhanTrangLsLayout.setHorizontalGroup(
            pnlPhanTrangLsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPhanTrangLsLayout.setVerticalGroup(
            pnlPhanTrangLsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout roundPanel2Layout = new javax.swing.GroupLayout(roundPanel2);
        roundPanel2.setLayout(roundPanel2Layout);
        roundPanel2Layout.setHorizontalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrLichSuMuaHang)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlPhanTrangLs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundPanel2Layout.createSequentialGroup()
                        .addComponent(lblDonHangDaMua, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 490, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        roundPanel2Layout.setVerticalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblDonHangDaMua)
                .addGap(15, 15, 15)
                .addComponent(scrLichSuMuaHang, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPhanTrangLs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        roundPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnThem.setText("Thêm");
        btnThem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setText("Sửa");
        btnSua.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSua.setEnabled(false);
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnXoa.setText("Xóa");
        btnXoa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXoa.setEnabled(false);
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        txtDiaChi.setColumns(20);
        txtDiaChi.setRows(5);
        jScrollPane2.setViewportView(txtDiaChi);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Địa chỉ:");

        lblTHongTin.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTHongTin.setText("Thiết lập thông tin khách hàng");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Tên khách hàng:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Số điện thoại:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Giới tính:");

        buttonGroup2.add(rbnNam);
        rbnNam.setSelected(true);
        rbnNam.setText("Nam");

        buttonGroup2.add(rbnNu);
        rbnNu.setText("Nữ");

        javax.swing.GroupLayout roundPanel3Layout = new javax.swing.GroupLayout(roundPanel3);
        roundPanel3.setLayout(roundPanel3Layout);
        roundPanel3Layout.setHorizontalGroup(
            roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, roundPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(201, 201, 201))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, roundPanel3Layout.createSequentialGroup()
                        .addGroup(roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(roundPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(rbnNam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(rbnNu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(146, 146, 146))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, roundPanel3Layout.createSequentialGroup()
                        .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, roundPanel3Layout.createSequentialGroup()
                        .addGroup(roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSoDienThoai, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTHongTin, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18))
        );
        roundPanel3Layout.setVerticalGroup(
            roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTHongTin, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbnNam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbnNu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1))
                    .addComponent(roundPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(roundPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(roundPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(123, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(roundPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblLichSuMuaHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLichSuMuaHangMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblLichSuMuaHangMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        executorService.submit(() -> {
            try {
                showData();
            } catch (Exception e) {
                e.printStackTrace();
                MessageToast.error(ErrorConstant.DEFAULT_ERROR);
            } finally {
                loading.dispose();
            }
        });
        loading.setVisible(true);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void tblKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKhachHangMouseClicked
        // TODO add your handling code here:
        int row = tblKhachHang.getSelectedRow();
        boolean isSelected = row >= 0;
        if (isSelected) {
            detailTable(row);
            showDataLS(1);
        } else {
            dtmLS.setRowCount(0);
            rerenderPaginationLs(0, 0);
        }
        
        btnThem.setEnabled(!isSelected);
        btnSua.setEnabled(isSelected);
        btnXoa.setEnabled(isSelected);
    }//GEN-LAST:event_tblKhachHangMouseClicked

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        // TODO add your handling code here:
        executorService.submit(() -> {
            try {
                clearFormData();

                SwingUtilities.invokeLater(() -> {
                    showData();
                    dtmLS.setRowCount(0);
                    rerenderPaginationLs(0, 0);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                MessageToast.error(ErrorConstant.DEFAULT_ERROR);
            } finally {
                loading.dispose();
            }
        });
        loading.setVisible(true);
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        int row = tblKhachHang.getSelectedRow();
        if (row < 0) { 
            MessageModal.warning("Vui lòng chọn 1 khách hàng muốn xoá!");
            return;
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return MessageModal.confirmInfo("Bạn thực sự muốn xóa khách hàng?");
            }

            @Override
            protected void done() {
                try {
                    if (get()) {

                        executorService.submit(() -> {
                            try {
                                
                                LamKhachHangModels kh = listKH.get(row);
                                
                                boolean check = false;
                                
                                if (repo.checkDelete(kh.getIdKH())) { 
                                    kh.setTrangThaiXoa(true);
                                    check = repo.updateKhachHang(kh);
                                } else {
                                    check = repo.deleteKhachHang(kh.getIdKH());
                                }
                                
                                if (check) {
                                    MessageModal.success("Xóa thông tin khách hàng thành công!");
                                    
                                    SwingUtilities.invokeLater(() -> {
                                        clearFormData();
                                        showData();
                                    });
                                } else {
                                    MessageModal.error("Xóa thông tin khách hàng không thành công!");
                                }
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                loading.dispose();
                            }
                        });
                        loading.setVisible(true);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed

        int row = tblKhachHang.getSelectedRow();
        if (row < 0) { 
            MessageModal.warning("Vui lòng chọn 1 khách hàng để sửa!");
            return;
        }
        
        LamKhachHangModels newKH = listKH.get(row);
        
        LamKhachHangModels kh = getFormData();
        String soDienThoai = kh.getSoDienThoai();
        String tenKH = kh.getTenKH();
        String diaChi = kh.getDiaChi();
        
        if ((tenKH == null || tenKH.isEmpty())) {
            MessageModal.warning("Vui lòng nhập tên khách hàng!");
            return;
        }
        
        if (!ValidateUtils.isFullName(tenKH)) {
            MessageModal.warning("Tên khách hàng không hợp lệ!");
            return;
        }
        
        if (soDienThoai == null || soDienThoai.length() < 10) {
            MessageModal.warning("Vui lòng điền số điện thoại!");
            return;
        }
        
        if (!validateLength(tenKH, soDienThoai, diaChi)) {
            return;
        }
        
        if (isCheckSdtSua(newKH.getIdKH(), kh.getSoDienThoai())) {
            MessageModal.error("Số điện thoại đã tồn tại!");
            return;
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return MessageModal.confirmInfo("Bạn thực sự muốn sửa thông tin khách hàng?");
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        executorService.submit(() -> {
                            try {
                                
                                
                                newKH.setTenKH(tenKH);
                                newKH.setSoDienThoai(soDienThoai);
                                newKH.setGioiTinh(kh.isGioiTinh());
                                newKH.setDiaChi(kh.getDiaChi());

                                boolean check = repo.updateKhachHang(newKH);
                                if (check) {
                                    MessageModal.success("Sửa thông tin khách hàng thành công!");
                                    SwingUtilities.invokeLater(() -> {
                                        clearFormData();
                                        showData();
                                    });
                                } else {
                                    MessageModal.error("Sửa thông tin khách hàng không thành công!");
                                }
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                loading.dispose();
                            }
                        });
                        loading.setVisible(true);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
        
        LamKhachHangModels newKH = getFormData();
        String soDienThoai = newKH.getSoDienThoai();
        String tenKH = newKH.getTenKH();
        String diachi = newKH.getDiaChi();
        
        if ((tenKH == null || tenKH.isEmpty())) {
            MessageModal.warning("Vui lòng nhập tên khách hàng!");
            return;
        }
        
        if (!ValidateUtils.isFullName(tenKH)) {
            MessageModal.warning("Tên khách hàng không hợp lệ!");
            return;
        }
        
        if (soDienThoai == null || soDienThoai.length() < 10) {
            MessageModal.warning("Vui lòng điền số điện thoại!");
            return;
        }
        
        if (!validateLength(tenKH, soDienThoai, diachi)) {
            return;
        }
        
        if (isCheckSdtThem(newKH.getSoDienThoai())) {
            MessageModal.error("Số điện thoại đã tồn tại!");
            return;
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return MessageModal.confirmInfo("Bạn thực sự muốn thêm thông tin khách hàng?");
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        
                        executorService.submit(() -> {
                            try {
                                
                                boolean check = repo.addKhachHang(newKH);

                                if (check) {
                                    clearFormData();
                                    MessageModal.success("Thêm thông tin khách hàng thành công!");

                                    SwingUtilities.invokeLater(() -> {
                                        showData(1);
                                    });
                                } else {
                                    MessageModal.error("Không thể thêm thông tin khách hàng!");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                loading.dispose();
                            }
                        });
                        loading.setVisible(true);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_btnThemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDanhSachKhachHang;
    private javax.swing.JLabel lblDonHangDaMua;
    private javax.swing.JLabel lblTHongTin;
    private javax.swing.JPanel pnlPhanTrang;
    private javax.swing.JPanel pnlPhanTrangLs;
    private javax.swing.JRadioButton rbnNam;
    private javax.swing.JRadioButton rbnNu;
    private com.app.views.UI.panel.RoundPanel roundPanel1;
    private com.app.views.UI.panel.RoundPanel roundPanel2;
    private com.app.views.UI.panel.RoundPanel roundPanel3;
    private javax.swing.JScrollPane scrKhachHang;
    private javax.swing.JScrollPane scrLichSuMuaHang;
    private javax.swing.JTable tblKhachHang;
    private javax.swing.JTable tblLichSuMuaHang;
    private javax.swing.JTextArea txtDiaChi;
    private javax.swing.JFormattedTextField txtSoDienThoai;
    private javax.swing.JTextField txtTenKH;
    private javax.swing.JFormattedTextField txtTimKiemKH;
    // End of variables declaration//GEN-END:variables


    private boolean validateLength(String tenKH, String soDienThoai, String diaChi) {
        int maxTenKHLength = 250;
        int maxSoDienThoaiLength = 13;
        int maxDiaChiLength = 2000;

        if (tenKH.length() > maxTenKHLength) {
            MessageModal.warning("Tên khách hàng không được vượt quá " + maxTenKHLength + " ký tự.");
            return false;
        }
        if (soDienThoai.length() > maxSoDienThoaiLength) {
            MessageModal.warning("Số điện thoại không được vượt quá " + maxSoDienThoaiLength + " ký tự.");
            return false;
        }
        if (diaChi.length() > maxDiaChiLength) {
            MessageModal.warning("Địa chỉ không được vượt quá " + maxDiaChiLength + " ký tự.");
            return false;
        }
        return true;
    }

}
