package com.enonic.xp.web.session.impl;

import java.util.Objects;

import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.NullSessionDataStoreFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.session")
public class NullSessionDataStoreFactoryActivator
    extends AbstractSessionDataStoreFactoryActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( NullSessionDataStoreFactoryActivator.class );

    private static final String STORE_MODE_CONFIG_VALUE = "non-persistent";

    @Activate
    public NullSessionDataStoreFactoryActivator( final BundleContext bundleContext )
    {
        super( bundleContext );
    }

    @Activate
    public void activate( final WebSessionConfig config )
    {
        final String storeMode = Objects.requireNonNullElse( config.storeMode(), "" );
        if ( storeMode.isEmpty() || STORE_MODE_CONFIG_VALUE.equals( storeMode ) )
        {
            final NullSessionDataStoreFactory sessionDataStoreFactory = new NullSessionDataStoreFactory();
            sessionDataStoreFactory.setSavePeriodSec( config.savePeriodSeconds() );

            registerServices( sessionDataStoreFactory, DefaultSessionCache::new );
        }
        else
        {
            LOG.debug( "Don't activate NullSessionDataStore when store_mode is {}", storeMode );
        }
    }

    @Deactivate
    public void deactivate()
    {
        unregisterServices();
    }
}
