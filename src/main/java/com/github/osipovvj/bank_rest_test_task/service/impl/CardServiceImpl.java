package com.github.osipovvj.bank_rest_test_task.service.impl;

import com.github.osipovvj.bank_rest_test_task.repository.CardRepository;
import com.github.osipovvj.bank_rest_test_task.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;


}
