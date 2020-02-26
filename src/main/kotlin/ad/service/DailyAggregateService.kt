package ad.service

import ad.constantData.CassandraData
import ad.entity.cassandraEntity.AdEvent
import ad.entity.cassandraEntity.DailyAdAggregate
import ad.repository.cassandraRepository.DailyAdEventAggregate
import com.datastax.driver.core.querybuilder.QueryBuilder
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class DailyAggregateService(private val dailyAdEventAggregate: DailyAdEventAggregate,
                            private val cassandraTemplate: CassandraTemplate) {


    fun addImpressionCountOfDailyAggregate(adEvent: AdEvent, click: Int, impression: Int) {
        val stamp = Timestamp(adEvent.impressionTime)
        val day = stamp.toLocalDateTime().minute
        val adId = adEvent.adId
        val appId = adEvent.appId
        val select = QueryBuilder.select().from(CassandraData.DAILY_AGGREGATE_TABLE)
                .where(QueryBuilder.eq("day", day))
                .and(QueryBuilder.eq("adId", adId))
                .and(QueryBuilder.eq("appId", appId))
        var dailyAggregate: DailyAdAggregate? = cassandraTemplate.selectOne(select, DailyAdAggregate::class.java)
        if (dailyAggregate == null) {
            println("not found and create")
            dailyAggregate = DailyAdAggregate(day, adId, appId, 0, 0)
        }
        dailyAggregate.impressionCount += impression
        dailyAggregate.clickCount += click
        dailyAdEventAggregate.insert(dailyAggregate.day, dailyAggregate.adId, dailyAggregate.appId,
                dailyAggregate.impressionCount, dailyAggregate.clickCount)
    }


}