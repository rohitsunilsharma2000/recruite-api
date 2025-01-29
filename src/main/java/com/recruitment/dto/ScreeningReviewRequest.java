package com.recruitment.dto;

import lombok.Data;
import java.util.List;

@Data
public class ScreeningReviewRequest {

    private String reviewType; // e.g., Pre-Screening, Behavioral Screening
    private int overallRating;
    private String status;
    private String overallComments;

    private List<QuestionReviewRequest> questionReviews; // ✅ This should also be a list!
}
