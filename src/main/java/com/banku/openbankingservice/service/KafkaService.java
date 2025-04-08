package com.banku.openbankingservice.service;

import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String topic, String userId, Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            log.info("Sending message to topic: {}, message: {}", topic, jsonMessage);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, userId, jsonMessage);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Message sent successfully to topic: {}", topic);
                } else {
                    log.error("Failed to send message to topic: {}, error: {}", topic, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error serializing message: {}", e.getMessage());
            throw new RuntimeException("Error sending message to Kafka", e);
        }
    }
} 