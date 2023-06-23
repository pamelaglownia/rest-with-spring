package com.baeldung.rws.endtoend.simplified;

import static com.baeldung.rws.commons.endtoend.spec.DtoFieldSpec.from;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;

import com.baeldung.rws.domain.model.TaskStatus;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baeldung.rws.commons.endtoend.client.SimpleWebTestClient;
import com.baeldung.rws.commons.endtoend.spec.DtoFieldSpec;
import com.baeldung.rws.endtoend.simplified.utils.ProjectDtoSpec;
import com.baeldung.rws.web.dto.ProjectDto;
import com.baeldung.rws.web.dto.TaskDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectsSimplifiedEndToEndApiTest {

    @Autowired
    SimpleWebTestClient webClient;

    // GET - single - status

    @Test
    void givenPreloadedData_whenGetSingleProject_thenExpectOkStatus() {
        webClient.get("/projects/3", ProjectDto.class);
    }

    // GET - single - FieldSpec

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseBodyContainsExpectedValuesCheckingWithFieldSpecList() {
        DtoFieldSpec<ProjectDto, Long> id = from("id", ProjectDto::id);
        id.define(equalTo(3L));

        DtoFieldSpec<ProjectDto, String> code = DtoFieldSpec.from("code", ProjectDto::code);
        code.define(containsString("P3"));

        webClient.getForFieldSpec("/projects/3", ProjectDto.class)
            .valueMatches(id, code);
    }

    // GET - single - 1

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseBodyContainsExpectedValues() { // @formatter:off
        webClient.get("/projects/3", ProjectDto.class)
            .valueMatches(new ProjectDtoSpec(
                equalTo(3L),
                containsString("P3"),
                anyOf(any(String.class), nullValue()),
                not(blankOrNullString()),
                collectionOfType(TaskDto.class)));
    } // @formatter:on

    // GET - single - 2

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseFieldsMatch() { // @formatter:off
        webClient.get("/projects/3", ProjectDto.class)
            .valueMatches(new ProjectDtoSpec(
                equalTo(3L),
                not(containsString(" ")),
                containsString("Project"),
                not(blankOrNullString()),
                collectionOfType(TaskDto.class)));
    } // @formatter:on

    // GET - list

    @Test
    void givenPreloadedData_whenGetProjects_thenResponseFieldsMatch() {
        ProjectDtoSpec expected1 = new ProjectDtoSpec(equalTo(1L), any(String.class), any(String.class), any(String.class), collectionOfType(TaskDto.class));
        ProjectDtoSpec expected2 = new ProjectDtoSpec(any(Long.class), equalTo("P2"), any(String.class), any(String.class), collectionOfType(TaskDto.class));

        webClient.getList("/projects", ProjectDto.class)
            .contains(expected1, expected2)
            .hasSize(3);
    }

    // POST - create

    @Test
    void whenCreateNewProject_thenCreatedAndResponseFieldsMatch() {
        ProjectDto newProjectBody = new ProjectDto(null, "TEST-E2E-S-PROJECT-NEW-4", "Test - New Project 4", "Description of new test project 4", emptySet());

        webClient.create("/projects", newProjectBody)
                .valueMatches(new ProjectDtoSpec(greaterThan(3L),
                        equalTo("TEST-E2E-S-PROJECT-NEW-4"),
                        equalTo("Test - New Project 4"),
                        equalTo("Description of new test project 4"),
                        emptyIterable()));
    }

    // POST - new - validations

    @Test
    void whenCreateNewProjectWithoutRequiredCodeField_thenBadRequest() {
        // null code
        ProjectDto nullCodeProjectBody = new ProjectDto(null, null, "Test - New Project Invalid", "Description of new test project Invalid", null);

        webClient.requestWithResponseStatus("/projects", HttpMethod.POST, nullCodeProjectBody, HttpStatus.BAD_REQUEST);
    }

    // PUT - update

    @Test
    void givenPreloadedData_whenUpdateExistingProject_thenOkWithSupportedFieldUpdated() {
        TaskDto taskBody = new TaskDto(null, null, "Test - Task X13", "Description of task", LocalDate.of(2030, 01, 01), TaskStatus.DONE, null, null);
        Set<TaskDto> tasksListBody = Set.of(taskBody);
        ProjectDto updatedProjectBody = new ProjectDto(null, "TEST-E2E-S-PROJECT-UPDATED-1", "Test - Updated Project 2", "Description of updated test project 2", tasksListBody);

        webClient.put("/projects/2", updatedProjectBody)
            .valueMatches(new ProjectDtoSpec(equalTo(2L), not(equalTo(updatedProjectBody.code())), equalTo(updatedProjectBody.name()), equalTo(updatedProjectBody.description()), allOf(not(emptyIterable())
            // , not(contains(hasProperty("name", taskBody.name()))) // hasProperty doesn't work for Java Records
            )))
            .value(resultProject -> assertThat(resultProject.tasks()).noneMatch(task -> task.name()
                .equals(taskBody.name())));
    }

    // PUT - update - validations

    @Test
    void givenPreloadedData_whenUpdateWithInvalidFields_thenBadRequest() {
        // null name
        ProjectDto nullNameProjectBody = new ProjectDto(null, "TEST-E2E-S-PROJECT-UPDATED-3", null, "Description of updated test project 3", null);

        webClient.requestWithResponseStatus("/projects/2", HttpMethod.PUT, nullNameProjectBody, HttpStatus.BAD_REQUEST);
    }

    private static <U> Matcher<Iterable<? extends U>> collectionOfType(Class<U> clazz) {
        return everyItem(any(clazz));
    }
}
