package com.enonic.wem.admin.rest.resource.repo;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.index.IndexService;
import com.enonic.wem.api.index.ReindexParams;
import com.enonic.wem.api.index.ReindexResult;

@Path(ResourceConstants.REST_ROOT + "repo")
@Produces(MediaType.APPLICATION_JSON)
@Component(immediate = true)
public class IndexResource
    implements AdminResource
{
    private IndexService indexService;

    @POST
    @Path("reindex")
    public ReindexResultJson reindex( final ReindexRequestJson request )
    {
        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            setBranches( request.getBranches() ).
            initialize( request.isInitialize() ).
            repositoryId( request.getRepositoryId() ).
            build() );

        return ReindexResultJsonFactory.create( result );
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
