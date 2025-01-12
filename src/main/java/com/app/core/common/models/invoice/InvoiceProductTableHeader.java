package com.app.core.common.models.invoice;

import com.app.core.common.constants.InvoiceConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InvoiceProductTableHeader {
    String description= InvoiceConstant.PRODUCT_TABLE_DESCRIPTION;
    String quantity=InvoiceConstant.PRODUCT_TABLE_QUANTITY;
    String price=InvoiceConstant.PRODUCT_TABLE_PRICE;

    public InvoiceProductTableHeader setDescription(String description) {
        this.description = description;
        return this;
    }

    public InvoiceProductTableHeader setQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public InvoiceProductTableHeader setPrice(String price) {
        this.price = price;
        return this;
    }
    public InvoiceProductTableHeader build(){
        return this;
    }
}
