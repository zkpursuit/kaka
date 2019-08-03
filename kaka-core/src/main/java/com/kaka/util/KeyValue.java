package com.kaka.util;

/**
 *
 * @author zkpursuit
 * @param <K>
 * @param <V>
 */
public interface KeyValue<K, V> {
    
    void setKey(K key);
    void setValue(V value);
    K getKey();
    V getValue();
    
}
