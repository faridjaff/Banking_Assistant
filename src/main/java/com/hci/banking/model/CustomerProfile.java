package com.hci.banking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CustomerProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String dateOfBirth;

  @Column(nullable = false, unique = true)
  private Long phone;

  @Column(nullable = false, unique = true)
  private String email;

  @Column
  private String address;

  @Column(nullable = false)
  private String identification;
}
