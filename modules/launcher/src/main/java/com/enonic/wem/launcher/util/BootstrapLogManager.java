package com.enonic.wem.launcher.util;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import org.apache.felix.utils.properties.InterpolationHelper;
import org.apache.felix.utils.properties.Properties;

/**
 * Convenience class for configuring java.util.logging to append to
 * the configured log4j log. This could be used for bootstrap logging
 * prior to start of the framework.
 */
public class BootstrapLogManager
{
    private static final String KARAF_BOOTSTRAP_LOG = "karaf.bootstrap.log";

    private static final String LOG4J_APPENDER_FILE = "log4j.appender.out.file";

    private static BootstrapLogManager instance;

    private Handler handler;

    private Properties configProps;

    private String log4jConfigPath;

    public BootstrapLogManager( Properties configProps, String log4jConfigPath )
    {
        this.configProps = configProps;
        this.log4jConfigPath = log4jConfigPath;
        this.handler = null;
    }

    public static void setProperties( Properties configProps )
    {
        setProperties( configProps, null );
    }

    public static void setProperties( Properties configProps, String log4jConfigPath )
    {
        instance = new BootstrapLogManager( configProps, log4jConfigPath );
    }

    private Properties loadPaxLoggingConfig()
    {
        Properties props = new Properties();
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream( log4jConfigPath );
            props.load( fis );
        }
        catch ( Exception e )
        {
            // Ignore
        }
        finally
        {
            close( fis );
        }
        return props;
    }

    private static void close( Closeable closeable )
    {
        try
        {
            if ( closeable != null )
            {
                closeable.close();
            }
        }
        catch ( IOException e )
        {
// Ignore
        }
    }

    String getLogFilePath()
    {
        String filename = configProps == null ? null : configProps.getProperty( KARAF_BOOTSTRAP_LOG );
        if ( filename != null )
        {
            return filename;
        }
        Properties props = loadPaxLoggingConfig();
        // Make a best effort to log to the default file appender configured for log4j
        return props.getProperty( LOG4J_APPENDER_FILE, "${karaf.data}/log/karaf.log" );
    }

    /**
     * Implementation of java.util.logging.Handler that does simple appending
     * to a named file. Should be able to use this for bootstrap logging
     * via java.util.logging prior to startup of pax logging.
     */
    public static class SimpleFileHandler
        extends StreamHandler
    {

        public SimpleFileHandler( File file )
            throws IOException
        {
            open( file, true );
        }

        private void open( File logfile, boolean append )
            throws IOException
        {
            if ( !logfile.getParentFile().exists() )
            {
                try
                {
                    logfile.getParentFile().mkdirs();
                }
                catch ( SecurityException se )
                {
                    throw new IOException( se.getMessage() );
                }
            }
            FileOutputStream fout = new FileOutputStream( logfile, append );
            BufferedOutputStream out = new BufferedOutputStream( fout );
            setOutputStream( out );
        }

        public synchronized void publish( LogRecord record )
        {
            if ( !isLoggable( record ) )
            {
                return;
            }
            super.publish( record );
            flush();
        }
    }


}