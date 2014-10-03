package com.enonic.wem.api.content;

import java.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public class Content<C>
    implements IllegalEditAware<Content>, ChangeTraceable
{
    private final boolean draft;

    private final String displayName;

    private final ContentTypeName type;

    private final ContentPath parentPath;

    private final ContentName name;

    private final ContentPath path;

    private final ContentId id;

    private final Form form;

    private final ContentData contentData;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final UserKey creator;

    private final UserKey owner;

    private final UserKey modifier;

    private final Page page;

    private final boolean hasChildren;

    private final Thumbnail thumbnail;

    protected Content( final BaseBuilder builder )
    {
        Preconditions.checkNotNull( builder.name, "name is required for a Content" );
        Preconditions.checkNotNull( builder.parentPath, "parentPath is required for a Content" );

        if ( builder.type == null )
        {
            builder.type = ContentTypeName.unstructured();
        }

        this.draft = builder.draft;
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.name = builder.name;
        this.parentPath = builder.parentPath;
        this.path = ContentPath.from( builder.parentPath, builder.name.toString() );
        this.id = builder.contentId;
        this.form = builder.form;
        this.contentData = builder.contentData;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.owner = builder.owner;
        this.page = builder.page;
        this.thumbnail = builder.thumbnail;
        this.hasChildren = builder.hasChildren;
    }

    public ContentPath getParentPath()
    {
        return parentPath;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public boolean isRoot()
    {
        return this.path.elementCount() == 1;
    }

    public ContentTypeName getType()
    {
        return type;
    }

    public ContentName getName()
    {
        return this.name;
    }

    public boolean isDraft()
    {
        return draft;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public UserKey getCreator()
    {
        return modifier;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UserKey getOwner()
    {
        return owner;
    }

    public Form getForm()
    {
        return form;
    }

    public ContentData getContentData()
    {
        return contentData;
    }

    public ContentId getId()
    {
        return id;
    }

    public boolean hasChildren()
    {
        return this.hasChildren;
    }

    public boolean isSite()
    {
        return this instanceof Site;
    }

    public boolean isPageTemplate()
    {
        return this instanceof PageTemplate;
    }

    public Site getSite()
    {
        return (Site) this;
    }

    public boolean hasPage()
    {
        return page != null;
    }

    public Page getPage()
    {
        return page;
    }

    public boolean hasThumbnail()
    {
        return this.thumbnail != null;
    }

    public Thumbnail getThumbnail()
    {
        return thumbnail;
    }

    @Override
    public void checkIllegalEdit( final Content to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.getId(), to.getId(), Content.class );
        IllegalEdit.check( "path", this.getPath(), to.getPath(), Content.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Content.class );
        IllegalEdit.check( "creator", this.getCreator(), to.getCreator(), Content.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Content.class );
        IllegalEdit.check( "modifier", this.getModifier(), to.getModifier(), Content.class );
        IllegalEdit.check( "owner", this.getOwner(), to.getOwner(), Content.class );
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "id", id );
        s.add( "draft", draft );
        s.add( "path", path );
        s.add( "displayName", displayName );
        s.add( "contentType", type );
        s.add( "createdTime", createdTime );
        s.add( "modifiedTime", modifiedTime );
        s.add( "creator", creator );
        s.add( "modifier", modifier );
        s.add( "owner", owner );
        return s.toString();
    }

    public static Builder newContent()
    {
        return new Builder();
    }

    public static Builder newContent( final Content content )
    {
        return new Builder( content );
    }

    public static EditBuilder editContent( final Content content )
    {
        return new EditBuilder( content );
    }

    static abstract class BaseBuilder
    {
        boolean draft;

        ContentPath parentPath;

        ContentName name;

        protected ContentId contentId;

        ContentTypeName type;

        Form form;

        protected ContentData contentData;

        String displayName;

        UserKey owner;

        Instant createdTime;

        Instant modifiedTime;

        UserKey creator;

        UserKey modifier;

        protected Page page;

        Thumbnail thumbnail;

        boolean hasChildren;

        BaseBuilder()
        {
            this.contentData = new ContentData();
        }

        BaseBuilder( final Content content )
        {
            this.contentId = content.id;
            this.draft = content.draft;
            this.parentPath = content.parentPath;
            this.name = content.name;
            this.type = content.type;
            this.form = content.form; // TODO make DataSet immutable, or make copy
            this.contentData = content.contentData; // TODO make DataSet immutable, or make copy
            this.displayName = content.displayName;
            this.owner = content.owner;
            this.createdTime = content.createdTime;
            this.modifiedTime = content.modifiedTime;
            this.creator = content.creator;
            this.modifier = content.modifier;
            this.hasChildren = content.hasChildren;
            this.page = content.page;
            this.thumbnail = content.thumbnail;
        }
    }

    public static class EditBuilder
        extends BaseBuilder
    {
        private final Content original;

        private final Changes.Builder changes = new Changes.Builder();

        public EditBuilder( final Content original )
        {
            super( original );
            this.original = original;
        }

        public EditBuilder type( final ContentTypeName type )
        {
            changes.recordChange( newPossibleChange( "type" ).from( this.original.getType() ).to( type ).build() );
            this.type = type;
            return this;
        }

        public EditBuilder draft( boolean draft )
        {
            this.draft = draft;
            return this;
        }

        public EditBuilder form( final Form form )
        {
            changes.recordChange( newPossibleChange( "form" ).from( this.original.getForm() ).to( form ).build() );
            this.form = form;
            return this;
        }

        public EditBuilder contentData( final ContentData contentData )
        {
            changes.recordChange( newPossibleChange( "contentData" ).from( this.original.getContentData() ).to( contentData ).build() );
            this.contentData = contentData;
            return this;
        }

        public EditBuilder displayName( final String displayName )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( displayName ).build() );
            this.displayName = displayName;
            return this;
        }

        public EditBuilder page( final Page page )
        {
            changes.recordChange( newPossibleChange( "page" ).from( this.original.getPage() ).to( page ).build() );
            this.page = page;
            return this;
        }

        public EditBuilder thumbnail( final Thumbnail thumbnail )
        {
            changes.recordChange( newPossibleChange( "thumbnail" ).from( this.original.getThumbnail() ).to( thumbnail ).build() );
            this.thumbnail = thumbnail;
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Content build()
        {
            return new Content( this );
        }
    }

    public static class Builder<BUILDER extends Builder, C extends Content>
        extends BaseBuilder
    {
        public Builder()
        {
            super();
        }

        public Builder( final Content content )
        {
            super( content );
        }

        public Builder<BUILDER, C> parentPath( final ContentPath path )
        {
            this.parentPath = path;
            return this;
        }

        public Builder<BUILDER, C> name( final String name )
        {
            this.name = ContentName.from( name );
            return this;
        }

        public Builder<BUILDER, C> name( final ContentName name )
        {
            this.name = name;
            return this;
        }

        public Builder<BUILDER, C> path( final String path )
        {
            return path( ContentPath.from( path ) );
        }

        public Builder<BUILDER, C> path( final ContentPath path )
        {
            this.parentPath = path.getParentPath();
            Preconditions.checkArgument( path.elementCount() > 0, "No content can be \"root content\": " + path.toString() );
            this.name = ContentName.from( path.getElement( path.elementCount() - 1 ) );
            return this;
        }

        public Builder<BUILDER, C> draft( final boolean draft )
        {
            this.draft = draft;
            return this;
        }

        public Builder<BUILDER, C> type( final ContentTypeName type )
        {
            this.type = type;
            return this;
        }

        public Builder<BUILDER, C> form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder<BUILDER, C> contentData( final ContentData contentData )
        {
            this.contentData = contentData;
            return this;
        }

        public Builder<BUILDER, C> displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder<BUILDER, C> owner( final UserKey owner )
        {
            this.owner = owner;
            return this;
        }

        public Builder<BUILDER, C> creator( final UserKey modifier )
        {
            this.creator = modifier;
            return this;
        }

        public Builder<BUILDER, C> modifier( final UserKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder<BUILDER, C> createdTime( final Instant createdTime )
        {
            this.createdTime = createdTime;
            return this;
        }

        public Builder<BUILDER, C> modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder<BUILDER, C> id( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder<BUILDER, C> hasChildren( final boolean hasChildren )
        {
            this.hasChildren = hasChildren;
            return this;
        }

        public Builder<BUILDER, C> page( final Page page )
        {
            this.page = page;
            return this;
        }

        public Builder<BUILDER, C> thumbnail( final Thumbnail thumbnail )
        {
            this.thumbnail = thumbnail;
            return this;
        }

        public C build()
        {
            return (C) new Content( this );
        }
    }
}
