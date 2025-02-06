package customer.demonstration.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class OrderValidationServiceTest {
    @Autowired
    private OrderValidationService orderValidationService;

    @Test
    void getOrdersByUser() throws InterruptedException {
        long startFirstAttempt = System.currentTimeMillis();
        orderValidationService.getOrdersByUser(1);
        long timeFromFirstAttempt = System.currentTimeMillis() - startFirstAttempt;
        log.info("Time after DB call: {} ms", timeFromFirstAttempt);

        Thread.sleep(Duration.ofSeconds(1));

        long startSecondAttempt = System.currentTimeMillis();
        orderValidationService.getOrdersByUser(1);
        long timeFromSecondAttempt = System.currentTimeMillis() - startSecondAttempt;
        log.info("Time after cache call: {} ms", timeFromSecondAttempt);

        assertTrue(timeFromFirstAttempt > timeFromSecondAttempt, "Cache is not working!");
        assertTrue(timeFromSecondAttempt < 500, "Cache response is too slow!");
    }


    @Test
    void checkJCacheProvider() {
        CachingProvider provider = Caching.getCachingProvider();
        log.info("JCache provider: {}", provider.getDefaultURI());
    }
}