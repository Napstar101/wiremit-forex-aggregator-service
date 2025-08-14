package money.wiremit.forex.repository;

import money.wiremit.forex.common.enums.CurrencyPair;
import money.wiremit.forex.models.RateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RateRecordRepository extends JpaRepository<RateRecord, Long> {
    Optional<RateRecord> findFirstByPairOrderByFetchedAtDesc(CurrencyPair pair);
    @Query("select r from RateRecord r where (:pair is null or r.pair=:pair) and r.fetchedAt between :from and :to order by r.fetchedAt desc")
    List<RateRecord> search(CurrencyPair pair, OffsetDateTime from, OffsetDateTime to);
}
