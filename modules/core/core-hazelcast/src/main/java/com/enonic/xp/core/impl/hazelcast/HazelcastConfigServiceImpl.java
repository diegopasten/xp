package com.enonic.xp.core.impl.hazelcast;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jetty.hazelcast.session.SessionDataSerializer;
import org.eclipse.jetty.server.session.SessionData;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.spi.properties.GroupProperty;

import com.enonic.xp.cluster.ClusterConfig;

import static java.util.Objects.requireNonNullElse;

@Component(configurationPid = "com.enonic.xp.hazelcast")
public class HazelcastConfigServiceImpl
    implements HazelcastConfigService
{
    private final ClusterConfig clusterConfig;

    private final HazelcastConfig hazelcastConfig;

    @Activate
    public HazelcastConfigServiceImpl( @Reference final ClusterConfig clusterConfig, final HazelcastConfig hazelcastConfig )
    {
        this.clusterConfig = clusterConfig;
        this.hazelcastConfig = hazelcastConfig;
    }

    public boolean isHazelcastEnabled()
    {
        return clusterConfig.isEnabled();
    }

    public Config configure()
    {
        Config config = new Config();

        config.setProperty( GroupProperty.SHUTDOWNHOOK_ENABLED.getName(), String.valueOf( false ) );
        config.setProperty( GroupProperty.SOCKET_BIND_ANY.getName(), String.valueOf( hazelcastConfig.system_hazelcast_socket_bind_any() ) );
        config.setProperty( GroupProperty.PHONE_HOME_ENABLED.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_phone_home_enabled() ) );

        if ( hazelcastConfig.clusterConfigDefaults() )
        {
            config.setProperty( "hazelcast.local.localAddress", clusterConfig.networkHost() );
            config.setProperty( "hazelcast.local.publicAddress", clusterConfig.networkPublishHost() );
        }

        config.setClassLoader( HazelcastConfigServiceImpl.class.getClassLoader() );
        config.setLiteMember( hazelcastConfig.lightMember() );
        config.setNetworkConfig( configureNetwork() );
        config.setSerializationConfig( configureSerialization() );
        return config;
    }

    private NetworkConfig configureNetwork()
    {
        final NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setPort( hazelcastConfig.network_port() );
        networkConfig.setPortCount( hazelcastConfig.network_portCount() );
        networkConfig.setPortAutoIncrement( hazelcastConfig.network_portAutoIncrement() );

        if ( !hazelcastConfig.clusterConfigDefaults() )
        {
            networkConfig.setPublicAddress( hazelcastConfig.network_publicAddress() );
        }

        networkConfig.setInterfaces( configureInterfaces() );
        networkConfig.setJoin( configureJoin() );

        return networkConfig;
    }

    private JoinConfig configureJoin()
    {
        final JoinConfig config = new JoinConfig();
        config.setMulticastConfig( configureMulticast() );
        config.setTcpIpConfig( configureTcpIp() );

        return config;
    }

    private MulticastConfig configureMulticast()
    {
        final MulticastConfig config = new MulticastConfig();
        config.setEnabled( hazelcastConfig.network_join_multicast_enabled() );

        return config;
    }

    private InterfacesConfig configureInterfaces()
    {
        final InterfacesConfig config = new InterfacesConfig();
        config.setEnabled( hazelcastConfig.network_join_interfaces_enabled() );
        final String interfacesConfig = requireNonNullElse( hazelcastConfig.network_join_interfaces(), "" ).trim();
        List<String> interfaces = Arrays.stream( interfacesConfig.split( "," ) ).
            filter( Predicate.not( String::isBlank ) ).
            collect( Collectors.toUnmodifiableList() );
        config.setInterfaces( interfaces );
        return config;
    }

    private TcpIpConfig configureTcpIp()
    {
        final TcpIpConfig config = new TcpIpConfig();
        config.setEnabled( hazelcastConfig.network_join_tcpIp_enabled() );

        final String membersConfig = requireNonNullElse( hazelcastConfig.network_join_tcpIp_members(), "" ).trim();
        final List<String> members;
        if ( !membersConfig.isEmpty() )
        {
            members = Arrays.stream( membersConfig.split( "," ) ).
                filter( Predicate.not( String::isBlank ) ).
                collect( Collectors.toUnmodifiableList() );
        }
        else if ( hazelcastConfig.clusterConfigDefaults() )
        {
            members = clusterConfig.discovery().get().stream().
                map( InetAddress::getHostAddress ).
                collect( Collectors.toUnmodifiableList() );
        }
        else
        {
            members = List.of();
        }
        config.setMembers( members );

        return config;
    }

    private SerializationConfig configureSerialization()
    {
        SerializationConfig config = new SerializationConfig();
        SerializerConfig jettySerConfig =
            new SerializerConfig().setImplementation( new SessionDataSerializer() ).setTypeClass( SessionData.class );
        config.addSerializerConfig( jettySerConfig );
        return config;
    }
}
