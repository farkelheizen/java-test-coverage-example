package com.example.service;

import java.util.HashMap;
import java.util.Map;

public class CacheService {

    private final Map<String, Object> cache = new HashMap<>();
    private final Map<String, Long> expiry = new HashMap<>();

    public void put(String key, Object value, long ttlSeconds) {
        cache.put(key, value);
        expiry.put(key, System.currentTimeMillis() + ttlSeconds * 1000);
    }

    public Object get(String key) {
        Long expiryTime = expiry.get(key);
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            cache.remove(key);
            expiry.remove(key);
            return null;
        }
        return cache.get(key);
    }

    public void invalidate(String key) {
        cache.remove(key);
        expiry.remove(key);
    }

    public void clear() {
        cache.clear();
        expiry.clear();
    }
}
