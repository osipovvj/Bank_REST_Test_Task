package com.github.osipovvj.bank_rest_test_task.entity;

import com.github.osipovvj.bank_rest_test_task.enums.CardStatus;
import com.github.osipovvj.bank_rest_test_task.util.YearMonthConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "cards")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Card {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String cardNumber;

    @Transient
    private String maskedCardNumber;

    @Column(nullable = false)
    @Convert(converter = YearMonthConverter.class)
    private YearMonth issueDate;

    @Column(nullable = false)
    @Convert(converter = YearMonthConverter.class)
    private YearMonth expirationDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(nullable = false)
    private BigDecimal balance;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
