
package com.keerthana.jobtracker;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JobApplicationValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
    }

    private JobApplication validApplication() {
        JobApplication app = new JobApplication();
        app.setCompany("TCS");
        app.setRole("Software Developer");
        app.setStatus("Applied");
        app.setAppliedDate(LocalDate.now());
        return app;
    }

    private boolean hasViolationOn(JobApplication app, String field) {
        Set<ConstraintViolation<JobApplication>> violations =
                validator.validate(app);

        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    @Test
    void validApplication_hasNoViolations() {
        assertTrue(validator.validate(validApplication()).isEmpty());
    }

    @Test
    void blankCompany_isRejected() {
        JobApplication app = validApplication();
        app.setCompany("  ");

        assertTrue(hasViolationOn(app, "company"));
    }

    @Test
    void unknownStatus_isRejected() {
        JobApplication app = validApplication();
        app.setStatus("banana");

        assertTrue(hasViolationOn(app, "status"));
    }

    @Test
    void futureAppliedDate_isRejected() {
        JobApplication app = validApplication();
        app.setAppliedDate(LocalDate.now().plusDays(1));

        assertTrue(hasViolationOn(app, "appliedDate"));
    }

    @Test
    void missingAppliedDate_isRejected() {
        JobApplication app = validApplication();
        app.setAppliedDate(null);

        assertTrue(hasViolationOn(app, "appliedDate"));
    }
}
