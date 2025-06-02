package com.github.osipovvj.bank_rest_test_task.service;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.CardFilterRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.TransferRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.BalanceResponse;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetCardsResponse;

import java.util.UUID;

public interface UserCardService {
    GetCardsResponse getCards(String userEmail, CardFilterRequest request);
    CardDto getCard(String userEmail, UUID cardId);
    BalanceResponse getBalance(String userEmail, UUID cardId);
    void blockCard(String userEmail, UUID cardId);
    BalanceResponse transfer(String userEmail, TransferRequest request);
}
