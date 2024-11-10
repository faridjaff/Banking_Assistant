package com.hci.banking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long accountId;

  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  private CustomerProfile customer;

  @Column(nullable = false)
  private String accountType;

  @Column(nullable = false)
  private Double balance;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
}
