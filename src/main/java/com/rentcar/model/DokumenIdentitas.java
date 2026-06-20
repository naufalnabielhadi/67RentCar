package com.rentcar.model;

import java.time.LocalDateTime;

public class DokumenIdentitas {
    private String idDokumen;
    private String idUser;
    private String namaFile;
    private String tipeFile;
    private LocalDateTime tanggalUpload;
    private StatusVerifikasi statusVerifikasi = StatusVerifikasi.MENUNGGU;

    public DokumenIdentitas() {
    }

    public DokumenIdentitas(String idDokumen, String idUser, String namaFile, String tipeFile,
                            LocalDateTime tanggalUpload, StatusVerifikasi statusVerifikasi) {
        this.idDokumen = idDokumen;
        this.idUser = idUser;
        this.namaFile = namaFile;
        this.tipeFile = tipeFile;
        this.tanggalUpload = tanggalUpload;
        setStatusVerifikasi(statusVerifikasi);
    }

    public String getIdDokumen() {
        return idDokumen;
    }

    public void setIdDokumen(String idDokumen) {
        this.idDokumen = idDokumen;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNamaFile() {
        return namaFile;
    }

    public String getNamaFilel() {
        return getNamaFile();
    }

    public void setNamaFile(String namaFile) {
        this.namaFile = namaFile;
    }

    public void namaFile(String namaFile) {
        setNamaFile(namaFile);
    }

    public String getTipeFile() {
        return tipeFile;
    }

    public void setTipeFile(String tipeFile) {
        this.tipeFile = tipeFile;
    }

    public void tipeFile(String tipeFile) {
        setTipeFile(tipeFile);
    }

    public LocalDateTime getTanggalUpload() {
        return tanggalUpload;
    }

    public void setTanggalUpload(LocalDateTime tanggalUpload) {
        this.tanggalUpload = tanggalUpload;
    }

    public void setTanggalUpload(String tanggal) {
        this.tanggalUpload = tanggal == null || tanggal.isBlank() ? null : LocalDateTime.parse(tanggal);
    }

    public StatusVerifikasi getStatusVerifikasi() {
        return statusVerifikasi;
    }

    public void setStatusVerifikasi(StatusVerifikasi statusVerifikasi) {
        this.statusVerifikasi = statusVerifikasi == null ? StatusVerifikasi.MENUNGGU : statusVerifikasi;
    }

    public void setStatusVerifikasi(String status) {
        this.statusVerifikasi = status == null || status.isBlank()
                ? StatusVerifikasi.MENUNGGU
                : StatusVerifikasi.valueOf(status.trim().toUpperCase());
    }
}
