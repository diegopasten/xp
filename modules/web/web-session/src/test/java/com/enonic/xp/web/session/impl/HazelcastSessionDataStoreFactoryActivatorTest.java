package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.server.session.SessionCacheFactory;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.hazelcast.core.HazelcastInstance;

import com.enonic.xp.cluster.ClusterConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastSessionDataStoreFactoryActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private ClusterConfig clusterConfig;

    @Mock
    private WebSessionConfig webSessionConfig;

    @Mock
    private ServiceRegistration<SessionDataStoreFactory> sessionDataStoreFactoryServiceRegistration;

    @Mock
    private ServiceRegistration<SessionCacheFactory> sessionCacheFactoryServiceRegistration;

    @Test
    void cluster_disabled_does_not_activate_services()
    {
        when( clusterConfig.isEnabled() ).thenReturn( false );
        verifyDisabledActivateDeactivate();
    }

    @Test
    void replication_disabled_does_not_activate_services()
    {
        when( clusterConfig.isEnabled() ).thenReturn( true );
        when( webSessionConfig.storeMode() ).thenReturn( "non-persistent" );

        verifyDisabledActivateDeactivate();
    }

    @Test
    void replication_enabled_activates_services()
    {
        when( clusterConfig.isEnabled() ).thenReturn( true );
        when( webSessionConfig.storeMode() ).thenReturn( "replicated" );

        verifyEnabledActivateDeactivate();
    }


    private void verifyDisabledActivateDeactivate()
    {
        final HazelcastSessionDataStoreFactoryActivator hzSessionDataStoreFactoryActivator =
            new HazelcastSessionDataStoreFactoryActivator( bundleContext, clusterConfig, mock( HazelcastInstance.class ) );

        hzSessionDataStoreFactoryActivator.activate( webSessionConfig );
        hzSessionDataStoreFactoryActivator.deactivate();

        verifyZeroInteractions( bundleContext, webSessionConfig );
    }

    private void verifyEnabledActivateDeactivate()
    {
        when( bundleContext.registerService( same( SessionDataStoreFactory.class ), any( SessionDataStoreFactory.class ), isNull() ) ).
            thenReturn( sessionDataStoreFactoryServiceRegistration );
        when( bundleContext.registerService( same( SessionCacheFactory.class ), any( SessionCacheFactory.class ), isNull() ) ).
            thenReturn( sessionCacheFactoryServiceRegistration );

        final HazelcastSessionDataStoreFactoryActivator hzSessionDataStoreFactoryActivator =
            new HazelcastSessionDataStoreFactoryActivator( bundleContext, clusterConfig, mock( HazelcastInstance.class ) );

        hzSessionDataStoreFactoryActivator.activate( webSessionConfig );

        verify( webSessionConfig ).savePeriodSeconds();
        verify( bundleContext ).registerService( same( SessionDataStoreFactory.class ), any( SessionDataStoreFactory.class ), isNull() );
        verify( bundleContext ).registerService( same( SessionCacheFactory.class ), any( SessionCacheFactory.class ), isNull() );

        hzSessionDataStoreFactoryActivator.deactivate();
        verify( sessionDataStoreFactoryServiceRegistration, times( 1 ) ).unregister();
        verify( sessionCacheFactoryServiceRegistration, times( 1 ) ).unregister();
    }
}