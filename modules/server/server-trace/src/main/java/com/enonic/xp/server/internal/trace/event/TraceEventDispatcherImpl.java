package com.enonic.xp.server.internal.trace.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceListener;

@Component
public final class TraceEventDispatcherImpl
    implements TraceEventDispatcher
{
    private final TraceListeners listeners = new TraceListeners();

    private final BlockingQueue<TraceEvent> queue = new LinkedBlockingQueue<>();

    private final Executor executor;

    private volatile boolean active = true;

    @Activate
    public TraceEventDispatcherImpl( @Reference(service = TraceEventDispatcherExecutor.class) final Executor executor )
    {
        this.executor = executor;
    }

    @Activate
    public void activate()
    {
        this.executor.execute( this::run );
    }

    @Deactivate
    public void deactivate()
    {
        active = false;
    }

    @Override
    public void queue( final TraceEvent event )
    {
        this.queue.add( event );
    }

    private void run()
    {
        while ( active )
        {
            try
            {
                final TraceEvent event = this.queue.take();
                this.listeners.onTrace( event );
            }
            catch ( final InterruptedException e )
            {
                return;
            }
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addListener( final TraceListener listener )
    {
        this.listeners.add( listener );
    }

    public void removeListener( final TraceListener listener )
    {
        this.listeners.remove( listener );
    }
}
