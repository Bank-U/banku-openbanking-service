package com.banku.openbankingservice.repository;

import com.banku.openbankingservice.aggregate.OpenBankingAggregate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OpenBankingAggregateRepository extends MongoRepository<OpenBankingAggregate, String> {
    Optional<OpenBankingAggregate> findByUserId(String userId);
} 