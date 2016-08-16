package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.ValueHelper;

class TermExpressionBuilder
{
    public static QueryBuilder build( final CompareExpr compareExpr, final QueryFieldNameResolver resolver )
    {
        final String queryFieldName = resolver.resolve( compareExpr );

        if ( compareExpr.getFirstValue() == null )
        {
            throw new IllegalArgumentException( "Invalid compare expression [" + compareExpr.toString() + "]" );
        }

        final Value value = compareExpr.getFirstValue().getValue();
        return QueryBuilders.termQuery( queryFieldName, ValueHelper.getValueAsType( value ) );
    }

    public static QueryBuilder build( final String fieldName, final Value value )
    {
        return QueryBuilders.termQuery( fieldName, ValueHelper.getValueAsType( value ) );
    }
}
