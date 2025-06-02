package com.github.osipovvj.bank_rest_test_task.service.impl;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.CardFilterRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.TransferRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.BalanceResponse;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetCardsResponse;
import com.github.osipovvj.bank_rest_test_task.entity.Card;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.exception.InsufficientFundsOnCardException;
import com.github.osipovvj.bank_rest_test_task.exception.InternalServerException;
import com.github.osipovvj.bank_rest_test_task.exception.ResourceNotFoundException;
import com.github.osipovvj.bank_rest_test_task.repository.CardRepository;
import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.service.CardService;
import com.github.osipovvj.bank_rest_test_task.service.UserCardService;
import com.github.osipovvj.bank_rest_test_task.util.Encryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCardServiceImpl implements UserCardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardService cardService;
    private final Encryptor encryptor;

    @Override
    @Transactional(readOnly = true)
    public GetCardsResponse getCards(String userEmail, CardFilterRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + userEmail + " не найден."));
        CardFilterRequest filter = new CardFilterRequest(
                user.getId(),
                request.status(),
                request.issueDateFrom(),
                request.issueDateTo(),
                request.expiringCards(),
                request.sortDesc(),
                request.page(),
                request.size()
        );

        return cardService.getCards(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public CardDto getCard(String userEmail, UUID cardId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + userEmail + " не найден."));
        Card card = cardRepository.findByIdAndOwnerId(cardId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Карта с id " + cardId + " не найдена."));

        String decryptNumber = decryptCardNumber(card.getCardNumber());
        card.setMaskedCardNumber(decryptNumber);

        return CardDto.toDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String userEmail, UUID cardId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + userEmail + " не найден."));
        Card card = cardRepository.findByIdAndOwnerId(cardId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Карта с id " + cardId + " не найдена."));

        String decryptCardNumber = decryptCardNumber(card.getCardNumber());
        card.setMaskedCardNumber(maskedCardNumber(decryptCardNumber));

        return BalanceResponse.toResponse(card);
    }

    @Override
    @Transactional(readOnly = true)
    public void blockCard(String userEmail, UUID cardId) {
        // Здесь можно реализовать логику оповещения админа, например:
        //  - отправка email
        //  - сообщение через Telegram Bot API
        //  - ответ на админку
        //  - и т.п.
    }

    @Override
    public BalanceResponse transfer(String userEmail, TransferRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + userEmail + " не найден."));
        Card fromCard = cardRepository.findByIdAndOwnerId(request.fromCard(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Карта с id " + request.fromCard() + " не найдена."));
        Card toCard = cardRepository.findByIdAndOwnerId(request.toCard(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Карта с id " + request.fromCard() + " не найдена."));

        BigDecimal result = fromCard.getBalance().subtract(request.amount());

        if (result.compareTo(new BigDecimal("0.0")) < 0) {
            throw new InsufficientFundsOnCardException("Недостаточно средств на карте.");
        }

        fromCard.setBalance(result);
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        cardRepository.saveAll(List.of(fromCard, toCard));

        String decryptCardNumber = decryptCardNumber(toCard.getCardNumber());
        toCard.setMaskedCardNumber(maskedCardNumber(decryptCardNumber));

        return BalanceResponse.toResponse(toCard);
    }

    private String decryptCardNumber(String cardNumber) {
        try {
            return encryptor.decrypt(cardNumber);
        } catch (Exception e) {
            throw new InternalServerException("Ошибка при при получении данных карты.");
        }
    }

    private String maskedCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
