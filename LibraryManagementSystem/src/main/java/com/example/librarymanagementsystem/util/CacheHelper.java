package com.example.librarymanagementsystem.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

// @Component creates a singleton cache for frequently accessed data
@Component
public class CacheHelper {

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final int CACHE_EXPIRY_MINUTES = 5;

    // Cache entry with timestamp for expiration
    private static class CacheEntry {
        final Object data;
        final LocalDateTime timestamp;

        CacheEntry(Object data) {
            this.data = data;
            this.timestamp = LocalDateTime.now();
        }

        boolean isExpired() {
            return timestamp.plusMinutes(CACHE_EXPIRY_MINUTES).isBefore(LocalDateTime.now());
        }
    }

    // Get cached data if not expired
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return (T) entry.data;
        }
        return null;
    }

    // Store data in cache
    public void put(String key, Object data) {
        cache.put(key, new CacheEntry(data));
    }

    // Clear specific cache entry
    public void evict(String key) {
        cache.remove(key);
    }

    // Clear all cache entries
    public void evictAll() {
        cache.clear();
    }

    // Clear expired entries
    public void cleanExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}