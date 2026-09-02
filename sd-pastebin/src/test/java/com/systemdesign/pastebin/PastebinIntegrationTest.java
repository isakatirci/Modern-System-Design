package com.systemdesign.pastebin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PastebinIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndReadsPaste() throws Exception {
        String response = mockMvc.perform(post("/api/v1/pastes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"hello world\",\"ttlSeconds\":3600}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.replaceAll(".*\"id\"\\s*:\\s*\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/v1/pastes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("hello world"));
    }
}
