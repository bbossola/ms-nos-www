package com.msnos.www.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.msnos.util.CircularArray;
import com.workshare.msnos.core.Message;

public class InmemoryMessageStore implements MessageStore {

    private static final int MEMORY_SIZE = Integer.getInteger("msnos.www.cloud.memory.size", 100);

    private Map<UUID, CircularArray<Message>> clouds;
    
    public InmemoryMessageStore() {
        clouds = new HashMap<UUID, CircularArray<Message>>();
    }

    @Override
    public void store(UUID cloudId, Message message) throws IOException {
        CircularArray<Message> messages = getMessages(cloudId);
        synchronized(messages) {
            messages.add(message);
        }
    }

    @Override
    public List<Message> load(UUID cloudId, UUID lastMessageId) throws IOException {
        CircularArray<Message> messages = getMessages(cloudId);
        synchronized(messages) {
            List<Message> result;
            if (lastMessageId != null) {
                boolean store = false;
                result = new ArrayList<Message>();
                for (int i=0; i<messages.size(); i++) {
                    Message m = messages.get(i);
                    if (store)
                        result.add(m);
                    else if (m.getUuid().equals(lastMessageId))
                        store = true;
                }
            } else {
                result = Arrays.asList(messages.toArray(new Message[messages.size()]));
            }
            
            return result;
        }
    }

    private CircularArray<Message> getMessages(UUID cloudId) {
        CircularArray<Message> messages;
        synchronized(clouds) {
            messages = clouds.get(cloudId);
            if (messages == null) {
                messages = new CircularArray<Message>(MEMORY_SIZE);
                clouds.put(cloudId, messages);
            }
        }
        return messages;
    }

}

