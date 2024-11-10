package com.hci.banking.controller;

import com.hci.banking.model.CreditLimit;
import com.hci.banking.repository.CreditLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/credit-limit")
public class CreditLimitController {

  @Autowired
  private CreditLimitRepository creditLimitRepository;

  @PostMapping("/increase")
  public ResponseEntity<String> increaseCreditLimit(@RequestParam Long accountId,
                                                    @RequestParam Double increaseAmount) {
    Optional<CreditLimit> creditLimit = creditLimitRepository.findByAccountAccountId(accountId);
    if (creditLimit.isPresent()) {
      CreditLimit cl = creditLimit.get();
      System.out.println("last updated is " + cl.getLastUpdated());
      System.out.println("Instant.now() is " + Instant.now());
      System.out.println("Duration is " + Duration.between(cl.getLastUpdated(), Instant.now()));
      if (Duration.between(cl.getLastUpdated(), Instant.now()).toMinutes() < 2) {
        return ResponseEntity.status(HttpStatus.TOO_EARLY).body("Credit limit was increased less than 2 mins ago.");
      }
      cl.setAssignedLimit(cl.getAssignedLimit() + increaseAmount);
      cl.setAvailableCredit(cl.getAvailableCredit() + increaseAmount);
      cl.setLastUpdated(Instant.now());
      creditLimitRepository.save(cl);
      return ResponseEntity.ok("Credit limit increased successfully.");
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping
  public ResponseEntity<CreditLimit> getCreditLimitForAccount(@RequestParam Long accountId) {
    Optional<CreditLimit> creditLimit = creditLimitRepository.findByAccountAccountId(accountId);
    if (creditLimit.isPresent()) {
      CreditLimit cl = creditLimit.get();
      return ResponseEntity.ok(cl);
    }
    return ResponseEntity.notFound().build();
  }
}
