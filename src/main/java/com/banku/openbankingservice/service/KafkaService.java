package com.banku.openbankingservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.banku.openbankingservice.event.OpenBankingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishEvent(OpenBankingEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("banku.openbanking", event.getAggregateId(), message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Event published successfully: {}", event.getEventType());
                        } else {
                            log.error("Failed to publish event: {}", event.getEventType(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error serializing event: {}", event.getEventType(), e);
            throw new RuntimeException("Error publishing event", e);
        }
    }
} 