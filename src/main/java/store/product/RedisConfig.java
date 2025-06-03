// RedisConfig.java
package store.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.URI;

@Configuration
public class RedisConfig {

    @Value("${REDIS_HOST:redis}")
    private String redisHostEnv;

    @Value("${REDIS_PORT:6379}")
    private String redisPortEnv;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String redisHost = redisHostEnv;
        int redisPort = Integer.parseInt(redisPortEnv);

        if (redisHostEnv.startsWith("tcp://")) {
            try {
                URI uri = new URI(redisHostEnv);
                redisHost = uri.getHost();
                redisPort = uri.getPort() != -1 ? uri.getPort() : redisPort;
            } catch (Exception e) {
                throw new RuntimeException("Invalid REDIS_HOST URL format: " + redisHostEnv, e);
            }
        }

        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}