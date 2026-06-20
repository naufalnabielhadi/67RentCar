package com.rentcar.model;

public class Pelanggan extends User implements RiwayatPemesanan {
    public Pelanggan() {
        setRole("PELANGGAN");
    }

    public Pelanggan(String idUser, String username, String email, String password, String role) {
        super(idUser, username, email, password, role);
    }

    public String lihatDaftarMobil() {
        return "Pelanggan melihat daftar mobil";
    }

    public String buatBooking() {
        return "Pelanggan membuat booking mobil";
    }

    public String batalkanBooking() {
        return "Pelanggan membatalkan booking";
    }

    @Override
    public String getKartuIdentitas() {
        return super.getKartuIdentitas();
    }

    @Override
    public String lihatRiwayatPesanan() {
        return "Pelanggan melihat riwayat pesanan";
    }

    @Override
    public String laman() {
        return "/pelanggan/dashboard";
    }
}
