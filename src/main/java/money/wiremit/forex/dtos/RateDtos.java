package money.wiremit.forex.dtos;

import java.time.OffsetDateTime;
import java.util.List;

public class RateDtos {
    public record RateView(CurrencyPair pair, double avgRate, double finalRate, String markupType, double markup,
                           OffsetDateTime fetchedAt) {}
    public record HistoricalQuery(String pair, String from, String to) {}
    public record HistoricalResponse(List<RateView> items) {}
}
