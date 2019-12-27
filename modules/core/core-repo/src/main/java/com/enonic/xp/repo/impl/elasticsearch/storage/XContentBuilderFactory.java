package com.enonic.xp.repo.impl.elasticsearch.storage;

import java.util.List;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.collect.Multimap;

import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.index.IndexValueNormalizer;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repository.IndexException;

class XContentBuilderFactory
{
    static XContentBuilder create( final StoreRequest doc )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            final Multimap<String, Object> values = doc.getEntries();

            for ( final String key : values.keySet() )
            {
                if ( BranchIndexPath.JOIN_FIELD.getPath().equals( key ) )
                {
                    addField( builder, key, ( (List) values.get( key ) ).get( 0 ) );
                }
                else
                {
                    addField( builder, key, values.get( key ) );
                }
            }

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for StorageDocument", e );
        }
    }

    private static XContentBuilder startBuilder()
        throws Exception
    {
        Thread thread = Thread.currentThread();
        ClassLoader contextClassLoader = thread.getContextClassLoader();
        thread.setContextClassLoader( RestHighLevelClient.class.getClassLoader() );
        final XContentBuilder result;
        try
        {
            result = XContentFactory.jsonBuilder();
        }
        finally
        {
            thread.setContextClassLoader( contextClassLoader );
        }
        result.startObject();

        return result;
    }

    private static void addField( XContentBuilder result, String name, Object value )
        throws Exception
    {
        if ( value == null )
        {
            return;
        }

        if ( value instanceof String )
        {
            value = IndexValueNormalizer.normalize( (String) value );
        }

        result.field( name, value );
    }


    private static void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }


}
