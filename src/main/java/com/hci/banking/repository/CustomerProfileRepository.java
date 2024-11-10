package com.hci.banking.repository;

import com.hci.banking.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
  List<CustomerProfile> findByFirstNameContainingOrLastNameContainingOrPhoneOrEmail(
      String firstName, String lastName, Long phone, String email);
}
