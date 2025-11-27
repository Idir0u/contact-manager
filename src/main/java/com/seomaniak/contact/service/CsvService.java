package com.seomaniak.contact.service;

import com.seomaniak.contact.model.entity.Contact;
import com.seomaniak.contact.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    private final ContactRepository contactRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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
        csv.append("ID;Prénom;Nom;Email;Téléphone;Date de création;Date de modification\n");
        
        // Data
        for (Contact contact : contacts) {
            // Utiliser ="..." pour forcer Excel à traiter le téléphone comme texte
            String phone = contact.getPhone() != null ? "=\"" + contact.getPhone() + "\"" : "";
            
            csv.append(String.format("%d;%s;%s;%s;%s;%s;%s\n",
                contact.getId(),
                escapeSpecialCharacters(contact.getFirstName()),
                escapeSpecialCharacters(contact.getLastName()),
                escapeSpecialCharacters(contact.getEmail()),
                phone,
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
                    Contact contact = Contact.builder()
                        .firstName(values[0].trim())
                        .lastName(values[1].trim())
                        .email(values[2].trim())
                        .phone(values.length > 3 && !values[3].trim().isEmpty() ? values[3].trim() : null)
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
