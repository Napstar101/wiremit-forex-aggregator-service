package money.wiremit.forex.services;

import com.wiremit.forex.model.Rate;
import com.wiremit.forex.repository.RateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateAggregationServiceTest {

    @Mock
    private RateRepository rateRepository;

    @Mock
    private ForexProviderClient providerClient;

    @InjectMocks
    private RateAggregationService rateAggregationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAggregateAndApplyMarkup() {
        // Given
        when(providerClient.getRate("USD", "GBP")).thenReturn(List.of(0.78, 0.80, 0.79));
        when(providerClient.getRate("USD", "ZAR")).thenReturn(List.of(18.2, 18.4, 18.3));

        // When
        Rate result = rateAggregationService.aggregateRate("USD", "GBP");

        // Then
        assertThat(result.getRate()).isGreaterThan(0.78).isLessThan(0.81);
        verify(rateRepository, times(1)).save(any(Rate.class));
    }
}
