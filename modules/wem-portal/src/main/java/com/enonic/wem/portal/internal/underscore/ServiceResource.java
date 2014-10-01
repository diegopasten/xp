package com.enonic.wem.portal.internal.underscore;

import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.internal.base.ModuleBaseResource;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsController;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;

public final class ServiceResource
    extends ModuleBaseResource
{
    protected JsControllerFactory controllerFactory;

    @Override
    protected Representation doHandle()
        throws ResourceException
    {
        final ModuleKey moduleKey = resolveModule();
        final JsContext context = new JsContext();

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( this.mode );
        jsRequest.setMethod( getRequest().getMethod().toString() );
        jsRequest.addParams( getParams() );
        context.setRequest( jsRequest );

        final String serviceName = getAttribute( "service" );
        final ResourceKey scriptDir = ResourceKey.from( moduleKey, "service/" + serviceName );
        final JsController controller = this.controllerFactory.newController( scriptDir );
        controller.execute( context );

        final RenderResult result = new JsHttpResponseSerializer( context.getResponse() ).serialize();
        return toRepresentation( result );
    }
}
