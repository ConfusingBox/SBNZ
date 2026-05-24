package com.ftn.sbnz.service;

import java.util.ArrayList;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.Klijent;
import com.ftn.sbnz.model.KreditnaIstorija;
import com.ftn.sbnz.model.KreditniZahtev;
import com.ftn.sbnz.model.TipKredita;

@Service
public class KreditniServis {

    @Autowired
    private KieContainer kieContainer;

    public void testirajMarka() {
        //otvaranje nove drools sesije
        KieSession kieSession = kieContainer.newKieSession();

        //inicijalizacija klijenta Marka i njegovog zahteva
        // Kreiraj klijenta
        Klijent marko = new Klijent(); 
        marko.setIme("Marko Markovic");
        marko.setSlobodanPrihod(0.4);
        marko.setStarost(34);
        marko.setMesecniPrihod(1200.0);
        marko.setMesecneObaveze(200.0);
        marko.setKreditnaIstorija(KreditnaIstorija.ODLICNA); // Ili ODLICNA, DOBRA...
        marko.setIstorija(new ArrayList<>()); // Prazna lista za početak
        KreditniZahtev zahtev = new KreditniZahtev(marko, 10000.0, TipKredita.STAMBENI); // Ovo radi
        System.out.println("--- POKRETANJE DROOLS PRAVILA ---");
        System.out.println("Pocetni status: " + zahtev.getStatus());

        //ubacivanje zahteva u sesiju
        System.out.println("DEBUG: Istorija klijenta: " + marko.getKreditnaIstorija());
        System.out.println("DEBUG: Slobodan prihod: " + marko.getSlobodanPrihod());
        kieSession.insert(zahtev);

        //okidanje svih pravila tj forward chaining
        int brojOkinutihPravila = kieSession.fireAllRules();
        
        //zatvaranje sesije
        kieSession.dispose();

        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        System.out.println("Broj izvrsenih pravila: " + brojOkinutihPravila);
        System.out.println("Krajnji status zahteva: " + zahtev.getStatus());
        System.out.println("Kalkulisani DTI: " + zahtev.getDti());
        System.out.println("Dodeljeni razred: " + zahtev.getRazred());
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
    }
}