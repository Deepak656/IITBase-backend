package com.iitbase.feedback;

import com.iitbase.common.ApiResponse;
import com.iitbase.feedback.dto.FeedbackRequest;
import com.iitbase.feedback.Feedback;
import com.iitbase.feedback.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
@Validated
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<Feedback>> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        try {
            Feedback feedback = feedbackService.createFeedback(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, feedback, "Feedback submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to submit feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Feedback>> getFeedback(@PathVariable Long id) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            if (feedback != null) {
                return ResponseEntity.ok(new ApiResponse<>(true, feedback, "Feedback retrieved successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, null, "Feedback not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Error retrieving feedback: " + e.getMessage()));
        }
    }
}