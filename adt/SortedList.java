package adt;

/**
 * Sorted Linked List implementation of ListADT
 * Maintains elements in ascending order based on Comparable<T>.
 * Author: [Your Name]
 */
public class SortedList<T extends Comparable<T>> implements ListADT<T> {
    private Node<T> head;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    @Override
    public void add(T item) {
        Node<T> newNode = new Node<>(item);

        if (head == null || head.data.compareTo(item) > 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null && current.next.data.compareTo(item) <= 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }

        size++;
    }

    @Override
    public void add(int index, T item) {
        // For SortedList, we ignore index parameter
        // Always insert in sorted order
        add(item);
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        if (index == 0) {
            head = head.next;
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            current.next = current.next.next;
        }
        size--;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    @Override
    public void set(int index, T item) {
        // Not allowed in SortedList, because it may break sorting
        throw new UnsupportedOperationException("set() not supported in SortedList");
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
        head = null;
        size = 0;
    }
}
