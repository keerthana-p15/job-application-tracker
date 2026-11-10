package com.keerthana.jobtracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    private static final String EMAIL = "a@example.com";

    @Mock
    private JobApplicationRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobApplicationService service;

    private User givenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        return user;
    }

    private JobApplication newApplication(String company, String status) {
        JobApplication app = new JobApplication();
        app.setCompany(company);
        app.setRole("Software Developer");
        app.setStatus(status);
        app.setAppliedDate(LocalDate.now());
        return app;
    }

    @Test
    void add_setsTheLoggedInUserAsOwner() {
        User user = givenUserExists();
        when(repository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        JobApplication result = service.add(EMAIL, newApplication("TCS", "Applied"));

        assertEquals(user, result.getUser());
    }

    @Test
    void add_ignoresAnIdSentByTheClient() {
        givenUserExists();
        when(repository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));
        JobApplication app = newApplication("TCS", "Applied");
        app.setId(99L);

        JobApplication result = service.add(EMAIL, app);

        assertNull(result.getId());
    }

    @Test
    void getById_returnsTheApplicationWhenItBelongsToTheUser() {
        User user = givenUserExists();
        JobApplication app = newApplication("Infosys", "Applied");
        when(repository.findByIdAndUser(5L, user)).thenReturn(Optional.of(app));

        JobApplication result = service.getById(EMAIL, 5L);

        assertEquals("Infosys", result.getCompany());
    }

    @Test
    void getById_throwsNotFoundWhenApplicationIsMissingOrNotOwned() {
        User user = givenUserExists();
        when(repository.findByIdAndUser(5L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(EMAIL, 5L));
    }

    @Test
    void update_changesTheFields() {
        User user = givenUserExists();
        JobApplication existing = newApplication("TCS", "Applied");
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existing));
        when(repository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        JobApplication changes = newApplication("TCS", "Interview");
        changes.setNotes("Interview next week");

        JobApplication result = service.update(EMAIL, 1L, changes);

        assertEquals("Interview", result.getStatus());
        assertEquals("Interview next week", result.getNotes());
    }

    @Test
    void delete_removesTheApplication() {
        User user = givenUserExists();
        JobApplication existing = newApplication("TCS", "Applied");
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existing));

        service.delete(EMAIL, 1L);

        verify(repository).delete(existing);
    }

    @Test
    void delete_doesNotRemoveAnotherUsersApplication() {
        User user = givenUserExists();
        when(repository.findByIdAndUser(7L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(EMAIL, 7L));
        verify(repository, never()).delete(any(JobApplication.class));
    }

    @Test
    void search_withStatus_usesTheStatusQuery() {
        User user = givenUserExists();
        JobApplication app = newApplication("Wipro", "Applied");
        when(repository.findByUserAndStatusIgnoreCase(eq(user), eq("Applied"), any(Sort.class)))
                .thenReturn(List.of(app));

        List<JobApplication> result = service.search(EMAIL, "Applied", null, "desc");

        assertEquals(1, result.size());
        assertEquals("Wipro", result.get(0).getCompany());
    }

    @Test
    void getStats_countsEachStatusForTheUser() {
        User user = givenUserExists();
        when(repository.countByUserAndStatus(user, "Applied")).thenReturn(2L);

        Map<String, Long> stats = service.getStats(EMAIL);

        assertEquals(2L, stats.get("Applied"));
        assertEquals(0L, stats.get("Offer"));
    }
}