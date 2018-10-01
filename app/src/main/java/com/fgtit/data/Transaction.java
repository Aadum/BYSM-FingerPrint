package com.fgtit.data;

public class Transaction {

    private int id;
    private int userid;
    private double amount;
    private String comment;
    private String pregnancy_status;
    private String payment_date;


    public Transaction() {
    }

    public Transaction(int id, int userid, double amount, String comment, String pregnancy_status, String payment_date) {
        this.id = id;
        this.comment = comment;
        this.pregnancy_status = pregnancy_status;
        this.userid = userid;
        this.amount = amount;
        this.payment_date = payment_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPregnancy_status() {
        return pregnancy_status;
    }

    public void setPregnancy_status(String pregnancy_status) {
        this.pregnancy_status = pregnancy_status;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPaymentDate() {
        return amount;
    }

    public void setPaymentDate(String payment_date) {
        this.payment_date = payment_date;
    }

    @Override
    public String toString() {
        return payment_date + " - " + amount + "  status: " + pregnancy_status ;
    }

}