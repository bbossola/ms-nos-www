package com.msnos.www.servlet;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.workshare.msnos.core.Iden;
import com.workshare.msnos.core.Message;

public class CoreServletGetTest extends AbstractCoreServletTest {

    protected static final Iden MESSAGE = new Iden(Iden.Type.MSG, UUID.randomUUID());

    private String cloud;
    private String message;

    private PrintWriter writer;
    
    @Before
    public void beforePost() throws Exception {
        cloud = CLOUD.getUUID().toString();
        message = MESSAGE.getUUID().toString();
        
        writer = mock(PrintWriter.class);
        when(httpResponse.getWriter()).thenReturn(writer);
    }
    
    @Test
    public void shouldSend400IfCloudIdIsMissing() throws Exception {
        
        cloud = null;
        invoke();
        
        verify(httpResponse).setStatus(400);
    }

    @Test
    public void shouldSend400IfCloudIsNotUUID() throws Exception {
        
        cloud = "XXX";
        invoke();
        
        verify(httpResponse).setStatus(400);
    }

    @Test
    public void shouldSend400IfMessageIsNotUUID() throws Exception {
        
        message = "1234";
        invoke();
        
        verify(httpResponse).setStatus(400);
    }

    @Test
    public void shouldSendCloudIdToMessageStore() throws Exception {
        
        invoke();
        
        verify(messages).load(eq(CLOUD.getUUID()), any(UUID.class));
    }

    @Test
    public void shouldSendMessageIdToMessageStore() throws Exception {
        
        invoke();
        
        verify(messages).load(any(UUID.class), eq(MESSAGE.getUUID()));
    }


    @Test
    public void shouldReturn200OnSuccess() throws Exception {
        
        invoke();
        
        verify(httpResponse).setStatus(200);
    }

    @Test
    public void shouldReturnStoredMessages() throws Exception {
        
        Message values[] = new Message[] {
                newSampleMessage(JOHN, CLOUD),
                newSampleMessage(SMITH, CLOUD),
        };
        when(messages.load(any(UUID.class), any(UUID.class))).thenReturn(Arrays.asList(values));

        invoke();
        
        verify(writer).println(serializer.toText(values[0]));
        verify(writer).println(serializer.toText(values[1]));
    }

    @Override
	protected void invoke() throws Exception {        
		if (cloud != null)
            when(httpRequest.getParameter("cloud")).thenReturn(cloud);
        if (message != null)
            when(httpRequest.getParameter("message")).thenReturn(message);

        when(httpRequest.getMethod()).thenReturn("GET");
        servlet.service(httpRequest, httpResponse);
	}
}
