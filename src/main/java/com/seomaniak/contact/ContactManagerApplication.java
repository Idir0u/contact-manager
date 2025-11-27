package com.seomaniak.contact;

import com.seomaniak.contact.model.entity.Contact;
import com.seomaniak.contact.repository.ContactRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class ContactManagerApplication implements CommandLineRunner {

    private final ContactRepository repository;

    public ContactManagerApplication(ContactRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ContactManagerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Seed seulement si la table est vide (fonctionne en local H2 ET Railway PostgreSQL)
        if (repository.count() == 0) {
            repository.saveAll(java.util.List.of(
                Contact.builder().firstName("Sarah").lastName("Dubois").email("sarah.d@gmail.com").phone("+212612345678").createdAt(LocalDateTime.now()).build(),
                Contact.builder().firstName("Mohamed").lastName("Benali").email("m.benali@outlook.com").phone("+212700112233").createdAt(LocalDateTime.now()).build(),
                Contact.builder().firstName("Amina").lastName("El Amrani").email("amina92@yahoo.com").phone("+212699887766").createdAt(LocalDateTime.now()).build(),
                Contact.builder().firstName("Karim").lastName("Fassi").email("karim.fassi@gmail.com").phone("+212655443322").createdAt(LocalDateTime.now()).build(),
                Contact.builder().firstName("Leila").lastName("Rahmani").email("leila.r@protonmail.com").phone("+212677889900").createdAt(LocalDateTime.now()).build(),
                Contact.builder().firstName("Youssef").lastName("Tazi").email("y.tazi@hotmail.com").phone("+212611223344").isDeleted(true).createdAt(LocalDateTime.now()).build(),
                Contact.builder().firstName("Nadia").lastName("Cherkaoui").email("nadia.cherkaoui@gmail.com").phone("+212633445566").createdAt(LocalDateTime.now()).build(),
                Contact.builder().firstName("Omar").lastName("Zaki").email("o.zaki@seomaniak.ma").phone("+212688776655").createdAt(LocalDateTime.now()).build()
            ));
            System.out.println("✅ 8 contacts insérés (seed data)");
        } else {
            System.out.println("ℹ️ Seed ignoré - La base contient déjà " + repository.count() + " contact(s)");
        }
    }
}
