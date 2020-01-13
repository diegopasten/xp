package com.enonic.xp.core.impl.app;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.ManagedExecutor;

@Component
public class ApplicationListenerHubExecutorImpl
    implements ApplicationListenerHubExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationListenerHubExecutorImpl.class );

    private final ManagedExecutor managedExecutor;

    public ApplicationListenerHubExecutorImpl()
    {
        managedExecutor = new ManagedExecutor( Executors::newSingleThreadExecutor, "app-lifecycle-event-dispatcher-thread",
                                               e -> LOG.error( "App lifecycle event dispatch failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        managedExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ),
                                                     notStarted -> LOG.warn( "Not all app lifecycle events were dispatched" ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        managedExecutor.execute( command );
    }
}
