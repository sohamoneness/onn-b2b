package com.b2bapp.onn.model;

import java.util.ArrayList;

public class DistributorOrderModel {
    private String id = "";
    private String order_no = "";
    private String store_name = "";
    private String store_code = "";
    private String sales_person = "";
    private String order_date = "";
    private String order_amount = "";

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    private String order_status = "";

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getSales_person() {
        return sales_person;
    }

    public void setSales_person(String sales_person) {
        this.sales_person = sales_person;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public ArrayList<ItemModel> getItemModelArrayList() {
        return itemModelArrayList;
    }

    public void setItemModelArrayList(ArrayList<ItemModel> itemModelArrayList) {
        this.itemModelArrayList = itemModelArrayList;
    }

    private ArrayList<ItemModel> itemModelArrayList = new ArrayList<ItemModel>();
}
