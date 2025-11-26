package com.seomaniak.contact.controller;

import com.seomaniak.contact.model.dto.ContactRequestDTO;
import com.seomaniak.contact.model.dto.ContactResponseDTO;
import com.seomaniak.contact.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService service;

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
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "contacts/form";
        }
        if (dto.getId() == null) {
            service.save(dto);
        } else {
            service.update(dto.getId(), dto);
        }
        return "redirect:/contacts";
    }

    // Suppression douce
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
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
}
