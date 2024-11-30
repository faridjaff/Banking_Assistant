package com.hci.banking.service;

import com.hci.banking.model.Transaction;
import com.hci.banking.repository.TransactionRepository;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountAccountId(accountId);
        logger.info("Found {} transactions for accountId={}", transactions.size(), accountId);
        return transactions;
    }

    public Optional<Transaction> findById(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }

    public List<Transaction> reverseCharges(Transaction originalTransaction) {
        // Check if already reversed
        if (transactionRepository.existsByOriginalTransactionId(originalTransaction.getTransactionId())) {
            logger.warn("Transaction {} has already been reversed", originalTransaction.getTransactionId());
            throw new IllegalStateException("This transaction has already been reversed");
        }

        // Check if this is a reversal transaction
        if (originalTransaction.getOriginalTransactionId() != null) {
            logger.warn("Cannot reverse a reversal transaction: {}", originalTransaction.getTransactionId());
            throw new IllegalArgumentException("Cannot reverse a reversal transaction");
        }

        Long accountId = originalTransaction.getAccount().getAccountId();
        
        // Find matching transactions
        List<Transaction> matchingTransactions = findMatchingTransactions(originalTransaction);
        
        if (matchingTransactions.isEmpty()) {
            throw new IllegalStateException("No duplicate charges found within 5 hours of this transaction");
        }

        try {
            return processReversals(matchingTransactions, originalTransaction);
        } catch (OptimisticLockException ole) {
            logger.error("Failed to save reversal transactions", ole);
            throw new OptimisticLockException("Failed to reverse charges due to concurrent update issues");
        }
    }

    private List<Transaction> findMatchingTransactions(Transaction originalTransaction) {
        Instant startTime = originalTransaction.getTransactionDate().minus(5, ChronoUnit.HOURS);
        Instant endTime = originalTransaction.getTransactionDate().plus(5, ChronoUnit.HOURS);
        
        List<Transaction> matchingTransactions = new ArrayList<>(transactionRepository.findByAccountAccountIdAndAmountAndTransactionTypeAndTransactionDateBetween(
            originalTransaction.getAccount().getAccountId(),
            originalTransaction.getAmount(),
            originalTransaction.getTransactionType(),
            startTime,
            endTime
        ));

        // Remove the original transaction from the list
        matchingTransactions.removeIf(trans -> 
            trans.getTransactionId().equals(originalTransaction.getTransactionId()));
            
        return matchingTransactions;
    }

    private List<Transaction> processReversals(List<Transaction> matchingTransactions, Transaction originalTransaction) {
        List<Transaction> reversals = matchingTransactions.stream()
            .map(matchingTrans -> createReversalTransaction(matchingTrans))
            .toList();
        
        // Mark original transactions as reversed
        matchingTransactions.forEach(mt -> {
            mt.setOriginalTransactionId(originalTransaction.getTransactionId());
            transactionRepository.save(mt);
        });
        
        transactionRepository.saveAll(reversals);
        logger.info("Created {} reversal transactions", reversals.size());
        return reversals;
    }

    private Transaction createReversalTransaction(Transaction originalTransaction) {
        Transaction reversedT = new Transaction();
        reversedT.setAccount(originalTransaction.getAccount());
        reversedT.setAmount(originalTransaction.getAmount());
        reversedT.setTransactionDate(Instant.now());
        reversedT.setTransactionType("Fee Reversal");
        reversedT.setOriginalTransactionId(originalTransaction.getTransactionId());
        return reversedT;
    }
} 