package ad.repository.cassandraRepository

import ad.constantData.CassandraData
import ad.entity.cassandraEntity.DailyAdAggregate
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query

interface DailyAdEventAggregate : CassandraRepository<DailyAdAggregate, String> {

    @Query(CassandraData.DAILY_AGGREGATE_INSERT_CQL)
    fun insert(day: Int, adId: String, appId: String, impressionCount: Int, clickCount: Int)
}