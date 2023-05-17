package com.baeldung.rws.endtoend.simplified;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baeldung.rws.commons.endtoend.client.SimpleWebTestClient;
import com.baeldung.rws.endtoend.simplified.utils.ProjectDtoSpec;
import com.baeldung.rws.web.dto.ProjectDto;
import com.baeldung.rws.web.dto.TaskDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectsSimplifiedEndToEndApiTest {

    @Autowired
    SimpleWebTestClient webClient;

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseBodyContainsExpectedValues() {
        webClient.get("/projects/3", ProjectDto.class)
            .valueMatches(new ProjectDtoSpec(equalTo(3L), equalTo("P3"), equalTo("Project 3"), equalTo("About Project 3"), collectionOfType(TaskDto.class)));
    }

    @Test
    void givenPreloadedData_whenGetSingleProject_thenResponseFieldsMatch() {
        webClient.get("/projects/3", ProjectDto.class)
            .valueMatches(new ProjectDtoSpec(equalTo(3L), not(containsString(" ")), containsString("Project"), not(emptyOrNullString()), collectionOfType(TaskDto.class)));
    }

    @Test
    void givenPreloadedData_whenGetProjects_thenResponseFieldsMatch() {
        ProjectDtoSpec expected1 = new ProjectDtoSpec(equalTo(1L), any(String.class), any(String.class), any(String.class), collectionOfType(TaskDto.class));
        ProjectDtoSpec expected2 = new ProjectDtoSpec(any(Long.class), equalTo("P2"), any(String.class), any(String.class), collectionOfType(TaskDto.class));

        webClient.getList("/projects", ProjectDto.class)
            .contains(expected1, expected2)
            .hasSize(3);
    }

    private static <U> Matcher<Iterable<? extends U>> collectionOfType(Class<U> clazz) {
        return everyItem(any(clazz));
    }
}
