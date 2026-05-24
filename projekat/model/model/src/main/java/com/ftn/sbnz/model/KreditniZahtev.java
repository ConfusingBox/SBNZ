package com.ftn.sbnz.model;

public class KreditniZahtev {
    private Klijent klijent;
    private double iznos;
    private String status = "UNOS"; // Pocetni status
    private double dti;
    private String razred;

    
    public KreditniZahtev(Klijent klijent, double iznos, String status, double dti, String razred) {
        this.klijent = klijent;
        this.iznos = iznos;
        this.status = status;
        this.dti = dti;
        this.razred = razred;
    }
    public Klijent getKlijent() {
        return klijent;
    }
    public void setKlijent(Klijent klijent) {
        this.klijent = klijent;
    }
    public double getIznos() {
        return iznos;
    }
    public void setIznos(double iznos) {
        this.iznos = iznos;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getDti() {
        return dti;
    }
    public void setDti(double dti) {
        this.dti = dti;
    }
    public String getRazred() {
        return razred;
    }
    public void setRazred(String razred) {
        this.razred = razred;
    }

    
}