/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.app.core.dattv.views;


import com.app.Application;
import com.app.common.helper.ExcelHelper;
import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.helper.QrCodeHelper;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.constants.PhuongThucThanhToanConstant;
import com.app.common.infrastructure.constants.TrangThaiHoaDonConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.common.infrastructure.request.FilterRequest;
import com.app.core.dattv.models.DatNhanvienModel;
import java.util.ArrayList;
import com.app.core.dattv.repositoris.DatHoaDonRepository;
import com.app.core.dattv.repositoris.DatNhanVienRepository;
import com.app.core.dattv.request.DatHoaDonRequest;
import com.app.core.dattv.service.DatHoaDonService;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.QrCodeUtils;
import com.app.utils.TimeUtils;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.panel.RoundPanel;
import com.app.views.UI.table.TableCustomUI;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import raven.datetime.component.date.DatePicker;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;


/**
 *
 * @author WIN
 */
public class DatHoaDonView  extends RoundPanel{
    DatHoaDonRepository datHoaDonRepository=new DatHoaDonRepository();
    DatHoaDonService datHoaDonService=new DatHoaDonService();
    ArrayList<DatHoaDonRequest> list;
    private static DatHoaDonView instance;
    private DatePicker datePicker = new DatePicker();
    private Pagination pagination =new Pagination();
    private int sizePage = pagination.getLimitItem();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final LoadingDialog loading = new LoadingDialog();
    
    /**
     * Creates new form DatHoaDon
     */
    
