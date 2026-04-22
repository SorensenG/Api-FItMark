package com.Sorensen.FitMark.security.ratelimit;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();
    private final AtomicInteger calls = new AtomicInteger(0);

    public void assertAllowed(String key, int maxRequests, Duration window, String message) {
        if (!isAllowed(key, maxRequests, window)) {
            throw new RateLimitExceededException(message);
        }
    }

    public boolean isAllowed(String key, int maxRequests, Duration window) {
        long now = System.currentTimeMillis();
        long windowMs = window.toMillis();

        WindowCounter counter = counters.computeIfAbsent(key, k -> new WindowCounter(now));
        boolean allowed;

        synchronized (counter) {
            if (now - counter.windowStart >= windowMs) {
                counter.windowStart = now;
                counter.count = 0;
            }
            counter.count++;
            allowed = counter.count <= maxRequests;
        }

        cleanupOccasionally(now, windowMs);
        return allowed;
    }

    private void cleanupOccasionally(long now, long latestWindowMs) {
        if (calls.incrementAndGet() % 500 != 0) {
            return;
        }

        counters.entrySet().removeIf(entry -> now - entry.getValue().windowStart > latestWindowMs * 2);
    }

    private static final class WindowCounter {
        private volatile long windowStart;
        private int count;

        private WindowCounter(long windowStart) {
            this.windowStart = windowStart;
            this.count = 0;
        }
    }
}

