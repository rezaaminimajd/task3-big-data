package ad.entity.cassandraEntity

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("adevent")
data class AdEvent(@PrimaryKey val requestId: String,
                   @Column val adId: String,
                   @Column val adTitle: String,
                   @Column val advertiserCost: Double,
                   @Column val appId: String,
                   @Column val appTitle: String,
                   @Column val impressionTime: Long,
                   @Column var clickTime: Long?)