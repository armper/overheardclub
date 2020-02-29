package com.perea.overheard.web.rest;

import com.perea.overheard.OverheardclubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Test class for the UprankResource REST controller.
 *
 * @see UprankResource
 */
@SpringBootTest(classes = OverheardclubApp.class)
public class UprankResourceIT {

    private MockMvc restMockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        UprankResource uprankResource = new UprankResource();
        restMockMvc = MockMvcBuilders
            .standaloneSetup(uprankResource)
            .build();
    }

    /**
     * Test uprank
     */
    @Test
    public void testUprank() throws Exception {
        restMockMvc.perform(post("/api/uprank/uprank"))
            .andExpect(status().isOk());
    }
}
