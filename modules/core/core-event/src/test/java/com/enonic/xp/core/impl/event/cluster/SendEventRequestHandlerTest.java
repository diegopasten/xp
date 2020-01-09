package com.enonic.xp.core.impl.event.cluster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.hazelcast.core.Message;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SendEventRequestHandlerTest
{
    private SendEventRequestHandler sendEventRequestHandler;

    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp()
    {
        this.eventPublisher = Mockito.mock( EventPublisher.class );

        this.sendEventRequestHandler = new SendEventRequestHandler();
        this.sendEventRequestHandler.setEventPublisher( this.eventPublisher );
    }

    @Test
    void testMessageReceived()
    {
        //Creates an event
        Event event = Event.create( "eventType" ).
            timestamp( 123L ).
            distributed( true ).
            value( "key1", "value1" ).
            value( "key2", 1234L ).build();

        //Passes the event received to SendEventRequestHandler
        Message<Event> message = new Message<>( "", event, System.currentTimeMillis(), null );
        this.sendEventRequestHandler.onMessage( message );

        //Checks that the event was correctly published
        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );
        Mockito.verify( this.eventPublisher ).publish( argumentCaptor.capture() );
        final Event eventForwarded = argumentCaptor.getValue();
        assertEquals( eventForwarded.getType(), event.getType() );
        assertEquals( eventForwarded.getTimestamp(), event.getTimestamp() );
        assertFalse( eventForwarded.isDistributed() );
        assertEquals( eventForwarded.getData(), event.getData() );
    }
}
