package money.wiremit.forex.config.webclient;

import money.wiremit.forex.common.enums.CurrencyPair;
import reactor.core.publisher.Mono;
import java.util.Map;

public interface ProviderClient {
    String name();
    Mono<Map<CurrencyPair, Double>> fetch();
}
