package com.baeldung.rws.endtoend.spring;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collections;
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
public class ProjectsEndToEndApiIntegrationTest {

    @Autowired
    WebTestClient webClient;

    // GET - single

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseIsEqualToExpectedObject() {
        webClient.get()
            .uri("/projects/3")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ProjectDto.class)
            .isEqualTo(new ProjectDto(3L, "P3", "Project 3", "About Project 3", Collections.emptySet()));
    }

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseContainsFields() {
        webClient.get()
            .uri("/projects/1")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ProjectDto.class)
            .value(dto -> {
                assertThat(dto.id()).isEqualTo(1L);
                assertThat(dto.code()).isNotBlank();
                assertThat(dto.name()).isNotBlank();
                assertThat(dto.description()).isNotBlank();
            });
    }

    // GET - list

    @Test
    void givenPreloadedData_whenGetProjects_thenResponseFieldsMatch() {
        webClient.get()
            .uri("/projects")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(ProjectDto.class)
            .value(projectsList -> {
                assertThat(projectsList).hasSizeGreaterThanOrEqualTo(2);
                assertThat(projectsList).extracting(ProjectDto::code)
                    .contains("P1", "P2", "P3");
                assertThat(projectsList).flatExtracting(ProjectDto::tasks)
                    .extracting(TaskDto::name)
                    .contains("Task 1", "Task 2", "Task 3", "Task 4");
            });

    }

    // POST - create

    @Test
    void whenCreateNewProject_thenCreatedAndResponseFieldsMatch() {
        ProjectDto newProjectBody = new ProjectDto(null, "TEST-E2E-PROJECT-NEW-1", "Test - New Project 1", "Description of new test project 1", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProjectDto.class)
            .value(resultingDto -> {
                ProjectDto expectedResult = new ProjectDto(resultingDto.id(), newProjectBody.code(), newProjectBody.name(), newProjectBody.description(), emptySet());
                assertThat(resultingDto).isEqualTo(expectedResult);
            });
    }

    @Test
    void whenCreateNewProjectWithDuplicatedCode_thenBadRequest() {
        ProjectDto newProjectBody = new ProjectDto(null, "TEST-E2E-PROJECT-NEW-2", "Test - New Project 3", "Description of new test project 3", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isCreated();

        ProjectDto newDuplicatedCodeProjectBody = new ProjectDto(null, "TEST-E2E-PROJECT-NEW-2", "Test - New Project 4", "Description of new test project 4", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newDuplicatedCodeProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    // POST - new - validations

    @Test
    void whenCreateNewProjectWithoutRequiredCodeField_thenBadRequest() {
        // null code
        ProjectDto nullCodeProjectBody = new ProjectDto(null, null, "Test - New Project Invalid", "Description of new test project Invalid", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(nullCodeProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    // PUT - update

    @Test
    void givenPreloadedData_whenUpdateExistingProject_thenOkWithSupportedFieldUpdated() {
        TaskDto taskBody = new TaskDto(null, null, "Test - Task X12", "Description of task", LocalDate.of(2030, 01, 01), TaskStatus.DONE, null, null);
        Set<TaskDto> tasksListBody = Set.of(new TaskDto(null, null, "Test - Task X12", "Description of task", LocalDate.of(2030, 01, 01), TaskStatus.DONE, null, null));
        ProjectDto updatedProjectBody = new ProjectDto(null, "TEST-E2E-PROJECT-UPDATED-1", "Test - Updated Project 2", "Description of updated test project 2", tasksListBody);

        webClient.put()
            .uri("/projects/2")
            .body(Mono.just(updatedProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ProjectDto.class)
            .value(dto -> {
                assertThat(dto.id()).isEqualTo(2L);
                assertThat(dto.code()).isNotEqualTo(updatedProjectBody.code());
                assertThat(dto.name()).isEqualTo(updatedProjectBody.name());
                assertThat(dto.description()).isEqualTo(updatedProjectBody.description());
                assertThat(dto.tasks()).isNotEmpty()
                    .noneMatch(task -> task.name()
                        .equals(taskBody.name()));
            });
    }

    @Test
    void givenPreloadedData_whenUpdateNonExistingProject_thenNotFound() {
        ProjectDto updatedProjectBody = new ProjectDto(null, null, "Test - Updated Project 2", "Description of updated test project 2", null);

        webClient.put()
            .uri("/projects/99")
            .body(Mono.just(updatedProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    // PUT - update - validations

    @Test
    void givenPreloadedData_whenUpdateWithInvalidFields_thenBadRequest() {
        // null name
        ProjectDto nullNameProjectBody = new ProjectDto(null, "TEST-E2E-PROJECT-UPDATED-3", null, "Description of updated test project 3", null);

        webClient.put()
            .uri("/projects/2")
            .body(Mono.just(nullNameProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

}
