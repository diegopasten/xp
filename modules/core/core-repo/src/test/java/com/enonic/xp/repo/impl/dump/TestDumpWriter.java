package com.enonic.xp.repo.impl.dump;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repository.RepositoryId;

class TestDumpWriter
    implements DumpWriter
{
    private final ListMultimap<RepoBranchEntry, BranchDumpEntry> entries;

    private RepoBranchEntry current;

    private final Set<String> binaries = Sets.newHashSet();

    private final Set<NodeVersionKey> nodeVersionKeys = Sets.newHashSet();

    private DumpMeta dumpMeta;

    private PropertyTree repositoryData;

    public TestDumpWriter()
    {
        this.entries = ArrayListMultimap.create();
    }

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        this.dumpMeta = dumpMeta;
    }

    @Override
    public void openBranchMeta( final RepositoryId repositoryId, final Branch branch )
    {
        current = new RepoBranchEntry( repositoryId, branch );
    }

    @Override
    public void openRepositoryDataMeta( final RepositoryId repositoryId )
    {
        // Do nothing yet
    }

    @Override
    public void openVersionsMeta( final RepositoryId repositoryId )
    {
        // Do nothing yet
    }

    @Override
    public void openCommitsMeta( final RepositoryId repositoryId )
    {
        // Do nothing yet
    }

    @Override
    public void close()
    {
        current = null;
    }

    @Override
    public void writeBranchEntry( final BranchDumpEntry branchDumpEntry )
    {
        entries.put( current, branchDumpEntry );
    }

    @Override
    public void writeVersionsEntry( final VersionsDumpEntry versionsDumpEntry )
    {
        // Do nothing yet
    }

    @Override
    public void writeCommitEntry( final CommitDumpEntry commitDumpEntry )
    {
        // Do nothing yet
    }

    @Override
    public void writePropertyTree( PropertyTree propertyTree, String name )
    {
        this.repositoryData = propertyTree;
    }

    @Override
    public void writeNodeVersionBlobs( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        nodeVersionKeys.add( nodeVersionKey );
    }

    @Override
    public void writeBinaryBlob( final RepositoryId repositoryId, final String key )
    {
        binaries.add( key );
    }

    public List<BranchDumpEntry> get( final RepositoryId repoId, final Branch branch )
    {
        return this.entries.get( new RepoBranchEntry( repoId, branch ) );
    }

    public Set<String> getBinaries()
    {
        return binaries;
    }

    public DumpMeta getDumpMeta()
    {
        return dumpMeta;
    }

    public boolean hasNodeVersions( final NodeVersionKey... nodeVersionKeys )
    {
        return this.nodeVersionKeys.containsAll( Arrays.asList( nodeVersionKeys ) );
    }

    public Set<NodeVersionKey> getNodeVersionKeys()
    {
        return nodeVersionKeys;
    }

    public PropertyTree getRepositoryData()
    {
        return repositoryData;
    }

    class RepoBranchEntry
    {
        private final RepositoryId repositoryId;

        private final Branch branch;

        public RepoBranchEntry( final RepositoryId repositoryId, final Branch branch )
        {
            this.repositoryId = repositoryId;
            this.branch = branch;
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

            final RepoBranchEntry that = (RepoBranchEntry) o;

            if ( repositoryId != null ? !repositoryId.equals( that.repositoryId ) : that.repositoryId != null )
            {
                return false;
            }
            return branch != null ? branch.equals( that.branch ) : that.branch == null;

        }

        @Override
        public int hashCode()
        {
            int result = repositoryId != null ? repositoryId.hashCode() : 0;
            result = 31 * result + ( branch != null ? branch.hashCode() : 0 );
            return result;
        }
    }
}

