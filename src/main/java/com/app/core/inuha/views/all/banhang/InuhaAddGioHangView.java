package com.app.core.inuha.views.all.banhang;

import com.app.core.inuha.models.InuhaSanPhamChiTietModel;
import com.app.core.inuha.models.InuhaSanPhamModel;
import com.app.core.inuha.models.sanpham.InuhaKichCoModel;
import com.app.core.inuha.services.InuhaSanPhamChiTietService;
import com.app.core.inuha.views.all.InuhaBanHangView;
import com.app.utils.ColorUtils;
import com.app.utils.ProductUtils;
import com.app.utils.ResourceUtils;
import com.app.views.UI.combobox.ComboBoxItem;
import com.app.views.UI.dialog.LoadingDialog;
import com.app.views.UI.picturebox.DefaultPictureBoxRender;
import com.app.views.UI.picturebox.PictureBox;
import com.app.views.UI.picturebox.SuperEllipse2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.text.NumberFormatter;
import org.apache.poi.ss.formula.functions.Match;
import raven.modal.ModalDialog;

/**
 *
 * @author inuHa
 */
public class InuhaAddGioHangView extends javax.swing.JPanel {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    private InuhaSanPhamModel sanPham = null;
    
    private List<InuhaKichCoModel> dataKichCo = new ArrayList<>();
    
    private List<InuhaSanPhamChiTietModel> dataSanPhamChiTiet = new ArrayList<>();
    
    private InuhaSanPhamChiTietModel currentSanPhamChiTiet = null;
    
    private int maxSoLuong = 1;
    
    /** Creates new form InuhaAddGioHangView
     * @param sanPham */
    public InuhaAddGioHangView(InuhaSanPhamModel sanPham) {
	initComponents();
	this.sanPham = sanPham;
	
	btnAdd.setBackground(ColorUtils.BUTTON_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
	btnAdd.setIcon(ResourceUtils.getSVG("/svg/plus.svg", new Dimension(20, 20)));
	
	pictureBox1.setImage(ProductUtils.getImage(sanPham.getHinhAnh()));
	pictureBox1.setBoxFit(PictureBox.BoxFit.CONTAIN);
        pictureBox1.setPictureBoxRender(new DefaultPictureBoxRender() {
            @Override
            public Shape render(Rectangle rec) {
                return new SuperEllipse2D(rec.x, rec.y, rec.width, rec.height, 12f).getShape();
            }
        });
	
	spnSoLuong.addMouseWheelListener(new MouseWheelListener() {
	    @Override
	    public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if (notches < 0) {
		    spnSoLuong.setValue(Math.min(maxSoLuong, (int) ((Number) spnSoLuong.getValue()).intValue() + 1));
		} else {
		    spnSoLuong.setValue(Math.max(1, (((Number) spnSoLuong.getValue()).intValue() - 1)));
		}
	    }
	});
		
        setLimit(1);
	
	LoadingDialog loading = new LoadingDialog();
	executorService.submit(() -> {
	    loadDataKichCo();
	    if (!dataKichCo.isEmpty()) { 
		loadDataSanPhamChiTiet(dataKichCo.get(0).getId());
	    }
	    loading.dispose();
	});
	loading.setVisible(true);
    }
    
    private void setLimit(int max) { 
	maxSoLuong = max;
	spnSoLuong.setValue(1);
	JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnSoLuong, "#");
	JFormattedTextField textField = editor.getTextField();
        NumberFormatter formatter = (NumberFormatter) textField.getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        formatter.setMaximum(maxSoLuong);
        formatter.setMinimum(1);
	
