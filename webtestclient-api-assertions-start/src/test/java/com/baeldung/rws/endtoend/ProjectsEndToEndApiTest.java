package com.baeldung.rws.endtoend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

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
}
