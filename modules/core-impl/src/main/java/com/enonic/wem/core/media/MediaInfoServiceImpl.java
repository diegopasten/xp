package com.enonic.wem.core.media;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.media.MediaInfo;
import com.enonic.wem.api.media.MediaInfoService;
import com.enonic.wem.api.util.Exceptions;

@Component
public final class MediaInfoServiceImpl
    implements MediaInfoService
{
    private Parser parser;

    private Detector detector;

    @Override
    public MediaInfo parseMediaInfo( final ByteSource byteSource )
    {
        final MediaInfo.Builder builder = MediaInfo.create();
        final Metadata metadata = parseMetadata( byteSource );

        // Get the detected media-type
        builder.mediaType( metadata.get( Metadata.CONTENT_TYPE ) );

        // Append metadata to info object
        final String[] names = metadata.names();
        for ( final String name : names )
        {
            final String value = metadata.get( name );
            builder.addMetadata( name, value );
        }
        try
        {
            builder.addMetadata( MediaInfo.MEDIA_SOURCE_SIZE, String.valueOf( byteSource.size() ) );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }

        return builder.build();
    }

    private Metadata parseMetadata( final ByteSource byteSource )
    {
        final ParseContext context = new ParseContext();
        final ContentHandler handler = new DefaultHandler();
        final Metadata metadata = new Metadata();

        // Parse metadata
        try (final InputStream stream = byteSource.openStream())
        {
            final AutoDetectParser autoDetectParser = new AutoDetectParser( this.detector, this.parser );
            autoDetectParser.parse( stream, handler, metadata, context );
        }
        catch ( IOException | SAXException | TikaException e )
        {
            throw Exceptions.unchecked( e );
        }

        return metadata;
    }

    @Reference
    public void setParser( final Parser parser )
    {
        this.parser = parser;
    }

    @Reference
    public void setDetector( final Detector detector )
    {
        this.detector = detector;
    }
}
