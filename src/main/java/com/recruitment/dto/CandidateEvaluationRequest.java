package com.recruitment.dto;

import lombok.Data;
import java.util.List;

@Data
public class CandidateEvaluationRequest {
    private GeneralReviewRequest generalReview;
    private List<ScreeningReviewRequest> screeningReviews;
}
