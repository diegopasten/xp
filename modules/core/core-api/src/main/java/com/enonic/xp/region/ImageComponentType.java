package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ImageComponentType
    extends ComponentType
{
    public final static ImageComponentType INSTANCE = new ImageComponentType();

    private ImageComponentType()
    {
        super( "image" );
    }
}
