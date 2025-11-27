package com.seomaniak.contact.controller;

import com.seomaniak.contact.model.dto.ContactRequestDTO;
import com.seomaniak.contact.model.dto.ContactResponseDTO;
import com.seomaniak.contact.service.ContactService;
import com.seomaniak.contact.service.CsvService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService service;
    private final CsvService csvService;

    public ContactController(ContactService service, CsvService csvService) {
        this.service = service;
        this.csvService = csvService;
    }

    // Page principale avec recherche + pagination
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        var pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        var result = service.findAll(search, pageable);

        model.addAttribute("contacts", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("search", search);

        return "contacts/list";
    }

    // Formulaire création
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("contact", new ContactRequestDTO());
        return "contacts/form";
    }

    // Formulaire édition
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("contact", service.findById(id));
        return "contacts/form";
    }

    // Sauvegarde (création + édition)
    @PostMapping
    public String save(@Valid @ModelAttribute("contact") ContactRequestDTO dto,
                       BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Veuillez corriger les erreurs du formulaire.");
            model.addAttribute("errorType", "validation");
            return "contacts/form";
        }
        
        try {
            if (dto.getId() == null) {
                service.save(dto);
                redirectAttributes.addFlashAttribute("successMessage", "Contact créé avec succès !");
            } else {
                service.update(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("successMessage", "Contact mis à jour avec succès !");
            }
            return "redirect:/contacts";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Une erreur est survenue lors de l'enregistrement.");
            model.addAttribute("errorType", "error");
            return "contacts/form";
        }
    }

    // Suppression douce
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Contact supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du contact.");
            redirectAttributes.addFlashAttribute("errorType", "error");
        }
        return "redirect:/contacts";
    }

    // API REST (pour Postman/Swagger)
    @GetMapping("/api")
    @ResponseBody
    public Page<ContactResponseDTO> apiList(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String search) {
        return service.findAll(search, PageRequest.of(page, size));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ContactResponseDTO apiGet(@PathVariable Long id) {
        return service.findById(id);
    }

    // ==================== CSV Export/Import ====================

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        String csv = csvService.exportToCsv();
        
        // Ajouter BOM UTF-8 pour Excel (reconnaissance automatique UTF-8)
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[bom.length + csvBytes.length];
        System.arraycopy(bom, 0, bytes, 0, bom.length);
        System.arraycopy(csvBytes, 0, bytes, bom.length, csvBytes.length);
        
        HttpHeaders headers = new HttpHeaders();
        // Utiliser text/csv avec charset UTF-8 explicite
        headers.set("Content-Type", "text/csv; charset=UTF-8");
        headers.setContentDispositionFormData("attachment", "contacts.csv");
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }

    @PostMapping("/import")
    public String importCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Veuillez sélectionner un fichier CSV");
                return "redirect:/contacts";
            }
            
            if (!file.getOriginalFilename().endsWith(".csv")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Le fichier doit être au format CSV");
                return "redirect:/contacts";
            }
            
            int count = csvService.importFromCsv(file);
            redirectAttributes.addFlashAttribute("successMessage", count + " contact(s) importé(s) avec succès !");
            
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'import: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'import des données");
        }
        
        return "redirect:/contacts";
    }
}
