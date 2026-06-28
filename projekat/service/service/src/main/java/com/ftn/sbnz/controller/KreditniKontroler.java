package com.ftn.sbnz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ftn.sbnz.service.KreditniServis;
import com.ftn.sbnz.model.KreditniZahtev;

@Controller
public class KreditniKontroler {

    @Autowired
    private KreditniServis kreditniServis;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analiza")
    public String pokreniAnalizu(
            @RequestParam String ime,
            @RequestParam int starost,
            @RequestParam double prihod,
            @RequestParam double obaveze,
            @RequestParam double iznosKredita, // Novo polje uhvaćeno sa forme
            @RequestParam String tipKredita,
            @RequestParam int rok,
            @RequestParam int kasnjenjeDo30,
            @RequestParam int kasnjenje30Do90,
            @RequestParam int kasnjenjePreko90,
            @RequestParam String prinudnaNaplata,
            @RequestParam String stecajReprogram,
            @RequestParam int odbijeniZadnjih6Mes,
            @RequestParam int odbijeniRanije,
            @RequestParam int urednoGodina,
            @RequestParam(defaultValue = "false") boolean kolateral,
            Model model) {
        
        KreditniZahtev obradjenZahtev = kreditniServis.pokreniAnalizuSaForme(
            ime, starost, prihod, obaveze, iznosKredita, tipKredita, rok, kasnjenjeDo30, kasnjenje30Do90,
            kasnjenjePreko90, prinudnaNaplata, stecajReprogram, odbijeniZadnjih6Mes, odbijeniRanije, urednoGodina, kolateral
        );
        
        model.addAttribute("poruka", "Analiza uspešno završena za klijenta: " + ime);
        model.addAttribute("status", obradjenZahtev.getStatus());
        model.addAttribute("razred", obradjenZahtev.getRazred());
        model.addAttribute("obrazlozenje", obradjenZahtev.getObrazlozenje());
        
        return "index";
    }
}