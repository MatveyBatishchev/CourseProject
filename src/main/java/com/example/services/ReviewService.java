package com.example.services;

import com.example.models.Doctor;
import com.example.models.Patient;
import com.example.models.Review;
import com.example.repo.DoctorRepository;
import com.example.repo.ReviewRepository;
import com.example.util.PatientSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Patient.class, new PatientSerializer()).setDateFormat("dd.MM.yyyy").excludeFieldsWithoutExposeAnnotation().create();
    private static final int pageSize = 3;

    public ModelAndView findAllReviewsView(ModelAndView modelAndView) {
        modelAndView.addObject("reviews", reviewRepository.findByApprovedOrderByDateDesc(false));
        modelAndView.setViewName("doctors/reviewModeration");
        return modelAndView;
    }

    public String addNewReview(Patient patient, String reviewComment, Long doctorId) {
        Review review = new Review();
        Doctor doctorById = doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Доктор с id " + doctorId + " не был найден!"));
        if (doctorById.getReviews().stream().anyMatch(r -> r.getPatient().getId().equals(patient.getId()))) {
            return "К сожалению, вы уже оставляли отзыв данному специалисту!";
        }
        review.setComment(reviewComment);
        review.setApproved(false);
        review.setDate(new Date(new java.util.Date().getTime()));
        review.setDoctor(doctorById);
        review.setPatient(patient);
        reviewRepository.save(review);
        return "Спасибо за ваш отзыв, он был отправлен на модерацию. Вы помогаете нам становиться лучше!";
    }

    public String getDoctorsReviewsWithPageJson(int pageNumber, Long doctorId) {
        return gson.toJson(reviewRepository.findByDoctorIdAndApproved(doctorId, true,  PageRequest.of(pageNumber, pageSize, Sort.by("date").descending())).getContent());
    }

    public void approveReviewById(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Отзыв c id " + id + " не был найден"));
        review.setApproved(true);
        reviewRepository.save(review);
    }

    public void deleteReviewById(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
