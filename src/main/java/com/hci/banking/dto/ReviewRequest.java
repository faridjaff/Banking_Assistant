package com.hci.banking.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long customerId;
    private Integer rating; // Optional customer rating (1-5)
    private String rawReview; // Raw text of the customer's review
    private Integer gptRating; // GPT-generated sentiment rating (1-5)
    private String gptAnalysis; // GPT's analysis of what the conversation was about
}
