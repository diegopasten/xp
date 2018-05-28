package com.enonic.xp.cluster.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.Clusters;

import static junit.framework.TestCase.assertTrue;

public class ClusterManagerImplTest
{
    private static final long CHECK_INTERVAL_MS = 200l;

    private ClusterManagerImpl clusterManager;

    @Before
    public void setUp()
        throws Exception
    {

    }

    @Test
    public void single_provider_life_cycle()
        throws Exception
    {
        createManager( "elasticsearch" );

        final TestCluster provider = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        this.clusterManager.addProvider( provider );
        Thread.sleep( CHECK_INTERVAL_MS );
        assertActive( provider );
        this.clusterManager.getClusterState();
        assertActive( provider );

        provider.setHealth( ClusterHealth.RED );
        Thread.sleep( CHECK_INTERVAL_MS );
        assertClusterError();
        assertDeactivated( provider );

        provider.setHealth( ClusterHealth.GREEN );
        Thread.sleep( CHECK_INTERVAL_MS );
        assertClusterOk();
        assertActive( provider );
    }

    @Test
    public void multiple_providers_life_cycle()
        throws Exception
    {
        createManager( "elasticsearch", "another" );

        final TestCluster provider1 = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        final TestCluster provider2 = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "another" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        this.clusterManager.addProvider( provider1 );
        Thread.sleep( CHECK_INTERVAL_MS );
        assertDeactivated( provider1 );

        this.clusterManager.addProvider( provider2 );
        Thread.sleep( CHECK_INTERVAL_MS );
        assertActive( provider1, provider2 );

        provider1.setHealth( ClusterHealth.RED );
        Thread.sleep( CHECK_INTERVAL_MS );
        assertClusterError();
        assertDeactivated( provider1, provider2 );
    }

    @Test
    public void fail_after_register()
        throws Exception
    {
        final TestCluster provider1 = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        final TestCluster provider2 = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "another" ) ).nodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            build() ).
            build();

        createManager( "elasticsearch", "another" );

        this.clusterManager.addProvider( provider1 );
        this.clusterManager.addProvider( provider2 );

        assertClusterOk();

        this.clusterManager.removeProvider( provider2 );

        assertClusterError();
    }

    private void assertClusterError()
    {
        Assert.assertEquals( ClusterState.ERROR, this.clusterManager.getClusterState() );
    }

    private void assertClusterOk()
    {
        Assert.assertEquals( ClusterState.OK, this.clusterManager.getClusterState() );
    }

    private void createManager( final String... required )
    {
        List<ClusterId> requiredIds = Lists.newArrayList();

        for ( final String req : required )
        {
            requiredIds.add( ClusterId.from( req ) );
        }

        this.clusterManager = ClusterManagerImpl.create().
            checkIntervalMs( CHECK_INTERVAL_MS / 2 ).
            requiredInstances( new Clusters( requiredIds ) ).
            build();
        this.clusterManager.activate();

    }

    private void assertActive( final TestCluster... providers )
    {
        for ( final TestCluster provider : providers )
        {
            assertTrue( String.format( "Provider '%s' not active", provider.getId() ), provider.isEnabled() );
        }
    }

    private void assertDeactivated( final TestCluster... providers )
    {
        for ( final TestCluster provider : providers )
        {
            Assert.assertFalse( String.format( "Provider '%s' not deactivated", provider.getId() ), provider.isEnabled() );
        }
    }
}