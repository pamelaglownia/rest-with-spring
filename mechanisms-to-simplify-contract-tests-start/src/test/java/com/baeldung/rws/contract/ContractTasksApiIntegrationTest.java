package com.baeldung.rws.contract;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;

import com.baeldung.rws.domain.model.TaskStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ContractTasksApiIntegrationTest {

    @Autowired
    WebTestClient webClient;

    @Value("classpath:task.json")
    Resource resource;

    // GET - single

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseContainsFields() { // @formatter:off
        webClient.get()
            .uri("/tasks/1")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .value(equalTo(1L), Long.class)
            .jsonPath("$.name")
            .exists()
            .jsonPath("$.status")
            .isEqualTo(TaskStatus.TO_DO.toValue())
            .jsonPath("$.description")
            .value(containsString("Description"));
    } // @formatter:on

    @Test
    void createNewTask_withValidTaskJsonInput_thenSuccess() throws Exception {
        String taskJson = generateTaskInput();

        webClient.post()
            .uri("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(taskJson)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .jsonPath("$.id")
            .exists()
            .jsonPath("$.name")
            .isEqualTo("Test - Template Task 1")
            .jsonPath("$.projectId")
            .value(greaterThan(0))
            .jsonPath("$.dueDate")
            .isEqualTo("2050-12-31");
    }

    private String generateTaskInput() throws Exception {
        Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }
}
