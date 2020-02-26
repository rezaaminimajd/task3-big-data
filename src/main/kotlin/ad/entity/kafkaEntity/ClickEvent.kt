package ad.entity.kafkaEntity

data class ClickEvent(val requestId: String,
                      val clickTime: Long,
                      val impressionTime: Long)