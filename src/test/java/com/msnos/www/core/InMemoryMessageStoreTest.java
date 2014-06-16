package com.msnos.www.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.msnos.www.core.InmemoryMessageStore;
import com.workshare.msnos.core.Iden;
import com.workshare.msnos.core.Message;

public class InMemoryMessageStoreTest {

    private static final UUID CLOUD = UUID.randomUUID();
    private static final UUID MESSAGE = UUID.randomUUID();
    private static final UUID OTHER_CLOUD = UUID.randomUUID();

    private InmemoryMessageStore store;
    
    @Before
    public void setup() {
        store = new InmemoryMessageStore();
    }
    
    @Test
    public void shouldReturnEmptyListWhenCloudNotFound() throws IOException {
        List<Message> result = store.load(CLOUD, MESSAGE);
        assertEquals(0, result.size());
    }
    
    @Test
    public void shouldReturnAllMessagesForACloudWhenLastNotSpecified() throws IOException {
        Message one = store(sampleMessageOf(CLOUD));
        Message two = store(sampleMessageOf(CLOUD));
        
        List<Message> result = store.load(CLOUD, null);

        assertEquals(2, result.size());
        assertEquals(one, result.get(0));
        assertEquals(two, result.get(1));
    }

    @Test
    public void shouldReturnAllMessagesFilteringUnrelatedClouds() throws IOException {
        Message one = store(sampleMessageOf(CLOUD));
        store(sampleMessageOf(OTHER_CLOUD));
        Message two = store(sampleMessageOf(CLOUD));
        
        List<Message> result = store.load(CLOUD, null);

        assertEquals(2, result.size());
        assertEquals(one, result.get(0));
        assertEquals(two, result.get(1));
    }

    @Test
    public void shouldReturnAllMessagesFilteringExcludingTheReadOnes() throws IOException {
        Message one = store(sampleMessageOf(CLOUD));
        Message two = store(sampleMessageOf(CLOUD));
        Message tre = store(sampleMessageOf(CLOUD));
        
        List<Message> result = store.load(CLOUD, one.getUuid());

        assertEquals(2, result.size());
        assertEquals(two, result.get(0));
        assertEquals(tre, result.get(1));
    }


    private Message store(Message message) throws IOException {
        store.store(message.getFrom().getUUID(), message);
        return message;
    }

    private Message sampleMessageOf(UUID cloud) {
        Message message = Mockito.mock(Message.class);
        Iden iden = new Iden(Iden.Type.CLD, cloud);
        when(message.getFrom()).thenReturn(iden);
        when(message.getUuid()).thenReturn(UUID.randomUUID());
        return message;
    }
}
