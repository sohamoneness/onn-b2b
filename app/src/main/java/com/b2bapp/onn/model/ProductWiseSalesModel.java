package com.b2bapp.onn.model;

import java.util.ArrayList;

public class ProductWiseSalesModel {
    private String style_no = "";
    private String product = "";
    private String quantity = "";
    private ArrayList<ProductWiseSalesDetailsModel> productWiseSalesDetailsModelArrayList = new ArrayList<ProductWiseSalesDetailsModel>();

    public ArrayList<ProductWiseSalesDetailsModel> getProductWiseSalesDetailsModelArrayList() {
        return productWiseSalesDetailsModelArrayList;
    }

    public void setProductWiseSalesDetailsModelArrayList(ArrayList<ProductWiseSalesDetailsModel> productWiseSalesDetailsModelArrayList) {
        this.productWiseSalesDetailsModelArrayList = productWiseSalesDetailsModelArrayList;
    }

    public String getStyle_no() {
        return style_no;
    }

    public void setStyle_no(String style_no) {
        this.style_no = style_no;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
