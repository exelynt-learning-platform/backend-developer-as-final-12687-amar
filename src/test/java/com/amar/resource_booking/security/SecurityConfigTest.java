package com.amar.resource_booking.security;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestShouldReturn401() throws Exception {

        mockMvc.perform(
                get("/api/resources")
        )
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void userShouldNotCreateResource() throws Exception {

        mockMvc.perform(
                post("/api/resources")
                        .with(user("user").roles("USER"))
        )
        .andExpect(status().isForbidden());
    }
    @Test
    void adminShouldCreateResource() throws Exception {

        mockMvc.perform(
                post("/api/resources")
                        .with(user("admin").roles("ADMIN"))
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Meeting Room",
                                    "description": "Test room",
                                    "type": "ROOM",
                                    "available": true,
                                    "price": 500.00
                                }
                                """)
        )
        .andExpect(status().isCreated());
    }
    @Test
    void userShouldReadResources() throws Exception {

        mockMvc.perform(
                get("/api/resources")
                        .with(user("user").roles("USER"))
        )
        .andExpect(status().isOk());
    }
    
    @Test
    void userShouldNotCancelReservation() throws Exception {

        mockMvc.perform(
                put("/api/reservations/1/cancel")
                        .with(user("user").roles("USER"))
        ).andExpect(status().isForbidden());
    }
}