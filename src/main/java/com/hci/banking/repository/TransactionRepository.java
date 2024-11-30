package com.hci.banking.repository;

import com.hci.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findByAccountAccountIdAndAmountAndTransactionDateBetween(
      Long accountId, 
      Double amount, 
      Instant startTime, 
      Instant endTime
  );

  List<Transaction> findByAccountAccountId(Long accountId);

  List<Transaction> findByAccountAccountIdAndAmountAndTransactionTypeAndTransactionDateBetween(
      Long accountId,
      Double amount,
      String transactionType,
      Instant startTime,
      Instant endTime
  );

  boolean existsByOriginalTransactionId(Long originalTransactionId);

}
