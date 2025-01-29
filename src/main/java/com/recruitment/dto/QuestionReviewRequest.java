package com.recruitment.dto;


import lombok.Data;

@Data
public class QuestionReviewRequest {
    private Long questionBankTemplateId;
    private int rating;
    private String comments;
}
