package com.hci.banking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class CreditLimit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long creditId;

  @OneToOne
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Column(nullable = false)
  private Double assignedLimit;

  @Column(nullable = false)
  private Double availableCredit;

  @Column(nullable = false)
  private Instant lastUpdated = Instant.now();
}
