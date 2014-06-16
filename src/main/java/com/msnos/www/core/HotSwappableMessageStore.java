package com.msnos.www.core;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.workshare.msnos.core.Message;

public class HotSwappableMessageStore implements MessageStore {
    
    private volatile MessageStore delegate;

    public HotSwappableMessageStore(MessageStore delegate) {
        swapDelegate(delegate);
    }

    public void swapDelegate(MessageStore delegate) {
        this.delegate = delegate;
    }
    
    public MessageStore getDelegate() {
        return delegate;
    }

    @Override
    public void store(UUID cloudId, Message message) throws IOException {
        delegate.store(cloudId, message);
    }

    @Override
    public List<Message> load(UUID cloudId, UUID lastMessageId) throws IOException {
        return delegate.load(cloudId, lastMessageId);
    }
}
