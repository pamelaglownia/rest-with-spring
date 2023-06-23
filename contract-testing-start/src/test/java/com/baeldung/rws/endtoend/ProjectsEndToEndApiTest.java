package com.baeldung.rws.endtoend;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.baeldung.rws.web.dto.ProjectDto;
import com.baeldung.rws.web.dto.TaskDto;

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
}
