package money.wiremit.forex.repository;

public interface RateRecordRepository extends JpaRepository<RateRecord, Long> {
    Optional<RateRecord> findFirstByPairOrderByFetchedAtDesc(CurrencyPair pair);
    @Query("select r from RateRecord r where (:pair is null or r.pair=:pair) and r.fetchedAt between :from and :to order by r.fetchedAt desc")
    List<RateRecord> search(CurrencyPair pair, OffsetDateTime from, OffsetDateTime to);
}
