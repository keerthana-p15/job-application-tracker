package com.keerthana.jobtracker;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationRepository repository;

    public JobApplicationController(JobApplicationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public JobApplication addApplication(@RequestBody JobApplication application) {
        return repository.save(application);
    }

    @GetMapping
    public List<JobApplication> getAllApplications() {
        return repository.findAll();
    }
}