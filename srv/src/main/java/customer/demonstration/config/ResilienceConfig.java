package customer.demonstration.config;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class ResilienceConfig {
    @Bean
    public ResilienceConfiguration resilienceConfiguration() {
        log.info("ResilienceConfig: Creating ResilienceConfiguration");
        return ResilienceConfiguration
                .empty("orderCache")
                .cacheConfiguration(cacheConfiguration());
    }

    @Bean
    public ResilienceConfiguration.CacheConfiguration cacheConfiguration() {
        log.info("ResilienceConfig: Creating CacheConfiguration");
        return ResilienceConfiguration.CacheConfiguration
                .of(Duration.ofDays(1))
                .withoutParameters();
    }
}
