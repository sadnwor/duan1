package com.app.core.inuha.views.quanly;

import com.app.common.helper.ExcelHelper;
import com.app.common.helper.MessageModal;
import com.app.common.helper.MessageToast;
import com.app.common.helper.Pagination;
import com.app.common.infrastructure.constants.ChartConstant;
import com.app.common.infrastructure.constants.ErrorConstant;
import com.app.common.infrastructure.exceptions.ServiceResponseException;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.thongke.InuhaThongKeChartModel;
import com.app.core.inuha.models.thongke.InuhaThongKeChiTietModel;
import com.app.core.inuha.models.thongke.InuhaThongKeSanPhamModel;
import com.app.core.inuha.models.thongke.InuhaThongKeTongSoModel;
import com.app.core.inuha.request.InuhaFilterThongKeRequest;
import com.app.core.inuha.services.InuhaThongKeService;
import com.app.utils.ColorUtils;
import com.app.utils.CurrencyUtils;
import com.app.utils.ResourceUtils;
import com.app.utils.ThemeUtils;
import com.app.utils.TimeUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.curvelinechart.chart.ModelChart;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.table.TableCustomUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import raven.datetime.component.date.DatePicker;

/**
 *
 * @author inuHa
 */
public class InuhaThongKeView extends javax.swing.JPanel {
    
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final InuhaThongKeService thongKeService = InuhaThongKeService.getInstance();
        
    private List<InuhaSanPhamModel> dataItemsSanPham = new ArrayList<>();
    
    private List<String> dataRangeDate = new ArrayList<>();
        
    private List<String> chartLabels = new ArrayList<>();

    private DatePicker datePicker = new DatePicker();
    
    public Pagination pagination = new Pagination();
    
    private int sizePage = pagination.getLimitItem();
    
    private List<InuhaThongKeSanPhamModel> dataItems = new ArrayList<>();
    
    private InuhaFilterThongKeRequest filter = new InuhaFilterThongKeRequest();
	
    private final LoadingDialog loading = new LoadingDialog();
    
    private boolean firstLoad = true;
    
