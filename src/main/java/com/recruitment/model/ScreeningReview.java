package com.recruitment.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScreeningReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evaluation_id", nullable = false)
    @JsonBackReference
    private CandidateEvaluation candidateEvaluation;

    @Column(nullable = false)
    private String reviewType; // ✅ Store reviewType here (e.g., Pre-Screening, Behavioral Screening)
    private int overallRating;
    private String status;
    private String overallComments;

    @OneToMany(mappedBy = "screeningReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<QuestionReview> questionReviews;


}
