package com.enonic.xp.repo.impl.branch.search;

import com.enonic.xp.repo.impl.branch.storage.NodeBranchVersionFactory;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class NodeBranchQueryResultFactory
{
    public static NodeBranchQueryResult create( final SearchResult searchResult )
    {
        if ( searchResult.isEmpty() )
        {
            return NodeBranchQueryResult.empty();
        }

        final NodeBranchQueryResult.Builder builder = NodeBranchQueryResult.create();

        for ( final SearchHit result : searchResult.getHits() )
        {
            builder.add( NodeBranchVersionFactory.create( result.getReturnValues() ) );
        }

        return builder.build();
    }
}
