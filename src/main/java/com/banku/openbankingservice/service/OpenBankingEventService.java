package com.banku.openbankingservice.service;

import com.banku.openbankingservice.aggregate.Aggregate;
import com.banku.openbankingservice.aggregate.OpenBankingEventAggregate;
import com.banku.openbankingservice.event.OpenBankingEvent;
import com.banku.openbankingservice.repository.OpenBankingEventRepository;
import com.banku.openbankingservice.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenBankingEventService {
    
    @Autowired
    private OpenBankingEventRepository eventRepository;
    
    @Transactional
    public OpenBankingEvent saveEvent(OpenBankingEvent event) {
        if (event.getAggregateId() == null) {
            String aggregateId = UUID.randomUUID().toString();
            event.setAggregateId(aggregateId);
            event.setId(UUID.randomUUID().toString());
            event.setTimestamp(System.currentTimeMillis());
            event.setVersion(1L);
            return eventRepository.save(event);
        }

        Aggregate existingEvent = findByAggregateId(event.getAggregateId());
        if (existingEvent == null) {
            event.setId(UUID.randomUUID().toString());
            event.setTimestamp(System.currentTimeMillis());
            event.setVersion(1L);
            return eventRepository.save(event);
        } else {
            event.setId(UUID.randomUUID().toString());
            event.setTimestamp(System.currentTimeMillis());
            event.setVersion(existingEvent.getVersion() + 1);
            return eventRepository.save(event);
        }
    }
    
    public Aggregate findByAggregateId(String aggregateId) {
        List<OpenBankingEvent> events = eventRepository.findByAggregateIdOrderByVersionAsc(aggregateId);
        if (events.isEmpty()) {
            return null;
        }

        OpenBankingEventAggregate aggregate = new OpenBankingEventAggregate();
        events.forEach(aggregate::apply);
        return aggregate;
    }
    
    public List<OpenBankingEvent> findByUserId(String userId) {
        return eventRepository.findByUserIdOrderByVersionAsc(userId);
    }
    
    public List<OpenBankingEventAggregate> findAggregatesByUserId(String userId) {
        Map<String, List<OpenBankingEvent>> eventsByAggregateId = findByUserId(userId).stream()
            .collect(Collectors.groupingBy(OpenBankingEvent::getAggregateId));
        
        List<OpenBankingEventAggregate> aggregates = new ArrayList<>();
        
        for (Map.Entry<String, List<OpenBankingEvent>> entry : eventsByAggregateId.entrySet()) {
            OpenBankingEventAggregate aggregate = new OpenBankingEventAggregate();
            for (OpenBankingEvent event : entry.getValue()) {
                aggregate.apply(event);
            }
            aggregates.add(aggregate);
        }
        
        return aggregates;
    }
    
    public OpenBankingEvent saveLinkCreatedEvent(String userId) {
        OpenBankingEvent event = new OpenBankingEvent();
        event.setUserId(userId);
        event.setEventType(OpenBankingEvent.EventType.LINK_CREATED);
        return saveEvent(event);
    }
    
    public OpenBankingEvent saveTokenExchangedEvent(String userId, String accessToken) {
        OpenBankingEvent event = new OpenBankingEvent();
        
        // Buscar el último evento del usuario para obtener el aggregateId
        List<OpenBankingEvent> userEvents = findByUserId(userId);
        if (!userEvents.isEmpty()) {
            String aggregateId = userEvents.get(0).getAggregateId();
            event.setAggregateId(aggregateId);
        }
        
        event.setUserId(userId);
        event.setEventType(OpenBankingEvent.EventType.TOKEN_EXCHANGED);
        event.setAccessToken(accessToken);
        return saveEvent(event);
    }
    
    public OpenBankingEvent saveDataFetchedEvent(String userId, 
                                                List<OpenBankingEvent.Account> accounts,
                                                List<OpenBankingEvent.Transaction> transactions) {
        OpenBankingEvent event = new OpenBankingEvent();
        
        // Buscar el último evento del usuario para obtener el aggregateId
        List<OpenBankingEvent> userEvents = findByUserId(userId);
        if (!userEvents.isEmpty()) {
            String aggregateId = userEvents.get(0).getAggregateId();
            event.setAggregateId(aggregateId);
        }
        
        event.setUserId(userId);
        event.setEventType(OpenBankingEvent.EventType.DATA_FETCHED);
        event.setAccounts(accounts);
        event.setTransactions(transactions);
        return saveEvent(event);
    }
    
    public OpenBankingEventAggregate getCurrentUserAggregate() {
        String userId = JwtService.extractUserId();
        List<OpenBankingEvent> userEvents = findByUserId(userId);
        log.info("userEvents: {}", userId);
        if (userEvents.isEmpty()) {
            return null;
        }
        String aggregateId = userEvents.get(0).getAggregateId();
        return (OpenBankingEventAggregate) findByAggregateId(aggregateId);
    }

    public List<OpenBankingEvent.Transaction> getTransactions() {
        OpenBankingEventAggregate aggregate = getCurrentUserAggregate();
        if (aggregate == null) {
            return null;
        }
        return aggregate.getTransactions();
    }

    public List<OpenBankingEvent.Account> getAccounts() {
        OpenBankingEventAggregate aggregate = getCurrentUserAggregate();
        if (aggregate == null) {
            return null;
        }
        return aggregate.getAccounts();
    }
} 