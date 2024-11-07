package ug.edu.pl.server.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

// We are using Caffeine as the cache implementation, which stores objects on the heap.
// While Caffeine provides excellent performance for in-memory caching, in a production
// environment it might be beneficial to consider alternatives like external cache (e.g. Redis) or off-heap caching.
// These alternatives can help avoid heap memory issues and reduce the impact on the gc.

@EnableCaching
@Configuration
class CacheConfiguration {

    @Bean
    Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES);
    }

    @Bean
    CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        var cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
