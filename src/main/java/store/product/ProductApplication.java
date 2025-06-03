// ProductApplication.java
package store.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@SpringBootApplication
@EnableCaching
public class ProductApplication {

    public static void main(String[] args) {
        waitForRedis();
        SpringApplication.run(ProductApplication.class, args);
    }

    private static void waitForRedis() {
        String redisHost = System.getenv().getOrDefault("REDIS_HOST", "redis");
        int redisPort = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));
        int retries = 30;
        int attempt = 0;

        while (attempt < retries) {
            LettuceConnectionFactory connectionFactory = null;
            try {
                connectionFactory = new LettuceConnectionFactory(redisHost, redisPort);
                connectionFactory.afterPropertiesSet();
                try (RedisConnection connection = connectionFactory.getConnection()) {
                    if ("PONG".equalsIgnoreCase(connection.ping())) {
                        System.out.println("✅ Connected to Redis at " + redisHost + ":" + redisPort);
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("⏳ Waiting for Redis... attempt " + (attempt + 1));
            } finally {
                if (connectionFactory != null) {
                    connectionFactory.destroy();
                }
            }
            attempt++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for Redis", ie);
            }
        }
        throw new RuntimeException("❌ Cannot connect to Redis at " + redisHost + ":" + redisPort + " after 30 seconds");
    }
}