package ad.configuration

import ad.constantData.CassandraData
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.SchemaAction


@Configuration
class CassandraConfig : AbstractCassandraConfiguration() {
    override fun getKeyspaceName(): String {
        return CassandraData.CASSANDRA_KEY_SPACE
    }

    @Bean
    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = CassandraClusterFactoryBean()
        cluster.setContactPoints(CassandraData.CASSANDRA_HOST)
        cluster.setPort(CassandraData.CASSANDRA_PORT)
        return cluster
    }

    override fun getSchemaAction(): SchemaAction {
        return SchemaAction.CREATE_IF_NOT_EXISTS
    }

}