	textField.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    handleClickButtonSubmit();
		    e.consume();
		}
	    }
	});
		    
	spnSoLuong.setEditor(editor);
	lblLimit.setText("(Tối đa " + maxSoLuong + ")");
    }

    private void loadDataKichCo() { 
	currentSanPhamChiTiet = null;
        dataKichCo = InuhaSanPhamChiTietService.getInstance().getAllKichCo(sanPham.getId());
        cboKichCo.removeAllItems();
        
        for(InuhaKichCoModel m: dataKichCo) { 
            cboKichCo.addItem(new ComboBoxItem<>(m.getTen(), m.getId()));
        }
    }
	
    private void loadDataSanPhamChiTiet(int idKichCo) { 
        dataSanPhamChiTiet = InuhaSanPhamChiTietService.getInstance().getAllByKichCo(sanPham.getId(), idKichCo);
        cboMauSac.removeAllItems();
        
        for(InuhaSanPhamChiTietModel m: dataSanPhamChiTiet) { 
            cboMauSac.addItem(new ComboBoxItem<>(m.getMauSac().getTen(), m.getMauSac().getId()));
        }
	
	if (!dataSanPhamChiTiet.isEmpty()) { 
	    currentSanPhamChiTiet = dataSanPhamChiTiet.get(0);
	    setLimit(dataSanPhamChiTiet.get(0).getSoLuong());
	}
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
        pictureBox1 = new com.app.views.UI.picturebox.PictureBox();
        jLabel1 = new javax.swing.JLabel();
        cboKichCo = new javax.swing.JComboBox();
        cboMauSac = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        spnSoLuong = new javax.swing.JSpinner();
        lblLimit = new javax.swing.JLabel();

        btnAdd.setText("Thêm vào giỏ hàng");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Kích cỡ:");

        cboKichCo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboKichCoItemStateChanged(evt);
            }
        });

        cboMauSac.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboMauSacItemStateChanged(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Màu sắc:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Số lượng");

        spnSoLuong.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        spnSoLuong.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lblLimit.setText("(Tối đa 1)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cboKichCo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(cboMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(spnSoLuong)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblLimit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(40, 40, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboKichCo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(lblLimit))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spnSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboKichCoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboKichCoItemStateChanged
        // TODO add your handling code here:
	handleChangeKichCo(evt);
    }//GEN-LAST:event_cboKichCoItemStateChanged

    private void cboMauSacItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboMauSacItemStateChanged
        // TODO add your handling code here:
	handleChangeMauSac(evt);
    }//GEN-LAST:event_cboMauSacItemStateChanged

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
	handleClickButtonSubmit();
    }//GEN-LAST:event_btnAddActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JComboBox cboKichCo;
    private javax.swing.JComboBox cboMauSac;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblLimit;
    private com.app.views.UI.picturebox.PictureBox pictureBox1;
    private javax.swing.JSpinner spnSoLuong;
    // End of variables declaration//GEN-END:variables

    private void handleChangeKichCo(ItemEvent evt) {
	if (evt.getStateChange() == ItemEvent.SELECTED) { 
	    ComboBoxItem<Integer> kichCo = (ComboBoxItem<Integer>) cboKichCo.getSelectedItem();
	    loadDataSanPhamChiTiet(kichCo.getValue());
	}
    }

    private void handleChangeMauSac(ItemEvent evt) {
	if (evt.getStateChange() == ItemEvent.SELECTED) { 
	    ComboBoxItem<Integer> mauSacSelected = (ComboBoxItem<Integer>) cboMauSac.getSelectedItem();
	    Optional<InuhaSanPhamChiTietModel> sanPhamChiTiet = dataSanPhamChiTiet.stream().filter(o -> o.getMauSac().getId() == mauSacSelected.getValue()).findFirst();
	    currentSanPhamChiTiet = sanPhamChiTiet.isPresent() ? sanPhamChiTiet.get() : null;
	    setLimit(currentSanPhamChiTiet.getSoLuong());
	}
    }

    private void handleClickButtonSubmit() {
	int soLuong = (int) spnSoLuong.getValue();
	InuhaBanHangView.getInstance().addToCart(currentSanPhamChiTiet, soLuong);
	ModalDialog.closeAllModal();
    }

}
