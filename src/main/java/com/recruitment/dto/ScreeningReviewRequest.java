package com.recruitment.dto;

import lombok.Data;
import java.util.List;

@Data
public class ScreeningReviewRequest {
    private int overallRating;
    private String status;
    private String overallComments;
    private List<QuestionReviewRequest> candidateGeneralAssessment;
    private String reviewType; // Ensure this field exists
}
