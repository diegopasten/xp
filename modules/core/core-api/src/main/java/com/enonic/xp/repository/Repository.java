package com.enonic.xp.repository;

import com.enonic.xp.data.PropertyTree;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

import java.util.Optional;

@Beta
public final class Repository
{
    private final RepositoryId id;

    private final Branches branches;

    private final RepositorySettings settings;

    private final RepositoryData data;

    private Repository( Builder builder )
    {
        this.id = builder.id;
        this.branches = builder.branches;
        this.settings = builder.settings == null ? RepositorySettings.create().build() : builder.settings;
        this.data = Optional.ofNullable( builder.data ).orElse( RepositoryData.create( new PropertyTree() ) );
    }

    public RepositoryId getId()
    {
        return id;
    }

    public RepositorySettings getSettings()
    {
        return settings;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Repository source )
    {
        return new Builder( source );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Repository that = (Repository) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public static final class Builder
    {
        private RepositoryId id;

        private RepositorySettings settings;

        private Branches branches;

        private RepositoryData data;

        private Builder()
        {
        }

        public Builder( final Repository source )
        {
            id = source.id;
            branches = source.branches;
            settings = source.settings;
            data = source.data;
        }

        public Builder id( final RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }


        public Builder branches( final Branch... branches )
        {
            this.branches = Branches.from( branches );
            return this;
        }

        public Builder settings( final RepositorySettings settings )
        {
            this.settings = settings;
            return this;
        }

        public Builder data( final RepositoryData data )
        {
            this.data = data;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( branches, "branches cannot be null" );
            Preconditions.checkArgument( branches.contains( RepositoryConstants.MASTER_BRANCH ), "branches must contain master branch." );
        }


        public Repository build()
        {
            validate();
            return new Repository( this );
        }
    }
}
