package com.example.catalog;

import com.example.catalog.interceptors.RateLimit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RateLimitTest {

    @Autowired
    private RateLimit rateLimit;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @BeforeEach
    void setUp() {
        rateLimit = new RateLimit();

        rateLimit.setRateLimitAlgo("fixed");
        rateLimit.setRateLimitRPM("10");
        rateLimit.setRateLimitEnabled(true);

    }


    @Test
    void testAllowsRequestsWithinLimit_MovingWindow() throws Exception {
        request.setRemoteAddr("192.168.1.1");

        for (int i = 0; i < 10; i++) {
            boolean allowed = rateLimit.preHandle(request, response, null);
            assertTrue(allowed);

            String remainingRequests = response.getHeader("X-Rate-Limit-Remaining");

            assertNotNull(remainingRequests, "X-Rate-Limit-Remaining header should not be null");
            assertEquals(10 - (i + 1), Integer.parseInt(remainingRequests));
        }
    }

    @Test
    void testBlocksRequestsExceedingLimit_MovingWindow() throws Exception {
        request.setRemoteAddr("192.168.1.1");

        for (int i = 0; i < 10; i++) {
            rateLimit.preHandle(request, response, null);
        }

        boolean allowed = rateLimit.preHandle(request, response, null);
        assertFalse(allowed);

        String remainingRequests = response.getHeader("X-Rate-Limit-Remaining");

        assertEquals("0", remainingRequests);
        assertEquals(429, response.getStatus());
    }

    @Test
    void testResetsAfterTimeWindow_MovingWindow() throws Exception {
        request.setRemoteAddr("192.168.1.1");

        for (int i = 0; i < 10; i++) {
            rateLimit.preHandle(request, response, null);
        }

        Thread.sleep(60000); // Wait for the sliding window to reset

        boolean allowed = rateLimit.preHandle(request, response, null);

        assertTrue(allowed);

        String remainingRequests = response.getHeader("X-Rate-Limit-Remaining");

        assertNotNull(remainingRequests);
        assertEquals(9, Integer.parseInt(remainingRequests));
    }

    @Test
    void testAllowsRequestsWithinLimit_FixedWindow() throws Exception {
        rateLimit.setRateLimitAlgo("fixed");
        request.setRemoteAddr("192.168.1.1");

        for (int i = 0; i < 10; i++) {
            boolean allowed = rateLimit.preHandle(request, response, null);
            assertTrue(allowed);

            String remainingRequests = response.getHeader("X-Rate-Limit-Remaining");
            assertNotNull(remainingRequests);
            assertEquals(10 - (i + 1), Integer.parseInt(remainingRequests));

            //Add a small delay
            Thread.sleep(10);
        }
    }


    @Test
    void testBlocksRequestsExceedingLimit_FixedWindow() throws Exception {
        rateLimit.setRateLimitAlgo("fixed");
        request.setRemoteAddr("192.168.1.1");

        for (int i = 0; i < 10; i++) {
            rateLimit.preHandle(request, response, null);
        }

        boolean allowed = rateLimit.preHandle(request, response, null);
        assertFalse(allowed);

        String remainingRequests = response.getHeader("X-Rate-Limit-Remaining");

        assertEquals("0", remainingRequests);
        assertEquals(429, response.getStatus());
    }

    @Test
    void testResetsAfterTimeWindow_FixedWindow() throws Exception {
        rateLimit.setRateLimitAlgo("fixed");
        request.setRemoteAddr("192.168.1.1");

        for (int i = 0; i < 10; i++) {
            rateLimit.preHandle(request, response, null);
        }

        Thread.sleep(60000);

        boolean allowed = rateLimit.preHandle(request, response, null);
        assertTrue(allowed);

        String remainingRequests = response.getHeader("X-Rate-Limit-Remaining");

        assertNotNull(remainingRequests);
        assertEquals(9, Integer.parseInt(remainingRequests));
    }

    @Test
    void testRetryAfterHeaderSetWhenBlocked() throws Exception {
        request.setRemoteAddr("192.168.1.1");

        for (int i = 0; i < 10; i++) {
            rateLimit.preHandle(request, response, null);
        }
        rateLimit.preHandle(request, response, null);
        String retryAfter = response.getHeader("X-Rate-Limit-Retry-After-Seconds");

        assertNotNull(retryAfter);
        assertTrue(Integer.parseInt(retryAfter) > 0);
    }

    @Test
    void testInternalEndpointExemption() throws Exception {
        request.setRequestURI("/internal");
        request.setRemoteAddr("192.168.1.1");
        boolean allowed = rateLimit.preHandle(request, response, null);

        assertTrue(allowed);
        assertNull(response.getHeader("X-Rate-Limit-Remaining"));
    }
}
