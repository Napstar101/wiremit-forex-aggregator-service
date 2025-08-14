package money.wiremit.forex.controllers;

import com.wiremit.forex.model.Rate;
import com.wiremit.forex.repository.RateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RateControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RateRepository rateRepository;

    @Test
    void shouldReturnRatesWhenAuthenticated() {
        // Insert sample data
        rateRepository.save(new Rate("USD-GBP", 0.80, LocalDateTime.now()));

        // Simulate JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer mocked-jwt-token");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/rates", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("USD-GBP");
    }
}
