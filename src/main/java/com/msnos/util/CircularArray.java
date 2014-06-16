package com.msnos.util;

/**
 * Please note that this class IS NOT THREADSAFE
 * @author bbossola
 *
 * @param <E>
 */
public class CircularArray<E> {
    
    private final int capacity;
    private final E[] buffer;

    private int head = 0;
    private int next = 0;
    private boolean full = false;
    
    @SuppressWarnings("unchecked")
    public CircularArray(int capacity) {
        this.capacity = capacity;
        this.buffer = (E[]) new Object[capacity];
    }
  
    public int size() {
        return (full ? capacity : next);
    }

    public void add(E element) {
        buffer[next] = element;
        step();
    }

    public E get(int i) {
        if (i<0 || i >= capacity)
            throw new ArrayIndexOutOfBoundsException("Index should be betweeen 0 and "+(capacity-1));
        return buffer[at(i)];
    }

    private void step() {
        if (full)
            head = ++head%capacity;
        
        next = ++next%capacity;
        if (next == 0)
            full = true;
    }

    private int at(int offset) {
        return (head+offset)%capacity;
    }

    public E[] toArray(E[] fillme) {
        if (full) {
            final int rlen = capacity-head;
            System.arraycopy(buffer, head, fillme, 0, rlen);
            System.arraycopy(buffer, 0, fillme, rlen, head);
        } else {
            System.arraycopy(buffer, head, fillme, 0, next);
        }
        
        return fillme;
    }
}