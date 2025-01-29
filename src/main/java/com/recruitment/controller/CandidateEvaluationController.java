package com.recruitment.controller;

import com.recruitment.dto.CandidateEvaluationRequest;
import com.recruitment.model.Candidate;
import com.recruitment.model.CandidateEvaluation;
import com.recruitment.model.GeneralReview;
import com.recruitment.model.QuestionBankTemplate;
import com.recruitment.model.QuestionReview;
import com.recruitment.model.ScreeningReview;
import com.recruitment.repository.CandidateEvaluationRepository;
import com.recruitment.repository.CandidateRepository;
import com.recruitment.repository.QuestionBankTemplateRepository;
import com.recruitment.service.CandidateEvaluationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/candidate-evaluations")
public class CandidateEvaluationController {

    private final CandidateRepository candidateRepository;
    private final CandidateEvaluationRepository candidateEvaluationRepository;
    private final QuestionBankTemplateRepository questionBankTemplateRepository;
    private  final CandidateEvaluationService candidateEvaluationService;
    public CandidateEvaluationController( CandidateRepository candidateRepository,
                                          CandidateEvaluationRepository candidateEvaluationRepository,
                                          QuestionBankTemplateRepository questionBankTemplateRepository , CandidateEvaluationService candidateEvaluationService ) {
        this.candidateRepository = candidateRepository;
        this.candidateEvaluationRepository = candidateEvaluationRepository;
        this.questionBankTemplateRepository = questionBankTemplateRepository;
        this.candidateEvaluationService = candidateEvaluationService;
    }

    /**
     * 2) Get all evaluations
     */
    @GetMapping
    public ResponseEntity<List<CandidateEvaluation>> getAllEvaluations() {
        log.info("Fetching all candidate evaluations...");
        List<CandidateEvaluation> evaluations = candidateEvaluationService.getAllEvaluations();
        log.info("Found {} evaluations.", evaluations.size());
        return ResponseEntity.ok(evaluations);
    }

    /**
     * 3) Get a single evaluation by its ID
     */
    @GetMapping("/by-id/{evaluationId}")
    public ResponseEntity<CandidateEvaluation> getEvaluationById(@PathVariable Long evaluationId) {
        log.info("Fetching evaluation ID: {}", evaluationId);
        CandidateEvaluation evaluation = candidateEvaluationService.getEvaluationById(evaluationId);
        return ResponseEntity.ok(evaluation);
    }

    /**
     * 4) Get an evaluation by candidate
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<CandidateEvaluation> getEvaluationByCandidate(@PathVariable Long candidateId) {
        log.info("Fetching evaluation for candidate ID: {}", candidateId);
        CandidateEvaluation evaluation = candidateEvaluationService.getEvaluationByCandidate(candidateId);
        return ResponseEntity.ok(evaluation);
    }

    /**
     * 5) Delete an evaluation by ID
     */
    @DeleteMapping("/{evaluationId}")
    public ResponseEntity<String> deleteEvaluation(@PathVariable Long evaluationId) {
        log.info("Deleting evaluation ID: {}", evaluationId);
        candidateEvaluationService.deleteEvaluation(evaluationId);
        return ResponseEntity.ok("Evaluation deleted successfully.");
    }
    @PostMapping("/{candidateId}")
    public ResponseEntity<CandidateEvaluation> evaluateCandidate(
            @PathVariable Long candidateId,
            @RequestBody CandidateEvaluationRequest request
    ) {
        log.info("Received evaluation request for candidate ID: {}", candidateId);
        CandidateEvaluation evaluation = candidateEvaluationService.evaluateCandidate(candidateId, request);
        log.info("Successfully saved evaluation ID: {}", evaluation.getId());
        return ResponseEntity.ok(evaluation);
    }

}
