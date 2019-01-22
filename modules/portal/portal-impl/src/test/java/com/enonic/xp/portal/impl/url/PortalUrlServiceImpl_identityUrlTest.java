package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_identityUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/site/draft/_/idprovider/system/login", url );
    }

    @Test
    public void createUrl_withContentPath()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            contextPathType( ContextPathType.RELATIVE.getValue() ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/site/draft/context/path/_/idprovider/system/login", url );
    }

    @Test
    public void createUrl_withoutFunction()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            idProviderKey( IdProviderKey.system() );

        final String url = this.service.identityUrl( params );
        assertEquals( "/site/draft/_/idprovider/system", url );
    }

    @Test
    public void createUrl_withVirtualHost()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        //Mocks a virtual host and the HTTP request
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        VirtualHostHelper.setVirtualHost( req, virtualHost );

        //Calls the method with a virtual mapping /main -> /
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/" );
        String url = this.service.identityUrl( params );
        assertEquals( "/main/site/draft/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main -> /site/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/draft/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main -> /site/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/draft" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping / -> /site/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/draft/context" );
        url = this.service.identityUrl( params );
        assertEquals( "/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main/path -> /site/draft/context/path
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main/path" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/draft/context/path" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/path/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /site/draft/context/path -> /site/draft/context/path
        Mockito.when( virtualHost.getSource() ).thenReturn( "/site/draft/context/path" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/draft/context/path" );
        url = this.service.identityUrl( params );
        assertEquals( "/site/draft/context/path/_/idprovider/system/login", url );

        //Post treatment
        ServletRequestHolder.setRequest( null );
    }

    @Test
    public void createUrl_absolute()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            type( UrlTypeConstants.ABSOLUTE ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        final String url = this.service.identityUrl( params );
        assertEquals( "http://localhost/site/draft/_/idprovider/system/login", url );
    }
}
