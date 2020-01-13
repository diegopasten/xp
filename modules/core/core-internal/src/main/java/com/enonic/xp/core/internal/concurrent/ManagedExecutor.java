package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ManagedExecutor
    implements Executor
{
    private final ExecutorService executorService;

    public ManagedExecutor( final Function<ThreadFactory, ExecutorService> executorServiceSupplier, final String namePrefix,
                            final Consumer<Throwable> uncaughtExceptionHandler )
    {
        Objects.requireNonNull( uncaughtExceptionHandler, "uncaughtExceptionHandler is required" );
        executorService = executorServiceSupplier.apply( new ThreadFactoryImpl( namePrefix, uncaughtExceptionHandler ) );
    }

    public boolean shutdownAndAwaitTermination( final Duration awaitTerminationDuration, final Consumer<List<Runnable>> notStartedConsumer )
    {
        // Give a chance to execution queue to drain.
        executorService.shutdown();
        try
        {
            if ( !executorService.awaitTermination( awaitTerminationDuration.toMillis(), TimeUnit.MILLISECONDS ) )
            {
                shutdownNowAndReportNotStarted( notStartedConsumer );
            }
        }
        catch ( InterruptedException e )
        {
            shutdownNowAndReportNotStarted( notStartedConsumer );
            Thread.currentThread().interrupt();
        }
        return executorService.isTerminated();
    }

    private void shutdownNowAndReportNotStarted( final Consumer<List<Runnable>> notStartedConsumer )
    {
        final List<Runnable> notStarted = executorService.shutdownNow();
        if ( !notStarted.isEmpty() )
        {
            notStartedConsumer.accept( notStarted );
        }
    }

    @Override
    public void execute( final Runnable command )
    {
        executorService.execute( command );
    }
}
