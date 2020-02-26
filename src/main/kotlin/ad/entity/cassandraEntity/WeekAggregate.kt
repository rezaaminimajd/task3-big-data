package ad.entity.cassandraEntity

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("week")
data class WeekAggregate(@PrimaryKey val adId: String,
                         @Column val appId: String,
                         @Column var impressionCount: Int,
                         @Column var clickCount: Int)