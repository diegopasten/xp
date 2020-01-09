package com.enonic.xp.core.impl.hazelcast;

public @interface HazelcastConfig
{
    boolean clusterConfigDefaults() default true;

    boolean system_hazelcast_phone_home_enabled() default true;

    boolean system_hazelcast_socket_bind_any() default false;

    boolean lightMember() default false;

    int network_port() default 5701;

    boolean network_portAutoIncrement() default false;

    int network_portCount() default 1;

    String network_publicAddress();

    boolean network_join_multicast_enabled() default false;

    boolean network_join_tcpIp_enabled() default true;

    String network_join_tcpIp_members();

    boolean network_join_interfaces_enabled() default false;

    String network_join_interfaces();

}
