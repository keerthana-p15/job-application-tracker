package com.keerthana.jobtracker;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository repository,
                                 UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public JobApplication add(String email, JobApplication application) {
        application.setId(null);
        application.setUser(getUser(email));
        return repository.save(application);
    }

    public List<JobApplication> search(String email, String status, String company, String sortDir) {
        User user = getUser(email);

        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by("appliedDate").ascending()
                : Sort.by("appliedDate").descending();

        boolean hasStatus = status != null && !status.isBlank();
        boolean hasCompany = company != null && !company.isBlank();

        if (hasStatus && hasCompany) {
            return repository.findByUserAndStatusIgnoreCaseAndCompanyContainingIgnoreCase(
                    user, status, company, sort);
        } else if (hasStatus) {
            return repository.findByUserAndStatusIgnoreCase(user, status, sort);
        } else if (hasCompany) {
            return repository.findByUserAndCompanyContainingIgnoreCase(user, company, sort);
        }
        return repository.findByUser(user, sort);
    }

    public JobApplication getById(String email, Long id) {
        return repository.findByIdAndUser(id, getUser(email))
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));
    }

    public JobApplication update(String email, Long id, JobApplication updated) {
        JobApplication existing = getById(email, id);
        existing.setCompany(updated.getCompany());
        existing.setRole(updated.getRole());
        existing.setStatus(updated.getStatus());
        existing.setAppliedDate(updated.getAppliedDate());
        existing.setNotes(updated.getNotes());
        return repository.save(existing);
    }

    public void delete(String email, Long id) {
        repository.delete(getById(email, id));
    }

    public Map<String, Long> getStats(String email) {
        User user = getUser(email);
        Map<String, Long> stats = new LinkedHashMap<>();
        for (String s : List.of("Applied", "Interview", "Offer", "Rejected")) {
            stats.put(s, repository.countByUserAndStatus(user, s));
        }
        return stats;
    }
}