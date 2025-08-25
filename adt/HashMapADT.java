package adt;

import java.util.function.Predicate;
import java.util.Comparator;

/**
 * Hash table implementation of HashMapInterface
 * Uses separate chaining for collision resolution
 * Author: [Your Name]
 */
@SuppressWarnings("unchecked")
public class HashMapADT<K, V> implements HashMapInterface<K, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Entry<K, V>[] table;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    public HashMapADT() {
        table = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        size = 0;
    }

    private int hash(K key) {
        if (key == null)
            return 0;
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % table.length;
    }

    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = (Entry<K, V>[]) new Entry[table.length * 2];
        size = 0;

        for (Entry<K, V> entry : oldTable) {
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }

    @Override
    public void put(K key, V value) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        Entry<K, V> entry = table[index];

        // Check if key already exists
        while (entry != null) {
            if ((key == null && entry.key == null) ||
                    (key != null && key.equals(entry.key))) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }

        // Add new entry at the beginning of the chain
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
    }

    @Override
    public V get(K key) {
        int index = hash(key);
        Entry<K, V> entry = table[index];

        while (entry != null) {
            if ((key == null && entry.key == null) ||
                    (key != null && key.equals(entry.key))) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int index = hash(key);
        Entry<K, V> entry = table[index];
        Entry<K, V> prev = null;

        while (entry != null) {
            if ((key == null && entry.key == null) ||
                    (key != null && key.equals(entry.key))) {
                V value = entry.value;

                if (prev == null) {
                    table[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }

                size--;
                return value;
            }
            prev = entry;
            entry = entry.next;
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                if ((value == null && entry.value == null) ||
                        (value != null && value.equals(entry.value))) {
                    return true;
                }
                entry = entry.next;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    @Override
    public ListInterface<K> keySet() {
        ListInterface<K> keys = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                keys.add(entry.key);
                entry = entry.next;
            }
        }
        return keys;
    }

    @Override
    public ListInterface<V> values() {
        ListInterface<V> values = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                values.add(entry.value);
                entry = entry.next;
            }
        }
        return values;
    }

    @Override
    public HashMapInterface<K, V> filter(Predicate<V> criteria) {
        HashMapInterface<K, V> results = new HashMapADT<>();
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                if (criteria.test(entry.value)) {
                    results.put(entry.key, entry.value);
                }
                entry = entry.next;
            }
        }
        return results;
    }

    @Override
    public ListInterface<V> toList() {
        ListInterface<V> list = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                list.add(entry.value);
                entry = entry.next;
            }
        }
        return list;
    }

}
