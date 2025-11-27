package com.seomaniak.contact.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequestDTO {

    private Long id;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    private String email;

    @Pattern(regexp = "^(\\+212[0-9]{9})?$", message = "Le téléphone doit être au format +212XXXXXXXXX")
    private String phone;

    // Nouveaux champs
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String address;

    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    private String city;

    @Pattern(regexp = "^[0-9]{0,10}$", message = "Le code postal doit contenir uniquement des chiffres")
    private String postalCode;

    @Size(max = 100, message = "Le pays ne peut pas dépasser 100 caractères")
    private String country;

    @Size(max = 100, message = "La société ne peut pas dépasser 100 caractères")
    private String company;

    @Size(max = 100, message = "Le poste ne peut pas dépasser 100 caractères")
    private String jobTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;

    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$|^$", message = "Le site web doit être une URL valide")
    @Size(max = 255, message = "Le site web ne peut pas dépasser 255 caractères")
    private String website;
}
