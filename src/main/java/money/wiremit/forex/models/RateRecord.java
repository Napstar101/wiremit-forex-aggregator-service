package money.wiremit.forex.models;

@Entity
public class RateRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CurrencyPair pair;

    private double avgRate;        // provider average before markup
    private double markup;         // applied markup value (absolute or %)
    private String markupType;     // ABSOLUTE or PERCENT
    private double finalRate;      // avg + markup (or avg*(1+%))

    private OffsetDateTime fetchedAt;
    private String providerSnapshot; // JSON of individual provider rates

}
