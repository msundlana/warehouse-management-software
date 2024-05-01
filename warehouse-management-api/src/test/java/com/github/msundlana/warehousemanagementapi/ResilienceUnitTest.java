package com.github.msundlana.warehousemanagementapi;

import com.github.msundlana.warehousemanagementapi.services.WarehouseService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static io.github.resilience4j.circuitbreaker.CallNotPermittedException.createCallNotPermittedException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BANDWIDTH_LIMIT_EXCEEDED;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResilienceUnitTest extends WarehouseManagementApplicationTests{
    @Autowired
    private  WebApplicationContext webApplicationContext;


    private MockMvc mockMvc;

    @MockBean
    WarehouseService warehouseService;

    private final String basePath = "/api/warehouse/products";

    @BeforeEach
    void setupTests() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    @Disabled
    public void testCircuitBreaker() {
        var circuitBreaker = CircuitBreaker.ofDefaults("testName");
        circuitBreaker.transitionToOpenState();

        final CallNotPermittedException callNotPermittedException = createCallNotPermittedException(circuitBreaker);

        when(warehouseService.getAllAvailableProducts("",0,10)).thenThrow(callNotPermittedException);

        IntStream.rangeClosed(1, 5)
                .forEach(i -> {
                    try {
                        mockMvc.perform(get(basePath)
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isServiceUnavailable());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    @Disabled
    void testBulkhead() throws Exception {
        var responseStatusCount = new ConcurrentHashMap<Integer, Integer>();
        var executorService = Executors.newFixedThreadPool(5);
        var latch = new CountDownLatch(5);

        IntStream.rangeClosed(1, 5)
                .forEach(i -> executorService.execute(() -> {
                    try {

                        var statusCode = mockMvc.perform(get(basePath)
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse().getStatus();

                        responseStatusCount.merge(statusCode, 1, Integer::sum);
                        latch.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }));
        latch.await();
        executorService.shutdown();

        assertEquals(2, responseStatusCount.keySet().size());
        assertTrue(responseStatusCount.containsKey(BANDWIDTH_LIMIT_EXCEEDED.value()));
    }

    @Test
    @Disabled
    public void testRateLimiter() {
        var responseStatusCount = new ConcurrentHashMap<Integer, Integer>();

        IntStream.rangeClosed(1, 50)
                .parallel()
                .forEach(i -> {
                    try {

                        var statusCode = mockMvc.perform(get(basePath)
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse().getStatus();

                        responseStatusCount.put(statusCode, responseStatusCount.getOrDefault(statusCode, 0) + 1);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                });

        assertEquals(4, responseStatusCount.keySet().size());
        assertTrue(responseStatusCount.containsKey(TOO_MANY_REQUESTS.value()));
    }
}