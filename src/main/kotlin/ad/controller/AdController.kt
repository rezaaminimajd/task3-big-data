package ad.controller

import ad.constantData.KafkaData
import ad.entity.kafkaEntity.ClickEvent
import ad.entity.kafkaEntity.ImpressionEvent
import ad.service.RedisService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/event")
class AdController(private val kafkaTemplate: KafkaTemplate<String, String>,
                   private val objectMapper: ObjectMapper,
                   private val redisService: RedisService) {

    @PostMapping("/impression")
    fun getImpression(@RequestBody request: ImpressionEvent) {
        val json = objectMapper.writeValueAsString(request)
        println(json)
        kafkaTemplate.send(KafkaData.REQUEST_INPUT_TOPIC, KafkaData.IMPRESSION_KEY, json)
    }

    @PostMapping("/click")
    fun getClick(@RequestBody request: ClickEvent) {
        val json = objectMapper.writeValueAsString(request)
        println(json)
        kafkaTemplate.send(KafkaData.REQUEST_INPUT_TOPIC, KafkaData.CLICK_KEY, json)
    }

    @GetMapping("ctr/{appId}")
    fun weekCTRAppId(@PathVariable("appId") appId: String): String {
        return appId
    }

    @GetMapping("ctr/{appId}/{adId}")
    fun weekCTRAdId(@PathVariable("appId") appId: String,
                    @PathVariable("adId") adId: String): Double? {
        return redisService.getCTR("$appId-$adId")
    }

    @GetMapping("adId/ctr/{adId}")
    fun weekCTRAppIdAndAdId(@PathVariable("adId") adId: String): Double? {
        return redisService.getCTR(adId)
    }

}