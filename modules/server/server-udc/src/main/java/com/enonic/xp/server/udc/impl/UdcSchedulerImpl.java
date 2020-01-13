package com.enonic.xp.server.udc.impl;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.ManagedRecurringJobScheduler;
import com.enonic.xp.core.internal.concurrent.RecurringJob;

@Component
public class UdcSchedulerImpl
    implements UdcScheduler
{
    private static final Logger LOG = LoggerFactory.getLogger( UdcSchedulerImpl.class );

    private final Duration initialDelay;

    private final Duration delay;

    private final ManagedRecurringJobScheduler jobScheduler;

    public UdcSchedulerImpl()
    {
        this( Duration.ofMinutes( 10 ), Duration.ofDays( 1 ) );
    }

    UdcSchedulerImpl( final Duration initialDelay, final Duration delay )
    {
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.jobScheduler = new ManagedRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "udc-thread" );
    }

    @Deactivate
    void deactivate()
    {
        jobScheduler.shutdownNow();
    }

    public RecurringJob scheduleWithFixedDelay( Runnable command )
    {
        return jobScheduler.
            scheduleWithFixedDelay( command, initialDelay, delay, e -> LOG.debug( "Error error while sending UDC", e ),
                                    e -> LOG.error( "Error error while sending UDC, no further attempts will be made", e ) );
    }
}
