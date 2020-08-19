package com.github.rami_sabbagh.codeforces.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeforcesClientTest {

    @Test
    @DisplayName("Create a default client")
    void newCodeforcesClient() {
        assertNotNull(CodeforcesClient.newCodeforcesClient());
    }

    @Test
    @DisplayName("Create a default client using the builder")
    void newBuilder() {
        assertNotNull(CodeforcesClient.newBuilder()
                .authorization()
                .language()
                .proxy()
                .build());
    }
}