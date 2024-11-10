package com.hci.banking.controller;

import com.hci.banking.dto.ReviewRequest;
import com.hci.banking.model.CustomerProfile;
import com.hci.banking.model.Review;
import com.hci.banking.repository.CustomerProfileRepository;
import com.hci.banking.repository.ReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

  @Autowired
  private ReviewRepository customerReviewRepository;

  @Autowired
  private CustomerProfileRepository customerProfileRepository;

  @Operation(summary = "Get Customer Reviews", description = "Fetch all reviews for a given customer ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "No reviews found for the given customer ID")
  })
  @GetMapping("/{customerId}")
  public ResponseEntity<List<Review>> getReviewsByCustomerId(@PathVariable Long customerId) {
    List<Review> reviews = customerReviewRepository.findByCustomerId(customerId);
    if (reviews.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok(reviews);
  }

  @Operation(summary = "Add Customer Review", description = "Submit a new customer review with optional rating, GPT sentiment rating, and GPT conversation analysis. Also submit a summary of the conversation so that the customer can be helped out faster next time with that context.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Review added successfully"),
      @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  @PostMapping("/add-review")
  public ResponseEntity<?> addReview(@RequestBody ReviewRequest reviewRequest) {
    Optional<CustomerProfile> customer = customerProfileRepository.findById(reviewRequest.getCustomerId());
    if (customer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Customer not found for ID: " + reviewRequest.getCustomerId());
    }

    Review review = new Review();
    review.setCustomer(customer.get());
    review.setRating(reviewRequest.getRating());
    review.setRawReview(reviewRequest.getRawReview());
    review.setGptRating(reviewRequest.getGptRating());
    review.setGptAnalysis(reviewRequest.getGptAnalysis());

    customerReviewRepository.save(review);
    return ResponseEntity.status(HttpStatus.CREATED).body("Review added successfully");
  }
}
