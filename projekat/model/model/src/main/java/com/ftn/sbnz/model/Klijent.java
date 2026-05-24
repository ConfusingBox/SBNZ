package com.ftn.sbnz.model;

public class Klijent {

    private int starost;
    private double mesecniPrihod;
    private double mesecneObaveze;

    public Klijent(int starost, double mesecniPrihod, double mesecneObaveze) {
        this.starost = starost;
        this.mesecniPrihod = mesecniPrihod;
        this.mesecneObaveze = mesecneObaveze;
    }
    
    public int getStarost() {
        return starost;
    }
    public void setStarost(int starost) {
        this.starost = starost;
    }
    public double getMesecniPrihod() {
        return mesecniPrihod;
    }
    public void setMesecniPrihod(double mesecniPrihod) {
        this.mesecniPrihod = mesecniPrihod;
    }
    public double getMesecneObaveze() {
        return mesecneObaveze;
    }
    public void setMesecneObaveze(double mesecneObaveze) {
        this.mesecneObaveze = mesecneObaveze;
    }
    
}