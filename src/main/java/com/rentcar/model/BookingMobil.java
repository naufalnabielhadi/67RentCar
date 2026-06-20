package com.rentcar.model;

public class BookingMobil {
    private String idBooking;
    private String idUser;
    private String status;
    private double totalBiaya;

    public BookingMobil() {
    }

    public BookingMobil(String idBooking, String idUser, String status, double totalBiaya) {
        this.idBooking = idBooking;
        this.idUser = idUser;
        this.status = status;
        this.totalBiaya = totalBiaya;
    }

    public BookingMobil(String idBooking, String status) {
        this.idBooking = idBooking;
        this.status = status;
    }

    public double hitungTotalBiaya(double hargaPerHari, long durasiHari) {
        totalBiaya = hargaPerHari * Math.max(1, durasiHari);
        return totalBiaya;
    }

    public void selesaikanBooking() {
        this.status = StatusBooking.SELESAI.name();
    }

    public String getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(String idBooking) {
        this.idBooking = idBooking;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StatusBooking getStatusBooking() {
        return status == null || status.isBlank() ? null : StatusBooking.valueOf(status);
    }

    public void setStatus(StatusBooking status) {
        this.status = status == null ? null : status.name();
    }

    public double getTotalBiaya() {
        return totalBiaya;
    }

    public void setTotalBiaya(double totalBiaya) {
        this.totalBiaya = totalBiaya;
    }
}
