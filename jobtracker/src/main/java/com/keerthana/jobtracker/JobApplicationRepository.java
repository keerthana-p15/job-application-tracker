
package com.keerthana.jobtracker;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatusIgnoreCase(String status, Sort sort);

    List<JobApplication> findByCompanyContainingIgnoreCase(String company, Sort sort);

    List<JobApplication> findByStatusIgnoreCaseAndCompanyContainingIgnoreCase(
            String status, String company, Sort sort);

    long countByStatus(String status);
}
