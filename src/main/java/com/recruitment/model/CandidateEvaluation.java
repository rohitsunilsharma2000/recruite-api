package com.recruitment.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidateEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonIgnore // Typically you don't expose the entire Candidate in the JSON
    private Candidate candidate;

    @Embedded
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GeneralReview generalReview;

    @OneToMany(mappedBy = "candidateEvaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ScreeningReview> screeningReviews;


}
