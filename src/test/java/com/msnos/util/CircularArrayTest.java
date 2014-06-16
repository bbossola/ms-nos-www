package com.msnos.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CircularArrayTest {

    private CircularArray<String> data;

    @Before
    public void setup () {
        data = new CircularArray<String>(3);
    }
    
    @Test
    public void shouldStoreOneElement() {
        data.add("one");
        assertEquals("one", data.get(0));
    }

    @Test
    public void shouldComputeSizeWithOneElement() {
        data.add("one");
        assertEquals(1, data.size());
    }

    @Test
    public void shouldScrollElementWhenOverflow() {
        data.add("aaa");
        data.add("bbb");
        data.add("ccc");
        data.add("ddd");

        assertEquals("bbb", data.get(0));
        assertEquals("ccc", data.get(1));
        assertEquals("ddd", data.get(2));
    }

    @Test
    public void shouldComputeSizeWhenOverflow() {
        data.add("aaa");
        data.add("bbb");
        data.add("ccc");
        data.add("ddd");

        assertEquals(3, data.size());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void shouldThrowExceptionWhenGetOutOfUpperBound() {
        data.get(3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void shouldThrowExceptionWhenGetOutOfLowerBound() {
        data.add("a");
        data.add("b");
        data.add("c");
        data.add("d");

        data.get(-1);
    }
    
    @Test
    public void shouldReturnElementsWhenHalfEmpty() {
        data.add("a");
        data.add("b");
        
        String[] values = data.toArray(new String[data.size()]);
        assertEquals("a", values[0]);
        assertEquals("b", values[1]);
    }

    @Test
    public void shouldReturnElementsWhenOverflow() {
        data.add("a");
        data.add("b");
        data.add("c");
        data.add("d");

        String[] values = data.toArray(new String[data.size()]);
        assertEquals("b", values[0]);
        assertEquals("c", values[1]);
        assertEquals("d", values[2]);
    }

}
