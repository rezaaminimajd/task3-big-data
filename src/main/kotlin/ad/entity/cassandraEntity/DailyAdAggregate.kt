package ad.entity.cassandraEntity

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("daily")
data class DailyAdAggregate(@PrimaryKey val day: Int,
                            @Column val adId: String,
                            @Column val appId: String,
                            @Column var impressionCount: Int,
                            @Column var clickCount: Int)