package com.app.common.helper;

import com.app.common.infrastructure.constants.PagableConstant;
import com.app.utils.ColorUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 *
 * @author InuHa
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    @Setter
    private JPanel panel = null;

    @Setter
    private int currentPage = 1;

    @Setter
    private int totalPages = 0;

    @Setter
    private Callback callback = null;

    private final JPanel listPage = new JPanel();

    private final int limitItem = PagableConstant.SIZE;

    private static final String PROPERTY_DATA = "data-page";

    public JPanel render() {
        if (panel == null) {
            panel = new JPanel();
        }

        listPage.setLayout(new MigLayout());
        listPage.setOpaque(false);

        panel.setLayout(new MigLayout("align left", "[left]", "[center]"));
        panel.setOpaque(false);

        JComboBox<Integer> selectLimit = new JComboBox<>();
        selectLimit.addItem(limitItem);
        selectLimit.addItem(20);
        selectLimit.addItem(30);
        selectLimit.addItem(50);
        selectLimit.addItem(100);

        selectLimit.addActionListener((e) -> {
            if (callback != null) {
                currentPage = 1;
                callback.onChangeLimitItem(selectLimit);
                renderListPage();
            }
        });

        panel.add(new JLabel("Hiển thị"), "gapx 10");
        panel.add(selectLimit);
        panel.add(listPage);

        renderListPage();
        return panel;
    }

    private void renderListPage() {

        listPage.removeAll();

        int limit = 6;
        int mid = limit - 2;
        int startPage, endPage;
        int totalPages = this.totalPages;
        int currentPage = this.currentPage;

        if (totalPages <= limit) {
            startPage = 1;
            endPage = totalPages;
        } else {
            if (currentPage <= mid) {
                startPage = 1;
                endPage = limit;
            } else if (currentPage + (limit - mid) >= totalPages) {
                startPage = totalPages - (limit - 1);
                endPage = totalPages;
            } else {
                startPage = currentPage - (mid - 1);
                endPage = currentPage + (limit - mid);
            }
        }

        ActionListener onClick = (e) -> {
            if (callback != null) {
                JButton btn = (JButton) e.getSource();
                int page = (int) btn.getClientProperty(PROPERTY_DATA);
                if (page == currentPage) {
                    return;
                }
                setCurrentPage(page);
                renderListPage();
                callback.onClickPage(page);
            }
        };



        JButton btnPre = new JButton();
        btnPre.putClientProperty(PROPERTY_DATA, currentPage - 1);
        btnPre.setText("<");
        btnPre.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPre.addActionListener(onClick);
        if (currentPage < 2) {
            btnPre.setEnabled(false);
            btnPre.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            btnPre.setOpaque(false);
        }
        listPage.add(btnPre);

        if (startPage > 1) {
            JButton firstPage = new JButton();
            firstPage.putClientProperty(PROPERTY_DATA, 1);
            firstPage.setText("1");
            firstPage.setCursor(new Cursor(Cursor.HAND_CURSOR));
            firstPage.addActionListener(onClick);
            listPage.add(firstPage);

            if (startPage - 1 != 1) {
                JButton dotfirst = new JButton();
                dotfirst.setText("...");
                dotfirst.setEnabled(false);
                dotfirst.setOpaque(false);
                listPage.add(dotfirst);
            }
        }

        Dimension maxSize = new Dimension(35, 35);

        for (int i = startPage; i <= endPage; i++) {
            JButton page = new JButton();
            page.putClientProperty(PROPERTY_DATA, i);
            page.setText(String.valueOf(i));
            page.setCursor(new Cursor(Cursor.HAND_CURSOR));
            page.addActionListener(onClick);
            page.setMaximumSize(maxSize);
            if (currentPage == i) {
                page.setBackground(ColorUtils.BUTTON_PRIMARY);
                page.setForeground(Color.WHITE);
                page.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            listPage.add(page);
        }

        if (endPage < totalPages) {

            if (endPage != totalPages - 1) {
                JButton dotEnd = new JButton();
                dotEnd.setText("...");
                dotEnd.setEnabled(false);
                dotEnd.setOpaque(false);
                listPage.add(dotEnd);
            }

            JButton lastPage = new JButton();
            lastPage.putClientProperty(PROPERTY_DATA, totalPages);
            lastPage.setText(String.valueOf(totalPages));
            lastPage.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lastPage.setMaximumSize(maxSize);
            lastPage.addActionListener(onClick);
            listPage.add(lastPage);
        }

        JButton btnNext = new JButton();
        btnNext.putClientProperty(PROPERTY_DATA, currentPage + 1);
        btnNext.setText(">");
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(onClick);
        if (currentPage >= totalPages) {
            btnNext.setEnabled(false);
            btnNext.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            btnNext.setOpaque(false);
        }
        listPage.add(btnNext);
        listPage.revalidate();
        listPage.repaint();
    }

    public void rerender(int currentPage, int totalPages) { 
        currentPage = currentPage < 1 ? 1 : currentPage;
        setCurrentPage(currentPage);
        setTotalPages(totalPages);
        renderListPage();
    }
    
    public interface Callback {

        void onChangeLimitItem(JComboBox<Integer> comboBox);

        void onClickPage(int page);

    }

}
