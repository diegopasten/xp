package com.enonic.xp.core.impl.app;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.core.impl.app.config.ApplicationConfigMap;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ApplicationListenerHubTest
    extends BundleBasedTest
{
    private ExecutorService executor;

    @BeforeEach
    public void setUp()
    {
        executor = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    public void tearDown()
    {
        executor.shutdownNow();
    }

    @Test
    void testActivatedDeactivated()
        throws Exception
    {
        Phaser phaser = new Phaser( 1 );

        ApplicationListenerHub dispatcher = new ApplicationListenerHub( executor );
        dispatcher.activate( getBundleContext() );

        final ApplicationListener listener = mock( ApplicationListener.class );
        dispatcher.addListener( listener );

        // must be the last listener
        dispatcher.addListener( new ApplicationListener()
        {
            @Override
            public void activated( final Application app )
            {
                phaser.arrive();
            }

            @Override
            public void deactivated( final Application app )
            {
                phaser.arrive();
            }
        } );

        final Bundle bundle = deployBundle();

        bundle.start();

        phaser.awaitAdvanceInterruptibly( 0, 1, TimeUnit.SECONDS );

        bundle.uninstall();

        phaser.awaitAdvanceInterruptibly( 1, 1, TimeUnit.SECONDS );

        dispatcher.deactivate();

        verify( listener, times( 1 ) ).activated( notNull() );
        verify( listener, times( 1 ) ).deactivated( notNull() );
    }

    private Bundle deployBundle()
        throws Exception
    {
        ApplicationConfigMap.INSTANCE.put( ApplicationKey.from( "myapplication" ), ConfigBuilder.create().build() );
        final InputStream in = newBundle( "myapplication", true ).
            set( Constants.BUNDLE_NAME, "myapplication" ).
            set( ApplicationHelper.X_APPLICATION_URL, "http://enonic.com/path/to/application" ).
            set( ApplicationHelper.X_SYSTEM_VERSION, "[1.2,2)" ).
            set( ApplicationHelper.X_VENDOR_NAME, "Enonic AS" ).
            set( ApplicationHelper.X_VENDOR_URL, "http://enonic.com" ).
            set( ApplicationHelper.X_SOURCE_PATHS, "/a/b,/c/d" ).
            build();

        return deploy( "bundle", in );
    }
}
