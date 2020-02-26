package ad.service

import ad.entity.kafkaEntity.ClickEvent
import ad.repository.cassandraRepository.AdEventCassandraRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*


@Service
class KafkaBackupService(private val adEventCassandraRepository: AdEventCassandraRepository,
                         private val objectReader: ObjectMapper,
                         private val dailyService: DailyAggregateService,
                         private val kafkaTemplate: KafkaTemplate<String, String>) {

    private val consumer: KafkaConsumer<String?, String?> = setConsumer()

    private final fun setConsumer(): KafkaConsumer<String?, String?> {
        val props = Properties()
        props.setProperty("bootstrap.servers", "localhost:9092")
        props.setProperty("group.id", "test")
        props.setProperty("enable.auto.commit", "true")
        props.setProperty("auto.commit.interval.ms", "1000")
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        val consumer: KafkaConsumer<String?, String?> = KafkaConsumer(props)
        consumer.subscribe(listOf("click"))
        return consumer
    }

    @Scheduled(fixedRate = 30000, initialDelay = 30000)
    fun resetClicks() {
        val records = consumer.poll(Duration.ofMillis(100))
        for (record in records) {
            System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value())
            val clickEvent = objectReader.readValue(record.value(), ClickEvent::class.java)
            if ((System.currentTimeMillis() - clickEvent.clickTime) > 10000) {
                setBackupClicks(clickEvent)
            } else {
                println("return click to kafka")
                kafkaTemplate.send("click", record.value())
            }
        }

    }

    fun setBackupClicks(clickEvent: ClickEvent) {
        val ad = adEventCassandraRepository.findByIdOrNull(clickEvent.requestId)
        if (ad != null) {
            ad.clickTime = clickEvent.clickTime
            adEventCassandraRepository.save(ad)
            dailyService.addImpressionCountOfDailyAggregate(ad, 1, 0)
            println("Yeah , save click")
        } else {
            println("Oh NO , not found impression")
        }

    }
}