package com.hci.banking.controller;

import com.hci.banking.model.Transaction;
import com.hci.banking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.OptimisticLockException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @Operation(
        summary = "Reverse a duplicate transaction",
        description = "Only supposed to be used when there is a known duplicate transaction on the same day of the same transaction type."
    )
    @PostMapping("/reverse-charge")
    public ResponseEntity<String> reverseCharge(@RequestParam Long transactionId) {
        try {
            var transaction = transactionService.findById(transactionId);
            if (transaction.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Transaction> reversals = transactionService.reverseCharges(transaction.get());
            return ResponseEntity.ok(String.format("Successfully reversed %d charges.", reversals.size()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Failed to reverse charges due to concurrent update issues. Please try again.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactionsForCustomer(@RequestParam Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transactions);
    }
}
