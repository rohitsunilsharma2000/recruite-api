package com.recruitment.dto;

import lombok.Data;

@Data
public class GeneralReviewRequest {
    private int rating;
    private String candidateStatus;
    private String overallComments;
}
