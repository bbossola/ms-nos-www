package com.msnos.www.core;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.workshare.msnos.core.Message;

public interface MessageStore {

	public void store(UUID cloudId, Message message) throws IOException;

	public List<Message> load(UUID cloudId, UUID lastMessageId) throws IOException;

}
