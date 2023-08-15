package com.epam.resourceservice.service;

import com.epam.resourceservice.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMqService {

    private final RabbitMqConfig rabbitMqConfig;

    public void sendMessage(String message) {
        rabbitMqConfig.rabbitTemplate().convertAndSend(rabbitMqConfig.queue().getName(), message);
    }
}
