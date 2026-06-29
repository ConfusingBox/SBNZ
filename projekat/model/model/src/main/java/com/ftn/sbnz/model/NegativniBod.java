package com.ftn.sbnz.model;

public class NegativniBod {
    private Klijent klijent;
    private int bodovi;

    public NegativniBod() {}

    public NegativniBod(Klijent klijent, int bodovi) {
        this.klijent = klijent;
        this.bodovi = bodovi;
    }

    public Klijent getKlijent() { return klijent; }
    public void setKlijent(Klijent klijent) { this.klijent = klijent; }
    public int getBodovi() { return bodovi; }
    public void setBodovi(int bodovi) { this.bodovi = bodovi; }
}