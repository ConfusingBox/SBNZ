package com.ftn.sbnz.model;


public class KreditniZahtev {
    
    private Klijent klijent;
    private double iznos;
    private int rokOtplate;
    private TipKredita tipKredita;
    private Status status = Status.UNOS;
    private KreditniRazred razred;
    private double dti;
    private double ltv;
    private String obrazlozenje;
    private boolean kolateralPostoji;

    public KreditniZahtev(Klijent klijent, double iznos, TipKredita tipKredita, int rokOtplate, boolean kolateralPostoji) {
        this.klijent = klijent;
        this.iznos = iznos;
        this.tipKredita = tipKredita;
        this.rokOtplate = rokOtplate;
        this.kolateralPostoji = kolateralPostoji;
        this.status = Status.UNOS;
    }
    public KreditniZahtev(Klijent klijent, double iznos, TipKredita tipKredita) {
    this.klijent = klijent;
    this.iznos = iznos;
    this.tipKredita = tipKredita;
    this.status = Status.UNOS; //pocetni status
}

    public KreditniZahtev() {
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

    public int getRokOtplate() {
        return rokOtplate;
    }

    public void setRokOtplate(int rokOtplate) {
        this.rokOtplate = rokOtplate;
    }

    public TipKredita getTipKredita() {
        return tipKredita;
    }

    public void setTipKredita(TipKredita tipKredita) {
        this.tipKredita = tipKredita;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public KreditniRazred getRazred() {
        return razred;
    }

    public void setRazred(KreditniRazred razred) {
        this.razred = razred;
    }

    public double getDti() {
        return dti;
    }

    public void setDti(double dti) {
        this.dti = dti;
    }

    public double getLtv() {
        return ltv;
    }

    public void setLtv(double ltv) {
        this.ltv = ltv;
    }

    public String getObrazlozenje() {
        return obrazlozenje;
    }

    public void setObrazlozenje(String obrazlozenje) {
        this.obrazlozenje = obrazlozenje;
    }

    public boolean isKolateralPostoji() {
        return kolateralPostoji;
    }

    public void setKolateralPostoji(boolean kolateralPostoji) {
        this.kolateralPostoji = kolateralPostoji;
    }
    
    
}