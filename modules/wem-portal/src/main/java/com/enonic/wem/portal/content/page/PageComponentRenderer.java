package com.enonic.wem.portal.content.page;


import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.rendering.Renderer;

abstract class PageComponentRenderer
    implements Renderer<PageComponent>
{
    private static final String EDIT_MODE = "edit";

    private static final String EMPTY_COMPONENT_HTML = "<div></div>";

    @Inject
    protected Client client;

    @Inject
    protected JsControllerFactory controllerFactory;

    public Response render( final PageComponent pageComponent, final JsContext context )
    {
        final Descriptor descriptor = resolveDescriptor( pageComponent );
        if ( descriptor == null )
        {
            if ( inEditMode( context ) )
            {
                return renderEmptyComponent();
            }
            throw new DescriptorNotFoundException( pageComponent.getDescriptor() );
        }

        // create controller
        final JsController controller = this.controllerFactory.newController();
        controller.scriptDir( descriptor.getComponentPath() );
        controller.context( context );

        // render
        final PageComponent previousComponent = context.getComponent();
        try
        {
            context.setComponent( pageComponent );
            return controller.execute();
        }
        finally
        {
            context.setComponent( previousComponent );
        }
    }

    private Response renderEmptyComponent()
    {
        return Response.ok().
            type( MediaType.TEXT_HTML_TYPE ).
            entity( EMPTY_COMPONENT_HTML ).
            build();
    }

    private Descriptor resolveDescriptor( final PageComponent pageComponent )
    {
        final DescriptorKey descriptorKey = pageComponent.getDescriptor();
        return descriptorKey == null ? null : getComponentDescriptor( descriptorKey );
    }

    protected abstract Descriptor getComponentDescriptor( final DescriptorKey descriptorKey );

    private boolean inEditMode( final JsContext context )
    {
        final JsHttpRequest req = context.getRequest();
        return req != null && EDIT_MODE.equals( req.getMode() );
    }
}
