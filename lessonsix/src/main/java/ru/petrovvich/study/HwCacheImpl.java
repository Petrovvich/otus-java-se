package ru.petrovvich.study;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.*;

public class HwCacheImpl<K, V> implements HwCache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HwCacheImpl.class);

    private int capacity = 0;
    private final Map<K, SoftReference<CacheElement<K, V>>> cacheElements = new LinkedHashMap<>();
    private List<HwListener<K, V>> listeners = new ArrayList<>();

    public HwCacheImpl() {
    }

    public HwCacheImpl(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void put(K key, V value) {
        LOGGER.info("Adding element into cash with params: key {}, value {}", key, value);

        CacheElement<K, V> cashingElement = new CacheElement<>(key, value);

        if (!cacheElements.isEmpty() && cacheElements.size() == capacity) {
            K firstKey = cacheElements.keySet().iterator().next();
            cacheElements.remove(firstKey);
            LOGGER.warn("Cache is full. Autoremoved element: {}", firstKey);
        }
        K elementKey = cashingElement.getKey();
        cacheElements.put(elementKey, new SoftReference<>(cashingElement));

        if (hasListeners()) {
            listeners.forEach(l -> notify());
        }
    }

    @Override
    public void remove(K key) {
        LOGGER.info("Removing element with key: {}", key);
        if (cacheElements.isEmpty()) {
            LOGGER.error("Cache is empty! Add something then try remove it.");
            return;
        }
        SoftReference<CacheElement<K, V>> cashingReference = cacheElements.get(key);
        if (cashingReference != null) {
            LOGGER.info("Success remove element with key: {}", key);
            cacheElements.remove(key);
            if (hasListeners()) {
                listeners.forEach(l -> notify());
            }
        } else {
            LOGGER.error("Element with key {} not found in cache.", key);
        }
    }

    @Override
    public V get(K key) {
        LOGGER.info("Getting element with key: {}", key);
        SoftReference<CacheElement<K, V>> softRef = cacheElements.get(key);

        CacheElement<K, V> element = softRef != null ? softRef.get() : null;
        LOGGER.debug("Found element: {}", element);
        if (element != null) {
            if (hasListeners()) {
                listeners.forEach(l -> notify());
            }
            return element.getValue();
        } else {
            LOGGER.error("Key not found: {}", key);
            return null;
        }
    }

    @Override
    public void addListener(HwListener listener) {
        LOGGER.info("Adding listener: {}", listener);
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener listener) {
        if (listeners.isEmpty()) {
            LOGGER.error("Listeners are empty! Add something then try remove it.");
            return;
        }
        if (listeners.contains(listener)) {
            LOGGER.info("Trying to delete listener: {}", listener);
            listeners.remove(listener);
        } else {
            LOGGER.error("Listener {} not found in list.", listener);
        }
    }

    private boolean hasListeners() {
        return listeners.isEmpty();
    }
}
