package com.enonic.wem.api.node;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.CompareStatus;

public class NodeComparisons
    implements Iterable<NodeComparison>
{
    private final ImmutableSet<NodeComparison> nodeComparisons;

    private NodeComparisons( Builder builder )
    {
        nodeComparisons = ImmutableSet.copyOf( builder.nodeComparisons );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<NodeComparison> iterator()
    {
        return nodeComparisons.iterator();
    }

    public static final class Builder
    {
        private final Set<NodeComparison> nodeComparisons = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder add( final NodeComparison nodeComparison )
        {
            nodeComparisons.add( nodeComparison );
            return this;
        }

        public NodeComparisons build()
        {
            return new NodeComparisons( this );
        }
    }

    public Set<NodeComparison> getWithStatus( final CompareStatus.Status status )
    {
        Set<NodeComparison> result = Sets.newHashSet();

        result.addAll( this.nodeComparisons.stream().filter(
            nodeComparison -> nodeComparison.getCompareStatus().getStatus().equals( status ) ).collect( Collectors.toList() ) );

        return result;
    }

    public ImmutableSet<NodeComparison> getNodeComparisons()
    {
        return nodeComparisons;
    }
}
