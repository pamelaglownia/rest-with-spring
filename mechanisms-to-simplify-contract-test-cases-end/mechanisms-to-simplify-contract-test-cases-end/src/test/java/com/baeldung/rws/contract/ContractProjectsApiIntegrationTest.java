package com.baeldung.rws.contract;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ContractProjectsApiIntegrationTest {

    @Autowired
    WebTestClient webClient;

    @Value("classpath:project-template.json")
    Resource templateResource;

    @Test
    void givenPreloadedData_whenGetProjects_thenResponseFieldsMatch() {
        webClient.get()
            .uri("/projects")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$[?(@.code == 'P1')].tasks.length()")
            // .isEqualTo(3) -> we have to be more flexible due to concurrent tests running on the same data
            .value(everyItem(greaterThanOrEqualTo(3)))
            .jsonPath("$[?(@.code == 'P1')].tasks[?(@.name == 'Task 2')].description")
            .isEqualTo("Task 2 Description")
            .jsonPath("$..tasks..name")
            .value(hasItems("Task 1", "Task 2", "Task 3", "Task 4"));
    }

    @Test
    void whenCreateNewProjectFromJsonPatternFile_thenCreatedAndResponseFieldsMatch() throws Exception {
        String newProjectJsonBody = generateProjectJsonFromTemplateFile("TEST-C-PROJECT-NEW-JSON-3", "Test - New JSON Project 3", null);

        webClient.post()
            .uri("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(newProjectJsonBody)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .jsonPath("$.id")
            .value(greaterThan(3))
            .jsonPath("$.code")
            .isEqualTo("TEST-C-PROJECT-NEW-JSON-3")
            .jsonPath("$.name")
            .isEqualTo("Test - New JSON Project 3")
            .jsonPath("$.description")
            .isEqualTo("Template description")
            .jsonPath("$.tasks")
            .isEmpty();
    }

    private String generateProjectJsonFromTemplateFile(String code, String name, String description) throws Exception {
        Reader reader = new InputStreamReader(templateResource.getInputStream(), StandardCharsets.UTF_8);
        String projectTemplate = FileCopyUtils.copyToString(reader);
        JsonNode node = new ObjectMapper().readTree(projectTemplate);
        ObjectNode objectNode = ((ObjectNode) node);
        objectNode.put("code", code);
        if (name != null)
            objectNode.put("name", name);
        if (description != null)
            objectNode.put("description", description);
        return objectNode.toString();
    }
}
