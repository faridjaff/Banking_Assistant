package com.hci.banking.service;

import com.hci.banking.model.Account;
import com.hci.banking.model.Transaction;
import com.hci.banking.repository.TransactionRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Random random;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        random = new Random();
        testAccount = new Account();
        testAccount.setAccountId(random.nextLong(1000000));
    }

    @Test
    void getTransactionsByAccountId_Success() {
        // Arrange
        Long accountId = testAccount.getAccountId();
        List<Transaction> expectedTransactions = List.of(
            createTestTransaction(random.nextLong(1000000)),
            createTestTransaction(random.nextLong(1000000))
        );
        when(transactionRepository.findByAccountAccountId(accountId))
            .thenReturn(expectedTransactions);

        // Act
        List<Transaction> actualTransactions = transactionService.getTransactionsByAccountId(accountId);

        // Assert
        assertEquals(expectedTransactions.size(), actualTransactions.size());
        verify(transactionRepository).findByAccountAccountId(accountId);
    }

    @Test
    void reverseCharges_Success() {
        // Arrange
        Transaction originalTransaction = createTestTransaction(1L);
        Transaction duplicateTransaction = createTestTransaction(2L);
        
        when(transactionRepository.existsByOriginalTransactionId(1L)).thenReturn(false);
        when(transactionRepository.findByAccountAccountIdAndAmountAndTransactionTypeAndTransactionDateBetween(
            any(), any(), any(), any(), any()
        )).thenReturn(List.of(originalTransaction, duplicateTransaction));

        // Act
        List<Transaction> reversals = transactionService.reverseCharges(originalTransaction);

        // Assert
        assertEquals(1, reversals.size());
        assertEquals("Fee Reversal", reversals.get(0).getTransactionType());
        verify(transactionRepository).saveAll(any());
    }

    @Test
    void reverseCharges_AlreadyReversed() {
        // Arrange
        Transaction originalTransaction = createTestTransaction(1L);
        when(transactionRepository.existsByOriginalTransactionId(1L)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> transactionService.reverseCharges(originalTransaction)
        );
        assertEquals("This transaction has already been reversed", exception.getMessage());
    }

    @Test
    void reverseCharges_IsReversalTransaction() {
        // Arrange
        Transaction reversalTransaction = createTestTransaction(1L);
        reversalTransaction.setOriginalTransactionId(2L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transactionService.reverseCharges(reversalTransaction)
        );
        assertEquals("Cannot reverse a reversal transaction", exception.getMessage());
    }

    @Test
    void reverseCharges_NoDuplicatesFound() {
        // Arrange
        Transaction originalTransaction = createTestTransaction(1L);
        when(transactionRepository.existsByOriginalTransactionId(1L)).thenReturn(false);
        when(transactionRepository.findByAccountAccountIdAndAmountAndTransactionTypeAndTransactionDateBetween(
            any(), any(), any(), any(), any()
        )).thenReturn(List.of(originalTransaction));

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> transactionService.reverseCharges(originalTransaction)
        );
        assertEquals("No duplicate charges found within 5 hours of this transaction", exception.getMessage());
    }

    @Test
    void reverseCharges_OptimisticLockException() {
        // Arrange
        Transaction originalTransaction = createTestTransaction(1L);
        Transaction duplicateTransaction = createTestTransaction(2L);
        
        when(transactionRepository.existsByOriginalTransactionId(1L)).thenReturn(false);
        when(transactionRepository.findByAccountAccountIdAndAmountAndTransactionTypeAndTransactionDateBetween(
            any(), any(), any(), any(), any()
        )).thenReturn(List.of(originalTransaction, duplicateTransaction));
        when(transactionRepository.saveAll(any())).thenThrow(OptimisticLockException.class);

        // Act & Assert
        assertThrows(
            OptimisticLockException.class,
            () -> transactionService.reverseCharges(originalTransaction)
        );
    }

    private Transaction createTestTransaction(Long transactionId) {
        Transaction tx = new Transaction();
        tx.setTransactionId(transactionId);
        tx.setAccount(testAccount);
        tx.setAmount(random.nextDouble(1000.0) + 0.01);
        tx.setTransactionType("Fee");
        tx.setTransactionDate(Instant.now());
        return tx;
    }
} 
