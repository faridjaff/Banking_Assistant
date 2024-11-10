package com.hci.banking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerProfile customer;

    @Column
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String rawReview;

    @Column(nullable = false)
    private Integer gptRating;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String gptAnalysis;
}
