package com.example.repo;

import com.example.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByDoctorIdAndApproved(Long doctorId, boolean approved, Pageable pageable);

    List<Review> findByApprovedOrderByDateDesc(boolean approved);
}
