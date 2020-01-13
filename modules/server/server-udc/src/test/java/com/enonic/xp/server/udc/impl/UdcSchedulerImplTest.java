package com.enonic.xp.server.udc.impl;

import java.time.Duration;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UdcSchedulerImplTest
{
    private UdcSchedulerImpl udcScheduler;

    private final Duration checkInterval = Duration.ofMillis( 1 );

    @BeforeEach
    void setUp()
    {
        udcScheduler = new UdcSchedulerImpl( Duration.ZERO, checkInterval );
    }

    @AfterEach
    void tearDown()
    {
        udcScheduler.deactivate();
    }


    @Test
    void scheduleWithFixedDelay_exception_does_not_stop_scheduling()
        throws Exception
    {
        Phaser phaser = new Phaser( 1 );
        udcScheduler.scheduleWithFixedDelay( () -> {
            try
            {
                throw new RuntimeException( "Intentional exception" );
            }
            finally
            {
                phaser.arrive();
            }
        } );
        phaser.awaitAdvanceInterruptibly( 0, checkInterval.multipliedBy( 100 ).toMillis(), TimeUnit.MILLISECONDS );

        phaser.awaitAdvanceInterruptibly( 1, checkInterval.multipliedBy( 100 ).toMillis(), TimeUnit.MILLISECONDS );
    }

    @Test
    void scheduleWithFixedDelay_error_stops_scheduling()
        throws Exception
    {
        Phaser phaser = new Phaser( 1 );
        udcScheduler.scheduleWithFixedDelay( () -> {
            try
            {
                throw new Error( "Intentional error" );
            }
            finally
            {
                phaser.arrive();
            }
        } );
        phaser.awaitAdvance( 0 );

        assertThrows( TimeoutException.class,
                      () -> phaser.awaitAdvanceInterruptibly( 1, checkInterval.multipliedBy( 100 ).toMillis(), TimeUnit.MILLISECONDS ) );
    }
}