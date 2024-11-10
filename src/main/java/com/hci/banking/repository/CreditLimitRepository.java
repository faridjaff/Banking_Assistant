package com.hci.banking.repository;

import com.hci.banking.model.CreditLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditLimitRepository extends JpaRepository<CreditLimit, Long> {
  Optional<CreditLimit> findByAccountAccountId(Long accountId);
}
