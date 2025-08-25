package adt;

import java.util.function.Predicate;

/**
 * HashMap Interface
 * Defines operations for a hash map data structure.
 * Author: [Your Name]
 */
public interface HashMapInterface<K, V> {
    // put key-value pair
    void put(K key, V value);

    // get value by key
    V get(K key);

    // remove key-value pair by key
    V remove(K key);

    // check if key exists
    boolean containsKey(K key);

    // check if value exists
    boolean containsValue(V value);

    // number of key-value pairs
    int size();

    // check if empty
    boolean isEmpty();

    // remove all key-value pairs
    void clear();

    // get all keys
    ListInterface<K> keySet();

    // get all values
    ListInterface<V> values();

    // filter: return a new filtered map
    HashMapInterface<K, V> filter(Predicate<V> criteria);

    // get all values as a list
    ListInterface<V> toList();

}
