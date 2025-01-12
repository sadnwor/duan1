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
public class InvoiceAddressDetails {
    private String billingInfoText = InvoiceConstant.BILLING_INFO;
    private String customerInfoText = InvoiceConstant.CUSTOMER_INFO;
    private String billingStoreText = InvoiceConstant.BILLING_STORE;
    private String billingStore = InvoiceConstant.EMPTY;
    private String billingUserText = InvoiceConstant.BILLING_USER;
    private String billingUser = InvoiceConstant.EMPTY;
    
    private String customerNameText = InvoiceConstant.CUSTOMER_NAME;
    private String customerName = InvoiceConstant.EMPTY;
    private String customerPhoneText = InvoiceConstant.CUSTOMER_PHONE;
    private String customerPhone = InvoiceConstant.EMPTY;
    private DeviceRgb borderColor = new DeviceRgb(128, 128, 128); // Màu xám

    public InvoiceAddressDetails setBillingInfoText(String billingInfoText) {
        this.billingInfoText = billingInfoText;
        return this;
    }

    public InvoiceAddressDetails setCustomerInfoText(String customerInfoText) {
        this.customerInfoText = customerInfoText;
        return this;
    }

    public InvoiceAddressDetails setBillingStoreText(String billingStoreText) {
        this.billingStoreText = billingStoreText;
        return this;
    }

    public InvoiceAddressDetails setBillingStore(String billingStore) {
        this.billingStore = billingStore;
        return this;
    }

    public InvoiceAddressDetails setBillingUserText(String billingUserText) {
        this.billingUserText = billingUserText;
        return this;
    }

    public InvoiceAddressDetails setBillingUser(String billingUser) {
        this.billingUser = billingUser;
        return this;
    }

    public InvoiceAddressDetails setCustomerNameText(String customerNameText) {
        this.customerNameText = customerNameText;
        return this;
    }

    public InvoiceAddressDetails setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public InvoiceAddressDetails setCustomerPhoneText(String customerPhoneText) {
        this.customerPhoneText = customerPhoneText;
        return this;
    }

    public InvoiceAddressDetails setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public InvoiceAddressDetails setBorderColor(DeviceRgb borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public InvoiceAddressDetails build() {
        return this;
    }
}