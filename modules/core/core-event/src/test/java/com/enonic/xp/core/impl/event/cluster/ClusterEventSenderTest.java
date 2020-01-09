package com.enonic.xp.core.impl.event.cluster;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;

import com.enonic.xp.event.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("hazelcast")
class ClusterEventSenderTest
{
    private ClusterEventSender clusterEventSender;

    private HazelcastInstance remoteHz;

    private HazelcastInstance localHz;

    private ITopic<Event> topic;

    @BeforeEach
    void setUp()
    {
        Config cfg = new Config();

        localHz = Hazelcast.newHazelcastInstance( cfg );
        topic = localHz.getTopic( ClusterEventSender.ACTION );

        remoteHz = Hazelcast.newHazelcastInstance( cfg );

        clusterEventSender = new ClusterEventSender();
        clusterEventSender.setHazelcastInstance( remoteHz );
        clusterEventSender.activate();
    }

    @AfterEach
    void tearDown()
    {
        remoteHz.shutdown();
        localHz.shutdown();
    }

    @Test
    void onEvent()
        throws Exception
    {
        CompletableFuture<Message<Event>> received = new CompletableFuture<>();
        final Event event = Event.create( "aaa" ).distributed( true ).build();

        topic.addMessageListener( received::complete );
        this.clusterEventSender.onEvent( event );

        assertEquals( received.get( 10, TimeUnit.SECONDS ).getMessageObject().getType(), "aaa" );
    }


    @Test
    void onNonDistributableEvent()
    {
        CompletableFuture<Message<Event>> received = new CompletableFuture<>();

        final Event event = Event.create( "aaa" ).build();

        topic.addMessageListener( received::complete );
        this.clusterEventSender.onEvent( event );
        assertThrows( TimeoutException.class, () -> received.get( 10, TimeUnit.SECONDS ) );
    }
}
