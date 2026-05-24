package com.ftn.sbnz.model;

import java.time.LocalDate;

public class KreditniDogadjaj {
    private String tipDogadjaja; // "STECAJ", "PRINUDNA_NAPLATA", itd.
    private LocalDate datum;
    
    // Konstruktor
    public KreditniDogadjaj(String tipDogadjaja, LocalDate datum) {
        this.tipDogadjaja = tipDogadjaja;
        this.datum = datum;
    }

    public String getTipDogadjaja() {
        return tipDogadjaja;
    }

    public void setTipDogadjaja(String tipDogadjaja) {
        this.tipDogadjaja = tipDogadjaja;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    
}