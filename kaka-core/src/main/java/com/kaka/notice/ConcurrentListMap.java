package com.kaka.notice;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 主要用于{@link com.kaka.notice.Facade}保存{@link com.kaka.notice.Mediator}的相关引用 <br>
 * 本类具有很大的局限性，如需广泛使用相似功能的数据结构，请使用或参考Google Guava框架中所提供的MultiMap <br>
 *
 * @param <K>
 * @param <V>
 */
class ConcurrentListMap<K, V> {

    private Map<K, List<V>> map;

    public ConcurrentListMap() {
        map = new ConcurrentHashMap<>();
    }

    public void put(K key, V value) {
        List<V> list;
        if (!map.containsKey(key)) {
            list = new CopyOnWriteArrayList();
            map.put(key, list);
        } else {
            list = get(key);
        }
        list.add(value);
    }

    public List<V> get(K key) {
        return map.get(key);
    }

    public boolean remove(K key, V value) {
        List<V> list = get(key);
        if (list != null) {
            return list.remove(value);
        }
        return false;
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public void forEach(K key, Consumer<? super V> action) {
        List<V> list = get(key);
        if (list != null && !list.isEmpty()) {
            list.forEach(action);
        }
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public void clear() {
        map.forEach((K key, List<V> list) -> {
            if (list != null && !list.isEmpty()) {
                list.clear();
            }
        });
        map.clear();
    }

}
