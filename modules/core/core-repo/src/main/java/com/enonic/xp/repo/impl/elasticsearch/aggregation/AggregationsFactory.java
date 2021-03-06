package com.enonic.xp.repo.impl.elasticsearch.aggregation;


import java.time.Instant;

import org.elasticsearch.search.aggregations.HasAggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.range.geodistance.InternalGeoDistance;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.joda.time.DateTime;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.Buckets;


public class AggregationsFactory
{
    public static Aggregations create( final org.elasticsearch.search.aggregations.Aggregations aggregations )
    {
        return doCreate( aggregations );
    }

    private static Aggregations doCreate( final org.elasticsearch.search.aggregations.Aggregations aggregations )
    {
        if ( aggregations == null )
        {
            return Aggregations.empty();
        }

        Aggregations.Builder aggregationsBuilder = new Aggregations.Builder();

        for ( final org.elasticsearch.search.aggregations.Aggregation aggregation : aggregations )
        {
            if ( aggregation instanceof Terms )
            {
                aggregationsBuilder.add( TermsAggregationFactory.create( (Terms) aggregation ) );
            }
            else if ( aggregation instanceof InternalGeoDistance )
            {
                aggregationsBuilder.add( GeoDistanceAggregationFactory.create( (InternalGeoDistance) aggregation ) );
            }
            else if ( aggregation instanceof InternalDateRange )
            {
                aggregationsBuilder.add( DateRangeAggregationFactory.create( (InternalDateRange) aggregation ) );
            }
            else if ( aggregation instanceof Range )
            {
                aggregationsBuilder.add( NumericRangeAggregationFactory.create( (Range) aggregation ) );
            }
            else if ( aggregation instanceof InternalHistogram )
            {
                aggregationsBuilder.add( DateHistogramAggregationFactory.create( (InternalHistogram) aggregation ) );
            }
            else if ( aggregation instanceof Histogram )
            {
                aggregationsBuilder.add( HistogramAggregationFactory.create( (Histogram) aggregation ) );
            }
            else if ( aggregation instanceof Stats )
            {
                aggregationsBuilder.add( StatsAggregationFactory.create( (Stats) aggregation ) );
            }
            else if ( aggregation instanceof ValueCount )
            {
                aggregationsBuilder.add( ValueCountAggregationFactory.create( (ValueCount) aggregation ) );
            }
            else if ( aggregation instanceof Min )
            {
                aggregationsBuilder.add( MinAggregationFactory.create( (Min) aggregation ) );
            }
            else if ( aggregation instanceof Max )
            {
                aggregationsBuilder.add( MaxAggregationFactory.create( (Max) aggregation ) );
            }
            else
            {
                throw new IllegalArgumentException( "Aggregation translator for " + aggregation.getClass().getName() + " not implemented" );
            }
        }

        return aggregationsBuilder.build();
    }

    static void createAndAddBucket( final Buckets.Builder bucketsBuilder, final MultiBucketsAggregation.Bucket bucket )
    {
        final Bucket.Builder builder = Bucket.create().
            key( bucket.getKeyAsString() ).
            docCount( bucket.getDocCount() );

        doAddSubAggregations( bucket, builder );

        bucketsBuilder.add( builder.build() );
    }

    static void doAddSubAggregations( final HasAggregations bucket, final Bucket.Builder builder )
    {
        final org.elasticsearch.search.aggregations.Aggregations subAggregations = bucket.getAggregations();

        builder.addAggregations( doCreate( subAggregations ) );
    }

    static Instant toInstant( final DateTime dateTime )
    {
        return dateTime == null ? null : java.time.Instant.ofEpochMilli( dateTime.getMillis() );
    }
}


