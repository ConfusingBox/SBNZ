package com.ftn.sbnz.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.Klijent;
import com.ftn.sbnz.model.KreditniDogadjaj;
import com.ftn.sbnz.model.KreditniZahtev;
import com.ftn.sbnz.model.TipKredita;
import com.ftn.sbnz.model.Status;

@Service
public class KreditniServis {

    @Autowired
    private KieContainer kieContainer;

    public KreditniZahtev pokreniAnalizuSaForme(
            String ime, int starost, double prihod, double obaveze, double iznosKredita, String tipKreditaStr, int rok,
            int kasnjenjeDo30, int kasnjenje30Do90, int kasnjenjePreko90,
            String prinudnaNaplata, String stecajReprogram,
            int odbijeniZadnjih6Mes, int odbijeniRanije, int urednoGodina, boolean kolateralPostoji) {
        
        KieSession kieSession = kieContainer.newKieSession();

        Klijent klijent = new Klijent(); 
        klijent.setIme(ime);
        klijent.setStarost(starost);
        klijent.setMesecniPrihod(prihod);
        klijent.setMesecneObaveze(obaveze);
        klijent.setSlobodanPrihod(0.0);
        
        TipKredita tip = TipKredita.valueOf(tipKreditaStr);
        KreditniZahtev zahtev = new KreditniZahtev(klijent, iznosKredita, tip, rok, kolateralPostoji); 
        zahtev.setStatus(Status.VALIDIRANO); 

        List<KreditniDogadjaj> istorija = new ArrayList<>();
        
        for(int i=0; i<kasnjenjeDo30; i++) 
            istorija.add(new KreditniDogadjaj("KASNJENJE_DO_30", LocalDateTime.now().minusMonths(1)));
        for(int i=0; i<kasnjenje30Do90; i++) 
            istorija.add(new KreditniDogadjaj("KASNJENJE_30_90", LocalDateTime.now().minusMonths(1)));
        for(int i=0; i<kasnjenjePreko90; i++) 
            istorija.add(new KreditniDogadjaj("KASNJENJE_PREKO_90", LocalDateTime.now().minusMonths(1)));

        if (prinudnaNaplata.equals("POSLEDNJIH_12_MESECI")) {
            istorija.add(new KreditniDogadjaj("PRINUDNA_NAPLATA", LocalDateTime.now().minusMonths(3)));
        } else if (prinudnaNaplata.equals("RANIJE")) {
            istorija.add(new KreditniDogadjaj("PRINUDNA_NAPLATA", LocalDateTime.now().minusMonths(15)));
        }

        if (stecajReprogram.equals("POSLEDNJIH_12_MESECI")) {
            istorija.add(new KreditniDogadjaj("STECAJ", LocalDateTime.now().minusMonths(4))); 
        } else if (stecajReprogram.equals("RANIJE")) {
            istorija.add(new KreditniDogadjaj("STECAJ", LocalDateTime.now().minusMonths(18)));
        }

        for(int i=0; i<odbijeniZadnjih6Mes; i++) 
            istorija.add(new KreditniDogadjaj("ODBIJEN_KREDIT", LocalDateTime.now().minusMonths(2))); 
        for(int i=0; i<odbijeniRanije; i++) 
            istorija.add(new KreditniDogadjaj("ODBIJEN_KREDIT", LocalDateTime.now().minusMonths(8)));

        if (urednoGodina >= 3) {
            istorija.add(new KreditniDogadjaj("UREDNO_IZMIRIVANJE", LocalDateTime.now().minusYears(4))); 
        }

        klijent.setIstorija(istorija);

        // Unošenje u radnu memoriju
        kieSession.insert(klijent);
        kieSession.insert(zahtev);
        for (KreditniDogadjaj dogadjaj : istorija) {
            kieSession.insert(dogadjaj);
        }

        System.out.println("--- OKIDANJE KJAR PRAVILA ---");
        int brojOkinutih = kieSession.fireAllRules();
        kieSession.dispose();

        System.out.println("Broj okinutih pravila za bodovanje i analizu: " + brojOkinutih);
        return zahtev;
    }
}