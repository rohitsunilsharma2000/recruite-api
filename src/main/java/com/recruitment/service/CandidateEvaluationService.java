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

    @Transactional
    public CandidateEvaluation evaluateCandidate(Long candidateId, CandidateEvaluationRequest request) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        CandidateEvaluation evaluation = new CandidateEvaluation();
        evaluation.setCandidate(candidate);

        if (request.getGeneralReview() != null) {
            GeneralReview generalReview = new GeneralReview();
            generalReview.setRating(request.getGeneralReview().getRating());
            generalReview.setCandidateStatus(request.getGeneralReview().getCandidateStatus());
            generalReview.setOverallComments(request.getGeneralReview().getOverallComments());
            evaluation.setGeneralReview(generalReview);
        }

        if (request.getScreeningReviews() != null) {
            List<ScreeningReview> screeningReviews = request.getScreeningReviews().stream()
                    .map(srRequest -> {
                        ScreeningReview screeningReview = new ScreeningReview();
                        screeningReview.setReviewType(srRequest.getReviewType());
                        screeningReview.setOverallRating(srRequest.getOverallRating());
                        screeningReview.setStatus(srRequest.getStatus());
                        screeningReview.setOverallComments(srRequest.getOverallComments());
                        screeningReview.setCandidateEvaluation(evaluation);

                        List<QuestionReview> questionReviews = srRequest.getQuestionReviews().stream()
                                .map(qrRequest -> {
                                    QuestionReview qr = new QuestionReview();
                                    qr.setScreeningReview(screeningReview);
                                    qr.setRating(qrRequest.getRating());
                                    qr.setComments(qrRequest.getComments());

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

                        return screeningReview;
                    })
                    .collect(Collectors.toList());

            evaluation.setScreeningReviews(screeningReviews);
        }

        CandidateEvaluation savedEvaluation = candidateEvaluationRepository.save(evaluation);
        return savedEvaluation;
    }

}