package com.rentcar.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DetailBooking {
    private String idDetail;
    private String idBooking;
    private String idMobil;
    private LocalDate tanggalSewa;
    private LocalDate tanggalKembali;
    private double subtotal;

    public DetailBooking() {
    }

    public DetailBooking(String idDetail, String idBooking, String idMobil, LocalDate tanggalSewa,
                         LocalDate tanggalKembali, double subtotal) {
        this.idDetail = idDetail;
        this.idBooking = idBooking;
        this.idMobil = idMobil;
        this.tanggalSewa = tanggalSewa;
        this.tanggalKembali = tanggalKembali;
        this.subtotal = subtotal;
    }

    public long hitungSisaDurasi() {
        if (tanggalSewa == null || tanggalKembali == null) {
            return 0;
        }
        return Math.max(1, ChronoUnit.DAYS.between(tanggalSewa, tanggalKembali));
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(String idBooking) {
        this.idBooking = idBooking;
    }

    public String getIdMobil() {
        return idMobil;
    }

    public void setIdMobil(String idMobil) {
        this.idMobil = idMobil;
    }

    public LocalDate getTanggalSewa() {
        return tanggalSewa;
    }

    public void setTanggalSewa(LocalDate tanggalSewa) {
        this.tanggalSewa = tanggalSewa;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
