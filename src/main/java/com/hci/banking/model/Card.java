package com.hci.banking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cardId;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Column(nullable = false, unique = true)
  private Long cardNumber;

  @Column(nullable = false)
  private String cardType;

  @Column(nullable = false)
  private java.time.LocalDate expirationDate;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
}
