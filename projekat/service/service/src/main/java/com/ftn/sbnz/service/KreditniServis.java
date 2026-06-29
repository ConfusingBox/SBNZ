package com.ftn.sbnz.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.drools.template.ObjectDataCompiler; 
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.Klijent;
import com.ftn.sbnz.model.KreditniDogadjaj;
import com.ftn.sbnz.model.KreditniZahtev;
import com.ftn.sbnz.model.TipKredita;
import com.ftn.sbnz.model.Status;
import com.ftn.sbnz.model.KreditniTemplateModel;

@Service
public class KreditniServis {

    @Autowired
    private KieContainer kieContainer;

    public KreditniZahtev pokreniAnalizuSaForme(
            String ime, int starost, double prihod, double obaveze, double iznosKredita, String tipKreditaStr, int rok,
            int kasnjenjeDo30, int kasnjenje30Do90, int kasnjenjePreko90,
            String prinudnaNaplata, String stecajReprogram,
            int odbijeniZadnjih6Mes, int odbijeniRanije, int urednoGodina, boolean kolateralPostoji) {
        
        // 1. KOMPAJLIRANJE SABLONA PREMA SPECIFIKACIJI SA VEZBI
        String generisaniDrl = "";
        try {
            InputStream templateStream = getClass().getResourceAsStream("/rules/template/kreditni_tipovi.drt");
            
            List<KreditniTemplateModel> data = new ArrayList<>();
            // Mapiranje podataka iz tabele: Tip, Min Prihod, Max Rok, Kolateral Obavezan, Min Razred, Poruka
            data.add(new KreditniTemplateModel("STAMBENI", 800.0, 30, true, "B", "Odbijeno: Stambeni kredit zahteva minimalan prihod 800 EUR, maksimalan rok 30 godina, obezbedjen kolateral i minimalno razred B."));
            data.add(new KreditniTemplateModel("POTROŠAČKI", 400.0, 7, false, "C", "Odbijeno: Potrosacki kredit zahteva minimalan prihod 400 EUR, maksimalan rok 7 godina i minimalno razred C."));
            data.add(new KreditniTemplateModel("POSLOVNI", 1500.0, 15, false, "B", "Odbijeno: Poslovni kredit zahteva minimalan prihod 1500 EUR, maksimalan rok 15 godina i minimalno razred B."));
            
            ObjectDataCompiler converter = new ObjectDataCompiler(); 
            generisaniDrl = converter.compile(data, templateStream); 
            System.out.println("--- USPESNO GENERISAN TEMPLATE DRL ---");
        } catch (Exception e) {
            System.out.println("Greska pri generisanju sablona: " + e.getMessage());
        }

        // 2. KREIRANJE STANDARDNE SESIJE IZ KIE-CONTAINER-A (KJAR)
        KieSession kieSession = kieContainer.newKieSession();

        // 3. KREIRANJE I SETOVANJE STVARNIH PODATAKA ZA KJAR I ISTORIJU
        Klijent klijent = new Klijent(); 
        klijent.setIme(ime);
        klijent.setStarost(starost);
        klijent.setMesecniPrihod(prihod);
        klijent.setMesecneObaveze(obaveze);
        klijent.setSlobodanPrihod(0.0);
        
        TipKredita tip = TipKredita.valueOf(tipKreditaStr);
        KreditniZahtev zahtev = new KreditniZahtev(klijent, iznosKredita, tip, rok, kolateralPostoji); 
        zahtev.setStatus(Status.VALIDIRANO); 

        // Generisanje istorijskih dogadjaja za CEP
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

        // 4. UNOSENJE OBJEKATA U GLAVNU RADJU MEMORIJU DROOLS-A (KJAR)
        kieSession.insert(klijent);
        kieSession.insert(zahtev);
        for (KreditniDogadjaj dogadjaj : istorija) {
            kieSession.insert(dogadjaj);
        }

        System.out.println("--- OKIDANJE KJAR ACCUMULATE I KLASIFIKACIJE ---");
        int brojOkinutih = kieSession.fireAllRules();
        kieSession.dispose();

        // 5. PROPUSTAMO ZAHTEV KROZ SABLON (Nakon sto imamo proracunat razred iz KJAR-a)
        boolean sablonOdbio = false;
        String porukaOdbijanja = "";

        if (!generisaniDrl.isEmpty()) {
            KieHelper kieHelper = new KieHelper();
            kieHelper.addContent(generisaniDrl, ResourceType.DRL);
            KieSession templateSession = kieHelper.build().newKieSession();
            
            templateSession.insert(klijent);
            templateSession.insert(zahtev);
            templateSession.fireAllRules();
            templateSession.dispose();

            if (zahtev.getStatus() == Status.ODBIJEN) {
                sablonOdbio = true;
                porukaOdbijanja = zahtev.getObrazlozenje();
            }
        }

        // Dodatna proverka maksimalnog roka iz tabele na osnovu tipa kredita
        if (!sablonOdbio && zahtev.getStatus() != Status.ODBIJEN) {
            if (tipKreditaStr.equals("STAMBENI") && rok > 30) {
                sablonOdbio = true;
                porukaOdbijanja = "Odbijeno: Stambeni kredit zahteva maksimalan rok 30 godina.";
            } else if (tipKreditaStr.equals("POTROŠAČKI") && rok > 7) {
                sablonOdbio = true;
                porukaOdbijanja = "Odbijeno: Potrosacki kredit zahteva maksimalan rok 7 godina.";
            } else if (tipKreditaStr.equals("POSLOVNI") && rok > 15) {
                sablonOdbio = true;
                porukaOdbijanja = "Odbijeno: Poslovni kredit zahteva maksimalan rok 15 godina.";
            }
        }

        // 6. FINALNA EVALUACIJA ODLUKE
        if (sablonOdbio) {
            zahtev.setStatus(Status.ODBIJEN);
            zahtev.setObrazlozenje(porukaOdbijanja);
            System.out.println("KONACAN ISHOD: Zahtev je ODBIJEN zbog sablonskih restrikcija.");
        } else if (zahtev.getStatus() == Status.VALIDIRANO && zahtev.getRazred() != null) {
            zahtev.setStatus(Status.ODOBREN);
            zahtev.setObrazlozenje("Odobreno: Zahtev uspesno prosao analizu rizika i restrikcije tipa kredita.");
            System.out.println("KONACAN ISHOD: Zahtev je uspesno ODOBREN.");
        }

        System.out.println("Broj okinutih pravila za bodovanje i analizu: " + brojOkinutih);
        return zahtev;
    }
}