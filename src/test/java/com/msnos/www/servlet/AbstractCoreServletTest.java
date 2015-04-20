package com.msnos.www.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.msnos.www.config.Configuration;
import com.msnos.www.core.MessageStore;
import com.msnos.www.servlet.CoreServlet;
import com.workshare.msnos.core.Iden;
import com.workshare.msnos.core.Message;
import com.workshare.msnos.core.MessageBuilder;
import com.workshare.msnos.core.serializers.WireJsonSerializer;
import com.workshare.msnos.core.serializers.WireSerializer;

public abstract class AbstractCoreServletTest {

    protected static final Iden CLOUD = new Iden(Iden.Type.CLD, UUID.randomUUID());
    protected static final Iden SMITH = new Iden(Iden.Type.AGT, UUID.randomUUID());
    protected static final Iden JOHN = new Iden(Iden.Type.AGT, UUID.randomUUID());
    
    protected static WireSerializer serializer = new WireJsonSerializer();
    
    protected HttpServletRequest httpRequest;
    protected HttpServletResponse httpResponse;
    protected StringWriter responseText;
    protected CoreServlet servlet;
    protected Map<String, String> headers;
    protected MessageStore messages;
    

    @Before
	public void init() throws IOException {
		httpRequest = mock(HttpServletRequest.class);
		httpResponse = mock(HttpServletResponse.class);
				
		headers = new HashMap<String, String>();
		doAnswer(new Answer<Void>() {
	        @Override
	        public Void answer(InvocationOnMock invocation) throws Throwable {
	        	Object[] args = invocation.getArguments();
	        	headers.put(args[0].toString().toLowerCase(), args[1].toString().toLowerCase());
	        	return null;
	        }
	    }).when(httpResponse).setHeader(anyString(), anyString());
		
		responseText = new StringWriter();
		when(httpResponse.getWriter()).thenReturn(new PrintWriter(responseText));

		messages = mock(MessageStore.class);
        servlet = new CoreServlet(messages, mock(Configuration.class));
	}

	@Test
	public void shouldReturnCacheControl() throws Exception {
		invoke();
		assertEquals("no-cache, no-store", headers.get("cache-control") );
	}

	@Test
	public void shouldReturnContentType() throws Exception {
		invoke();
		assertEquals("application/json", headers.get("content-type") );
	}

    @Test
    public void shouldReturnAccessControlForCORS() throws Exception {
        invoke();
    
        assertEquals("*", headers.get("access-control-allow-origin") );
        assertEquals("x-requested-with", headers.get("access-control-allow-headers") );
        assertEquals("get, post, options", headers.get("access-control-allow-methods") );
        assertEquals("true", headers.get("access-control-allow-credentials") );
        assertEquals("1728000", headers.get("access-control-max-age") );        
    }

    protected abstract void invoke() throws Exception
	;

    protected Message newSampleMessage(final Iden from, final Iden to) {
        return new MessageBuilder(Message.Type.ENQ, from, to).with(UUID.randomUUID()).make();
    }

}
