package ad.repository.cassandraRepository

import ad.entity.cassandraEntity.WeekAggregate
import org.springframework.data.cassandra.repository.CassandraRepository

interface WeekAdEventAggregate : CassandraRepository<WeekAggregate, String>