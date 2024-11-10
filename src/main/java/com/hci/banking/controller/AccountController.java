package com.hci.banking.controller;

import com.hci.banking.model.Account;
import com.hci.banking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

  @Autowired
  private AccountRepository accountRepository;

  @GetMapping("/balance/{accountId}")
  public ResponseEntity<Double> getBalance(@PathVariable Long accountId) {
    Optional<Account> account = accountRepository.findById(accountId);
    return account.map(a -> ResponseEntity.ok(a.getBalance()))
        .orElse(ResponseEntity.notFound().build());
  }
}
