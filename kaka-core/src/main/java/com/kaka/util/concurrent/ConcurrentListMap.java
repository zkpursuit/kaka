package com.kaka.util.concurrent;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * 键对应列表集合的Map结构
 *
 * @param <K> 键
 * @param <V> 列表值
 */
public class ConcurrentListMap<K, V> implements Serializable {

    private Map<K, List<V>> map;

    public ConcurrentListMap() {
        map = new HashMap<>();
    }

    public ConcurrentListMap(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }

    public void put(K key, V value) {
        List<V> list;
        synchronized (map) {
            list = map.get(key);
            if (list == null) {
                list = Collections.synchronizedList(new ArrayList<>());
                map.put(key, list);
            }
        }
        list.add(value);
    }

    public List<V> get(K key) {
        synchronized (map) {
            return map.get(key);
        }
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public boolean remove(K key, V value) {
        List<V> list = get(key);
        if (list != null) {
            return list.remove(value);
        }
        return false;
    }

    public void removeAll(K key) {
        List<V> list = get(key);
        if (list != null) {
            list.clear();
        }
    }

    public void remove(K key) {
        removeAll(key);
        synchronized (map) {
            map.remove(key);
        }
    }

    public boolean containsValue(V value) {
        synchronized (map) {
            Collection<List<V>> vs = map.values();
            for (List<V> list : vs) {
                if (list.contains(value)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void forEach(K key, Consumer<? super V> action) {
        List<V> list = get(key);
        if (list != null) {
            if (!list.isEmpty()) {
                list.forEach(action);
            }
        }
    }

    public Set<K> keySet() {
        synchronized (map) {
            return map.keySet();
        }
    }

    public void clear() {
        synchronized (map) {
            map.forEach((K key, List<V> list) -> {
                if (list != null && !list.isEmpty()) {
                    list.clear();
                }
            });
            map.clear();
        }
    }

}