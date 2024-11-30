package com.hci.banking.controller;

import com.hci.banking.model.Account;
import com.hci.banking.model.Transaction;
import com.hci.banking.service.TransactionService;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    private final Random random = new Random();

    @Test
    public void testReverseCharge_Success() throws Exception {
        // Setup test data
        Long transactionId = random.nextLong(1000000);
        Transaction originalTx = createTestTransaction(transactionId);

        // Mock service responses
        when(transactionService.findById(transactionId)).thenReturn(Optional.of(originalTx));
        when(transactionService.reverseCharges(any(Transaction.class)))
            .thenReturn(Arrays.asList(createTestTransaction(random.nextLong(1000000))));

        // Perform test
        mockMvc.perform(post("/api/transactions/reverse-charge?transactionId=" + transactionId))
            .andExpect(status().isOk())
            .andExpect(content().string("Successfully reversed 1 charges."));
    }

    @Test
    public void testReverseCharge_NoDuplicates() throws Exception {
        Long transactionId = random.nextLong(1000000);
        Transaction originalTx = createTestTransaction(transactionId);

        when(transactionService.findById(transactionId)).thenReturn(Optional.of(originalTx));
        when(transactionService.reverseCharges(any(Transaction.class)))
            .thenThrow(new IllegalStateException("No duplicate charges found within 5 hours of this transaction"));

        mockMvc.perform(post("/api/transactions/reverse-charge?transactionId=" + transactionId))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("No duplicate charges found within 5 hours of this transaction"));
    }

    @Test
    public void testReverseCharge_ConcurrentUpdate() throws Exception {
        Long transactionId = random.nextLong(1000000);
        Transaction originalTx = createTestTransaction(transactionId);

        when(transactionService.findById(transactionId)).thenReturn(Optional.of(originalTx));
        when(transactionService.reverseCharges(any(Transaction.class)))
            .thenThrow(new OptimisticLockException("Concurrent update"));

        mockMvc.perform(post("/api/transactions/reverse-charge?transactionId=" + transactionId))
            .andExpect(status().isConflict())
            .andExpect(content().string(
                "Failed to reverse charges due to concurrent update issues. Please try again."));
    }

    @Test
    public void testGetAllTransactions_Success() throws Exception {
        Long accountId = random.nextLong(1000000);
        when(transactionService.getTransactionsByAccountId(accountId))
            .thenReturn(Arrays.asList(
                createTestTransaction(random.nextLong(1000000)),
                createTestTransaction(random.nextLong(1000000))
            ));

        mockMvc.perform(get("/api/transactions")
                .param("accountId", accountId.toString()))
            .andExpect(status().isOk());
    }

    @Test
    public void testGetAllTransactions_NotFound() throws Exception {
        Long accountId = random.nextLong(1000000);
        when(transactionService.getTransactionsByAccountId(accountId))
            .thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/transactions")
                .param("accountId", accountId.toString()))
            .andExpect(status().isNotFound());
    }

    private Transaction createTestTransaction(Long transactionId) {
        Account account = new Account();
        account.setAccountId(random.nextLong(1000000));

        Transaction tx = new Transaction();
        tx.setTransactionId(transactionId);
        tx.setAccount(account);
        tx.setAmount(random.nextDouble(1000.0) + 0.01);
        tx.setTransactionType("Fee");
        tx.setTransactionDate(Instant.now());
        return tx;
    }
} 
