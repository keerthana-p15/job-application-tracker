package com.keerthana.jobtracker;

import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Sort;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public JobApplication add(JobApplication application) {
        return repository.save(application);
    }

    public List<JobApplication> getAll() {
        return repository.findAll();
    }

    public JobApplication getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));
    }

    public JobApplication update(Long id, JobApplication updated) {
        JobApplication existing = getById(id);
        existing.setCompany(updated.getCompany());
        existing.setRole(updated.getRole());
        existing.setStatus(updated.getStatus());
        existing.setAppliedDate(updated.getAppliedDate());
        existing.setNotes(updated.getNotes());
        return repository.save(existing);
    }

    public void delete(Long id) {
        JobApplication existing = getById(id);
        repository.delete(existing);
    }
    public List<JobApplication> search(String status, String company, String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by("appliedDate").ascending()
                : Sort.by("appliedDate").descending();

        boolean hasStatus = status != null && !status.isBlank();
        boolean hasCompany = company != null && !company.isBlank();

        if (hasStatus && hasCompany) {
            return repository.findByStatusIgnoreCaseAndCompanyContainingIgnoreCase(status, company, sort);
        } else if (hasStatus) {
            return repository.findByStatusIgnoreCase(status, sort);
        } else if (hasCompany) {
            return repository.findByCompanyContainingIgnoreCase(company, sort);
        }
        return repository.findAll(sort);
    }

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        for (String s : List.of("Applied", "Interview", "Offer", "Rejected")) {
            stats.put(s, repository.countByStatus(s));
        }
        return stats;
    }
}