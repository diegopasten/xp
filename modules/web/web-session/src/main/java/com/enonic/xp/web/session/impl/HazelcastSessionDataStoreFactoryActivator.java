package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.hazelcast.session.HazelcastSessionDataStoreFactory;
import org.eclipse.jetty.server.session.NullSessionCache;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import com.enonic.xp.cluster.ClusterConfig;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.session")
public class HazelcastSessionDataStoreFactoryActivator
    extends AbstractSessionDataStoreFactoryActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( HazelcastSessionDataStoreFactoryActivator.class );

    private final ClusterConfig clusterConfig;

    private final HazelcastInstance hazelcastInstance;

    @Activate
    public HazelcastSessionDataStoreFactoryActivator( final BundleContext bundleContext, @Reference final ClusterConfig clusterConfig,
                                                      @Reference HazelcastInstance hazelcastInstance )
    {
        super( bundleContext );
        this.clusterConfig = clusterConfig;
        this.hazelcastInstance = hazelcastInstance;
    }

    @Activate
    public void activate( final WebSessionConfig config )
    {
        if ( clusterConfig.isEnabled() && "replicated".equals( config.storeMode() ) )
        {
            final HazelcastSessionDataStoreFactory sessionDataStoreFactory = new HazelcastSessionDataStoreFactory();
            sessionDataStoreFactory.setHazelcastInstance( hazelcastInstance );
            sessionDataStoreFactory.setSavePeriodSec( config.savePeriodSeconds() );

            registerServices( sessionDataStoreFactory, NullSessionCache::new );
        }
        else
        {
            LOG.debug( "Don't activate HazelcastSessionDataStore when session replication is disabled" );
        }
    }

    @Deactivate
    public void deactivate()
    {
        unregisterServices();
    }
}
