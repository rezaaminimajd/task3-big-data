package ad.service

import ad.constantData.CassandraData
import ad.entity.cassandraEntity.DailyAdAggregate
import ad.entity.cassandraEntity.WeekAggregate
import ad.repository.cassandraRepository.WeekAdEventAggregate
import com.datastax.driver.core.querybuilder.QueryBuilder
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.sql.Timestamp


@Service
class WeekAggregateService(private val cassandraTemplate: CassandraTemplate,
                           private val weekAdEventAggregate: WeekAdEventAggregate) {


    @Scheduled(cron = "0 * * * * *")
    fun weekAggregate() {
        val stamp = Timestamp(System.currentTimeMillis())
        val day = stamp.toLocalDateTime().dayOfYear
        val select = QueryBuilder.select().from(CassandraData.DAILY_AGGREGATE_TABLE)
                .where(QueryBuilder.gt("day", day - 7))
                .and(QueryBuilder.lt("day", day)).allowFiltering()
        val dailyAggregates: List<DailyAdAggregate> = cassandraTemplate.select(select, DailyAdAggregate::class.java)
        val merged = dailyAggregates.groupBy { it.adId + "-" + it.appId }
                .values
                .map {
                    it.reduce { a, b ->
                        DailyAdAggregate(
                                0,
                                a.adId,
                                a.appId,
                                a.impressionCount + b.impressionCount,
                                a.clickCount + b.clickCount)
                    }
                }
        for (sevenDay in merged) {
            val week = WeekAggregate(sevenDay.adId,
                    sevenDay.appId,
                    sevenDay.impressionCount,
                    sevenDay.clickCount)
            weekAdEventAggregate.save(week)
        }
        deleteExtraData(day)
    }

    fun deleteExtraData(day: Int) {
        val select = QueryBuilder.select().from(CassandraData.DAILY_AGGREGATE_TABLE)
                .where(QueryBuilder.lte("day", day - 7)).allowFiltering()
        val dailyAggregates: List<DailyAdAggregate> = cassandraTemplate.select(select, DailyAdAggregate::class.java)
        dailyAggregates.map {
            cassandraTemplate.delete(it)
        }
    }
}
