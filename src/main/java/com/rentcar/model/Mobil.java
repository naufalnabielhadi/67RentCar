package com.rentcar.model;

public class Mobil {
    public static final String STATUS_TERSEDIA = "TERSEDIA";
    public static final String STATUS_TIDAK_TERSEDIA = "TIDAK_TERSEDIA";
    public static final String STATUS_DALAM_PERBAIKAN = STATUS_TIDAK_TERSEDIA;
    public static final String STATUS_DISEWA = "DISEWA";
    public static final String STATUS_SUDAH_DIKEMBALIKAN = "SUDAH_DIKEMBALIKAN";

    private String idMobil;
    private String merk;
    private String model;
    private String platNomor;
    private double hargaSewaPerHari;
    private boolean status;
    private int tahun;
    private String transmisi;
    private String bahanBakar;
    private String kapasitas;
    private String gambar;
    private String statusMobil = STATUS_TERSEDIA;

    public Mobil() {
    }

    public Mobil(String idMobil, String merk, String model, String platNomor, double hargaSewaPerHari,
                 boolean status, int tahun, String transmisi, String bahanBakar, String kapasitas, String gambar) {
        this.idMobil = idMobil;
        this.merk = merk;
        this.model = model;
        this.platNomor = platNomor;
        this.hargaSewaPerHari = hargaSewaPerHari;
        setStatus(status);
        this.tahun = tahun;
        this.transmisi = transmisi;
        this.bahanBakar = bahanBakar;
        this.kapasitas = kapasitas;
        this.gambar = gambar;
    }

    public Mobil(String idMobil, String merk, String model, String platNomor, double hargaSewaPerHari,
                 boolean status, int tahun, String transmisi, String bahanBakar, String kapasitas, String gambar,
                 String statusMobil) {
        this(idMobil, merk, model, platNomor, hargaSewaPerHari, status, tahun, transmisi, bahanBakar, kapasitas, gambar);
        setStatusMobil(statusMobil);
    }

    public String tampilDetailMobil() {
        return merk + " " + model + " - " + platNomor;
    }

    public boolean cekKetersediaan() {
        return STATUS_TERSEDIA.equals(statusMobil);
    }

    public String getIdMobil() {
        return idMobil;
    }

    public void setIdMobil(String idMobil) {
        this.idMobil = idMobil;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlatNomor() {
        return platNomor;
    }

    public void setPlatNomor(String platNomor) {
        this.platNomor = platNomor;
    }

    public double getHargaSewaPerHari() {
        return hargaSewaPerHari;
    }

    public void setHargaSewaPerHari(double hargaSewaPerHari) {
        this.hargaSewaPerHari = hargaSewaPerHari;
    }

    public boolean isStatus() {
        return STATUS_TERSEDIA.equals(statusMobil);
    }

    public StatusMobil getStatus() {
        return StatusMobil.valueOf(statusMobil);
    }

    public void setStatus(boolean status) {
        this.status = status;
        this.statusMobil = status ? STATUS_TERSEDIA : STATUS_TIDAK_TERSEDIA;
    }

    public void setStatus(StatusMobil status) {
        setStatusMobil(status == null ? null : status.name());
    }

    public String getStatusMobil() {
        return statusMobil;
    }

    public void setStatusMobil(String statusMobil) {
        String normalizedStatus = statusMobil == null ? "" : statusMobil.trim().toUpperCase();
        if ("DALAM_PERBAIKAN".equals(normalizedStatus)) {
            normalizedStatus = STATUS_TIDAK_TERSEDIA;
        }
        if (!STATUS_TIDAK_TERSEDIA.equals(normalizedStatus)
                && !STATUS_DISEWA.equals(normalizedStatus)
                && !STATUS_SUDAH_DIKEMBALIKAN.equals(normalizedStatus)) {
            normalizedStatus = STATUS_TERSEDIA;
        }
        this.statusMobil = normalizedStatus;
        this.status = STATUS_TERSEDIA.equals(normalizedStatus);
    }

    public String getStatusLabel() {
        if (STATUS_DISEWA.equals(statusMobil)) {
            return "Disewa";
        }
        if (STATUS_SUDAH_DIKEMBALIKAN.equals(statusMobil)) {
            return "Sudah Dikembalikan";
        }
        if (STATUS_TIDAK_TERSEDIA.equals(statusMobil)) {
            return "Tidak Tersedia";
        }
        return "Tersedia";
    }

    public String getStatusBadgeClass() {
        if (STATUS_DISEWA.equals(statusMobil)) {
            return "warning";
        }
        if (STATUS_SUDAH_DIKEMBALIKAN.equals(statusMobil)) {
            return "returned";
        }
        if (STATUS_TIDAK_TERSEDIA.equals(statusMobil)) {
            return "muted";
        }
        return "ok";
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public String getTransmisi() {
        return transmisi;
    }

    public void setTransmisi(String transmisi) {
        this.transmisi = transmisi;
    }

    public String getBahanBakar() {
        return bahanBakar;
    }

    public void setBahanBakar(String bahanBakar) {
        this.bahanBakar = bahanBakar;
    }

    public String getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(String kapasitas) {
        this.kapasitas = kapasitas;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getGambarPath() {
        String cleanPath = normalizeAssetPath(gambar);
        if (cleanPath == null || cleanPath.isBlank()) {
            return "img/default-car.svg";
        }
        if (cleanPath.contains("/")) {
            return cleanPath;
        }
        return "img/" + cleanPath;
    }

    private String normalizeAssetPath(String path) {
        if (path == null) {
            return null;
        }
        String cleanPath = path.trim().replace("\\", "/");
        while (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }
        if (cleanPath.startsWith("assets/")) {
            cleanPath = cleanPath.substring("assets/".length());
        }
        return cleanPath;
    }
}
