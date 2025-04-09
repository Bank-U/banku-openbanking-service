package com.banku.openbankingservice.repository;

import com.banku.openbankingservice.event.OpenBankingEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenBankingEventRepository extends MongoRepository<OpenBankingEvent, String> {
    List<OpenBankingEvent> findByAggregateIdOrderByVersionAsc(String aggregateId);
    List<OpenBankingEvent> findByUserIdOrderByVersionAsc(String userId);
} 