package ad.service


import ad.constantData.KafkaData
import ad.entity.cassandraEntity.AdEvent
import ad.entity.kafkaEntity.ClickEvent
import ad.entity.kafkaEntity.ImpressionEvent
import ad.repository.cassandraRepository.AdEventCassandraRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Service

@Service
class AdEventService(private val adEventCassandraRepository: AdEventCassandraRepository,
                     private val objectReader: ObjectMapper,
                     private val dailyService: DailyAggregateService,
                     private val kafkaTemplate: KafkaTemplate<String, String>) {

    @KafkaListener(topics = [KafkaData.REQUEST_INPUT_TOPIC], groupId = KafkaData.REQUEST_GROUP_ID)
    fun listener(jsonEvent: String, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        if (key == KafkaData.IMPRESSION_KEY) {
            val impressionEvent = objectReader.readValue(jsonEvent, ImpressionEvent::class.java)
            saveImpressionEvent(impressionEvent)
        } else {
            val clickEvent = objectReader.readValue(jsonEvent, ClickEvent::class.java)
            setClickEventTime(clickEvent)
        }
    }

    fun saveImpressionEvent(impressionEvent: ImpressionEvent) {
        val adEvent = AdEvent(impressionEvent.requestId,
                impressionEvent.adId,
                impressionEvent.adTitle,
                impressionEvent.advertiserCost,
                impressionEvent.appId,
                impressionEvent.appTitle,
                impressionEvent.impressionTime,
                null)
        adEventCassandraRepository.save(adEvent)
        dailyService.addImpressionCountOfDailyAggregate(adEvent, 0, 1)
    }

    fun setClickEventTime(clickEvent: ClickEvent) {
        val ad = adEventCassandraRepository.findByIdOrNull(clickEvent.requestId)
        if (ad != null) {
            ad.clickTime = clickEvent.clickTime
            adEventCassandraRepository.save(ad)
            dailyService.addImpressionCountOfDailyAggregate(ad, 1, 0)
        } else {
            val json = objectReader.writeValueAsString(clickEvent)
            kafkaTemplate.send(KafkaData.WRONG_CLICK_TOPIC, json)
        }
    }

}