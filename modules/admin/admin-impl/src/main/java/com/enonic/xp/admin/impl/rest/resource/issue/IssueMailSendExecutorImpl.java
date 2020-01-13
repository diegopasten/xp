package com.enonic.xp.admin.impl.rest.resource.issue;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.ManagedExecutor;

@Component
public class IssueMailSendExecutorImpl
    implements IssueMailSendExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( IssueMailSendExecutorImpl.class );

    private final ManagedExecutor managedExecutor;

    public IssueMailSendExecutorImpl()
    {
        managedExecutor = new ManagedExecutor( Executors::newCachedThreadPool, "issue-mail-sender-thread-%d",
                                               e -> LOG.error( "Message sending failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        managedExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ), notStarted -> LOG.warn( "Not all messages were sent" ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        managedExecutor.execute( command );
    }
}
