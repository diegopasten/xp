package com.enonic.xp.core.internal.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

final class ThreadFactoryImpl
    implements ThreadFactory
{
    private final AtomicLong count = new AtomicLong( 1 );

    private final String namePrefix;

    private final Consumer<Throwable> uncaughtExceptionHandler;

    public ThreadFactoryImpl( final String namePattern )
    {
        this.namePrefix = namePattern;
        this.uncaughtExceptionHandler = null;
    }

    public ThreadFactoryImpl( final String namePrefix, final Consumer<Throwable> uncaughtExceptionHandler )
    {
        this.namePrefix = namePrefix;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public Thread newThread( final Runnable r )
    {
        final Thread thread = Executors.defaultThreadFactory().newThread( r );

        thread.setName( String.format( namePrefix, count.getAndIncrement() ) );

        if ( uncaughtExceptionHandler != null )
        {
            thread.setUncaughtExceptionHandler( ( t, e ) -> uncaughtExceptionHandler.accept( e ) );
        }
        return thread;
    }
}
