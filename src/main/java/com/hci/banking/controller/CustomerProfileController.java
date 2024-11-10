package com.hci.banking.controller;

import com.hci.banking.model.CustomerProfile;
import com.hci.banking.repository.CustomerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerProfileController {

  @Autowired
  private CustomerProfileRepository customerProfileRepository;

  @GetMapping("/search")
  public List<CustomerProfile> searchCustomers(@RequestParam(required = false) String firstName,
                                               @RequestParam(required = false) String lastName,
                                               @RequestParam(required = false) Long phone,
                                               @RequestParam(required = false) String email) {
    return customerProfileRepository.findByFirstNameContainingOrLastNameContainingOrPhoneOrEmail(
        firstName, lastName, phone, email);
  }
}
