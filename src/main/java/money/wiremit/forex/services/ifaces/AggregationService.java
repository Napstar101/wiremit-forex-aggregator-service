package money.wiremit.forex.services.ifaces;

import money.wiremit.forex.models.RateRecord;

import java.util.List;

public interface AggregationService {
    Mono<List<RateRecord>> fetchAndStore();
}
