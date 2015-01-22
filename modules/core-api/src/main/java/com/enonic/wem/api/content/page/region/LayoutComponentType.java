package com.enonic.wem.api.content.page.region;

public final class LayoutComponentType
    extends ComponentType
{
    public final static LayoutComponentType INSTANCE = new LayoutComponentType();

    private LayoutComponentType()
    {
        super( "layout", LayoutComponent.class );
    }

}
