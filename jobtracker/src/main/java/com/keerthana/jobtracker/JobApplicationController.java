package com.keerthana.jobtracker;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplication add(Principal principal, @Valid @RequestBody JobApplication application) {
        return service.add(principal.getName(), application);
    }

    @GetMapping
    public List<JobApplication> getAll(
            Principal principal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "desc") String sort) {
        return service.search(principal.getName(), status, company, sort);
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats(Principal principal) {
        return service.getStats(principal.getName());
    }

    @GetMapping("/{id}")
    public JobApplication getById(Principal principal, @PathVariable Long id) {
        return service.getById(principal.getName(), id);
    }

    @PutMapping("/{id}")
    public JobApplication update(Principal principal, @PathVariable Long id,
                                 @Valid @RequestBody JobApplication application) {
        return service.update(principal.getName(), id, application);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Principal principal, @PathVariable Long id) {
        service.delete(principal.getName(), id);
    }
}