    public DatHoaDonView() {
        initComponents();
        instance = this;
        
        datePicker.setEditor(txtKhoangThoiGian);
	datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
	datePicker.setSeparator("  đến  ");
	datePicker.setUsePanelOption(true);
	datePicker.setCloseAfterSelected(true);
        
        btnLoc.setBackground(ColorUtils.BUTTON_PRIMARY);
        
        tblHoadon.setRowHeight(50);
        loadCbbNguoitaoHD();
        
        TableCustomUI.apply(scrDanhsach, TableCustomUI.TableType.DEFAULT);
        TableCustomUI.resizeColumnHeader(tblHoadon);
        
        setupPagination();
        loadData(1);
    }
   
 
    private void setupPagination() { 
        pagination.setPanel(pnlPhanTrang);
        pagination.setCallback(new Pagination.Callback() {
            @Override
            public void onChangeLimitItem(JComboBox<Integer> comboBox) {
                sizePage = (int) comboBox.getSelectedItem();
		executorService.submit(() -> { 
		    loadData(1);
		    loading.dispose();
		});
                loading.setVisible(true);
            }

            @Override
            public void onClickPage(int page) {
		executorService.submit(() -> { 
		    loadData(page);
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
    
    public void loadCbbNguoitaoHD() {
        DatNhanVienRepository datNhanVienRepository=new DatNhanVienRepository();
        ArrayList<DatNhanvienModel> listNv=datNhanVienRepository.getAll();
        cbbNguoitao.removeAllItems();
        cbbNguoitao.addItem("--- Chọn nhân viên ---");
        for (DatNhanvienModel datNhanvienModel : listNv) {
            cbbNguoitao.addItem(datNhanvienModel);
        }
    }
    

    void loadData() {
        loadData(pagination.getCurrentPage());
    }
    
    void loadData(int page) {
        
        DefaultTableModel tableModel = (DefaultTableModel) tblHoadon.getModel();
        tableModel.setRowCount(0);

        String tuKhoa = txtTimkiem.getText().trim();
        tuKhoa = tuKhoa.replaceAll("\\s+", " ");
	    
        if (tuKhoa.length() > 50) {
            MessageToast.warning("Từ khoá tìm kiếm chỉ được chứa tối đa 50 ký tự");
            return;
        }
        
        int pt = -1;
        int trangThai = -1;
        String username = cbbNguoitao.getSelectedItem().toString();
        if (username.equals("--- Chọn nhân viên ---")) {
            username = null;
        }
        
        

        // Xác định phương thức thanh toán
        switch (cbbPhuongthucTT.getSelectedItem().toString()) {
            case "Tiền mặt":
                pt = PhuongThucThanhToanConstant.TIEN_MAT;
                break;
            case "Chuyển khoản":
                pt = PhuongThucThanhToanConstant.CHUYEN_KHOAN;
                break;
            case "Tiền mặt + Chuyển khoản":
                pt = PhuongThucThanhToanConstant.KET_HOP;
                break;
        }

        // Xác định trạng thái thanh toán
        switch (cbbTrangthai.getSelectedItem().toString()) {
            case "Đã thanh toán":
                trangThai = TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN;
                break;
            case "Chưa thanh toán":
                trangThai = TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN;
                break;
            case "Đã hủy":
                trangThai = TrangThaiHoaDonConstant.STATUS_DA_HUY;
                break;
        }

        String startDate = null;
        String endDate = null;

        LocalDate[] dates = datePicker.getSelectedDateRange();
        if (dates != null) { 
            startDate = dates[0].toString();
            endDate = dates[1].toString();
        }

        FilterRequest request = new FilterRequest();
        request.setSize(sizePage);

        int totalPages = datHoaDonRepository.count(tuKhoa, pt, trangThai, username, startDate, endDate, request);
        if (totalPages < page) { 
            page = totalPages;
        }

        request.setPage(page);
        
        list = datHoaDonRepository.locDataPage(tuKhoa, pt, trangThai, username, startDate, endDate, request);
    
        
        for (DatHoaDonRequest datHoaDonRequest : list) {
            String trangThaiString = getTrangThaiString(datHoaDonRequest.getTrangThai());
            String phuongThucThanhToanString = getPhuongThucThanhToanString(datHoaDonRequest.getPhuongThucTT());
            
            Object[] row = {
                false,
                datHoaDonRequest.getStt(),
                datHoaDonRequest.getMaHd(),
                TimeUtils.date("dd-MM-yyyy HH:mm", datHoaDonRequest.getThoiGian()),
                datHoaDonRequest.getKhachHang(),
                CurrencyUtils.parseString(datHoaDonRequest.getTongTienhang()),
                CurrencyUtils.parseString(datHoaDonRequest.getGiamGia()),
                CurrencyUtils.parseString(datHoaDonRequest.getThanhTien()),
                phuongThucThanhToanString,
                CurrencyUtils.parseString(datHoaDonRequest.getTienMat()),
                CurrencyUtils.parseString(datHoaDonRequest.getTienChuyenKhoan()),
                datHoaDonRequest.getTenNv(),
                trangThaiString
            };

            tableModel.addRow(row);
        }
        
        rerenderPagination(page, totalPages);
    }
    
    public void printSelected(List<DatHoaDonRequest> selectedInvoices){
String fileName = "HoaDon-" + TimeUtils.now("dd_MM_yyyy__hh_mm_a");
	String[] headers = new String[] {
	    "STT",
	    "Mã hoá đơn",
	    "Ngày tạo",
	    "Khách hàng",
            "Số điện thoại",
	    "Tổng tiền hàng",
	    "Giảm giá",
	    "Thành tiền",
            "Phương thức thanh toán",
            "Tiền mặt",
            "Tiền chuyển khoản",
            "Người tạo",
	    "Trạng thái"
	};

	executorService.submit(() -> { 
	    try {

		List<String[]> rows = new ArrayList<>();
                for (int i = 0; i < selectedInvoices.size(); i++) {
                    rows.add(new String[] {
                        String.valueOf(i+1),
                        selectedInvoices.get(i).getMaHd(),
                        TimeUtils.date("dd-MM-yyyy HH:mm", selectedInvoices.get(i).getThoiGian()),
                        selectedInvoices.get(i).getKhachHang(),
                        selectedInvoices.get(i).getSdt(),
                        String.valueOf(selectedInvoices.get(i).getTongTienhang()),
                        String.valueOf(selectedInvoices.get(i).getGiamGia()),
                        String.valueOf(selectedInvoices.get(i).getThanhTien()),
                        String.valueOf(getPhuongThucThanhToanString(selectedInvoices.get(i).getPhuongThucTT())),
                        String.valueOf(selectedInvoices.get(i).getTienMat()),
                        String.valueOf(selectedInvoices.get(i).getTienChuyenKhoan()),
                        selectedInvoices.get(i).getTenNv(),
                        getTrangThaiString(selectedInvoices.get(i).getTrangThai())
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


    private String getTrangThaiString(int trangThai) {
        switch (trangThai) {
            case TrangThaiHoaDonConstant.STATUS_CHO_THANH_TOAN:
                return "Chưa thanh toán";
            case TrangThaiHoaDonConstant.STATUS_DA_THANH_TOAN:
                return "Đã thanh toán";
            case TrangThaiHoaDonConstant.STATUS_DA_HUY:
                return "Đã hủy";
            default:
                return "Trạng thái không xác định";
        }
    }

    private String getPhuongThucThanhToanString(int trangThai) {
        switch (trangThai) {
            case PhuongThucThanhToanConstant.TIEN_MAT:
                return "Tiền mặt";
            case PhuongThucThanhToanConstant.CHUYEN_KHOAN:
                return "Chuyển khoản";
            case PhuongThucThanhToanConstant.KET_HOP:
                return "Kết hợp";
            default:
                return "Không rõ";
        }
    }

    private void handleClickButtonScanQrCode() {
        QrCodeHelper.showWebcam("Tìm kiếm hóa đơn bằng QR", result -> {

           
            executorService.submit(() -> {
               try {
                    String code = result.getText();

                    int id = QrCodeUtils.getIdHoaDon(code);
                    if (id > 0) {
                        DatHoaDonRequest hoaDonRequest = datHoaDonService.getById(id);
                        showHoadonchitiet(hoaDonRequest);
                    } else { 
                       MessageToast.error("QRCode không hợp lệ!!!");
                   }
                   
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

    public void inDanhSach(){
        String fileName = "HoaDon-" + TimeUtils.now("dd_MM_yyyy__hh_mm_a");
	String[] headers = new String[] {
	    "STT",
	    "Mã hoá đơn",
	    "Ngày tạo",
	    "Khách hàng",
            "Số điện thoại",
	    "Tổng tiền hàng",
	    "Giảm giá",
	    "Thành tiền",
            "Phương thức thanh toán",
            "Tiền mặt",
            "Tiền chuyển khoản",
            "Người tạo",
	    "Trạng thái"
	};

	executorService.submit(() -> { 
	    try {

		List<String[]> rows = new ArrayList<>();
		ArrayList<DatHoaDonRequest> list = datHoaDonRepository.getAll();
                for (int i = 0; i < list.size(); i++) {
                    rows.add(new String[] {
                        String.valueOf(i+1),
                        list.get(i).getMaHd(),
                        TimeUtils.date("dd-MM-yyyy HH:mm", list.get(i).getThoiGian()),
                        list.get(i).getKhachHang(),
                        list.get(i).getSdt(),
                        String.valueOf(list.get(i).getTongTienhang()),
                        String.valueOf(list.get(i).getGiamGia()),
                        String.valueOf(list.get(i).getThanhTien()),
                        String.valueOf(getPhuongThucThanhToanString(list.get(i).getPhuongThucTT())),
                        String.valueOf(list.get(i).getTienMat()),
                        String.valueOf(list.get(i).getTienChuyenKhoan()),
                        list.get(i).getTenNv(),
                        getTrangThaiString(list.get(i).getTrangThai())
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
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        plnTime = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtKhoangThoiGian = new javax.swing.JFormattedTextField();
        txtTimkiem = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        plnTrangthai = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        cbbTrangthai = new javax.swing.JComboBox<>();
        plnPhuongthucTT = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cbbPhuongthucTT = new javax.swing.JComboBox<>();
        plnNguoitao = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cbbNguoitao = new javax.swing.JComboBox<>();
        btnLoc = new javax.swing.JButton();
        btnHuyLoc = new javax.swing.JButton();
        plnTable = new javax.swing.JPanel();
        scrDanhsach = new javax.swing.JScrollPane();
        tblHoadon = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnXuatdanhsach = new javax.swing.JButton();
        btnQrcode = new javax.swing.JButton();
        pnlPhanTrang = new javax.swing.JPanel();

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Khoảng thời gian");

        txtTimkiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimkiemActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Tìm kiếm mã hoá đơn");

        javax.swing.GroupLayout plnTimeLayout = new javax.swing.GroupLayout(plnTime);
        plnTime.setLayout(plnTimeLayout);
        plnTimeLayout.setHorizontalGroup(
            plnTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnTimeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plnTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTimkiem, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtKhoangThoiGian)
                    .addGroup(plnTimeLayout.createSequentialGroup()
                        .addGroup(plnTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        plnTimeLayout.setVerticalGroup(
            plnTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnTimeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtTimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txtKhoangThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Trạng Thái");

        cbbTrangthai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- Tất cả trạng thái ---", "Chưa thanh toán", "Đã thanh toán", "Đã hủy" }));
        cbbTrangthai.setAutoscrolls(true);
        cbbTrangthai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbTrangthaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plnTrangthaiLayout = new javax.swing.GroupLayout(plnTrangthai);
        plnTrangthai.setLayout(plnTrangthaiLayout);
        plnTrangthaiLayout.setHorizontalGroup(
            plnTrangthaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnTrangthaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plnTrangthaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plnTrangthaiLayout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cbbTrangthai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        plnTrangthaiLayout.setVerticalGroup(
            plnTrangthaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnTrangthaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(cbbTrangthai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Phương thức thanh toán");

        cbbPhuongthucTT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- Tất cả phương thức  ---", "Tiền mặt", "Chuyển khoản", "Tiền mặt + Chuyển khoản" }));
        cbbPhuongthucTT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbPhuongthucTTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plnPhuongthucTTLayout = new javax.swing.GroupLayout(plnPhuongthucTT);
        plnPhuongthucTT.setLayout(plnPhuongthucTTLayout);
        plnPhuongthucTTLayout.setHorizontalGroup(
            plnPhuongthucTTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnPhuongthucTTLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plnPhuongthucTTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plnPhuongthucTTLayout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 36, Short.MAX_VALUE))
                    .addComponent(cbbPhuongthucTT, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        plnPhuongthucTTLayout.setVerticalGroup(
            plnPhuongthucTTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnPhuongthucTTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbbPhuongthucTT, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Người tạo");

        cbbNguoitao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout plnNguoitaoLayout = new javax.swing.GroupLayout(plnNguoitao);
        plnNguoitao.setLayout(plnNguoitaoLayout);
        plnNguoitaoLayout.setHorizontalGroup(
            plnNguoitaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnNguoitaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plnNguoitaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbbNguoitao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        plnNguoitaoLayout.setVerticalGroup(
            plnNguoitaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnNguoitaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(cbbNguoitao, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnLoc.setText("Lọc");
        btnLoc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });

        btnHuyLoc.setText("Huỷ lọc");
        btnHuyLoc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHuyLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyLocActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plnTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plnTrangthai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plnPhuongthucTT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plnNguoitao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnLoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHuyLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(plnTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(plnTrangthai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(plnPhuongthucTT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(plnNguoitao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHuyLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        plnTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblHoadon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "#", "Mã hóa đơn", "Ngày tạo", "Khách hàng", "Tổng tiền hàng", "Giảm giá", "Thành tiền", "Phương thức thanh toán", "Tiền mặt", "Tiền chuyển khoản", "Người tạo", "Trạng thái"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHoadon.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblHoadon.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblHoadon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoadonMouseClicked(evt);
            }
        });
        scrDanhsach.setViewportView(tblHoadon);
        if (tblHoadon.getColumnModel().getColumnCount() > 0) {
            tblHoadon.getColumnModel().getColumn(1).setMaxWidth(100);
        }

        javax.swing.GroupLayout plnTableLayout = new javax.swing.GroupLayout(plnTable);
        plnTable.setLayout(plnTableLayout);
        plnTableLayout.setHorizontalGroup(
            plnTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhsach, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 735, Short.MAX_VALUE)
        );
        plnTableLayout.setVerticalGroup(
            plnTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhsach)
        );

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle.setText("Hóa Đơn");

        btnXuatdanhsach.setText("Xuất danh sách");
        btnXuatdanhsach.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXuatdanhsach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatdanhsachActionPerformed(evt);
            }
        });

        btnQrcode.setText("Quét QR");
        btnQrcode.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQrcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQrcodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnQrcode, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnXuatdanhsach)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnXuatdanhsach, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnQrcode, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlPhanTrangLayout = new javax.swing.GroupLayout(pnlPhanTrang);
        pnlPhanTrang.setLayout(pnlPhanTrangLayout);
        pnlPhanTrangLayout.setHorizontalGroup(
            pnlPhanTrangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPhanTrangLayout.setVerticalGroup(
            pnlPhanTrangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 34, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plnTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlPhanTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plnTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(pnlPhanTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTimkiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimkiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimkiemActionPerformed

    private void btnQrcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQrcodeActionPerformed
        // TODO add your handling code here:
        handleClickButtonScanQrCode();
    }//GEN-LAST:event_btnQrcodeActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        // TODO add your handling code here:   
        executorService.submit(() -> {
            try {
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
                MessageToast.error(ErrorConstant.DEFAULT_ERROR);
            } finally {
                loading.dispose();
            }
        });
        loading.setVisible(true);
    }//GEN-LAST:event_btnLocActionPerformed

    private void cbbTrangthaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbTrangthaiActionPerformed
        // TODO add your handling code here:
       
        
    }//GEN-LAST:event_cbbTrangthaiActionPerformed

    private void cbbPhuongthucTTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbPhuongthucTTActionPerformed
        // TODO add your handling code here:
      
        
    }//GEN-LAST:event_cbbPhuongthucTTActionPerformed

    private void tblHoadonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoadonMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount()>1){
            showHoadonchitiet(list.get(tblHoadon.getSelectedRow()));
        }
    }//GEN-LAST:event_tblHoadonMouseClicked

    private void btnXuatdanhsachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatdanhsachActionPerformed
        // TODO add your handling code here:
        List<DatHoaDonRequest> selectedInvoices = new ArrayList<>();
        for (int i = 0; i < tblHoadon.getRowCount(); i++) {
            Boolean isChecked = (Boolean) tblHoadon.getValueAt(i, 0);
            if (isChecked) {
                selectedInvoices.add(list.get(i));
            }
        }
        if(selectedInvoices.isEmpty()){
            inDanhSach();
        }else{
            printSelected(selectedInvoices);
        }
         
    }//GEN-LAST:event_btnXuatdanhsachActionPerformed

    private void btnHuyLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyLocActionPerformed
        // TODO add your handling code here:
        cbbNguoitao.setSelectedIndex(0);
        cbbPhuongthucTT.setSelectedIndex(0);
        cbbTrangthai.setSelectedIndex(0);
        txtTimkiem.setText("");
        txtKhoangThoiGian.setText("");
        datePicker.clearSelectedDate();
        
        executorService.submit(() -> { 
            loadData();
            loading.dispose();
        });
        loading.setVisible(true);
        
    }//GEN-LAST:event_btnHuyLocActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHuyLoc;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnQrcode;
    private javax.swing.JButton btnXuatdanhsach;
    private javax.swing.JComboBox<Object> cbbNguoitao;
    private javax.swing.JComboBox<Object> cbbPhuongthucTT;
    private javax.swing.JComboBox<String> cbbTrangthai;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel plnNguoitao;
    private javax.swing.JPanel plnPhuongthucTT;
    private javax.swing.JPanel plnTable;
    private javax.swing.JPanel plnTime;
    private javax.swing.JPanel plnTrangthai;
    private javax.swing.JPanel pnlPhanTrang;
    private javax.swing.JScrollPane scrDanhsach;
    private javax.swing.JTable tblHoadon;
    private javax.swing.JFormattedTextField txtKhoangThoiGian;
    private javax.swing.JTextField txtTimkiem;
    // End of variables declaration//GEN-END:variables
    
   
    
    private void showHoadonchitiet(DatHoaDonRequest hoaDonRequest) {
        
        ModalDialog.showModal(Application.app, new SimpleModalBorder(new DatHoaDonChiTietView(hoaDonRequest), null));
            
        
    }
}
