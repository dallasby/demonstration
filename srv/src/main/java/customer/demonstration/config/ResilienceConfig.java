package customer.demonstration.config;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Slf4j
@Configuration
public class ResilienceConfig {
    @Bean
    public ResilienceConfiguration resilienceConfiguration() {
        log.info("ResilienceConfig: Creating ResilienceConfiguration");
        ResilienceConfiguration configuration = ResilienceConfiguration
                .empty("orderCache")
                .cacheConfiguration(cacheConfiguration());
        log.info("Cache enabled: {}", configuration.cacheConfiguration().isEnabled());
        return configuration;
    }

    @Bean
    public ResilienceConfiguration.CacheConfiguration cacheConfiguration() {
        CachingProvider provider = Caching.getCachingProvider();
        log.info("Caching provider: {}", provider.getDefaultURI());
        return ResilienceConfiguration.CacheConfiguration
                .of(Duration.ofDays(1))
                .withoutParameters();
    }
}
