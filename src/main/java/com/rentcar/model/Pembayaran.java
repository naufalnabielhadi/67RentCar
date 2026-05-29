package com.rentcar.model;

import java.time.LocalDate;

public class Pembayaran {
    private String idPembayaran;
    private String idBooking;
    private double jumlah;
    private String status;
    private String metodePembayaran;
    private LocalDate tanggalPembayaran;

    public Pembayaran() {
    }

    public Pembayaran(String idPembayaran, String idBooking, double jumlah, String status,
                      String metodePembayaran, LocalDate tanggalPembayaran) {
        this.idPembayaran = idPembayaran;
        this.idBooking = idBooking;
        this.jumlah = jumlah;
        this.status = status;
        this.metodePembayaran = metodePembayaran;
        this.tanggalPembayaran = tanggalPembayaran;
    }

    public void bayar() {
        this.status = "LUNAS";
        this.tanggalPembayaran = LocalDate.now();
    }

    public String getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(String idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public String getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(String idBooking) {
        this.idBooking = idBooking;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public LocalDate getTanggalPembayaran() {
        return tanggalPembayaran;
    }

    public void setTanggalPembayaran(LocalDate tanggalPembayaran) {
        this.tanggalPembayaran = tanggalPembayaran;
    }
}
