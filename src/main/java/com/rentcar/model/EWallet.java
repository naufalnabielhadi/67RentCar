package com.rentcar.model;

public class EWallet extends Pembayaran {
    private String kodeQR;
    private String provider;

    public String getKodeQR() {
        return kodeQR;
    }

    public void setKodeQR(String kodeQR) {
        this.kodeQR = kodeQR;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
