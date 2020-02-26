package ad.repository.cassandraRepository

import ad.entity.cassandraEntity.AdEvent
import org.springframework.data.cassandra.repository.CassandraRepository

interface AdEventCassandraRepository : CassandraRepository<AdEvent, String>