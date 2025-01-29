package com.recruitment.service;

import com.recruitment.dto.CandidateEvaluationRequest;
import com.recruitment.model.*;
import com.recruitment.repository.CandidateEvaluationRepository;
import com.recruitment.repository.CandidateRepository;
import com.recruitment.repository.QuestionBankTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CandidateEvaluationService {



    private final CandidateRepository candidateRepository;
    private final CandidateEvaluationRepository candidateEvaluationRepository;
    private final QuestionBankTemplateRepository questionBankTemplateRepository;

    public CandidateEvaluationService(CandidateRepository candidateRepository,
                                      CandidateEvaluationRepository candidateEvaluationRepository,
                                      QuestionBankTemplateRepository questionBankTemplateRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateEvaluationRepository = candidateEvaluationRepository;
        this.questionBankTemplateRepository = questionBankTemplateRepository;
    }

    /**
     * Creates or updates a CandidateEvaluation by parsing a raw JSON payload.
     */
    @Transactional
    public CandidateEvaluation evaluateCandidate(Long candidateId, CandidateEvaluationRequest request) {
        log.info("Evaluating candidate with ID: {}", candidateId);

        // 1) Fetch Candidate
        Candidate candidate = candidateRepository.findById(candidateId)
                                                 .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));

        // 2) Create CandidateEvaluation entity
        CandidateEvaluation evaluation = new CandidateEvaluation();
        evaluation.setCandidate(candidate);

        // 3) Parse General Review
        if (request.getGeneralReview() != null) {
            GeneralReview generalReview = new GeneralReview();
            generalReview.setRating(request.getGeneralReview().getRating());
            generalReview.setCandidateStatus(request.getGeneralReview().getCandidateStatus());
            generalReview.setOverallComments(request.getGeneralReview().getOverallComments());
            evaluation.setGeneralReview(generalReview);
        }

        // 4) Parse Screening Reviews
        if (request.getScreeningReviews() != null) {
            ScreeningReview screeningReview = new ScreeningReview();

            screeningReview.setOverallRating(request.getScreeningReviews().getOverallRating());
            screeningReview.setStatus(request.getScreeningReviews().getStatus());
            screeningReview.setOverallComments(request.getScreeningReviews().getOverallComments());

            screeningReview.setCandidateEvaluation(evaluation);

            // 4a) Map Question Reviews
            List<QuestionReview> questionReviews = Optional.ofNullable(request.getScreeningReviews().getCandidateGeneralAssessment())
                                                           .orElse(Collections.emptyList())
                                                           .stream()
                                                           .map(qrRequest -> {
                                                               QuestionReview qr = new QuestionReview();
                                                               qr.setScreeningReview(screeningReview);
                                                               qr.setRating(qrRequest.getRating());
                                                               qr.setComments(qrRequest.getComments());

                                                               // Fetch and set QuestionBankTemplate
                                                               QuestionBankTemplate questionBankTemplate = questionBankTemplateRepository
                                                                       .findById(qrRequest.getQuestionBankTemplateId())
                                                                       .orElseThrow(() -> new IllegalArgumentException(
                                                                               "QuestionBankTemplate not found with ID: " + qrRequest.getQuestionBankTemplateId()
                                                                       ));
                                                               qr.setQuestionBankTemplate(questionBankTemplate);

                                                               return qr;
                                                           })
                                                           .collect(Collectors.toList());

            screeningReview.setQuestionReviews(questionReviews);
            evaluation.setScreeningReviews(List.of(screeningReview));
        }

        // 5) Save the evaluation
        CandidateEvaluation savedEvaluation = candidateEvaluationRepository.save(evaluation);
        log.info("Successfully saved evaluation ID: {}", savedEvaluation.getId());

        return savedEvaluation;
    }
    private QuestionReview mapToQuestionReview(ScreeningReview screeningReview, Map<String, Object> qrMap) {
        QuestionReview qr = new QuestionReview();
        qr.setScreeningReview(screeningReview);

        // rating, comments
        qr.setRating(((Number) qrMap.getOrDefault("rating", 0)).intValue());
        qr.setComments((String) qrMap.getOrDefault("comments", ""));

        // questionBankTemplateId
        Number templateId = (Number) qrMap.get("questionBankTemplateId");
        if (templateId == null) {
            throw new IllegalArgumentException("questionBankTemplateId is required and cannot be null.");
        }
        QuestionBankTemplate qbt = questionBankTemplateRepository.findById(templateId.longValue())
                                                                 .orElseThrow(() -> new IllegalArgumentException("QuestionBankTemplate not found: " + templateId));
        qr.setQuestionBankTemplate(qbt);

        return qr;
    }

    // Additional CRUD methods

    @Transactional(readOnly = true)
    public List<CandidateEvaluation> getAllEvaluations() {
        log.debug("Fetching all candidate evaluations...");
        return candidateEvaluationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CandidateEvaluation getEvaluationById(Long evaluationId) {
        log.debug("Fetching evaluation by ID: {}", evaluationId);
        return candidateEvaluationRepository.findById(evaluationId)
                                            .orElseThrow(() -> new IllegalArgumentException("Evaluation not found with ID: " + evaluationId));
    }

    @Transactional(readOnly = true)
    public CandidateEvaluation getEvaluationByCandidate(Long candidateId) {
        log.debug("Fetching evaluation for candidate ID: {}", candidateId);
        return candidateEvaluationRepository.findByCandidateId(candidateId)
                                            .orElseThrow(() -> new IllegalArgumentException("No evaluation found for candidate: " + candidateId));
    }

    @Transactional
    public void deleteEvaluation(Long evaluationId) {
        log.debug("Deleting evaluation ID: {}", evaluationId);
        CandidateEvaluation evaluation = getEvaluationById(evaluationId);
        candidateEvaluationRepository.delete(evaluation);
        log.info("Evaluation ID: {} deleted successfully.", evaluationId);
    }
}