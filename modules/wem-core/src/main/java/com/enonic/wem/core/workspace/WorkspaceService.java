package com.enonic.wem.core.workspace;

import com.enonic.wem.core.entity.NodeId;

public interface WorkspaceService
{
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument, final WorkspaceContext context );

    public void delete( final NodeId nodeId, final WorkspaceContext context );
}
