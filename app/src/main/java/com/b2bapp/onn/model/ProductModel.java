package com.b2bapp.onn.model;

public class ProductModel {
    private String id = "";
    private String name = "";
    private String desc = "";
    private String image = "";
    private String price = "";
    private String offer_price = "";
    private String pack = "";
    private String style_no = "";
    private String slug = "";
    private String collection_id = "";
    private String category_id = "";

    public String getMaster_pack_count() {
        return master_pack_count;
    }

    public void setMaster_pack_count(String master_pack_count) {
        this.master_pack_count = master_pack_count;
    }

    private String master_pack_count = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(String offer_price) {
        this.offer_price = offer_price;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getStyle_no() {
        return style_no;
    }

    public void setStyle_no(String style_no) {
        this.style_no = style_no;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(String collection_id) {
        this.collection_id = collection_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
