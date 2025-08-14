package money.wiremit.forex.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping
public class RateController {
    private final RateRecordRepository repo;
    private final AggregationService aggregation;

    public RateController(RateRecordRepository repo, AggregationService aggregation) {
        this.repo = repo; this.aggregation = aggregation;
    }

    @GetMapping("/rates")
    public List<RateView> latestAll(){
        List<RateView> out = new ArrayList<>();
        for (CurrencyPair p : CurrencyPair.values()){
            var rec = repo.findFirstByPairOrderByFetchedAtDesc(p)
                    .orElseThrow(() -> new RuntimeException("No data yet for " + p));
            out.add(toView(rec));
        }
        return out;
    }

    @GetMapping("/rates/{currency}")
    public RateView latestFor(@PathVariable("currency") String pair){
        var p = CurrencyPair.of(pair);
        var rec = repo.findFirstByPairOrderByFetchedAtDesc(p)
                .orElseThrow(() -> new RuntimeException("No data yet for " + p));
        return toView(rec);
    }

    @GetMapping("/historical/rates")
    public HistoricalResponse historical(
            @RequestParam(required = false) String pair,
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to){
        var p = pair==null? null : CurrencyPair.of(pair);
        var list = repo.search(p, from, to).stream().map(this::toView).toList();
        return new HistoricalResponse(list);
    }

    @PostMapping("/rates/refresh")
    public Mono<List<RateView>> forceRefresh(){
        return aggregation.fetchAndStore().map(list -> list.stream().map(this::toView).toList());
    }

    private RateView toView(com.wiremit.forex.domain.RateRecord r){
        return new RateView(r.getPair(), r.getAvgRate(), r.getFinalRate(),
                r.getMarkupType(), r.getMarkup(), r.getFetchedAt());
    }
}
