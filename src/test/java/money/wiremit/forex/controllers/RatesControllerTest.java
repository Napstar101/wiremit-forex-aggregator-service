package money.wiremit.forex.controllers;

import com.wiremit.forex.service.RateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatesController.class)
class RatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateService rateService;

    @Test
    void testGetRates() throws Exception {
        Mockito.when(rateService.getLatestRates())
                .thenReturn(Map.of("USD-GBP", 0.79, "USD-ZAR", 18.24));

        mockMvc.perform(get("/rates")
                        .header("Authorization", "Bearer dummyToken")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['USD-GBP']").value(0.79))
                .andExpect(jsonPath("$.['USD-ZAR']").value(18.24));
    }
}
