package com.enonic.wem.core.search.elastic;

public class IndexMapping
{
    private final String indexName;

    private final String indexType;

    private final String source;

    public IndexMapping( final String indexName, final String indexType, final String source )
    {
        this.indexName = indexName;
        this.indexType = indexType;
        this.source = source;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public String getIndexType()
    {
        return indexType;
    }

    public String getSource()
    {
        return source;
    }
}
