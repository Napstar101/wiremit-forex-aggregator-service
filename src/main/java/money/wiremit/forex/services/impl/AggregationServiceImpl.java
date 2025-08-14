package money.wiremit.forex.services.impl;

import money.wiremit.forex.common.enums.CurrencyPair;
import money.wiremit.forex.config.webclient.ProviderClient;
import money.wiremit.forex.models.RateRecord;
import money.wiremit.forex.repository.RateRecordRepository;
import money.wiremit.forex.services.ifaces.AggregationService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
public class AggregationServiceImpl implements AggregationService {
    private final List<ProviderClient> providers;
    private final RateRecordRepository repo;
    private final ObjectMapper om = new ObjectMapper();
    private final String markupType;
    private final double markupValue;
    private final ZoneId zoneId;

    public AggregationServiceImpl(List<ProviderClient> providers, RateRecordRepository repo,
                              @Value("${app.aggregation.markupType}") String markupType,
                              @Value("${app.aggregation.markupValue}") double markupValue,
                              @Value("${app.aggregation.zoneId}") String zoneId) {
        this.providers = providers; this.repo = repo;
        this.markupType = markupType; this.markupValue = markupValue; this.zoneId = ZoneId.of(zoneId);
    }

    public Mono<List<RateRecord>> fetchAndStore() {
        return Mono.zip(providers.stream().map(ProviderClient::fetch).toList(), results -> {
            // Merge: for each provider we have map pair->rate
            Map<CurrencyPair, List<Double>> collector = new EnumMap<>(CurrencyPair.class);
            for (Object obj : results) {
                var map = (Map<CurrencyPair, Double>) obj;
                map.forEach((pair, rate) -> collector.computeIfAbsent(pair, k->new ArrayList<>()).add(rate));
            }
            List<RateRecord> toSave = new ArrayList<>();
            for (var e : collector.entrySet()) {
                double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElseThrow();
                double finalRate = markupType.equalsIgnoreCase("PERCENT") ? avg * (1.0 + markupValue) : avg + markupValue;
                Map<String, Object> snapshot = new LinkedHashMap<>();
                for (ProviderClient p : providers) snapshot.put(p.name(), "see latest call (not stored per-provider here)");
                String snap;
                try { snap = om.writeValueAsString(Map.of("providerCount", providers.size(), "pair", e.getKey().asCode())); }
                catch (JsonProcessingException ex) { snap = "{}"; }

                var rec = new RateRecord();
                rec.setPair(e.getKey()); rec.setAvgRate(avg); rec.setFinalRate(finalRate);
                rec.setMarkup(markupValue); rec.setMarkupType(markupType.toUpperCase());
                rec.setFetchedAt(OffsetDateTime.now(zoneId));
                rec.setProviderSnapshot(snap);
                toSave.add(rec);
            }
            return repo.saveAll(toSave);
        });
    }
}
}
