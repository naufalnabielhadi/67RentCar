package com.rentcar.model;

public abstract class User {
    private String idUser;
    private String username;
    private String email;
    private String password;
    private String role;
    private String telepon;
    private String fotoProfil;
    private String statusAkun = "AKTIF";

    protected User() {
    }

    protected User(String idUser, String username, String email, String password, String role) {
        this.idUser = idUser;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public boolean login(String email, String password) {
        return this.email != null && this.email.equals(email)
                && this.password != null && this.password.equals(password);
    }

    public void logout() {
        // Session web dihapus dari Servlet.
    }

    public abstract String laman();

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getFotoProfil() {
        return fotoProfil;
    }

    public void setFotoProfil(String fotoProfil) {
        this.fotoProfil = fotoProfil;
    }

    public String getStatusAkun() {
        return statusAkun;
    }

    public void setStatusAkun(String statusAkun) {
        this.statusAkun = statusAkun;
    }

    public boolean isAktif() {
        return statusAkun == null || "AKTIF".equals(statusAkun);
    }
}
