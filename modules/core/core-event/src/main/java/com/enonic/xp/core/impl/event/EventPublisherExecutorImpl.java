package com.enonic.xp.core.impl.event;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.ManagedExecutor;

@Component
public class EventPublisherExecutorImpl
    implements EventPublisherExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( EventPublisherExecutorImpl.class );

    private final ManagedExecutor managedExecutor;

    public EventPublisherExecutorImpl()
    {
        managedExecutor = new ManagedExecutor( Executors::newSingleThreadExecutor, "event-publisher-thread",
                                               e -> LOG.error( "Event publishing failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        managedExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ), notStarted -> LOG.warn( "Not all events were published" ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        managedExecutor.execute( command );
    }
}
