package adt;

/**
 * List Abstract Data Type (ADT)
 * Defines operations for an ordered collection of elements.
 * Author: [Your Name]
 */
public interface ListADT<T> {
    void add(T item);               // add at end
    void add(int index, T item);    // add at specific index
    void remove(int index);         // remove element at index
    T get(int index);               // retrieve element
    void set(int index, T item);    // replace element
    int size();                     // number of elements
    boolean isEmpty();              // check if empty
    void clear();                   // remove all elements
}
