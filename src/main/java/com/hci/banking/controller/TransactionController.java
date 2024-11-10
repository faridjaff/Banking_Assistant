package com.hci.banking.controller;

import com.hci.banking.model.Transaction;
import com.hci.banking.repository.TransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

  @Autowired
  private TransactionRepository transactionRepository;

  @Operation(
      summary = "Reverse a duplicate transaction",
      description = "Only supposed to be used when there is a known duplicate transaction on the same day of the same transaction type."
  )
  @PostMapping("/reverse-charge")
  public ResponseEntity<String> reverseCharge(@RequestParam Long transactionId) {
    Optional<Transaction> transaction = transactionRepository.findById(transactionId);
    if (transaction.isPresent()) {
      Transaction t = transaction.get();
      Transaction reversedT = new Transaction();
      reversedT.setAccount(t.getAccount());
      reversedT.setAmount(t.getAmount());
      reversedT.setTransactionDate(Instant.now());
      reversedT.setTransactionType("Fee Reversal");
      transactionRepository.save(reversedT);
      return ResponseEntity.ok("Charge reversed successfully.");
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping
  public ResponseEntity<List<Transaction>> getAllTransactionsForCustomer(@RequestParam Long accountId) {
    List<Transaction> transactions = transactionRepository.findByAccountAccountId(accountId);
    if (transactions.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(transactions);
  }
}
