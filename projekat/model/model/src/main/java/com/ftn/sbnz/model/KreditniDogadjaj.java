package com.ftn.sbnz.model;

import java.time.LocalDateTime; // Promenjeno sa LocalDate

public class KreditniDogadjaj {

    private String tipDogadjaja;
    private LocalDateTime datum; // Promenjeno na LocalDateTime

    public KreditniDogadjaj() {
    }

    public KreditniDogadjaj(String tipDogadjaja, LocalDateTime datum) {
        this.tipDogadjaja = tipDogadjaja;
        this.datum = datum;
    }

    public String getTipDogadjaja() {
        return tipDogadjaja;
    }

    public void setTipDogadjaja(String tipDogadjaja) {
        this.tipDogadjaja = tipDogadjaja;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }
}