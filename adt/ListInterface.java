package adt;

import java.util.Comparator;

/**
 * List Abstract Data Type (ADT)
 * Defines operations for an ordered collection of elements.
 * Author: [Your Name]
 */
public interface ListInterface<T> extends Iterable<T>{   
    // add at end
    void add(T item);            
  
    // add at specific index
    void add(int index, T item);  
      
    // remove element at index
    T remove(int index);   

    // retrieve element
    T get(int index);               

    // replace element
    void set(int index, T item);    

    // number of elements
    int size();                     

    // check if empty
    boolean isEmpty();      

    // remove all elements
    void clear();      
    
    void sort(Comparator<T> comparator);

    void reverseSort(Comparator<T> comparator);
}
