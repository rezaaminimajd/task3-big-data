package ad.repository.cassandraRepository

import ad.entity.cassandraEntity.DailyAdAggregate
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query

interface DailyAdEventAggregate : CassandraRepository<DailyAdAggregate, String> {

    @Query("INSERT INTO daily (day, adid, appid," +
            " impressioncount, clickcount) values (?0, ?1 , ?2, ?3, ?4) USING TTL 430")
    fun insert(day: Int, adId: String, appId: String, impressionCount: Int, clickCount: Int)
}