package com.seomaniak.contact.controller;

import com.seomaniak.contact.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDuplicateEmail(DataIntegrityViolationException ex, RedirectAttributes redirectAttributes) {
        String message = "Cet email existe déjà. Veuillez utiliser une adresse email différente.";
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unique")) {
            redirectAttributes.addFlashAttribute("errorMessage", message);
            redirectAttributes.addFlashAttribute("errorType", "duplicate");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'enregistrement du contact.");
            redirectAttributes.addFlashAttribute("errorType", "error");
        }
        return "redirect:/contacts/new";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("error", "Une erreur est survenue");
        return "error/500";
    }
}
