package com.msnos.www.servlet;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.workshare.msnos.core.Message;

public class CoreServletPostTest  extends AbstractCoreServletTest {

    private StringBuilder input;
    private String cloud_uuid;
    
    @Before
    public void beforePost() throws Exception {
        input = new StringBuilder();
        cloud_uuid = CLOUD.getUUID().toString();
    }
    
    @Test
    public void shouldReturn400WhenMissingCloudId() throws Exception {
        cloud_uuid = null;
        invoke();
        
        verify(httpResponse).setStatus(400);
    }

    @Test
    public void shouldSend400IfCloudIsNotUUID() throws Exception {
        
        cloud_uuid = "XXX";
        invoke();
        
        verify(httpResponse).setStatus(400);
    }


    @Test
    public void shouldSendSingleMessageToStore() throws Exception {
        Message message = appendInputLine(newSampleMessage(SMITH, CLOUD));
        
        invoke();
        
        verify(messages).store(any(UUID.class),eq(message));
    }

    @Test
    public void shouldSendMultipleMessagesToStore() throws Exception {
        Message message1 = appendInputLine(newSampleMessage(SMITH, CLOUD));
        Message message2 = appendInputLine(newSampleMessage(JOHN, CLOUD));
        
        invoke();
        
        verify(messages).store(eq(CLOUD.getUUID()),eq(message1));
        verify(messages).store(eq(CLOUD.getUUID()),eq(message2));
    }

    @Test
    public void shouldSkipRubbishMessages() throws Exception {
        appendInputLine("{'zoo':3}");
        appendInputLine("  ");
        Message message = appendInputLine(newSampleMessage(SMITH, CLOUD));
        appendInputLine("{'foo':'yoda'}");
        
        invoke();
        
        verify(messages).store(eq(CLOUD.getUUID()),eq(message));
        verifyNoMoreInteractions(messages);
    }

    private Message appendInputLine(Message message) {
        final String text = serializer.toText(message);
        appendInputLine(text);
        return message;
    }

    private void appendInputLine(final String text) {
        input.append(text);
        input.append('\n');
    }

    
    @Override
	protected void invoke() throws IOException {
        final StringReader reader = new StringReader(input.toString());
        when(httpRequest.getReader()).thenReturn(new BufferedReader(reader));

        if (cloud_uuid != null)
            when(httpRequest.getParameter("cloud")).thenReturn(cloud_uuid);

        when(httpRequest.getMethod()).thenReturn("POST");
		servlet.doPost(httpRequest, httpResponse);
	}
}
