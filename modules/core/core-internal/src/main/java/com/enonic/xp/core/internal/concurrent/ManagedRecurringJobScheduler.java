package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public final class ManagedRecurringJobScheduler
    implements RecurringJobScheduler
{
    private final ScheduledExecutorService scheduledExecutorService;

    public ManagedRecurringJobScheduler( final Function<ThreadFactory, ScheduledExecutorService> scheduledExecutorServiceSupplier,
                                         final String namePrefix )
    {
        scheduledExecutorService = scheduledExecutorServiceSupplier.apply( new ThreadFactoryImpl( namePrefix ) );
    }

    public RecurringJob scheduleWithFixedDelay( final Runnable command, final Duration initialDelay, final Duration delay,
                                                ScheduledJobExceptionHandler<Exception> exceptionHandler,
                                                ScheduledJobExceptionHandler<Throwable> errorHandler )
    {
        final Runnable runnable = () -> {
            try
            {
                command.run();
            }
            catch ( Exception e )
            {
                // give a chance to log exception
                exceptionHandler.handle( e );
                // continue tu run
            }
            catch ( Throwable t )
            {
                // give a chance to log error
                errorHandler.handle( t );
                // exception thrown from Runnable aborts scheduled job.
                // it is set in ScheduledFuture "outcome", but we don't expose it.
                throw t;
            }
        };
        final ScheduledFuture<?> scheduledFuture =
            scheduledExecutorService.scheduleWithFixedDelay( runnable, initialDelay.toMillis(), delay.toMillis(), TimeUnit.MILLISECONDS );

        return () -> scheduledFuture.cancel( true );
    }

    public List<Runnable> shutdownNow()
    {
        return scheduledExecutorService.shutdownNow();
    }
}
