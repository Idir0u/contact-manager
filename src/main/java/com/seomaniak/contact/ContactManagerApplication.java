package com.seomaniak.contact;

import com.seomaniak.contact.model.entity.Contact;
import com.seomaniak.contact.repository.ContactRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
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
                Contact.builder()
                    .firstName("Sarah").lastName("Dubois")
                    .email("sarah.d@gmail.com").phone("+212612345678")
                    .company("SEOMANIAK").jobTitle("Directrice Marketing")
                    .address("45 Avenue Hassan II").city("Casablanca")
                    .postalCode("20000").country("Maroc")
                    .birthday(LocalDate.of(1990, 5, 15))
                    .website("https://www.seomaniak.ma")
                    .notes("Contact principal pour les projets digitaux")
                    .createdAt(LocalDateTime.now()).build(),
                    
                Contact.builder()
                    .firstName("Mohamed").lastName("Benali")
                    .email("m.benali@outlook.com").phone("+212700112233")
                    .company("TechSolutions").jobTitle("Développeur Full Stack")
                    .address("12 Rue de la Liberté").city("Rabat")
                    .postalCode("10000").country("Maroc")
                    .birthday(LocalDate.of(1988, 3, 22))
                    .website("https://github.com/mbenali")
                    .createdAt(LocalDateTime.now()).build(),
                    
                Contact.builder()
                    .firstName("Amina").lastName("El Amrani")
                    .email("amina92@yahoo.com").phone("+212699887766")
                    .company("Freelance").jobTitle("Designer UX/UI")
                    .address("78 Boulevard Zerktouni").city("Marrakech")
                    .postalCode("40000").country("Maroc")
                    .birthday(LocalDate.of(1992, 11, 8))
                    .notes("Spécialiste en design d'interfaces mobiles")
                    .createdAt(LocalDateTime.now()).build(),
                    
                Contact.builder()
                    .firstName("Karim").lastName("Fassi")
                    .email("karim.fassi@gmail.com").phone("+212655443322")
                    .company("DataCorp").jobTitle("Data Analyst")
                    .address("23 Rue Moulay Ismail").city("Fès")
                    .postalCode("30000").country("Maroc")
                    .birthday(LocalDate.of(1985, 7, 12))
                    .createdAt(LocalDateTime.now()).build(),
                    
                Contact.builder()
                    .firstName("Leila").lastName("Rahmani")
                    .email("leila.r@protonmail.com").phone("+212677889900")
                    .company("CloudTech").jobTitle("Chef de Projet")
                    .address("56 Avenue Mohammed V").city("Tanger")
                    .postalCode("90000").country("Maroc")
                    .birthday(LocalDate.of(1987, 9, 25))
                    .website("https://leilarahmani.com")
                    .notes("Expert en gestion de projets agiles")
                    .createdAt(LocalDateTime.now()).build(),
                    
                Contact.builder()
                    .firstName("Youssef").lastName("Tazi")
                    .email("y.tazi@hotmail.com").phone("+212611223344")
                    .company("StartupHub").jobTitle("CEO")
                    .isDeleted(true)
                    .createdAt(LocalDateTime.now()).build(),
                    
                Contact.builder()
                    .firstName("Nadia").lastName("Cherkaoui")
                    .email("nadia.cherkaoui@gmail.com").phone("+212633445566")
                    .company("MediaGroup").jobTitle("Content Manager")
                    .address("34 Rue des FAR").city("Agadir")
                    .postalCode("80000").country("Maroc")
                    .createdAt(LocalDateTime.now()).build(),
                    
                Contact.builder()
                    .firstName("Omar").lastName("Zaki")
                    .email("o.zaki@seomaniak.ma").phone("+212688776655")
                    .company("SEOMANIAK").jobTitle("CTO")
                    .address("45 Avenue Hassan II").city("Casablanca")
                    .postalCode("20000").country("Maroc")
                    .birthday(LocalDate.of(1983, 1, 10))
                    .website("https://www.seomaniak.ma")
                    .notes("Co-fondateur et responsable technique")
                    .createdAt(LocalDateTime.now()).build()
            ));
            System.out.println("✅ 8 contacts insérés avec informations complètes (seed data)");
        } else {
            System.out.println("ℹ️ Seed ignoré - La base contient déjà " + repository.count() + " contact(s)");
        }
    }
}
