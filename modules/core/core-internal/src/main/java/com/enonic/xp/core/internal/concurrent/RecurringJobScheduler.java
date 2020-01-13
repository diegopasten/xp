package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;

public interface RecurringJobScheduler
{
    RecurringJob scheduleWithFixedDelay( Runnable command, Duration initialDelay, Duration delay,
                                         ScheduledJobExceptionHandler<Exception> exceptionHandler,
                                         ScheduledJobExceptionHandler<Throwable> errorHandler );
}
