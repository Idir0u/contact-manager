package com.seomaniak.contact.service;

import com.seomaniak.contact.model.entity.Contact;
import com.seomaniak.contact.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    private final ContactRepository contactRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public CsvService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Exporte tous les contacts en CSV (format Excel français avec point-virgule)
     */
    public String exportToCsv() {
        List<Contact> contacts = contactRepository.findByIsDeletedFalse();
        StringBuilder csv = new StringBuilder();
        
        // Header avec séparateur point-virgule pour Excel français
        csv.append("ID;Prénom;Nom;Email;Téléphone;Société;Poste;Adresse;Ville;Code postal;Pays;Date de naissance;Site web;Notes;Date de création;Date de modification\n");
        
        // Data
        for (Contact contact : contacts) {
            // Utiliser ="..." pour forcer Excel à traiter le téléphone comme texte
            String phone = contact.getPhone() != null ? "=\"" + contact.getPhone() + "\"" : "";
            String birthday = contact.getBirthday() != null ? contact.getBirthday().format(DATE_ONLY_FORMATTER) : "";
            
            csv.append(String.format("%d;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s\n",
                contact.getId(),
                escapeSpecialCharacters(contact.getFirstName()),
                escapeSpecialCharacters(contact.getLastName()),
                escapeSpecialCharacters(contact.getEmail()),
                phone,
                escapeSpecialCharacters(contact.getCompany()),
                escapeSpecialCharacters(contact.getJobTitle()),
                escapeSpecialCharacters(contact.getAddress()),
                escapeSpecialCharacters(contact.getCity()),
                escapeSpecialCharacters(contact.getPostalCode()),
                escapeSpecialCharacters(contact.getCountry()),
                birthday,
                escapeSpecialCharacters(contact.getWebsite()),
                escapeSpecialCharacters(contact.getNotes()),
                contact.getCreatedAt() != null ? contact.getCreatedAt().format(DATE_FORMATTER) : "",
                contact.getUpdatedAt() != null ? contact.getUpdatedAt().format(DATE_FORMATTER) : ""
            ));
        }
        
        return csv.toString();
    }

    /**
     * Importe des contacts depuis un fichier CSV
     */
    public int importFromCsv(MultipartFile file) throws IOException {
        List<Contact> contacts = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Parse CSV line
                String[] values = parseCsvLine(line);
                
                if (values.length >= 3) { // Au minimum: prénom, nom, email
                    LocalDate birthday = null;
                    if (values.length > 11 && !values[11].trim().isEmpty()) {
                        try {
                            birthday = LocalDate.parse(values[11].trim(), DATE_ONLY_FORMATTER);
                        } catch (DateTimeParseException e) {
                            // Ignorer les dates invalides
                        }
                    }
                    
                    Contact contact = Contact.builder()
                        .firstName(values[0].trim())
                        .lastName(values[1].trim())
                        .email(values[2].trim())
                        .phone(values.length > 3 && !values[3].trim().isEmpty() ? values[3].trim() : null)
                        .company(values.length > 4 && !values[4].trim().isEmpty() ? values[4].trim() : null)
                        .jobTitle(values.length > 5 && !values[5].trim().isEmpty() ? values[5].trim() : null)
                        .address(values.length > 6 && !values[6].trim().isEmpty() ? values[6].trim() : null)
                        .city(values.length > 7 && !values[7].trim().isEmpty() ? values[7].trim() : null)
                        .postalCode(values.length > 8 && !values[8].trim().isEmpty() ? values[8].trim() : null)
                        .country(values.length > 9 && !values[9].trim().isEmpty() ? values[9].trim() : null)
                        .birthday(birthday)
                        .website(values.length > 12 && !values[12].trim().isEmpty() ? values[12].trim() : null)
                        .notes(values.length > 13 && !values[13].trim().isEmpty() ? values[13].trim() : null)
                        .createdAt(LocalDateTime.now())
                        .isDeleted(false)
                        .build();
                    
                    contacts.add(contact);
                }
            }
        }
        
        // Sauvegarder tous les contacts
        contactRepository.saveAll(contacts);
        return contacts.size();
    }

    /**
     * Parse une ligne CSV en tenant compte des guillemets (supporte virgule ET point-virgule)
     */
    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        // Détecter le séparateur (point-virgule ou virgule)
        char separator = line.contains(";") ? ';' : ',';
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == separator && !inQuotes) {
                values.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        values.add(current.toString());
        return values.toArray(new String[0]);
    }

    /**
     * Échappe les caractères spéciaux pour CSV
     */
    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\"", "\"\"");
        return escapedData;
    }
}
