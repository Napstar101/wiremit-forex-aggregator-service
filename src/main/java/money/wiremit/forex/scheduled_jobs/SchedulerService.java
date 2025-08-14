package money.wiremit.forex.scheduled_jobs;

import com.wiremit.forex.domain.CurrencyPair;
import com.wiremit.forex.domain.RateRecord;
import com.wiremit.forex.repo.RateRecordRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@EnableScheduling
public class SchedulerService {
    private final AggregationService svc;
    private final RateRecordRepository repo;
    public SchedulerService(AggregationService svc, RateRecordRepository repo){ this.svc=svc; this.repo=repo; }

    // Every hour at minute 0 (Africa/Harare is configured inside service for timestamps)
    @Scheduled(cron = "0 0 * * * *")
    public void refreshHourly(){
        svc.fetchAndStore().onErrorResume(e -> {
            // Fail-safe: log and continue
            e.printStackTrace();
            return Mono.just(List.<RateRecord>of());
        }).block(); // run within scheduler
    }

    // On startup, ensure we have a recent snapshot
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void warmup(){ refreshHourly(); }
}
