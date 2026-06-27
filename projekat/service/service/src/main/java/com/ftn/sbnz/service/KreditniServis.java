package com.ftn.sbnz.service;

import java.time.LocalDateTime; // Promenjeno na LocalDateTime
import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.Klijent;
import com.ftn.sbnz.model.KreditnaIstorija;
import com.ftn.sbnz.model.KreditniDogadjaj;
import com.ftn.sbnz.model.KreditniZahtev;
import com.ftn.sbnz.model.TipKredita;

@Service
public class KreditniServis {

    @Autowired
    private KieContainer kieContainer;

    public void testirajMarka() {
        KieSession kieSession = kieContainer.newKieSession();

        Klijent marko = new Klijent(); 
        marko.setIme("Marko Markovic");
        marko.setSlobodanPrihod(0.4);
        marko.setStarost(34);
        marko.setMesecniPrihod(1200.0);
        marko.setMesecneObaveze(200.0);
        marko.setKreditnaIstorija(KreditnaIstorija.ODLICNA); 
        marko.setIstorija(new ArrayList<>()); 
        
        KreditniZahtev zahtev = new KreditniZahtev(marko, 10000.0, TipKredita.STAMBENI); 
        
        System.out.println("--- POKRETANJE PRAVOG CEP DROOLS-A ---");

        List<KreditniDogadjaj> istorijaMarka = new ArrayList<>();
        // Koristimo LocalDateTime.now()
        istorijaMarka.add(new KreditniDogadjaj("STECAJ", LocalDateTime.now().minusMonths(1)));
        istorijaMarka.add(new KreditniDogadjaj("ODBIJEN_KREDIT", LocalDateTime.now().minusYears(1)));
        
        marko.setIstorija(istorijaMarka);

        // Ubacujemo klijenta i zahtev
        kieSession.insert(marko); 
        kieSession.insert(zahtev);

        // KLJUČNO ZA CEP: Ispaljujemo događaje pojedinačno u radnu memoriju (strim)
        for (KreditniDogadjaj dogadjaj : istorijaMarka) {
            kieSession.insert(dogadjaj);
        }

        int brojOkinutihPravila = kieSession.fireAllRules();
        kieSession.dispose();

        System.out.println("---------------------------------");
        System.out.println("Broj izvrsenih pravila: " + brojOkinutihPravila);
        System.out.println("Krajnji status zahteva: " + zahtev.getStatus());
        System.out.println("---------------------------------");
    }
}