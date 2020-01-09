package com.enonic.xp.core.impl.event.cluster;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;

@Component(immediate = true)
public final class SendEventRequestHandler
    implements MessageListener<Event>
{
    private HazelcastInstance hazelcastInstance;

    private EventPublisher eventPublisher;

    private ITopic<Event> topic;

    private String registrationId;

    @Activate
    public void activate()
    {
        topic = hazelcastInstance.getTopic( ClusterEventSender.ACTION );
        registrationId = topic.addMessageListener( this );
    }

    @Deactivate
    public void deactivate()
    {
        topic.removeMessageListener( registrationId );
    }

    @Override
    public void onMessage( final Message<Event> message )
    {
        final Event forwardedEvent = Event.create( message.getMessageObject() ).
            distributed( false ).
            localOrigin( false ).
            build();
        this.eventPublisher.publish( forwardedEvent );
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }

    @Reference
    public void setHazelcastInstance( final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }
}
