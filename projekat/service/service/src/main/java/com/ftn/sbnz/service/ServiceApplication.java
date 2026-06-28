package com.ftn.sbnz.service;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication(scanBasePackages = "com.ftn.sbnz")
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Bean
    public KieContainer kieContainer() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks
            .newKieContainer(ks.newReleaseId("com.ftn.sbnz", "kjar", "0.0.1-SNAPSHOT"));
        KieScanner kScanner = ks.newKieScanner(kContainer);
        kScanner.start(1000);
        return kContainer;
    }

    // OBRISAN ILI ZAKOMENTARISAN COMMANDLINERUNNER JER NAM VIŠE NE TREBA HARDKODOVANI TEST
    /*
    @Bean
    public CommandLineRunner run(KreditniServis kreditniServis) {
        return args -> {
            // Ovo je bacalo grešku jer se metod u servisu sada zove drugačije
        };
    }
    */
}