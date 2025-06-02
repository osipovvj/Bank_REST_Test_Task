package com.github.osipovvj.bank_rest_test_task.service;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.CardFilterRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.TransferRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.BalanceResponse;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetCardsResponse;
import com.github.osipovvj.bank_rest_test_task.entity.Card;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.enums.CardStatus;
import com.github.osipovvj.bank_rest_test_task.exception.InsufficientFundsOnCardException;
import com.github.osipovvj.bank_rest_test_task.exception.ResourceNotFoundException;
import com.github.osipovvj.bank_rest_test_task.repository.CardRepository;
import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.service.impl.UserCardServiceImpl;
import com.github.osipovvj.bank_rest_test_task.util.Encryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class UserCardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardService cardService;

    @Mock
    private Encryptor encryptor;

    @InjectMocks
    private UserCardServiceImpl userCardService;

    private User testUser;
    private Card testCard;
    private String userEmail;
    private UUID cardId;
    private String encryptedNumber;
    private String decryptedNumber;

    @BeforeEach
    void setUp() {
        userEmail = "john@example.com";
        cardId = UUID.randomUUID();
        encryptedNumber = "encrypted_number";
        decryptedNumber = "1234567890123456";

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email(userEmail)
                .firstName("John")
                .lastName("Doe")
                .build();

        testCard = Card.builder()
                .id(cardId)
                .cardNumber(encryptedNumber)
                .owner(testUser)
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("1000.0"))
                .issueDate(YearMonth.now())
                .expirationDate(YearMonth.now().plusYears(4))
                .build();
    }

    @Test
    void getCards_Success() {
        CardFilterRequest request = new CardFilterRequest(
                null, null, null, null,
                false, false, 0, 10
        );
        GetCardsResponse expectedResponse = new GetCardsResponse(1L, 1, 0, 1,
                List.of(CardDto.toDto(testCard)));

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(cardService.getCards(any(CardFilterRequest.class))).thenReturn(expectedResponse);

        GetCardsResponse response = userCardService.getCards(userEmail, request);

        assertThat(response.total()).isEqualTo(1L);
        assertThat(response.cards()).hasSize(1);
        verify(cardService).getCards(any(CardFilterRequest.class));
    }

    @Test
    void getCards_UserNotFound_ThrowsException() {
        CardFilterRequest request = new CardFilterRequest(
                null, null, null, null,
                false, false, 0, 10
        );
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCardService.getCards(userEmail, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(userEmail);
    }

    @Test
    void getCard_Success() throws Exception {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(cardRepository.findByIdAndOwnerId(cardId, testUser.getId()))
                .thenReturn(Optional.of(testCard));
        when(encryptor.decrypt(encryptedNumber)).thenReturn(decryptedNumber);

        CardDto result = userCardService.getCard(userEmail, cardId);

        assertThat(result.id()).isEqualTo(cardId);
        assertThat(result.status()).isEqualTo(CardStatus.ACTIVE);
    }

    @Test
    void getCard_CardNotFound_ThrowsException() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(cardRepository.findByIdAndOwnerId(cardId, testUser.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCardService.getCard(userEmail, cardId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(cardId.toString());
    }

    @Test
    void getBalance_Success() throws Exception {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(cardRepository.findByIdAndOwnerId(cardId, testUser.getId()))
                .thenReturn(Optional.of(testCard));
        when(encryptor.decrypt(encryptedNumber)).thenReturn(decryptedNumber);

        BalanceResponse response = userCardService.getBalance(userEmail, cardId);

        assertThat(response.balance()).isEqualTo(new BigDecimal("1000.0"));
        assertThat(response.cardNumber()).startsWith("****");
    }

    @Test
    void transfer_Success() throws Exception {
        UUID fromCardId = UUID.randomUUID();
        UUID toCardId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.0");

        Card fromCard = Card.builder()
                .id(fromCardId)
                .cardNumber(testCard.getCardNumber())
                .owner(testCard.getOwner())
                .status(testCard.getStatus())
                .balance(new BigDecimal("1000.0"))
                .issueDate(testCard.getIssueDate())
                .expirationDate(testCard.getExpirationDate())
                .build();
        Card toCard = Card.builder()
                .id(toCardId)
                .cardNumber(testCard.getCardNumber())
                .owner(testCard.getOwner())
                .status(testCard.getStatus())
                .balance(new BigDecimal("500.0"))
                .issueDate(testCard.getIssueDate())
                .expirationDate(testCard.getExpirationDate())
                .build();

        TransferRequest request = new TransferRequest(fromCardId, toCardId, amount);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(cardRepository.findByIdAndOwnerId(fromCardId, testUser.getId()))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwnerId(toCardId, testUser.getId()))
                .thenReturn(Optional.of(toCard));
        when(cardRepository.saveAll(any())).thenReturn(List.of(fromCard, toCard));
        when(cardRepository.saveAll(any())).thenReturn(List.of(fromCard, toCard));
        when(encryptor.decrypt(any())).thenReturn(decryptedNumber);

        BalanceResponse response = userCardService.transfer(userEmail, request);

        assertThat(response.balance()).isEqualTo(new BigDecimal("1000.0")); // Новый баланс после перевода
        assertThat(response.cardNumber()).startsWith("****");
        verify(cardRepository).saveAll(any());
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        UUID fromCardId = UUID.randomUUID();
        UUID toCardId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1500.0");

        Card fromCard = Card.builder()
                .id(fromCardId)
                .cardNumber(testCard.getCardNumber())
                .owner(testCard.getOwner())
                .status(testCard.getStatus())
                .balance(new BigDecimal("1000.0"))
                .issueDate(testCard.getIssueDate())
                .expirationDate(testCard.getExpirationDate())
                .build();
        Card toCard = Card.builder()
                .id(toCardId)
                .cardNumber(testCard.getCardNumber())
                .owner(testCard.getOwner())
                .status(testCard.getStatus())
                .balance(new BigDecimal("500.0"))
                .issueDate(testCard.getIssueDate())
                .expirationDate(testCard.getExpirationDate())
                .build();

        TransferRequest request = new TransferRequest(fromCardId, toCardId, amount);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(cardRepository.findByIdAndOwnerId(fromCardId, testUser.getId()))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwnerId(toCardId, testUser.getId()))
                .thenReturn(Optional.of(toCard));

        assertThatThrownBy(() -> userCardService.transfer(userEmail, request))
                .isInstanceOf(InsufficientFundsOnCardException.class);
    }

    @Test
    void transfer_CardNotFound_ThrowsException() {
        UUID fromCardId = UUID.randomUUID();
        UUID toCardId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(fromCardId, toCardId, BigDecimal.TEN);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(cardRepository.findByIdAndOwnerId(fromCardId, testUser.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCardService.transfer(userEmail, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(fromCardId.toString());
    }
}