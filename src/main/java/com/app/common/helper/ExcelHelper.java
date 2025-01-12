package com.app.common.helper;

import com.app.Application;
import com.app.views.UI.dialog.LoadingDialog;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jnafilechooser.api.JnaFileChooser;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 *
 * @author inuHa
 */
public class ExcelHelper {
    
    private static String getCellValueAsString(Cell cell) {
	if (cell == null) { 
	    return "";
	}
    
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.format("%d", (int) numericValue);
                    } else {
                        return String.format("%f", numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getStringCellValue();
            default:
                return "";
        }
    }
	
    public static List<String[]> readFile(File file, boolean skipHeader) {
        List<String[]> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            
            if (skipHeader && rowIterator.hasNext()) {
                rowIterator.next();
            }
	    
	    int maxColumns = getMaxColumnCount(sheet);
	    
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String[] rowData = new String[maxColumns];

                for (int i = 0; i < maxColumns; i++) {
                    rowData[i] = "";
                }

                for (Cell cell : row) {
                    int columnIndex = cell.getColumnIndex();
                    if (columnIndex < maxColumns) {
                        rowData[columnIndex] = getCellValueAsString(cell);
                    }
                }
                
                data.add(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
	
    private static int getMaxColumnCount(Sheet sheet) {
        int maxColumns = 0;
        for (Row row : sheet) {
            int lastCellNum = row.getLastCellNum();
            if (lastCellNum > maxColumns) {
                maxColumns = lastCellNum;
            }
        }
        return maxColumns;
    }
	
    private static void writeExcel(File file, String[] headers, List<String[]> rows) throws IOException { 
        List<String[]> data = new ArrayList<>();
	if (headers != null) { 
	    data.add(headers);
	}
	data.addAll(rows);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = (Sheet) workbook.createSheet("Data");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = (Font) workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode("#8bc34a"), null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int rowNum = 0;
        for (String[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(rowData[i]);
                if (headers != null && rowNum == 1) {
                    cell.setCellStyle(headerStyle);
                }
            }
        }
        int numberOfColumns = rows.size();
        for (int col = 0; col <= numberOfColumns; col++) {
            int maxWidth = 0;

            for (int row = 0; row <= sheet.getLastRowNum(); row++) {
                Row currentRow = sheet.getRow(row);
                if (currentRow != null) {
                    Cell cell = currentRow.getCell(col);
                    if (cell != null) {
                        int cellWidth = cell.toString().length();
                        if (cellWidth > maxWidth) {
                            maxWidth = cellWidth;
                        }
                    }
                }
            }

            sheet.setColumnWidth(col, maxWidth * 256 + 2048);
        }
	
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
    
    public static void writeFile(String fileName, List<String[]> rows) {
	writeFile(fileName, null, rows);
    }
    
    public static void writeFile(String fileName, String[] headers, List<String[]> rows) {
	File folder = selectFolder();
        if (folder != null) {
            File file = new File(folder, fileName + ".xlsx");
	    
	    ExecutorService executorService = Executors.newSingleThreadExecutor();
	    LoadingDialog loading = new LoadingDialog();
	    executorService.submit(() -> { 
		try {
		    writeExcel(file, headers, rows);
		    MessageToast.success("Xuất dữ liệu sang Excel thành công!");
		} catch (IOException e) {
		    e.printStackTrace();
		    MessageToast.error("Không thể xuất dữ liệu sang Excel!!!!");
		} finally {
		    loading.dispose();
		    executorService.shutdown();
		}
	    });
	    loading.setVisible(true);

        }
    }
    
    public static File selectFolder() {
        JnaFileChooser ch = new JnaFileChooser();
        ch.setMode(JnaFileChooser.Mode.Directories);
        boolean act = ch.showOpenDialog(Application.app);
        if (act) {
            File folder = ch.getSelectedFile();
	    return folder;
	}
	return null;
    }
	
    public static File selectFile() {
        JnaFileChooser ch = new JnaFileChooser();
        ch.addFilter("Tệp Excel (*.xlsx, *.xls)", "xlsx", "xls");
        boolean act = ch.showOpenDialog(Application.app);
        if (act) {
            File file = ch.getSelectedFile();
	    return file;
        }
	return null;
    }
    
}
