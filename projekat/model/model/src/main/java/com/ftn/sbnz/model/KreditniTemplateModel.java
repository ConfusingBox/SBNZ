package com.ftn.sbnz.model;

public class KreditniTemplateModel {
    private String tipKreditaStr;
    private double minPrihod;
    private int maxRok;
    private boolean kolateralObavezan;
    private String minRazred;
    private String porukaOdbijen;

    public KreditniTemplateModel(String tipKreditaStr, double minPrihod, int maxRok, boolean kolateralObavezan, String minRazred, String porukaOdbijen) {
        this.tipKreditaStr = tipKreditaStr;
        this.minPrihod = minPrihod;
        this.maxRok = maxRok;
        this.kolateralObavezan = kolateralObavezan;
        this.minRazred = minRazred;
        this.porukaOdbijen = porukaOdbijen;
    }

    // Getteri koje Drools poziva da zameni @{polje} unutar .drt fajla
    public String getTipKreditaStr() { return tipKreditaStr; }
    public double getMinPrihod() { return minPrihod; }
    public int getMaxRok() { return maxRok; }
    public boolean isKolateralObavezan() { return kolateralObavezan; }
    public String getMinRazred() { return minRazred; }
    public String getPorukaOdbijen() { return porukaOdbijen; }
}