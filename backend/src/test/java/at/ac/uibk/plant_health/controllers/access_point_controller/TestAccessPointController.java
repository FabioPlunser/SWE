package at.ac.uibk.plant_health.controllers.access_point_controller;

import at.ac.uibk.plant_health.models.user.Permission;
import at.ac.uibk.plant_health.service.AccessPointService;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TestAccessPointController {
    @Autowired
    private AccessPointService accessPointService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAccessPointRegister() throws Exception{
        UUID accessPointId = UUID.randomUUID();
        // register access point expect accessPoint is locked
        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/register-access-point")
            .param("accessPointId", String.valueOf(accessPointId))
            .param("roomName", "Office1")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401)
        );

        // unlock access point and try again
        accessPointService.setUnlocked(accessPointId, true);

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/register-access-point")
            .param("accessPointId", String.valueOf(accessPointId))
            .param("roomName", "Office1")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        );
    }
}
