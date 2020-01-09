package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.server.session.SessionCacheFactory;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NullSessionDataStoreFactoryActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private WebSessionConfig webSessionConfig;

    @Mock
    private ServiceRegistration<SessionDataStoreFactory> sessionDataStoreFactoryServiceRegistration;

    @Mock
    private ServiceRegistration<SessionCacheFactory> sessionCacheFactoryServiceRegistration;

    @Test
    void cluster_disabled_activates_services()
    {
        when( webSessionConfig.storeMode() ).thenReturn( "" );

        verifyEnabledActivateDeactivate();
    }

    @Test
    void replication_disabled_activates_services()
    {
        when( webSessionConfig.storeMode() ).thenReturn( "non-persistent" );

        verifyEnabledActivateDeactivate();
    }

    @Test
    void replication_enabled_does_not_activate_services()
    {
        when( webSessionConfig.storeMode() ).thenReturn( "replicated" );

        verifyDisabledActivateDeactivate();
    }

    private void verifyDisabledActivateDeactivate()
    {
        final NullSessionDataStoreFactoryActivator nullSessionDataStoreFactoryActivator =
            new NullSessionDataStoreFactoryActivator( bundleContext );

        nullSessionDataStoreFactoryActivator.activate( webSessionConfig );
        nullSessionDataStoreFactoryActivator.deactivate();

        verifyZeroInteractions( bundleContext, webSessionConfig );
    }

    private void verifyEnabledActivateDeactivate()
    {
        when( webSessionConfig.savePeriodSeconds() ).thenReturn( 10 );

        when( bundleContext.registerService( same( SessionDataStoreFactory.class ), any( SessionDataStoreFactory.class ), isNull() ) ).
            thenReturn( sessionDataStoreFactoryServiceRegistration );
        when( bundleContext.registerService( same( SessionCacheFactory.class ), any( SessionCacheFactory.class ), isNull() ) ).
            thenReturn( sessionCacheFactoryServiceRegistration );

        final NullSessionDataStoreFactoryActivator nullSessionDataStoreFactoryActivator =
            new NullSessionDataStoreFactoryActivator( bundleContext );

        nullSessionDataStoreFactoryActivator.activate( webSessionConfig );

        verify( webSessionConfig ).savePeriodSeconds();
        verify( bundleContext ).registerService( same( SessionDataStoreFactory.class ), any( SessionDataStoreFactory.class ), isNull() );
        verify( bundleContext ).registerService( same( SessionCacheFactory.class ), any( SessionCacheFactory.class ), isNull() );

        nullSessionDataStoreFactoryActivator.deactivate();
        verify( sessionDataStoreFactoryServiceRegistration, times( 1 ) ).unregister();
        verify( sessionCacheFactoryServiceRegistration, times( 1 ) ).unregister();
    }
}