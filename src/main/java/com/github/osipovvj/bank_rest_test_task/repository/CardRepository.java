package com.github.osipovvj.bank_rest_test_task.repository;

import com.github.osipovvj.bank_rest_test_task.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
    boolean existsByCardNumber(String cardNumber);
    Optional<Card> findByIdAndOwnerId(UUID id, UUID ownerId);
    Page<Card> findAllByOwnerId(UUID ownerId, Specification<Card> spec, Pageable pageable);
}
