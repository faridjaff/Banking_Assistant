package com.hci.banking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long transactionId;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Column(nullable = false)
  private String transactionType;

  @Column(nullable = false)
  private Double amount;

  @Column(nullable = false, updatable = false)
  private Instant transactionDate = Instant.now();
}
