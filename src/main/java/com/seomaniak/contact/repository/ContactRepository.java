package com.seomaniak.contact.repository;

import com.seomaniak.contact.model.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT c FROM Contact c WHERE c.isDeleted = false " +
           "AND (:search IS NULL OR :search = '' OR " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Contact> findAllActiveWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.id = :id AND c.isDeleted = false")
    java.util.Optional<Contact> findByIdAndNotDeleted(@Param("id") Long id);
}
