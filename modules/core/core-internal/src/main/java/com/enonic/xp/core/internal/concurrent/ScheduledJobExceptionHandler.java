package com.enonic.xp.core.internal.concurrent;

/**
 * Functional interface which helps to log exceptions/errors happened in scheduled runnables.
 *
 * @param <E>
 */
public interface ScheduledJobExceptionHandler<E extends Throwable>
{
    /**
     * @param e exception to handle
     */
    void handle( E e );
}
