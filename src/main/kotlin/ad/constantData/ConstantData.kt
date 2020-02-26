package ad.constantData

object CassandraData {
    const val CASSANDRA_HOST = "localhost"
    const val CASSANDRA_KEY_SPACE = "reza"
    const val CASSANDRA_PORT = 9042
    const val AD_EVENT_TABLE = "adevent"
    const val DAILY_AGGREGATE_TABLE = "daily"
    const val WEEK_TABLE = "week"
    private const val DAILY_AGGREGATE_INSERT_TTL = 7 * 24 * 3699 // 99 second extra
    const val DAILY_AGGREGATE_INSERT_CQL = "INSERT INTO daily (day, adid, appid, impressioncount, clickcount) values " +
            "(?0, ?1 , ?2, ?3, ?4) USING TTL" + DAILY_AGGREGATE_INSERT_TTL.toString()


}

object KafkaData {
    const val BOOTSTRAP_SERVERS_CONFIG = "localhost:9092"
    const val GROUP_ID_CONFIG = "adEvent"
    const val REQUEST_INPUT_TOPIC = "task"
    const val IMPRESSION_KEY = "impression"
    const val CLICK_KEY = "click"

}