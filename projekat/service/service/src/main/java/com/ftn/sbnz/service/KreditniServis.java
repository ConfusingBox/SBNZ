package com.ftn.sbnz.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieBase;
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
import com.ftn.sbnz.model.KreditniRazred;

@Service
public class KreditniServis {

    @Autowired
    private KieContainer kieContainer;

    public void testirajMarka() {
        // 1. Definisanje podataka iz tabele iz specifikacije za šablone
        List<KreditniParametri> podaciIzTabele = new ArrayList<>();
        podaciIzTabele.add(new KreditniParametri("STAMBENI", 800.0, 0.45, 30, true, "B"));
        podaciIzTabele.add(new KreditniParametri("POTROSACKI", 400.0, 0.40, 7, false, "C"));
        podaciIzTabele.add(new KreditniParametri("POSLOVNI", 1500.0, 0.50, 15, false, "B"));

        // 2. Kompajliranje .drt šablona sa podacima u sirovi DRL string
        String drlIzTemplatea = "";
        try {
            String apsolutnaPutanja = "C:\\Users\\Leon\\Desktop\\Faks\\8 Semestar\\Sistemi bazirani na znanju\\projekat\\projekat\\kjar\\kjar\\src\\main\\resources\\rules\\kreditni_template.drt";
            java.nio.file.Path path = java.nio.file.Paths.get(apsolutnaPutanja);
            java.io.InputStream templateStream = java.nio.file.Files.newInputStream(path);

            ObjectDataCompiler compiler = new ObjectDataCompiler();
            drlIzTemplatea = compiler.compile(podaciIzTabele, templateStream);
            System.out.println(">> TEMPLATE: Uspešno generisana pravila iz šablona!");
        } catch (Exception e) {
            System.err.println("Greška prilikom učitavanja ili kompajliranja šablona: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. POUZDANO SPAJANJE: Uzimamo čistu sesiju iz tvog kontejnera
        KieSession kieSession = kieContainer.newKieSession();

        // Dinamički dodajemo kompajlirana pravila iz šablona preko standardnog KnowledgeBuilder-a
        try {
            org.drools.core.impl.KnowledgeBaseImpl kBase = (org.drools.core.impl.KnowledgeBaseImpl) kieSession.getKieBase();
            org.kie.internal.builder.KnowledgeBuilder kbuilder = org.kie.internal.builder.KnowledgeBuilderFactory.newKnowledgeBuilder();
            
            kbuilder.add(org.kie.internal.io.ResourceFactory.newReaderResource(new java.io.StringReader(drlIzTemplatea)), 
                         org.kie.api.io.ResourceType.DRL);
            
            if (kbuilder.hasErrors()) {
                System.err.println("Greška u sintaksi generisanog šablona: " + kbuilder.getErrors().toString());
            } else {
                // Ubacujemo pakete iz šablona direktno u postojeću bazu koja već drži kjar pravila
                kBase.addPackages(kbuilder.getKnowledgePackages());
                System.out.println(">> TEMPLATE: Pravila uspešno spojena sa kjar pravilima u radnoj sesiji!");
            }
        } catch (Exception e) {
            System.err.println("Greška prilikom dinamičkog spajanja šablona: " + e.getMessage());
        }

        // --- SIMULACIJA PODATAKA (Klijent koji ISPUNJAVA uslove šablona) ---
        Klijent marko = new Klijent(); 
        marko.setIme("Marko Markovic");
        marko.setSlobodanPrihod(0.4);
        marko.setStarost(34);
        
        marko.setMesecniPrihod(2500.0);       
        marko.setMesecneObaveze(300.0);
        marko.setKreditnaIstorija(KreditnaIstorija.ODLICNA); 
        marko.setIstorija(new ArrayList<>()); 
        
        KreditniZahtev zahtev = new KreditniZahtev(marko, 100000.0, TipKredita.STAMBENI, 20, true); 

        System.out.println("--- POKRETANJE PRAVOG CEP DROOLS-A + TEMPLATE ---");
        System.out.println("Početni status: " + zahtev.getStatus());

        List<KreditniDogadjaj> istorijaMarka = new ArrayList<>();
        istorijaMarka.add(new KreditniDogadjaj("STECAJ", LocalDateTime.now().minusMonths(1)));
        istorijaMarka.add(new KreditniDogadjaj("ODBIJEN_KREDIT", LocalDateTime.now().minusYears(1)));
        
        for(int i=0; i<kasnjenjeDo30; i++) 
            istorija.add(new KreditniDogadjaj("KASNJENJE_DO_30", LocalDateTime.now().minusMonths(1)));
        for(int i=0; i<kasnjenje30Do90; i++) 
            istorija.add(new KreditniDogadjaj("KASNJENJE_30_90", LocalDateTime.now().minusMonths(1)));
        for(int i=0; i<kasnjenjePreko90; i++) 
            istorija.add(new KreditniDogadjaj("KASNJENJE_PREKO_90", LocalDateTime.now().minusMonths(1)));

        // 4. Ubacivanje objekata u zajedničku radnu memoriju
        kieSession.insert(marko); 
        kieSession.insert(zahtev);

        for (KreditniDogadjaj dogadjaj : istorijaMarka) {
            kieSession.insert(dogadjaj);
        }

        // Pokretanje svih pravila
        int brojOkinutihPravila = kieSession.fireAllRules();
        kieSession.dispose();

        System.out.println("---------------------------------");
        System.out.println("Broj izvršenih pravila: " + brojOkinutihPravila);
        System.out.println("Krajnji status zahteva: " + zahtev.getStatus());
        System.out.println("Kreditni razred zahteva: " + zahtev.getRazred());
        System.out.println("Obrazloženje: " + zahtev.getObrazlozenje());
        System.out.println("---------------------------------");
    }

    public static class KreditniParametri {
        private String tipKredita;
        private double minPrihod;
        private double maxDti;
        private int maxRok;
        private boolean kolateralObavezan;
        private String minRazred;

        public KreditniParametri(String tipKredita, double minPrihod, double maxDti, int maxRok, boolean kolateralObavezan, String minRazred) {
            this.tipKredita = tipKredita;
            this.minPrihod = minPrihod;
            this.maxDti = maxDti;
            this.maxRok = maxRok;
            this.kolateralObavezan = kolateralObavezan;
            this.minRazred = minRazred;
        }

        public String getTipKredita() { return tipKredita; }
        public double getMinPrihod() { return minPrihod; }
        public double getMaxDti() { return maxDti; }
        public int getMaxRok() { return maxRok; }
        public boolean isKolateralObavezan() { return kolateralObavezan; }
        public String getMinRazred() { return minRazred; }
    }
}