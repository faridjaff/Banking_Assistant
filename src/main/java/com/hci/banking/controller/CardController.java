package com.hci.banking.controller;

import com.hci.banking.model.Card;
import com.hci.banking.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class CardController {

  @Autowired
  private CardRepository customerCardRepository;

  @PostMapping("/activate")
  public ResponseEntity<String> activateCard(@RequestParam Long cardId) {
    Optional<Card> card = customerCardRepository.findById(cardId);
    if (card.isPresent()) {
      Card c = card.get();
      if (c.getStatus().equals("active")) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
      }
      c.setStatus("active");
      customerCardRepository.save(c);
      return ResponseEntity.ok("Card activated successfully.");
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping("/deactivate")
  public ResponseEntity<String> deactivateCard(@RequestParam Long cardId) {
    Optional<Card> card = customerCardRepository.findById(cardId);
    if (card.isPresent()) {
      Card c = card.get();
      if(c.getStatus().equals("inactive")) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
      }
      c.setStatus("inactive");
      customerCardRepository.save(c);
      return ResponseEntity.ok("Card deactivated successfully.");
    }
    return ResponseEntity.notFound().build();
  }
}
