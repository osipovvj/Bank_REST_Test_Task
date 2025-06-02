package com.github.osipovvj.bank_rest_test_task.service.impl;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.CardFilterRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.CreateCardRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.ChangeCardStatusRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetCardsResponse;
import com.github.osipovvj.bank_rest_test_task.entity.Card;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.enums.CardStatus;
import com.github.osipovvj.bank_rest_test_task.exception.InternalServerException;
import com.github.osipovvj.bank_rest_test_task.exception.ResourceNotFoundException;
import com.github.osipovvj.bank_rest_test_task.repository.CardRepository;
import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.service.CardService;
import com.github.osipovvj.bank_rest_test_task.util.Encryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final Encryptor encryptor;

    @Override
    @Transactional(readOnly = true)
    public GetCardsResponse getCards(CardFilterRequest filter) {
        Specification<Card> spec = Specification.not(null);

        if (filter.ownerId() != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("owner").get("id"), filter.ownerId()));
        }

        if (filter.status() != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), filter.status()));
        }

        if (filter.issueDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("issueDate"),
                    filter.issueDateFrom()));
        }

        if (filter.issueDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("issueDate"),
                    filter.issueDateTo()));
        }

        if (Boolean.TRUE.equals(filter.expiringCards())) {
            YearMonth threshold = YearMonth.now().plusMonths(3);
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("expirationDate"), threshold));
        }

        Sort sort = Boolean.TRUE.equals(filter.sortDesc())
            ? Sort.by("issueDate").descending()
            : Sort.by("issueDate").ascending();

        PageRequest pageRequest = PageRequest.of(
            filter.page() != null ? filter.page() : 0,
            filter.size() != null ? filter.size() : 10,
            sort
        );

        Page<Card> cards = cardRepository.findAll(spec, pageRequest);

        cards.forEach(card -> {
            try {
                String decryptedNumber = encryptor.decrypt(card.getCardNumber());
                card.setMaskedCardNumber(maskedCardNumber(decryptedNumber));
            } catch (Exception e) {
                throw new InternalServerException("Ошибка при расшифровке номера карты");
            }
        });

        return GetCardsResponse.toResponse(cards);
    }

    @Override
    public CardDto createCard(CreateCardRequest request) {
        User user = userRepository.findById(request.cardholderId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + request.cardholderId() + " не найден."));

        String number;
        String encryptNumber;

        do {
            number = generateNumber();
        } while (cardRepository.existsByCardNumber(number));

        try {
            encryptNumber = encryptor.encrypt(number);
        } catch (Exception e) {
            throw new InternalServerException("Ошибка при создании карты.");
        }

        Card card = Card.builder()
                .cardNumber(encryptNumber)
                .owner(user)
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("0.0"))
                .issueDate(YearMonth.now())
                .expirationDate(YearMonth.now().plusYears(4))
                .build();

        card = cardRepository.save(card);
        card.setMaskedCardNumber(maskedCardNumber(number));

        return CardDto.toDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public CardDto getCardById(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта с ID " + cardId + " не найдена."));

        String decryptedNumber;

        try {
            decryptedNumber = encryptor.decrypt(card.getCardNumber());
        } catch (Exception e) {
            throw new InternalServerException("Ошибка при при получении данных карты.");
        }

        card.setMaskedCardNumber(maskedCardNumber(decryptedNumber));

        return CardDto.toDto(card);
    }

    @Override
    public CardDto updateCard(UUID cardId, ChangeCardStatusRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта с ID " + cardId + " не найдена."));

        String decryptedNumber;

        if (!card.getStatus().equals(request.status())) card.setStatus(request.status());

        try {
            decryptedNumber = encryptor.decrypt(card.getCardNumber());
        } catch (Exception e) {
            throw new InternalServerException("Ошибка при при получении данных карты.");
        }

        card.setMaskedCardNumber(maskedCardNumber(decryptedNumber));

        return CardDto.toDto(card);
    }

    @Override
    public void deleteCard(UUID cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new ResourceNotFoundException("Карта с ID " + cardId + " не найдена.");
        }

        cardRepository.deleteById(cardId);
    }

    private String generateNumber() {
        long randomLong = ThreadLocalRandom.current().nextLong(1, 10000000000000000L);

        return String.format("%016d", randomLong);
    }

    private String maskedCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
