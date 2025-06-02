package com.github.osipovvj.bank_rest_test_task.service;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.CardFilterRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.CreateCardRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.ChangeCardStatusRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetCardsResponse;

import java.util.UUID;

public interface CardService {
    GetCardsResponse getCards(CardFilterRequest filter);
    CardDto createCard(CreateCardRequest request);
    CardDto getCardById(UUID cardId);
    CardDto updateCard(UUID cardId, ChangeCardStatusRequest request);
    void deleteCard(UUID cardId);
}
