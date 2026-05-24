package com.ftn.sbnz.service;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.Klijent;
import com.ftn.sbnz.model.KreditniZahtev;

@Service
public class KreditniServis {

    @Autowired
    private KieContainer kieContainer;

    public void testirajMarka() {
        //otvaranje nove drools sesije
        KieSession kieSession = kieContainer.newKieSession();

        //inicijalizacija klijenta Marka i njegovog zahteva
        Klijent marko = new Klijent(34, 1200.0, 200.0);
        KreditniZahtev zahtev = new KreditniZahtev(marko, 8000.0, "UNOS", 0.0, null);

        System.out.println("--- POKRETANJE DROOLS PRAVILA ---");
        System.out.println("Pocetni status: " + zahtev.getStatus());

        //ubacivanje zahteva u sesiju
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