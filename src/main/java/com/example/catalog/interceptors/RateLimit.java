package com.example.catalog.interceptors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class RateLimit implements HandlerInterceptor {

    @Value("${rate-limit.algo}")
    private String rateLimitAlgo;

    @Value("${rate-limit.rpm}")
    private String rateLimitRPM;

    @Value("${rate-limit.enabled}")
    private boolean rateLimitEnabled;

    private final Map<String, SlidingWindow> slidingWindowState = new ConcurrentHashMap<>();
    private final Map<String, FixedWindowCounter> fixedWindowState = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();

        if (!rateLimitEnabled) {
            return true;
        }

        // Skip rate limiting for internal endpoints
        if ("/internal".equals(request.getRequestURI())) {
            return true;
        }


        if (!isAllowed(clientIp, response)) {
            response.setStatus(429);
            response.setHeader("X-Rate-Limit-Remaining", "0");
            return false;
        }

        return true;
    }

    private boolean isAllowed(String clientIp, HttpServletResponse response) {
        int rateLimitRPMInt = Integer.parseInt(rateLimitRPM);
        if ("moving".equalsIgnoreCase(rateLimitAlgo)) {
            SlidingWindow rateLimiter = slidingWindowState.computeIfAbsent(clientIp, ip -> new SlidingWindow(rateLimitRPMInt));
            if (!rateLimiter.allowRequest()) {
                response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(rateLimiter.getRetryAfterSeconds()));
                return false;
            }
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(rateLimiter.getRemainingRequests()));
        } else if ("fixed".equalsIgnoreCase(rateLimitAlgo)) {
            FixedWindowCounter rateLimiter = fixedWindowState.computeIfAbsent(clientIp, ip -> new FixedWindowCounter(rateLimitRPMInt));
            if (!rateLimiter.allowRequest()) {
                response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(rateLimiter.getRetryAfterSeconds()));
                return false;
            }
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(rateLimiter.getRemainingRequests()));
        }
        return true;
    }

    private static class SlidingWindow {
        private final long[] timestamps; // Circular buffer to store timestamps
        private final int maxRequests; // Maximum allowed requests per minute
        private int head; // Points to the next insertion index
        private int tail; // Points to the oldest request index
        private int count; // Number of valid requests in the window
        private long oldestTimestamp; // Timestamp of the oldest request

        SlidingWindow(int maxRequests) {
            this.maxRequests = maxRequests;
            this.timestamps = new long[maxRequests];
            this.head = 0;
            this.tail = 0;
            this.count = 0;
            this.oldestTimestamp = 0;
        }

        public synchronized boolean allowRequest() {
            long now = System.currentTimeMillis();

            // Remove expired requests
            for (int i = 0; i < maxRequests; i++) {
                if (count > 0 && now - oldestTimestamp >= 60000) {
                    // Move tail to the next
                    tail = (tail + 1) % maxRequests;
                    count--;
                    if (count > 0) {
                        // Update oldest timestamp
                        oldestTimestamp = timestamps[tail];
                    }
                }
            }

            // Check if the request can be allowed
            if (count < maxRequests) {
                timestamps[head] = now; // Store the current request
                head = (head + 1) % maxRequests;
                count++;
                if (count == 1) {
                    oldestTimestamp = now;
                }
                return true;
            }
            return false;
        }

        public synchronized int getRemainingRequests() {
            long now = System.currentTimeMillis();

            // Remove expired requests
            for (int i = 0; i < maxRequests; i++) {
                if (count > 0 && now - oldestTimestamp >= 60000) {
                    tail = (tail + 1) % maxRequests;
                    count--;
                    if (count > 0) {
                        oldestTimestamp = timestamps[tail];
                    }
                }
            }
            return maxRequests - count;
        }

        public synchronized long getRetryAfterSeconds() {
            if (count == 0) {
                return 0;
            }

            long now = System.currentTimeMillis();
            return Math.max(0, (60000 - (now - oldestTimestamp)) / 1000);
        }
    }

    // Fixed Window Implementation
    private static class FixedWindowCounter {
        private final int maxRequests;
        private int currentCount;
        private long windowStart;
        private static final long WINDOW_SIZE_MILLIS = 60000; // 1 minute

        FixedWindowCounter(int maxRequests) {
            this.maxRequests = maxRequests;
            this.currentCount = 0;
            this.windowStart = System.currentTimeMillis();
        }

        public synchronized boolean allowRequest() {
            long now = System.currentTimeMillis();

            if (now - windowStart >= WINDOW_SIZE_MILLIS) {
                windowStart = now;
                currentCount = 0;
            }

            if (currentCount < maxRequests) {
                currentCount++;
                return true;
            }

            return false;
        }

        public synchronized int getRemainingRequests() {
            return Math.max(0, maxRequests - currentCount);
        }

        public synchronized long getRetryAfterSeconds() {
            long now = System.currentTimeMillis();
            return Math.max(0, (windowStart + WINDOW_SIZE_MILLIS - now) / 1000);
        }
    }

    // Setters for testing
    public void setRateLimitEnabled(boolean b) {
        this.rateLimitEnabled = b;
    }

    public void setRateLimitAlgo(String rateLimitAlgo) {
        this.rateLimitAlgo = rateLimitAlgo;
    }

    public void setRateLimitRPM(String rateLimitRPM) {
        this.rateLimitRPM = rateLimitRPM;
    }
}
