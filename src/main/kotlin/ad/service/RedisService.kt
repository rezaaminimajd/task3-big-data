package ad.service

import ad.entity.cassandraEntity.DailyAdAggregate
import ad.entity.cassandraEntity.WeekAggregate
import com.datastax.driver.core.querybuilder.QueryBuilder
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class RedisService(private val cassandraTemplate: CassandraTemplate,
                   private val redisTemplate: RedisTemplate<String, Double>) {


    @Scheduled(fixedRate = 10000)
    fun setCTRs() {
        val stamp = Timestamp(System.currentTimeMillis())
        val day = stamp.toLocalDateTime().minute
        val selectWeek = QueryBuilder.select().from("week")
        var weekAggregate: MutableList<WeekAggregate> = cassandraTemplate.select(selectWeek, WeekAggregate::class.java)
        val selectDaily = QueryBuilder.select().from("daily")
                .where(QueryBuilder.eq("day", day))
                .allowFiltering()

        val dailyAggregates: List<DailyAdAggregate> = cassandraTemplate.select(selectDaily, DailyAdAggregate::class.java)
        weekAggregate = createWeekFromDays(dailyAggregates, weekAggregate)
        val week = compactWeeks(weekAggregate)
        weekCTRAdId(week)
        weekCTRAppId(week)
        weekCTRAppIdAndAdId(week)
    }

    private fun createWeekFromDays(dailyAggregates: List<DailyAdAggregate>, weekAggregate: MutableList<WeekAggregate>): MutableList<WeekAggregate> {
        dailyAggregates.map {
            weekAggregate.add(
                    WeekAggregate(
                            it.adId,
                            it.appId,
                            it.impressionCount,
                            it.clickCount
                    )
            )
        }
        return weekAggregate
    }

    private fun compactWeeks(weekAggregate: MutableList<WeekAggregate>): List<WeekAggregate> {
        return weekAggregate.groupBy { it.adId + "-" + it.appId }
                .values
                .map {
                    it.reduce { a, b ->
                        WeekAggregate(a.adId,
                                a.appId,
                                a.impressionCount + b.impressionCount,
                                a.clickCount + b.clickCount)
                    }
                }
    }

    fun weekCTRAppId(week: List<WeekAggregate>) {
        week.groupBy { it.appId }.values.map { it ->
            var click = 0
            var impression = 0
            it.map {
                click += it.clickCount
                impression += it.impressionCount
            }
            val key = it[0].appId
            saveToRedis(click.toDouble() / impression.toDouble(), key)
        }
    }

    fun weekCTRAdId(week: List<WeekAggregate>) {
        week.groupBy { it.adId }.values.map { it ->
            var click = 0
            var impression = 0
            it.map {
                click += it.clickCount
                impression += it.impressionCount
            }
            val key = it[0].adId
            saveToRedis(click.toDouble() / impression.toDouble(), key)
        }
    }

    fun weekCTRAppIdAndAdId(week: List<WeekAggregate>) {
        week.map {
            val click = it.clickCount
            val impression = it.impressionCount
            val key = it.appId + "-" + it.adId
            saveToRedis(click.toDouble() / impression.toDouble(), key)
        }
    }

    fun saveToRedis(ctr: Double, key: String) {
        redisTemplate.delete(key)
        redisTemplate.opsForValue().set(key, ctr)
    }


    fun getCTR(key: String): Double? {
        return redisTemplate.opsForValue()[key]
    }

}