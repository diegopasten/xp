package com.enonic.xp.cluster.impl;

import java.time.Duration;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ClusterCheckSchedulerImplTest
{
    private ClusterCheckSchedulerImpl clusterCheckScheduler;

    private final Duration checkInterval = Duration.ofMillis( 1 );

    @BeforeEach
    void setUp()
    {
        clusterCheckScheduler = new ClusterCheckSchedulerImpl( Duration.ZERO, checkInterval );
    }

    @AfterEach
    void tearDown()
    {
        clusterCheckScheduler.deactivate();
    }

    @Test
    void scheduleWithFixedDelay_exception_does_not_stop_scheduling()
        throws Exception
    {
        Phaser phaser = new Phaser( 1 );
        clusterCheckScheduler.scheduleWithFixedDelay( () -> {
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
        clusterCheckScheduler.scheduleWithFixedDelay( () -> {
            try
            {
                throw new Error( "Intentional error" );
            }
            finally
            {
                phaser.arrive();
            }
        } );
        phaser.awaitAdvanceInterruptibly( 0, checkInterval.multipliedBy( 100 ).toMillis(), TimeUnit.MILLISECONDS );

        assertThrows( TimeoutException.class,
                      () -> phaser.awaitAdvanceInterruptibly( 1, checkInterval.multipliedBy( 100 ).toMillis(), TimeUnit.MILLISECONDS ) );
    }
}