    /** Creates new form BanHangView */
    public InuhaThongKeView() {
	initComponents();
	
	txtThoiGian.setVisible(false);
	
	datePicker.setEditor(txtThoiGian);
	datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
	datePicker.setSeparator("  tới ngày  ");
	datePicker.setUsePanelOption(true);
	datePicker.setDateSelectionAble(localDate -> !localDate.isAfter(LocalDate.now()));
	datePicker.setCloseAfterSelected(true);
	
	imgSoLuongBan.setIcon(ResourceUtils.getSVG("/svg/cart-check.svg", new Dimension(58, 58)));
	imgTongDoanhThu.setIcon(ResourceUtils.getSVG("/svg/retirement.svg", new Dimension(58, 58)));
	imgTongLoiNhuan.setIcon(ResourceUtils.getSVG("/svg/money-bag.svg", new Dimension(58, 58)));
	btnExport.setIcon(ResourceUtils.getSVG("/svg/export.svg", new Dimension(20, 20)));
	
	btnFilter.setIcon(ResourceUtils.getSVG("/svg/filter-n.svg", new Dimension(20, 20)));
	btnFilter.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnFilter.setForeground(Color.WHITE);
        
	pnlSoLuongBan.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlDoanhThu.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlLoiNhuan.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlChart.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlChiTiet.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	
	pnlList.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
		
	cboThoiGian.removeAllItems();
	cboThoiGian.addItem(new ComboBoxItem<>("7 ngày gần nhất", 0));
	cboThoiGian.addItem(new ComboBoxItem<>("Từ trước đến nay", 1));
	cboThoiGian.addItem(new ComboBoxItem<>("Tuỳ chỉnh thời gian", 2));
	
	cboSapXep.removeAllItems();
	cboSapXep.addItem(new ComboBoxItem<>("Sản phẩm bán chạy", 0));
	cboSapXep.addItem(new ComboBoxItem<>("Doanh thu cao nhất", 1));
	cboSapXep.addItem(new ComboBoxItem<>("Lợi nhuận cao nhất", 2));
	
        btnClear.setBackground(ColorUtils.BUTTON_GRAY);
        
	Dimension cboSize = new Dimension(150, 36);
	cboSanPham.setPreferredSize(cboSize);
	cboSapXep.setPreferredSize(cboSize);
	cboThoiGian.setPreferredSize(cboSize);
	
	SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                getListSanPham();
                loadDataChart();
                firstLoad = false;
                return null;
            }
        };
        worker.execute();

        if (ThemeUtils.isLight()) {
            chartDoanhThu.setColorLabel(ColorUtils.PRIMARY_TEXT);
            chartDoanhThu.setForeground(ColorUtils.PRIMARY_TEXT);
        } else {
            chartDoanhThu.setColorLabel(Color.WHITE);
        }
	
	chartDoanhThu.setLabelName("đ");
	chartDoanhThu.setTitle("Biểu đồ chi tiết");
        chartDoanhThu.addLegend("Doanh thu", Color.decode("#e65c00"), Color.decode("#F9D423"));
        chartDoanhThu.addLegend("Lợi nhuận", Color.decode("#0099F7"), Color.decode("#F11712"));
	
	setupTable(tblDanhSach);
	setupPagination();
    }
	
    private void getListSanPham() { 
	dataItemsSanPham = thongKeService.getListSanPham();
        cboSanPham.removeAllItems();
	
        cboSanPham.addItem(new ComboBoxItem<>("-- Tất cả sản phẩm --", -1));
        for(InuhaSanPhamModel m: dataItemsSanPham) { 
            cboSanPham.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
    }
    
    private void getRangeDate() {
	dataRangeDate.clear();
	
	ComboBoxItem<Integer> kieuNgay = (ComboBoxItem<Integer>) cboThoiGian.getSelectedItem();
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                
	String start = thongKeService.getFirstDate();
	String end = dtf.format(LocalDateTime.now());
	
	
	if (kieuNgay.getValue() == 0) {
	    LocalDate today = LocalDate.now();
	    start = today.format(dtf);
	    end = today.minusDays(6).format(dtf);
	} else {
	    LocalDate[] dates = datePicker.getSelectedDateRange();
	    if (dates != null) { 
		start = dates[0].toString();
		end = dates[1].toString();
	    } else {
                datePicker.setSelectedDateRange(LocalDate.parse(start), LocalDate.parse(end));
            }
	}
	
	if (start == null || end == null) { 
	    return;
	}
	
	try {
	    LocalDate fromDate = LocalDate.parse(start);
	    LocalDate toDate = LocalDate.parse(end);

	    LocalDate startDate = fromDate.isBefore(toDate) ? fromDate : toDate;
	    LocalDate endDate = fromDate.isBefore(toDate) ? toDate : fromDate;

	    LocalDate currentDate = startDate;
	    while (!currentDate.isAfter(endDate)) {
		dataRangeDate.add(currentDate.toString());
		currentDate = currentDate.plusDays(1);
	    }
	} catch (Exception e) { 
	    e.printStackTrace();
	}
    }
    
    private void loadDataChart() { 
	getRangeDate();
	chartLabels.clear();
	
	LocalDate startDate = null; 
	LocalDate endDate = null;
        ChartConstant type = null;

        if (!dataRangeDate.isEmpty()) {
            startDate = LocalDate.parse(dataRangeDate.get(0));
            endDate = LocalDate.parse(dataRangeDate.get(dataRangeDate.size() - 1));

            long intervalInDays = ChronoUnit.DAYS.between(startDate, endDate);

            if (intervalInDays >= 365) {
                type = ChartConstant.TYPE_YEAR;
                LocalDate currentDate = startDate;
                while (currentDate.isBefore(endDate) || currentDate.equals(endDate)) {
                    chartLabels.add(String.valueOf(currentDate.getYear()));
                    currentDate = currentDate.plusYears(1);
                }
            } else if (intervalInDays >= 31) {
                type = ChartConstant.TYPE_MONTH;
                for (int i = 1; i <= 12; i++) {
                    String month = String.format("%02d", i);
                    chartLabels.add("Tháng " + month);
                }
            } else if (intervalInDays >= 1) {
                type = ChartConstant.TYPE_DAY;
                LocalDate currentDate = startDate;
                while (currentDate.isBefore(endDate) || currentDate.equals(endDate)) {
                    chartLabels.add(currentDate.format(DateTimeFormatter.ofPattern("dd/MM")));
                    currentDate = currentDate.plusDays(1);
                }
            } else {
                type = ChartConstant.TYPE_HOUR;
                LocalTime startTime = LocalTime.of(0, 0);
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                LocalTime currentTime = startTime;
                for(int i = 0; i < 24; i++) {
                    chartLabels.add(currentTime.format(timeFormatter));
                    currentTime = currentTime.plusHours(1);
                }
            }
        }
	
	chartDoanhThu.clear();
	
	ComboBoxItem<Integer> sanPham = (ComboBoxItem<Integer>) cboSanPham.getSelectedItem();
	
	filter.setSanPham(sanPham);
	filter.setType(type);
	filter.setStartDate(startDate);
	filter.setEndDate(endDate);
	
	InuhaThongKeTongSoModel dataTongSo = thongKeService.getTongSo(filter);
	
	lblTongSanPham.setText(CurrencyUtils.parseNumber(dataTongSo.getTongSanPham()));
	lblTongDoanhThu.setText(CurrencyUtils.parseNumber(dataTongSo.getTongDoanhThu()));
	lblTongLoiNhuan.setText(CurrencyUtils.parseNumber(dataTongSo.getTongLoiNhuan()));
	
	lblTongSanPham.setForeground(dataTongSo.getTongSanPham() > 0 ? ColorUtils.PRIMARY_COLOR : ColorUtils.PRIMARY_TEXT);	
	lblTongDoanhThu.setForeground(dataTongSo.getTongDoanhThu() > 0 ? ColorUtils.PRIMARY_COLOR : ColorUtils.PRIMARY_TEXT);	
	lblTongLoiNhuan.setForeground(dataTongSo.getTongLoiNhuan() > 0 ? ColorUtils.PRIMARY_COLOR : ColorUtils.PRIMARY_TEXT);	

		
	InuhaThongKeChiTietModel dataChiTiet = thongKeService.getDetail(filter);
	
	lblTongDon.setText(String.valueOf(dataChiTiet.getTongHoaDon()));	
	lblTongDonDaHuy.setText(String.valueOf(dataChiTiet.getDaHuy()));
	lblTongDonChoThanhToan.setText(String.valueOf(dataChiTiet.getChoThanhToan()));
	lblTongDonDaThanhToan.setText(String.valueOf(dataChiTiet.getDaThanhToan()));
	lblTongKhachHang.setText(String.valueOf(dataChiTiet.getKhachHang()));
	
	lblTongDon.setForeground(dataChiTiet.getTongHoaDon() > 0 ? ColorUtils.PRIMARY_COLOR : ColorUtils.PRIMARY_TEXT);	
	lblTongDonDaHuy.setForeground(dataChiTiet.getDaHuy()> 0 ? ColorUtils.PRIMARY_COLOR : ColorUtils.PRIMARY_TEXT);
	lblTongDonChoThanhToan.setForeground(dataChiTiet.getChoThanhToan()> 0 ? ColorUtils.PRIMARY_COLOR : ColorUtils.PRIMARY_TEXT);
	lblTongDonDaThanhToan.setForeground(dataChiTiet.getDaThanhToan()> 0 ? ColorUtils.PRIMARY_COLOR : ColorUtils.PRIMARY_TEXT);
	lblTongKhachHang.setForeground(dataChiTiet.getKhachHang()> 0 ? ColorUtils.PRIMARY_COLOR : ColorUtils.PRIMARY_TEXT);
	
	List<InuhaThongKeChartModel> dataChart = thongKeService.getDataChart(filter);
	
	for (String label : chartLabels) {
	    double[] data = {0, 0};
	    Optional<InuhaThongKeChartModel> find = dataChart.stream().filter(o -> o.getLabel().equals(label)).findAny();
	    if (find.isPresent()) { 
		data[0] = find.get().getDoanhThu();
		data[1] = find.get().getLoiNhuan();
	    }
            chartDoanhThu.addData(new ModelChart(label, data));
        }
	chartDoanhThu.start();
	
	loadDataPage(1);
    }
    
   
    private void setupTable(JTable table) { 
	pnlList.setBackground(ColorUtils.BACKGROUND_DASHBOARD);
	pnlDanhSach.setBackground(ColorUtils.BACKGROUND_TABLE);
        TableCustomUI.apply(scrDanhSach, TableCustomUI.TableType.DEFAULT);

        tblDanhSach.setBackground(ColorUtils.BACKGROUND_TABLE);
        tblDanhSach.getTableHeader().setBackground(ColorUtils.BACKGROUND_TABLE);
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
            
	    ComboBoxItem<Integer> sapXep = (ComboBoxItem<Integer>) cboSapXep.getSelectedItem();
	    
	    filter.setOrderBy(sapXep);
            filter.setSize(sizePage);
	    
            int totalPages = thongKeService.getTotalPage(filter);
            if (totalPages < page) { 
                page = totalPages;
            }
            
            filter.setPage(page);

           
            dataItems = thongKeService.getPage(filter);
            
            for(InuhaThongKeSanPhamModel m: dataItems) { 
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        pnlChart = new com.app.views.UI.panel.RoundPanel();
        chartDoanhThu = new com.app.views.UI.curvelinechart.chart.CurveLineChart();
        pnlChiTiet = new com.app.views.UI.panel.RoundPanel();
        jLabel4 = new javax.swing.JLabel();
        splitLine1 = new com.app.views.UI.label.SplitLine();
        jLabel8 = new javax.swing.JLabel();
        lblTongDon = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblTongDonChoThanhToan = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblTongDonDaThanhToan = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblTongDonDaHuy = new javax.swing.JLabel();
        lblTongKhachHang = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        pnlList = new com.app.views.UI.panel.RoundPanel();
        pnlDanhSach = new javax.swing.JPanel();
        scrDanhSach = new javax.swing.JScrollPane();
        tblDanhSach = new javax.swing.JTable();
        btnExport = new javax.swing.JButton();
        pnlPhanTrang = new javax.swing.JPanel();
        cboSapXep = new javax.swing.JComboBox();
        pnlSoLuongBan = new com.app.views.UI.panel.RoundPanel();
        jLabel1 = new javax.swing.JLabel();
        imgSoLuongBan = new javax.swing.JLabel();
        lblTongSanPham = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pnlDoanhThu = new com.app.views.UI.panel.RoundPanel();
        jLabel2 = new javax.swing.JLabel();
        imgTongDoanhThu = new javax.swing.JLabel();
        lblTongDoanhThu = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        pnlLoiNhuan = new com.app.views.UI.panel.RoundPanel();
        jLabel3 = new javax.swing.JLabel();
        imgTongLoiNhuan = new javax.swing.JLabel();
        lblTongLoiNhuan = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnFilter = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        txtThoiGian = new javax.swing.JFormattedTextField();
        cboThoiGian = new javax.swing.JComboBox();
        cboSanPham = new javax.swing.JComboBox();

        javax.swing.GroupLayout pnlChartLayout = new javax.swing.GroupLayout(pnlChart);
        pnlChart.setLayout(pnlChartLayout);
        pnlChartLayout.setHorizontalGroup(
            pnlChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChartLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(chartDoanhThu, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        pnlChartLayout.setVerticalGroup(
            pnlChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChartLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(chartDoanhThu, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel4.setText("Chi tiết số lượng");

        javax.swing.GroupLayout splitLine1Layout = new javax.swing.GroupLayout(splitLine1);
        splitLine1.setLayout(splitLine1Layout);
        splitLine1Layout.setHorizontalGroup(
            splitLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        splitLine1Layout.setVerticalGroup(
            splitLine1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jLabel8.setText("Tổng đơn");

        lblTongDon.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongDon.setText("0");

        jLabel9.setText("Chờ thanh toán");

        lblTongDonChoThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongDonChoThanhToan.setText("0");

        jLabel10.setText("Đã thanh toán");

        lblTongDonDaThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongDonDaThanhToan.setText("0");

        jLabel11.setText("Đơn đã huỷ");

        lblTongDonDaHuy.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongDonDaHuy.setText("0");

        lblTongKhachHang.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongKhachHang.setText("0");

        jLabel12.setText("Khách hàng mới");

        javax.swing.GroupLayout pnlChiTietLayout = new javax.swing.GroupLayout(pnlChiTiet);
        pnlChiTiet.setLayout(pnlChiTietLayout);
        pnlChiTietLayout.setHorizontalGroup(
            pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitLine1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlChiTietLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChiTietLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(20, 20, 20))
                    .addGroup(pnlChiTietLayout.createSequentialGroup()
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblTongDon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                        .addContainerGap(25, Short.MAX_VALUE))
                    .addGroup(pnlChiTietLayout.createSequentialGroup()
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblTongKhachHang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblTongDonDaThanhToan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblTongDonChoThanhToan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblTongDonDaHuy, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        pnlChiTietLayout.setVerticalGroup(
            pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChiTietLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(0, 0, 0)
                .addComponent(lblTongDon, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(0, 0, 0)
                .addComponent(lblTongDonChoThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(0, 0, 0)
                .addComponent(lblTongDonDaThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(0, 0, 0)
                .addComponent(lblTongDonDaHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(0, 0, 0)
                .addComponent(lblTongKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(pnlChiTiet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlChiTiet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        jTabbedPane1.addTab("Thống kê doanh thu", jPanel1);

        tblDanhSach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Mã sản phẩm", "Tên sản phẩm", "Doanh thu", "Lợi nhuận", "Số lượng đã bán", "Số lượng tồn"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSach.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrDanhSach.setViewportView(tblDanhSach);
        if (tblDanhSach.getColumnModel().getColumnCount() > 0) {
            tblDanhSach.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, 929, Short.MAX_VALUE)
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrDanhSach, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
        );

        btnExport.setText("Xuất Excel");
        btnExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

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

        cboSapXep.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboSapXep.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboSapXepItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlListLayout = new javax.swing.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlListLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlPhanTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlListLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(cboSapXep, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSapXep, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPhanTrang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(pnlList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Thống kê chi tiết", jPanel2);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Tổng số lượng bán");

        lblTongSanPham.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTongSanPham.setText("0");

        jLabel5.setText("Sản phẩm đã bán");

        javax.swing.GroupLayout pnlSoLuongBanLayout = new javax.swing.GroupLayout(pnlSoLuongBan);
        pnlSoLuongBan.setLayout(pnlSoLuongBanLayout);
        pnlSoLuongBanLayout.setHorizontalGroup(
            pnlSoLuongBanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSoLuongBanLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnlSoLuongBanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSoLuongBanLayout.createSequentialGroup()
                        .addGroup(pnlSoLuongBanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTongSanPham, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(imgSoLuongBan, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))
                    .addGroup(pnlSoLuongBanLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pnlSoLuongBanLayout.setVerticalGroup(
            pnlSoLuongBanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSoLuongBanLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSoLuongBanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(imgSoLuongBan, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlSoLuongBanLayout.createSequentialGroup()
                        .addComponent(lblTongSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Tổng doanh thu");

        lblTongDoanhThu.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTongDoanhThu.setText("0");

        jLabel6.setText("Tiền thu về");

        javax.swing.GroupLayout pnlDoanhThuLayout = new javax.swing.GroupLayout(pnlDoanhThu);
        pnlDoanhThu.setLayout(pnlDoanhThuLayout);
        pnlDoanhThuLayout.setHorizontalGroup(
            pnlDoanhThuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDoanhThuLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnlDoanhThuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDoanhThuLayout.createSequentialGroup()
                        .addGroup(pnlDoanhThuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTongDoanhThu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(imgTongDoanhThu, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );
        pnlDoanhThuLayout.setVerticalGroup(
            pnlDoanhThuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDoanhThuLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDoanhThuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(imgTongDoanhThu, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlDoanhThuLayout.createSequentialGroup()
                        .addComponent(lblTongDoanhThu, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Tổng lợi nhuận");

        lblTongLoiNhuan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTongLoiNhuan.setText("0");

        jLabel7.setText("Lợi nhuận ước tính");

        javax.swing.GroupLayout pnlLoiNhuanLayout = new javax.swing.GroupLayout(pnlLoiNhuan);
        pnlLoiNhuan.setLayout(pnlLoiNhuanLayout);
        pnlLoiNhuanLayout.setHorizontalGroup(
            pnlLoiNhuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoiNhuanLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnlLoiNhuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlLoiNhuanLayout.createSequentialGroup()
                        .addGroup(pnlLoiNhuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTongLoiNhuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(imgTongLoiNhuan, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))
                    .addGroup(pnlLoiNhuanLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pnlLoiNhuanLayout.setVerticalGroup(
            pnlLoiNhuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoiNhuanLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLoiNhuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(imgTongLoiNhuan, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlLoiNhuanLayout.createSequentialGroup()
                        .addComponent(lblTongLoiNhuan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        btnFilter.setText("Lọc");
        btnFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        btnClear.setText("Huỷ");
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        cboThoiGian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Từ trước đến nay" }));
        cboThoiGian.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboThoiGianItemStateChanged(evt);
            }
        });

        cboSanPham.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tất cả sản phẩm" }));
        cboSanPham.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboSanPhamItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSoLuongBan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlDoanhThu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlLoiNhuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cboSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlDoanhThu, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSoLuongBan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlLoiNhuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jTabbedPane1)
                .addGap(15, 15, 15))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboThoiGianItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboThoiGianItemStateChanged
        // TODO add your handling code here:
	if (evt.getStateChange() == ItemEvent.SELECTED) { 
	    ComboBoxItem<Integer> item = (ComboBoxItem<Integer>) cboThoiGian.getSelectedItem();
	    boolean isCustom = item.getValue() == 2;
	    txtThoiGian.setVisible(isCustom);
	    revalidate();
	    repaint();
	    if (!isCustom) {
		handleClickButtonFilter();
	    }
	}
    }//GEN-LAST:event_cboThoiGianItemStateChanged

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        // TODO add your handling code here:
	handleClickButtonFilter();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void cboSapXepItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboSapXepItemStateChanged
        // TODO add your handling code here:
	if (firstLoad) {
	    return;
	}
	
	if (evt.getStateChange() == ItemEvent.SELECTED) { 
	    executorService.submit(() -> { 
		loadDataPage(1);
		loading.dispose();
	    });
	    loading.setVisible(true);
	}
    }//GEN-LAST:event_cboSapXepItemStateChanged

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        executorService.submit(() -> {
            firstLoad = true;
            cboSanPham.setSelectedIndex(0);
            cboThoiGian.setSelectedIndex(0);
            firstLoad = false;
            loadDataChart();
            loading.dispose();
        });
        loading.setVisible(true);
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
	handleClickButtonExport();
    }//GEN-LAST:event_btnExportActionPerformed

    private void cboSanPhamItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboSanPhamItemStateChanged
        // TODO add your handling code here:
	handleClickButtonFilter();
    }//GEN-LAST:event_cboSanPhamItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnFilter;
    private javax.swing.JComboBox cboSanPham;
    private javax.swing.JComboBox cboSapXep;
    private javax.swing.JComboBox cboThoiGian;
    private com.app.views.UI.curvelinechart.chart.CurveLineChart chartDoanhThu;
    private javax.swing.JLabel imgSoLuongBan;
    private javax.swing.JLabel imgTongDoanhThu;
    private javax.swing.JLabel imgTongLoiNhuan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblTongDoanhThu;
    private javax.swing.JLabel lblTongDon;
    private javax.swing.JLabel lblTongDonChoThanhToan;
    private javax.swing.JLabel lblTongDonDaHuy;
    private javax.swing.JLabel lblTongDonDaThanhToan;
    private javax.swing.JLabel lblTongKhachHang;
    private javax.swing.JLabel lblTongLoiNhuan;
    private javax.swing.JLabel lblTongSanPham;
    private com.app.views.UI.panel.RoundPanel pnlChart;
    private com.app.views.UI.panel.RoundPanel pnlChiTiet;
    private javax.swing.JPanel pnlDanhSach;
    private com.app.views.UI.panel.RoundPanel pnlDoanhThu;
    private com.app.views.UI.panel.RoundPanel pnlList;
    private com.app.views.UI.panel.RoundPanel pnlLoiNhuan;
    private javax.swing.JPanel pnlPhanTrang;
    private com.app.views.UI.panel.RoundPanel pnlSoLuongBan;
    private javax.swing.JScrollPane scrDanhSach;
    private com.app.views.UI.label.SplitLine splitLine1;
    private javax.swing.JTable tblDanhSach;
    private javax.swing.JFormattedTextField txtThoiGian;
    // End of variables declaration//GEN-END:variables

    private void handleClickButtonFilter() {
	if (firstLoad) {
	    return;
	}
	executorService.submit(() -> {
	    loadDataChart();
	    loading.dispose();
	});
	loading.setVisible(true);
    }

    private void handleClickButtonExport() {
        String fileName = "ThongKe-" + TimeUtils.now("dd_MM_yyyy__hh_mm_a");
	String[] headers = new String[] {
	    "STT",
	    "Mã sản phẩm",
	    "Tên sản phẩm",
	    "Doanh thu",
	    "Lợi nhuận",
	    "Số lượng bán",
	    "Số lượng tồn"
	};

	executorService.submit(() -> { 
	    
	    try {
		
		List<InuhaThongKeSanPhamModel> items = thongKeService.getAll(filter);
		
		List<String[]> rows = new ArrayList<>();
		for(InuhaThongKeSanPhamModel item: items) { 
		    rows.add(item.toDataRow());
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

}
