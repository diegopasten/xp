package com.enonic.wem.repo.internal.entity;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

final class NodePermissionsResolver
{
    public static void requireContextUserPermission( final Permission permission, final Node node )
        throws NodeAccessException
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        requireContextUserPermission( authInfo, permission, node );
    }

    public static void requireContextUserPermissionOrAdmin( final Permission permission, final Node node )
        throws NodeAccessException
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( authInfo.getPrincipals().contains( RoleKeys.ADMIN ) )
        {
            return;
        }
        requireContextUserPermission( authInfo, permission, node );
    }

    public static void requireContextUserPermission( final AuthenticationInfo authInfo, final Permission permission, final Node node )
        throws NodeAccessException
    {
        final boolean hasPermission = userHasPermission( authInfo, permission, node );
        if ( !hasPermission )
        {
            throw new NodeAccessException( authInfo.getUser(), node.path(), permission );
        }
    }

    public static boolean contextUserHasPermission( final Permission permission, final Node node )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return userHasPermission( authInfo, permission, node );
    }

    public static boolean contextUserHasPermissionOrAdmin( final Permission permission, final Node node )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return authInfo.getPrincipals().contains( RoleKeys.ADMIN ) || userHasPermission( authInfo, permission, node );
    }

    public static boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission, final Node node )
    {
        return userHasPermission( authInfo, permission, node.getPermissions() );
    }

    public static boolean userHasPermission( final AuthenticationInfo authInfo, final Permission permission,
                                             final AccessControlList nodePermissions )
    {
        final PrincipalKeys authInfoPrincipals = authInfo.getPrincipals();
        final PrincipalKeys principalsAllowed = nodePermissions.getPrincipalsWithPermission( permission );

        return principalsAllowed.stream().anyMatch( ( authInfoPrincipals::contains ) );
    }

}
