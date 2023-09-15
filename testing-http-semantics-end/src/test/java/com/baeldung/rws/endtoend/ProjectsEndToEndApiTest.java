package com.baeldung.rws.endtoend;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.baeldung.rws.domain.model.TaskStatus;
import com.baeldung.rws.web.dto.ProjectDto;
import com.baeldung.rws.web.dto.TaskDto;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProjectsEndToEndApiTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void givenRunningService_whenGetSingleProject_thenExpectStatus() {
        webClient.get()
            .uri("/projects/3")
            .exchange()
            .expectStatus()
            .isOk();
    }

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseBodyContainsExpectedValues() {
        webClient.get()
            .uri("/projects/3")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ProjectDto.class)
            .isEqualTo(new ProjectDto(3L, "P3", "Project 3", "About Project 3", Collections.emptySet()));
    }

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseFieldsMatch() {
        webClient.get()
            .uri("/projects/3")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ProjectDto.class)
            .value(dto -> {
                assertThat(dto.id()).isEqualTo(3L);
                assertThat(dto.description()).isNotBlank();
                assertThat(dto.name()).contains("Project");
                assertThat(dto.code()).doesNotContainAnyWhitespaces();
            });
    }

    @Test
    void givenPreloadedData_whenGetProjects_thenResponseFieldsMatch() {
        webClient.get()
            .uri("/projects")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(ProjectDto.class)
            .value(projectsList -> {
                assertThat(projectsList).hasSizeGreaterThanOrEqualTo(3);
                assertThat(projectsList).extracting(ProjectDto::code)
                    .contains("P1", "P2", "P3");
                assertThat(projectsList).flatExtracting(ProjectDto::tasks)
                    .extracting(TaskDto::name)
                    .contains("Task 1", "Task 2", "Task 3", "Task 4");
            });
    }

    @Test
    void givenPreloadedData_whenGetNonExistingProject_thenNotFoundErrorWithUnknownStructure() {
        ParameterizedTypeReference<Map<String, Object>> mapType = new ParameterizedTypeReference<Map<String, Object>>() {
        };
        webClient.get()
            .uri("/projects/99")
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody(mapType)
            .value(mapResponseBody -> {
                assertThat(mapResponseBody).containsEntry("status", 404);
                assertThat(mapResponseBody).containsEntry("title", "Not Found");
            });
    }

    @Test
    void givenPreloadedData_whenGetProjectsMappingToListOfMaps_thenListMappedCorrectly() {
        ParameterizedTypeReference<Collection<Map<String, Object>>> listOfElementsType = new ParameterizedTypeReference<Collection<Map<String, Object>>>() {
        };
        webClient.get()
            .uri("/projects")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(listOfElementsType)
            .value(mapResponseBody -> {
                assertThat(mapResponseBody).hasSizeGreaterThan(1);
            });
    }

    @Test
    void givenPreloadedData_whenGetNonExistingProject_thenNotFoundErrorWithProblemDetailsFormat() {
        webClient.get()
            .uri("/projects/99")
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectHeader()
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody(ProblemDetail.class)
            .value(problemDetailResponseBody -> {
                assertThat(problemDetailResponseBody.getStatus()).isEqualTo(404);
                assertThat(problemDetailResponseBody.getTitle()).isEqualTo("Not Found");
            });

    }

    @Test
    void whenCreateNewProject_thenIsCreated() {
        ProjectDto newProjectBody = new ProjectDto(null, "PROJECT-NEW-CODE", "Test - New Project 1", "Description of new test project 1", null);

        webClient.post()
            .uri("/projects")
            .body(Mono.just(newProjectBody), ProjectDto.class)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProjectDto.class)
            .value(newProject -> {
                assertThat(newProject.id()).isNotNull();
                assertThat(newProject.tasks()).isNotNull();
                assertThat(newProject.tasks()).isEmpty();
                assertThat(newProject.code()).isEqualTo(newProjectBody.code());
                assertThat(newProject.name()).isEqualTo(newProjectBody.name());
                assertThat(newProject.description()).isEqualTo(newProjectBody.description());
            });
    }

    @Test
    void whenCreateNewProjectWithDuplicatedCode_thenBadRequest() {
        ProjectDto newProjectBody = new ProjectDto(null, "PROJECT-DUPLICATED-CODE", "Test - New Project 2", "Description of new test project 2", null);

        webClient.post()
            .uri("/projects")
            .bodyValue(newProjectBody)
            .exchange()
            .expectStatus()
            .isCreated();

        ProjectDto newDuplicatedCodeProjectBody = new ProjectDto(null, "PROJECT-DUPLICATED-CODE", "Test - New Project 3", "Description of new test project 3", null);

        webClient.post()
            .uri("/projects")
            .bodyValue(newDuplicatedCodeProjectBody)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void givenPreloadedData_whenUpdateExistingProject_thenOkWithSupportedFieldUpdated() {
        TaskDto taskBody = new TaskDto(null, null, "Test - Task", "Description of task", LocalDate.of(2030, 01, 01), TaskStatus.DONE, null, null);
        Set<TaskDto> tasksListBody = Set.of(taskBody);
        ProjectDto updatedProjectBody = new ProjectDto(null, "PROJECT-CODE-UPDATED", "Test - Updated Project 1", "Description of updated test project 1", tasksListBody);

        webClient.put()
            .uri("/projects/2")
            .bodyValue(updatedProjectBody)
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
    void givenPreloadedData_whenUpdateExistingProjectWithInvalidFields_thenBadRequest() {
        // null name
        ProjectDto nullNameProjectBody = new ProjectDto(null, "PROJECT-CODE-UPDATED", null, "Description of updated test project 2", null);

        webClient.put()
            .uri("/projects/2")
            .bodyValue(nullNameProjectBody)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void whenCreateNewProjectWithContentTypeText_thenResponseIncludeAppropriateHeaders() {
        webClient.post()
            .uri("/projects")
            .contentType(MediaType.TEXT_PLAIN)
            .header("Custom-Header", "Custom value")
            .bodyValue("Test - New Project 3")
            .exchange()
            .expectHeader()
            .value(HttpHeaders.ACCEPT, headerValue -> headerValue.contains("application/json"))
            .expectHeader()
            .contentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void whenCreateNewProjectWithContentTypeText_thenUnsupportedMediaType() {
        webClient.post()
            .uri("/projects")
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue("Test - New Project 3")
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .expectHeader()
            .value(HttpHeaders.ACCEPT, headerValue -> headerValue.contains("application/json"));
    }

    @Test
    void whenCreateNewProjectWithContentTypeText_thenClientErrorResponse() {
        webClient.post()
            .uri("/projects")
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue("Test - New Project 3")
            .exchange()
            .expectStatus()
            .is4xxClientError();
    }

    @Test
    void givenPreloadedData_whenCreateProject_thenResponseConsumeWith() {
        ProjectDto newProjectBody = new ProjectDto(null, "PROJECT-CONSUMEWITH", "Test - New Project - consumeWith", "Description of new test project", null);

        webClient.post()
            .uri("/projects")
            .bodyValue(newProjectBody)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProjectDto.class)
            .consumeWith(exchangeResult -> {
                assertThat(exchangeResult.getRequestHeaders()).extractingByKey(HttpHeaders.CONTENT_TYPE)
                    .asList()
                    .contains(MediaType.APPLICATION_JSON_VALUE);
                assertThat(exchangeResult.getResponseCookies()).isEmpty();
                assertThat(exchangeResult.getResponseBody()
                    .code()).isNotNull();
            });
    }
}
