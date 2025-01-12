package com.app.core.common.models.invoice;

import com.app.core.common.constants.InvoiceConstant;
import com.itextpdf.kernel.colors.DeviceRgb;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InvoiceHeaderDetails {
    String invoiceTitle = InvoiceConstant.INVOICE_TITLE;
    String invoiceNoText = InvoiceConstant.INVOICE_NO_TEXT;
    String invoiceDateText = InvoiceConstant.INVOICE_DATE_TEXT;
    String invoiceNo = InvoiceConstant.EMPTY;
    String invoiceDate = InvoiceConstant.EMPTY;
    DeviceRgb borderColor = new DeviceRgb(128, 128, 128); // Màu xám

    public InvoiceHeaderDetails setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
        return this;
    }

    public InvoiceHeaderDetails setInvoiceNoText(String invoiceNoText) {
        this.invoiceNoText = invoiceNoText;
        return this;
    }

    public InvoiceHeaderDetails setInvoiceDateText(String invoiceDateText) {
        this.invoiceDateText = invoiceDateText;
        return this;
    }

    public InvoiceHeaderDetails setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
        return this;
    }

    public InvoiceHeaderDetails setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
        return this;
    }

    public InvoiceHeaderDetails setBorderColor(DeviceRgb borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public InvoiceHeaderDetails build() {
        return this;
    }
}
