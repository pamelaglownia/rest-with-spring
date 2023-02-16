package com.baeldung.rws.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.baeldung.rws.domain.model.TaskStatus;
import com.baeldung.rws.web.dto.ProjectDto;
import com.baeldung.rws.web.dto.TaskDto;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RwsAppProjectsIntegrationTest {

    @Autowired
    WebTestClient webClient;

    // GET - by id

    @Test
    void givenPreloadedData_whenGetProject_thenOk() {
        webClient.get()
            .uri("/projects/1")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(1L)
            .jsonPath("$.code")
            .isNotEmpty()
            .jsonPath("$.name")
            .isNotEmpty()
            .jsonPath("$.description")
            .isNotEmpty()
            .jsonPath("$.tasks..name")
            .isNotEmpty();
    }

    // GET - list

    @Test
    void givenPreloadedData_whenGetProjects_thenOk() {
        webClient.get()
            .uri("/projects")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()")
            .value(greaterThan(1))
            .jsonPath("$..code")
            .isNotEmpty();
    }

    // POST - new

    @Test
    void whenCreateNewProject_thenCreatedWithNoTasks() {
        Set<TaskDto> tasksBody = Set.of(new TaskDto(null, null, "Test - Task X", "Description of task", LocalDate.of(2030, 01, 01), TaskStatus.DONE, null, null, 1));
        ProjectDto newProjectBody = new ProjectDto(null, "TEST-PROJECT-NEW-1", "Test - New Project 1", "Description of new test project 1", tasksBody);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .jsonPath("$.id")
            .value(greaterThan(3))
            .jsonPath("$.code")
            .isEqualTo("TEST-PROJECT-NEW-1")
            .jsonPath("$.name")
            .isEqualTo("Test - New Project 1")
            .jsonPath("$.description")
            .isEqualTo("Description of new test project 1")
            .jsonPath("$.tasks")
            .isEmpty();
    }

    @Test
    void whenCreateNewProjectPresentingExistingId_thenCreatedWithoutUpdatingExistingProject() {
        ProjectDto newProjectBody = new ProjectDto(1L, "TEST-PROJECT-NEW-2", "Test - New Project 2", "Description of new test project 2", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .jsonPath("$.id")
            .value(greaterThan(3))
            .jsonPath("$.code")
            .isEqualTo("TEST-PROJECT-NEW-2")
            .jsonPath("$.name")
            .isEqualTo("Test - New Project 2")
            .jsonPath("$.description")
            .isEqualTo("Description of new test project 2");
    }

    @Test
    void whenCreateNewProjectWithDuplicatedCode_thenServerError() {
        ProjectDto newProjectBody = new ProjectDto(null, "TEST-PROJECT-NEW-3", "Test - New Project 3", "Description of new test project 3", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isCreated();

        ProjectDto newDuplicatedCodeProjectBody = new ProjectDto(null, "TEST-PROJECT-NEW-3", "Test - New Project 4", "Description of new test project 4", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newDuplicatedCodeProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .is5xxServerError();
    }

    @Test
    void whenCreateNewProjectPointingToExistingTasks_thenCreatedWithNoTasks() {
        Set<TaskDto> tasksBody = Set.of(new TaskDto(1L, null, "Test - Task X", "Description of task", LocalDate.of(2030, 01, 01), TaskStatus.DONE, null, null, 1), new TaskDto(2L, "any-uuid", null, null, null, null, null, null, 1));
        ProjectDto newProjectBody = new ProjectDto(null, "TEST-PROJECT-NEW-5", "Test - New Project 5", "Description of new test project 5", tasksBody);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .jsonPath("$.id")
            .value(greaterThan(3))
            .jsonPath("$.code")
            .isEqualTo("TEST-PROJECT-NEW-5")
            .jsonPath("$.tasks")
            .isEmpty();
    }

    // POST - new - validations

    @Test
    void whenCreateNewProjectWithoutRequiredFields_thenBadRequest() {
        // null code
        ProjectDto nullCodeProjectBody = new ProjectDto(null, null, "Test - New Project Invalid", "Description of new test project Invalid", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(nullCodeProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.errors..field")
            .value(hasItem("code"));

        // null name
        ProjectDto nullNameProjectBody = new ProjectDto(null, "TEST-PROJECT-NEW-INVALID", null, "Description of new test project Invalid", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(nullNameProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.errors..field")
            .value(hasItem("name"));

        // short description
        ProjectDto shortDescriptionProjectBody = new ProjectDto(null, "TEST-PROJECT-NEW-VALID-1", "Test - New Project Valid 1", "desc", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(shortDescriptionProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.errors..field")
            .value(hasItem("description"));

        // null description (valid)
        ProjectDto nullDescriptionProjectBody = new ProjectDto(null, "TEST-PROJECT-NEW-VALID-1", "Test - New Project Valid 1", null, null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(nullDescriptionProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isCreated();
    }

    // PUT - update

    @Test
    void givenPreloadedData_whenUpdateExistingProject_thenOkWithSupportedFieldUpdated() {
        Set<TaskDto> tasksBody = Set.of(new TaskDto(null, null, "Test - Task X", "Description of task", LocalDate.of(2030, 01, 01), TaskStatus.DONE, null, null, 1));
        ProjectDto updatedProjectBody = new ProjectDto(null, "TEST-PROJECT-UPDATED-1", "Test - Updated Project 2", "Description of updated test project 2", tasksBody);

        webClient.put()
            .uri("/projects/2")
            .body(Mono.just(updatedProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(2L)
            .jsonPath("$.code")
            .value(not("TEST-PROJECT-UPDATED-1"))
            .jsonPath("$.name")
            .isEqualTo("Test - Updated Project 2")
            .jsonPath("$.description")
            .isEqualTo("Description of updated test project 2")
            .jsonPath("$.tasks")
            .isNotEmpty()
            .jsonPath("$.tasks..name")
            .value(not(hasItem("Test - Task X")));
    }

    @Test
    void givenPreloadedData_whenUpdateNonExistingProject_thenNotFound() {
        ProjectDto updatedProjectBody = new ProjectDto(null, "TEST-PROJECT-UPDATED-2", "Test - Updated Project 2", "Description of updated test project 2", null);

        webClient.put()
            .uri("/projects/99")
            .body(Mono.just(updatedProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.error")
            .isNotEmpty();
    }

    @Test
    void givenPreloadedData_whenUpdateExistingProjectUsingExistingTask_thenTaskNotSwitchedToProject() {
        // create Task assigning it to Project 1
        TaskDto newTaskBody = new TaskDto(null, null, "Test - Project Task X", "Description of Project task", LocalDate.of(2030, 01, 01), null, 1L, null, 1);

        TaskDto newTask = webClient.post()
            .uri("/tasks")
            .body(Mono.just(newTaskBody), TaskDto.class)
            .exchange()
            .expectStatus()
            .isCreated()
            .returnResult(TaskDto.class)
            .getResponseBody()
            .blockFirst();

        Long newTaskId = newTask.id();

        assertThat(newTaskId).isPositive();

        Set<TaskDto> tasksBody = Set.of(newTask);
        ProjectDto updatedProjectBody = new ProjectDto(null, "TEST-PROJECT-UPDATED-3", "Test - Updated Project 3", "Description of updated test project 3", tasksBody);

        webClient.put()
            .uri("/projects/2")
            .body(Mono.just(updatedProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(2L)
            .jsonPath("$.name")
            .isEqualTo("Test - Updated Project 3")
            .jsonPath("$.tasks..id")
            .value(not(hasItem(newTaskId)));

        webClient.get()
            .uri("/tasks/" + newTaskId)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.projectId")
            .isEqualTo(1L);
    }

    // PUT - update - validations

    @Test
    void givenPreloadedData_whenUpdateWithInvalidFields_thenErrors() {
        // null name
        ProjectDto nullNameProjectBody = new ProjectDto(null, "TEST-PROJECT-UPDATED-3", null, "Description of updated test project 3", null);

        webClient.put()
            .uri("/projects/2")
            .body(Mono.just(nullNameProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .is4xxClientError();

        // short description
        ProjectDto shortDescriptionProjectBody = new ProjectDto(null, "TEST-PROJECT-UPDATED-3", "Test - Updated Project 4", "Desc", null);

        webClient.put()
            .uri("/projects/2")
            .body(Mono.just(shortDescriptionProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.errors..field")
            .value(hasItem("description"));

        // null code
        ProjectDto nullCodeProjectBody = new ProjectDto(null, null, "Test - Updated Project 4", "Description of updated test project 4", null);

        webClient.put()
            .uri("/projects/2")
            .body(Mono.just(nullCodeProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .is4xxClientError();
    }
}
