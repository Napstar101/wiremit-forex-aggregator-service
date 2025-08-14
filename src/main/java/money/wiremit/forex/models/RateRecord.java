package money.wiremit.forex.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import money.wiremit.forex.common.enums.CurrencyPair;


import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RateRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
