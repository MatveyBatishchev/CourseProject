package com.example.controllers;

import com.example.models.Patient;
import com.example.services.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllUnmoderatedReviews(ModelAndView modelAndView) {
        return reviewService.findAllReviewsView(modelAndView);
    }

    @GetMapping("/doctor/{doctorId}/page/{pageNumber}")
    @ResponseBody
    public String getDoctorsReviewsWithPageJson(@PathVariable("pageNumber") Integer pageNumber, @PathVariable("doctorId") Long doctorId) {
        return reviewService.getDoctorsReviewsWithPageJson(pageNumber, doctorId);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseBody
    public String addNewReviewForDoctor(@AuthenticationPrincipal Patient patient, @RequestParam("reviewComment") String reviewComment,
                                        @RequestParam("doctorId") Long doctorId) {
        return reviewService.addNewReview(patient, reviewComment, doctorId);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public void approvewReview(@PathVariable("id") Long reviewId) {
        reviewService.approveReviewById(reviewId);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public void deleteReview(@PathVariable("id") Long reviewId) {
        reviewService.deleteReviewById(reviewId);
    }


}
