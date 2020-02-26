package ad.constantData

object CassandraData {
    const val CASSANDRA_HOST = "localhost"
    const val CASSANDRA_KEY_SPACE = "reza"
    const val CASSANDRA_PORT = 9042

}

object KafkaData {
    const val BOOTSTRAP_SERVERS_CONFIG = "localhost:9092"
    const val GROUP_ID_CONFIG = "adEvent"
    const val REQUEST_INPUT_TOPIC = "task"
    const val IMPRESSION_KEY = "impression"
    const val CLICK_KEY = "click"

}