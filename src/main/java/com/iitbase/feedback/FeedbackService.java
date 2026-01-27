package com.iitbase.feedback;

import com.iitbase.feedback.dto.FeedbackRequest;
import com.iitbase.feedback.Feedback;
import com.iitbase.feedback.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Transactional
    public Feedback createFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackType(request.getFeedbackType());
        feedback.setName(request.getName());
        feedback.setEmail(request.getEmail());
        feedback.setSubject(request.getSubject());
        feedback.setMessage(request.getMessage());

        return feedbackRepository.save(feedback);
    }

    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }
}