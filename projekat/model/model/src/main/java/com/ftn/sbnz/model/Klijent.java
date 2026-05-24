package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Klijent {
    private String ime;
    private int starost;
    private double mesecniPrihod;
    private double mesecneObaveze;
    private double slobodanPrihod; // Procentualna vrednost (0.0 do 1.0)
    private KreditnaIstorija kreditnaIstorija;
    private List<KreditniDogadjaj> istorija = new ArrayList<>();

    // Metoda koju Drools koristi direktno u pravilu
    public boolean imaStecajIliNaplatuU12Meseci() {
        LocalDate pre12Meseci = LocalDate.now().minusMonths(12);
        for (KreditniDogadjaj d : istorija) {
            if ((d.getTipDogadjaja().equals("STECAJ") || d.getTipDogadjaja().equals("PRINUDNA_NAPLATA")) 
                 && d.getDatum().isAfter(pre12Meseci)) {
                return true;
            }
        }
        return false;
    }
    

    public Klijent(String ime, int starost, double mesecniPrihod, double mesecneObaveze, double slobodanPrihod,
            KreditnaIstorija kreditnaIstorija, List<KreditniDogadjaj> istorija) {
        this.ime = ime;
        this.starost = starost;
        this.mesecniPrihod = mesecniPrihod;
        this.mesecneObaveze = mesecneObaveze;
        this.slobodanPrihod = slobodanPrihod;
        this.kreditnaIstorija = kreditnaIstorija;
        this.istorija = istorija;
    }

    public Klijent() {}
    
    public String getIme() {
        return ime;
    }

    public int getStarost() {
        return starost;
    }

    public double getMesecniPrihod() {
        return mesecniPrihod;
    }

    public double getMesecneObaveze() {
        return mesecneObaveze;
    }

    public double getSlobodanPrihod() {
        return slobodanPrihod;
    }

    public KreditnaIstorija getKreditnaIstorija() {
        return kreditnaIstorija;
    }

    public List<KreditniDogadjaj> getIstorija() {
        return istorija;
    }


    public void setIme(String ime) {
        this.ime = ime;
    }


    public void setStarost(int starost) {
        this.starost = starost;
    }


    public void setMesecniPrihod(double mesecniPrihod) {
        this.mesecniPrihod = mesecniPrihod;
    }


    public void setMesecneObaveze(double mesecneObaveze) {
        this.mesecneObaveze = mesecneObaveze;
    }


    public void setSlobodanPrihod(double slobodanPrihod) {
        this.slobodanPrihod = slobodanPrihod;
    }


    public void setKreditnaIstorija(KreditnaIstorija kreditnaIstorija) {
        this.kreditnaIstorija = kreditnaIstorija;
    }


    public void setIstorija(List<KreditniDogadjaj> istorija) {
        this.istorija = istorija;
    }


    
}