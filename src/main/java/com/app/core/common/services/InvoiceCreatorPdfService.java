package com.app.core.common.services;

import com.app.core.common.constants.InvoiceConstant;
import com.app.core.common.models.invoice.InvoiceAddressDetails;
import com.app.core.common.models.invoice.InvoiceDataModel;
import com.app.core.common.models.invoice.InvoiceHeaderDetails;
import com.app.core.common.models.invoice.InvoiceProduct;
import com.app.core.common.models.invoice.InvoiceProductTableHeader;
import com.app.utils.CurrencyUtils;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class InvoiceCreatorPdfService {
    Document document;
    PdfDocument pdfDocument;
    String pdfName;
    
    float threecol = 190f;
    float twocol = 285f;
    float twocol150 = twocol + 150f;
    float[] twocolumnWidth = {twocol150, twocol};
    float[] threeColumnWidth = {threecol, threecol, threecol};
    float[] fullwidth = {threecol * 3};

    public InvoiceCreatorPdfService(String pdfName) {
        this.pdfName = pdfName;
    }
	
    public void createDocument() throws FileNotFoundException {
        PdfWriter pdfWriter = new PdfWriter(pdfName);
        pdfDocument = new PdfDocument(pdfWriter);
        //pdfDocument.setDefaultPageSize(PageSize.LETTER);
        this.document = new Document(pdfDocument);

	try {
	    String fontPath = "/assets/fonts/arial.ttf";
	    PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
	    this.document.setFont(font);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
    
    public void createQrCode(ImageIcon image) {
        if (image != null) { 
            BufferedImage bufferedImage = new BufferedImage(
                image.getIconWidth(),
                image.getIconHeight(),
                BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image.getImage(), 0, 0, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage, "png", baos);
            } catch (IOException ex) {
            }
            byte[] imageBytes = baos.toByteArray();

            float newWidth = 200;
            float newHeight = 200;


            ImageData data = ImageDataFactory.create(imageBytes);
            Image imageData = new Image(data);

            imageData.setWidth(newWidth);
            imageData.setHeight(newHeight);


            document.add(imageData);
        }
        document.close();
    }
    
    public void close() {
	this.document.close();
    }

    public void createProduct(InvoiceDataModel data) {
	List<InvoiceProduct> productList = data.getHoaDonChiTiet();
	productList = modifyProductList(productList);
	    
        Table threeColTable2 = new Table(threeColumnWidth);
        float totalSum = getTotalSum(productList);
        for (InvoiceProduct product : productList) {
            float total = product.getQuantity() * product.getPriceperpeice();
            threeColTable2.addCell(new Cell().add(new Paragraph(product.getPname().orElse(""))).setBorder(Border.NO_BORDER).setMarginLeft(10f));
            threeColTable2.addCell(new Cell().add(new Paragraph(CurrencyUtils.parseNumber(product.getQuantity()))).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            threeColTable2.addCell(new Cell().add(new Paragraph(CurrencyUtils.parseString(total))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setMarginRight(15f));                
        }
        document.add(threeColTable2.setMarginBottom(20f));

        float[] onetwo = {threecol + 125f, threecol * 2};
        Table threeColTable1 = new Table(onetwo);
        threeColTable1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        threeColTable1.addCell(new Cell().add(fullwidthDashedBorder(fullwidth)).setBorder(Border.NO_BORDER));
        document.add(threeColTable1);

        Table threeColTable3 = new Table(threeColumnWidth);
        threeColTable3.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER).setMarginLeft(10f));
        threeColTable3.addCell(new Cell().add(new Paragraph(InvoiceConstant.TOTAL_PRICE)).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threeColTable3.addCell(new Cell().add(new Paragraph(CurrencyUtils.parseString(data.getTongTienHang()))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setMarginRight(15f));
        document.add(threeColTable3);
	
        Table threeColTable4 = new Table(threeColumnWidth);
        threeColTable4.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER).setMarginLeft(10f));
        threeColTable4.addCell(new Cell().add(new Paragraph(InvoiceConstant.TOTAL_SALE)).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threeColTable4.addCell(new Cell().add(new Paragraph(CurrencyUtils.parseString(data.getTongTienGiam()))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setMarginRight(15f));
        document.add(threeColTable4);
	
        Table threeColTable5 = new Table(threeColumnWidth);
        threeColTable5.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER).setMarginLeft(10f));
        threeColTable5.addCell(new Cell().add(new Paragraph(InvoiceConstant.TOTAL_PAID)).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threeColTable5.addCell(new Cell().add(new Paragraph(CurrencyUtils.parseString(totalSum - data.getTongTienGiam()))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setMarginRight(15f));
        document.add(threeColTable5);

	Table threeColTable6 = new Table(onetwo);
        threeColTable6.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        threeColTable6.addCell(new Cell().add(fullwidthDashedBorder(fullwidth)).setBorder(Border.NO_BORDER));
        document.add(threeColTable6);
	
	Table threeColTable7 = new Table(threeColumnWidth);
        threeColTable7.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER).setMarginLeft(10f));
        threeColTable7.addCell(new Cell().add(new Paragraph(InvoiceConstant.PRICE_PAID)).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threeColTable7.addCell(new Cell().add(new Paragraph(CurrencyUtils.parseString(data.getTienKhachTra()))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setMarginRight(15f));
        document.add(threeColTable7);
	
	Table threeColTable8 = new Table(threeColumnWidth);
        threeColTable8.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER).setMarginLeft(10f));
        threeColTable8.addCell(new Cell().add(new Paragraph(InvoiceConstant.PRICE_CHANGE)).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threeColTable8.addCell(new Cell().add(new Paragraph(CurrencyUtils.parseString(Math.abs(data.getTienKhachTra() - (totalSum - data.getTongTienGiam()))))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setMarginRight(15f));
        document.add(threeColTable8);
	
        document.add(fullwidthDashedBorder(fullwidth));
        document.add(new Paragraph("\n"));
        document.add(getDividerTable(fullwidth).setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)).setMarginBottom(15f));
    }

    public float getTotalSum(List<InvoiceProduct> productList) {
        return (float) productList.stream().mapToLong(p -> (long) (p.getQuantity() * p.getPriceperpeice())).sum();
    }


    public void createTableHeader(InvoiceProductTableHeader productTableHeader) throws IOException {
        Paragraph producPara = new Paragraph(InvoiceConstant.PRODUCT).setBold();
        document.add(producPara);

        Table threeColTable1 = new Table(threeColumnWidth)
                .setBackgroundColor(new DeviceRgb(0, 0, 0), 0.7f);

        threeColTable1.addCell(new Cell()
                .add(new Paragraph(InvoiceConstant.PRODUCT_TABLE_DESCRIPTION))
                .setBold()
                .setFontColor(DeviceRgb.WHITE)
                .setBorder(Border.NO_BORDER));

        threeColTable1.addCell(new Cell()
                .add(new Paragraph(InvoiceConstant.PRODUCT_TABLE_QUANTITY))
                .setBold()
                .setFontColor(DeviceRgb.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER));

        threeColTable1.addCell(new Cell()
                .add(new Paragraph(InvoiceConstant.PRODUCT_TABLE_PRICE))
                .setBold()
                .setFontColor(DeviceRgb.WHITE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
                .setMarginRight(15f));

        document.add(threeColTable1);
    }

    public void createAddress(InvoiceAddressDetails addressDetails) {
        Table twoColTable = new Table(twocolumnWidth);
        twoColTable.addCell(getBillingandShippingCell(addressDetails.getBillingInfoText()));
        twoColTable.addCell(getBillingandShippingCell(addressDetails.getCustomerInfoText()));
        document.add(twoColTable.setMarginBottom(12f));

        Table twoColTable2 = new Table(twocolumnWidth);
        twoColTable2.addCell(getCell10fLeft(addressDetails.getBillingStoreText(), true));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getCustomerNameText(), true));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getBillingStore(), false));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getCustomerName(), false));
        document.add(twoColTable2);

        Table twoColTable3 = new Table(twocolumnWidth);
        twoColTable3.addCell(getCell10fLeft(addressDetails.getBillingUserText(), true));
        twoColTable3.addCell(getCell10fLeft(addressDetails.getCustomerPhoneText(), true));
        twoColTable3.addCell(getCell10fLeft(addressDetails.getBillingUser(), false));
        twoColTable3.addCell(getCell10fLeft(addressDetails.getCustomerPhone(), false));
        document.add(twoColTable3);

        document.add(fullwidthDashedBorder(fullwidth));
    }

    public void createHeader(InvoiceHeaderDetails header) {
        Table table = new Table(twocolumnWidth);
        table.addCell(new Cell().add(new Paragraph(header.getInvoiceTitle())).setFontSize(20f).setBorder(Border.NO_BORDER).setBold());
        
        Table nestedTable = new Table(new float[]{twocol / 2, twocol / 2});
        nestedTable.addCell(getHeaderTextCell(header.getInvoiceNoText()));
        nestedTable.addCell(getHeaderTextCellValue(header.getInvoiceNo()));
        nestedTable.addCell(getHeaderTextCell(header.getInvoiceDateText()));
        nestedTable.addCell(getHeaderTextCellValue(header.getInvoiceDate()));
        table.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));
        
        Border gb = new SolidBorder((Color) header.getBorderColor(), 2f);
        document.add(table);
        document.add(getNewLineParagraph());
        document.add(getDividerTable(fullwidth).setBorder(gb));
        document.add(getNewLineParagraph());
    }

    public List<InvoiceProduct> modifyProductList(List<InvoiceProduct> productList) {
        Map<String, InvoiceProduct> map = new HashMap<>();
        productList.forEach(i -> {
            if (map.containsKey(i.getPname().orElse(""))) {
                i.setQuantity(map.getOrDefault(i.getPname().orElse(""), null).getQuantity() + i.getQuantity());
                map.put(i.getPname().orElse(""), i);
            } else {
                map.put(i.getPname().orElse(""), i);
            }
        });
        return map.values().stream().collect(Collectors.toList());
    }
    

    static Table getDividerTable(float[] fullwidth) {
        return new Table(fullwidth);
    }

    static Table fullwidthDashedBorder(float[] fullwidth) {
        Table tableDivider2 = new Table(fullwidth);
        Border dgb = new DashedBorder(new DeviceRgb(0, 0, 0), 0.5f);
        tableDivider2.setBorder(dgb);
        return tableDivider2;
    }
    

    static Paragraph getNewLineParagraph() {
        return new Paragraph("\n");
    }

    static Cell getHeaderTextCell(String textValue) {
        return new Cell().add(new Paragraph(textValue)).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }

    static Cell getHeaderTextCellValue(String textValue) {
        return new Cell().add(new Paragraph(textValue)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static Cell getBillingandShippingCell(String textValue) {
        return new Cell().add(new Paragraph(textValue)).setFontSize(12f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static Cell getCell10fLeft(String textValue, Boolean isBold) {
        Cell myCell = new Cell().add(new Paragraph(textValue)).setFontSize(10f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return isBold ? myCell.setBold() : myCell;
    }
}