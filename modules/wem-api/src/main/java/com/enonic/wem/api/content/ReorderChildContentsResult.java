package com.enonic.wem.api.content;

public class ReorderChildContentsResult
{
    private final int movedChildren;

    public ReorderChildContentsResult( final int movedChildren )
    {
        this.movedChildren = movedChildren;
    }

    public int getMovedChildren()
    {
        return movedChildren;
    }
}
