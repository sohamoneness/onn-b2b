package com.b2bapp.onn.model;

public class UserModel {
    private String id = "";
    private String fname = "";
    private String lname = "";
    private String email = "";
    private String mobile = "";
    private String whatsapp_no = "";
    private String employee_id = "";

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String city = "";

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private String state = "";

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    private String user_type = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWhatsapp_no() {
        return whatsapp_no;
    }

    public void setWhatsapp_no(String whatsapp_no) {
        this.whatsapp_no = whatsapp_no;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }
}
