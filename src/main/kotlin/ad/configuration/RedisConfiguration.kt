package ad.configuration



import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration


@Configuration
@EnableCaching
@PropertySource("classpath:application.properties")
class RedisConfiguration(val env: Environment) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisConf = RedisStandaloneConfiguration()
        redisConf.hostName = env.getProperty("spring.redis.host")!!
        redisConf.port = env.getProperty("spring.redis.port")!!.toInt()
        return LettuceConnectionFactory(redisConf)
    }

    @Bean
    fun cacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(10))
                .disableCachingNullValues()
    }

    @Bean
    fun cacheManageCorrecter(): RedisCacheManager {
        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(cacheConfiguration())
                .transactionAware()
                .build()
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Double>? {
        val template = RedisTemplate<String, Double>()
        template.setConnectionFactory(redisConnectionFactory())
        return template
    }
}