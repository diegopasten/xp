package com.enonic.xp.project;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

public final class ProjectName
{
    public static final ProjectName DEFAULT_PROJECT_NAME = new ProjectName("default");

    private static final Pattern VALID_PROJECT_NAME_REGEX = Pattern.compile( "^[a-z0-9\\-][a-z0-9\\-_]*$" );

    private String value;

    private ProjectName( String value )
    {
        Preconditions.checkNotNull( value, "ProjectName name cannot be null" );
        Preconditions.checkArgument( VALID_PROJECT_NAME_REGEX.matcher( value ).matches(),
                String.format( "Project name format incorrect: %s", value ) );

        this.value = value;
    }

    public static ProjectName from( String name )
    {
        return new ProjectName( name );
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof ProjectName ) )
        {
            return false;
        }
        ProjectName that = (ProjectName) o;
        return Objects.equal( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( value );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
                .add( "value", value )
                .toString();
    }
}
