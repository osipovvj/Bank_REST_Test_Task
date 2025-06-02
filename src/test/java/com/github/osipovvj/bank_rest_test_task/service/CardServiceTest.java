package com.github.osipovvj.bank_rest_test_task.service;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.CardFilterRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.ChangeCardStatusRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.CreateCardRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetCardsResponse;
import com.github.osipovvj.bank_rest_test_task.entity.Card;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.enums.CardStatus;
import com.github.osipovvj.bank_rest_test_task.exception.InternalServerException;
import com.github.osipovvj.bank_rest_test_task.exception.ResourceNotFoundException;
import com.github.osipovvj.bank_rest_test_task.repository.CardRepository;
import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.service.impl.CardServiceImpl;
import com.github.osipovvj.bank_rest_test_task.util.Encryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Encryptor encryptor;

    @InjectMocks
    private CardServiceImpl cardService;

    private UUID cardId;
    private UUID userId;
    private Card testCard;
    private User testUser;
    private String encryptedNumber;
    private String decryptedNumber;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();
        encryptedNumber = "encrypted_number";
        decryptedNumber = "1234567890123456";

        testUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        testCard = Card.builder()
                .id(cardId)
                .cardNumber(encryptedNumber)
                .owner(testUser)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .issueDate(YearMonth.now())
                .expirationDate(YearMonth.now().plusYears(4))
                .build();
    }

    @Test
    void getCards_WithAllFilters_ShouldReturnFilteredCards() throws Exception {
        CardFilterRequest filter = new CardFilterRequest(
                userId,
                CardStatus.ACTIVE,
                YearMonth.now().minusMonths(1),
                YearMonth.now(),
                true,
                true,
                0,
                10
        );

        Page<Card> page = new PageImpl<>(List.of(testCard));
        when(cardRepository.findAll(Mockito.<Specification<Card>>any(), any(Pageable.class))).thenReturn(page);
        when(encryptor.decrypt(encryptedNumber)).thenReturn(decryptedNumber);

        GetCardsResponse response = cardService.getCards(filter);

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.cards()).hasSize(1);
        assertThat(response.cards().get(0).status()).isEqualTo(CardStatus.ACTIVE);
        verify(cardRepository).findAll(Mockito.<Specification<Card>>any(), any(Pageable.class));
    }

    @Test
    void getCards_WithNoFilters_ShouldReturnAllCards() throws Exception {
        CardFilterRequest filter = new CardFilterRequest(
                null, null, null, null, null, null, null, null
        );

        Page<Card> page = new PageImpl<>(List.of(testCard));
        when(cardRepository.findAll(Mockito.<Specification<Card>>any(), any(Pageable.class))).thenReturn(page);
        when(encryptor.decrypt(encryptedNumber)).thenReturn(decryptedNumber);

        GetCardsResponse response = cardService.getCards(filter);

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.page()).isZero();
        verify(cardRepository).findAll(Mockito.<Specification<Card>>any(), any(Pageable.class));
    }

    @Test
    void createCard_Success() throws Exception {
        CreateCardRequest request = new CreateCardRequest(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cardRepository.existsByCardNumber(any())).thenReturn(false);
        when(encryptor.encrypt(any())).thenReturn(encryptedNumber);
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        CardDto result = cardService.createCard(request);

        assertThat(result.id()).isEqualTo(cardId);
        assertThat(result.status()).isEqualTo(CardStatus.ACTIVE);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_UserNotFound_ShouldThrowException() {
        CreateCardRequest request = new CreateCardRequest(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.createCard(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    @Test
    void getCardById_Success() throws Exception {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
        when(encryptor.decrypt(encryptedNumber)).thenReturn(decryptedNumber);

        CardDto result = cardService.getCardById(cardId);

        assertThat(result.id()).isEqualTo(cardId);
        assertThat(result.maskedNumber()).startsWith("****");
        verify(cardRepository).findById(cardId);
    }

    @Test
    void updateCard_Success() throws Exception {
        ChangeCardStatusRequest request = new ChangeCardStatusRequest(CardStatus.BLOCKED);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
        when(encryptor.decrypt(encryptedNumber)).thenReturn(decryptedNumber);

        CardDto result = cardService.updateCard(cardId, request);

        assertThat(result.status()).isEqualTo(CardStatus.BLOCKED);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void deleteCard_Success() {
        when(cardRepository.existsById(cardId)).thenReturn(true);

        cardService.deleteCard(cardId);

        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void getCards_WhenDecryptionFails_ShouldThrowException() throws Exception {
        CardFilterRequest filter = new CardFilterRequest(
                null, null, null, null, null, null, 0, 10
        );
        Page<Card> page = new PageImpl<>(List.of(testCard));
        when(cardRepository.findAll(Mockito.<Specification<Card>>any(), any(Pageable.class))).thenReturn(page);
        when(encryptor.decrypt(any())).thenThrow(new Exception("Decryption failed"));

        assertThatThrownBy(() -> cardService.getCards(filter))
                .isInstanceOf(InternalServerException.class)
                .hasMessageContaining("Ошибка при расшифровке номера карты");
    }

    @Test
    void getCards_WithExpiringCards_ShouldReturnOnlyExpiringCards() throws Exception {
        CardFilterRequest filter = new CardFilterRequest(
                null, null, null, null, true, null, 0, 10
        );
        Card expiringCard = Card.builder()
                .id(testCard.getId())
                .cardNumber(testCard.getCardNumber())
                .owner(testCard.getOwner())
                .status(testCard.getStatus())
                .balance(testCard.getBalance())
                .issueDate(testCard.getIssueDate())
                .expirationDate(YearMonth.now().plusMonths(2))
                .build();
        Page<Card> page = new PageImpl<>(List.of(expiringCard));
        when(cardRepository.findAll(Mockito.<Specification<Card>>any(), any(Pageable.class))).thenReturn(page);
        when(encryptor.decrypt(encryptedNumber)).thenReturn(decryptedNumber);

        GetCardsResponse response = cardService.getCards(filter);

        assertThat(response.total()).isEqualTo(1);
        verify(cardRepository).findAll(Mockito.<Specification<Card>>any(), any(Pageable.class));
    }
}
