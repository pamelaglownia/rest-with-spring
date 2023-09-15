package com.baeldung.rws.contract.simplified;

import static com.baeldung.rws.commons.contract.SimpleRequestBodyBuilder.fromResource;
import static java.util.Map.entry;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.baeldung.rws.commons.contract.SimpleContractWebTestClient;
import com.baeldung.rws.commons.contract.SimpleRequestBodyBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContractProjectsSimplifiedApiIntegrationTest {

    @Autowired
    SimpleContractWebTestClient webClient;

    @Value("classpath:project-template.json")
    Resource resource;

    @Value("classpath:task.json")
    Resource taskResource;

    // GET - single

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseContainsFields() {
        webClient.get("/projects/1")
            .containsFields("id", "code", "name", "description", "tasks..name");

    }

    // GET - list

    @Test
    void givenPreloadedData_whenGetProjectsUsingTuples_thenResponseFieldsMatch() { // @formatter:off
        // We can use a fluent API to use the wrapper methods that are more suitable for the validations we want to run
        webClient.get("/projects")
            .fieldsMatch(entry("length()", greaterThanOrEqualTo(2)))
            .listFieldsMatch(
                entry("code", hasItems("P1", "P2", "P3")),
                entry("tasks..name", hasItems("Task 1", "Task 2", "Task 3", "Task 4")));
    } // @formatter:on

    // POST - create

    @Test
    void whenCreateNewProject_thenCreatedAndResponseFieldsMatch() throws Exception {// @formatter:off
        String newProjectBody = baseProjectInput()
            .with("code", "TEST-C-S-PROJECT-NEW-1")
            .with("name", "Test - New JSON Project 1")
            .with("description", "Description of new JSON test project 1")
            .build();

        webClient.create("/projects", newProjectBody)
            .fieldsMatch(
                entry("id", greaterThan(3)),
                entry("tasks", empty()),
                entry("code", equalTo("TEST-C-S-PROJECT-NEW-1")),
                entry("name", equalTo("Test - New JSON Project 1")),
                entry("description", equalTo("Description of new JSON test project 1")));
    } // @formatter:on

    // POST - new - validations

    @Test
    void whenCreateNewProjectWithoutRequiredCodeField_thenBadRequest() throws Exception {
        // null code
        String nullCodeProjectBody = baseProjectInput().withNull("code")
            .build();

        webClient.requestWithResponseStatus("/projects", HttpMethod.POST, nullCodeProjectBody, HttpStatus.BAD_REQUEST);
    }

    // PUT - update

    @Test
    void givenPreloadedData_whenUpdateExistingProject_thenOkWithSupportedFieldUpdated() throws Exception { // @formatter:off
        String updatedProjectBody = baseProjectInput()
            .with("tasks", Arrays.asList(taskResource))
            .with("code", "UPDATED-CODE")
            .with("name", "Updated Name")
            .with("description", "Updated Description")
            .build();

        
        webClient.put("/projects/2", updatedProjectBody)
            .fieldsMatch(
                entry("id", equalTo(2)),
                entry("code", not(equalTo("UPDATED-CODE"))),
                entry("name", equalTo("Updated Name")),
                entry("description", equalTo("Updated Description")),
                entry("tasks", not(empty())),
                entry("tasks..name", not(hasItem("Test - Template Task Name"))));
     
    } // @formatter:on

    @Test
    void givenPreloadedData_whenUpdateNonExistingProject_thenNotFound() throws Exception {
        String updatedProjectBody = generateProjectInput();

        webClient.requestWithResponseStatus("/projects/99", HttpMethod.PUT, updatedProjectBody, HttpStatus.NOT_FOUND);
    }

    // PUT - update - validations

    @Test
    void givenPreloadedData_whenUpdateWithInvalidFields_thenBadRequest() throws Exception {
        // null name
        String nullNameProjectBody = baseProjectInput().withNull("name")
            .build();

        webClient.requestWithResponseStatus("/projects/2", HttpMethod.PUT, nullNameProjectBody, HttpStatus.BAD_REQUEST);
    }

    private String generateProjectInput() throws IOException {
        return fromResource(this.resource).withRandom("code")
            .build();
    }

    private SimpleRequestBodyBuilder baseProjectInput() throws IOException {
        return fromResource(this.resource).withRandom("code");
    }
}
