package money.wiremit.forex.config.webclient;

import com.wiremit.forex.domain.CurrencyPair;
import com.wiremit.forex.service.ProviderClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.*;

@Component
public class FrankfurterClient implements ProviderClient {
    private final WebClient wc;
    public FrankfurterClient(WebClient wc){ this.wc=wc; }
    @Override public String name(){ return "frankfurter.app"; }

    @Override
    public Mono<Map<CurrencyPair, Double>> fetch() {
        Mono<Map<String, Double>> usd = wc.get()
                .uri("https://api.frankfurter.app/latest?from=USD&to=GBP,ZAR")
                .retrieve().bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m.get("rates"))
                .map(r -> Map.of("USD-GBP", ((Number)r.get("GBP")).doubleValue(),
                        "USD-ZAR", ((Number)r.get("ZAR")).doubleValue()));
        return usd.map(m -> {
            double usdGbp = m.get("USD-GBP");
            double usdZar = m.get("USD-ZAR");
            double zarGbp = usdGbp / usdZar;
            Map<CurrencyPair, Double> out = new EnumMap<>(CurrencyPair.class);
            out.put(CurrencyPair.USD_GBP, usdGbp);
            out.put(CurrencyPair.USD_ZAR, usdZar);
            out.put(CurrencyPair.ZAR_GBP, zarGbp);
            return out;
        });
    }
}
