package com.enonic.xp.server.internal.trace.event;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceListener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TraceEventDispatcherImplTest
{
    private TraceEventDispatcherExecutorImpl executor;

    @BeforeEach
    public void setUp()
    {
        executor = new TraceEventDispatcherExecutorImpl();
    }

    @AfterEach
    public void tearDown()
    {
        executor.deactivate();
    }

    @Test
    void testQueue()
    {
        Phaser phaser = new Phaser( 1 );

        TraceEventDispatcherImpl dispatcher = new TraceEventDispatcherImpl( executor );
        dispatcher.activate();

        final TraceListener listener = Mockito.mock( TraceListener.class );
        dispatcher.addListener( listener );

        // must be the last listener
        dispatcher.addListener( event -> phaser.arrive() );

        final TraceEvent event = TraceEvent.start( null );
        dispatcher.queue( event );

        phaser.awaitAdvance( 0 );

        dispatcher.removeListener( listener );
        dispatcher.queue( event );

        phaser.awaitAdvance( 1 );

        verify( listener, times( 1 ) ).onTrace( event );

        dispatcher.deactivate();
    }
}
