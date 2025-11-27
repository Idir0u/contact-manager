package com.seomaniak.contact.service;

import com.seomaniak.contact.exception.ResourceNotFoundException;
import com.seomaniak.contact.model.dto.ContactRequestDTO;
import com.seomaniak.contact.model.dto.ContactResponseDTO;
import com.seomaniak.contact.model.entity.Contact;
import com.seomaniak.contact.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository repository;

    @Transactional(readOnly = true)
    public Page<ContactResponseDTO> findAll(String search, Pageable pageable) {
        return repository.findAllActiveWithSearch(search, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ContactResponseDTO findById(Long id) {
        Contact contact = repository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact non trouvé avec l'ID : " + id));
        return toResponseDTO(contact);
    }

    @Transactional
    public ContactResponseDTO save(ContactRequestDTO dto) {
        Contact contact = Contact.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .isDeleted(false)
                .build();

        Contact saved = repository.save(contact);
        return toResponseDTO(saved);
    }

    @Transactional
    public ContactResponseDTO update(Long id, ContactRequestDTO dto) {
        Contact contact = repository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact non trouvé avec l'ID : " + id));

        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());

        Contact updated = repository.save(contact);
        return toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        Contact contact = repository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact non trouvé avec l'ID : " + id));

        contact.setIsDeleted(true);
        repository.save(contact);
    }

    private ContactResponseDTO toResponseDTO(Contact contact) {
        return ContactResponseDTO.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .isDeleted(contact.getIsDeleted())
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }
} 
