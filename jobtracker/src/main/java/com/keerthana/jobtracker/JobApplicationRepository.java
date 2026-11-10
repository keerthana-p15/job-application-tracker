package com.keerthana.jobtracker;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Optional<JobApplication> findByIdAndUser(Long id, User user);

    List<JobApplication> findByUser(User user, Sort sort);

    List<JobApplication> findByUserAndStatusIgnoreCase(User user, String status, Sort sort);

    List<JobApplication> findByUserAndCompanyContainingIgnoreCase(User user, String company, Sort sort);

    List<JobApplication> findByUserAndStatusIgnoreCaseAndCompanyContainingIgnoreCase(
            User user, String status, String company, Sort sort);

    long countByUserAndStatus(User user, String status);
}