package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetContentByPathAndVersionIdCommandTest
{
    private final ContentPath contentPath = ContentPath.ROOT;

    private final ContentVersionId versionId = ContentVersionId.from( "versionId" );

    private final Node node = Node.create().build();

    private NodeService nodeService;

    private ContentNodeTranslator translator;

    private ContentTypeService contentTypeService;

    private EventPublisher eventPublisher;

    @BeforeEach
    public void setUp()
    {
        nodeService = Mockito.mock( NodeService.class );
        translator = Mockito.mock( ContentNodeTranslator.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );
        eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    public void testExecute()
    {
        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "property", "value" );

        final Content content = Content.create().
            name( "name" ).
            parentPath( ContentPath.ROOT ).
            data( contentData ).
            build();

        when( nodeService.getByPathAndVersionId( any( NodePath.class ), any( NodeVersionId.class ) ) ).thenReturn( node );
        when( translator.fromNode( any( Node.class ), anyBoolean() ) ).thenReturn( content );

        final Content result = createInstance().execute();

        assertNotNull( result );
        assertEquals( content, result );

        verify( nodeService, times( 1 ) ).
            getByPathAndVersionId( any( NodePath.class ), any( NodeVersionId.class ) );
        verify( translator, times( 1 ) ).
            fromNode( any( Node.class ), anyBoolean() );
        verifyNoMoreInteractions( nodeService, translator );
    }

    @Test
    public void testExecute_NodeNotFound()
    {
        when( nodeService.getByPathAndVersionId( any( NodePath.class ), any( NodeVersionId.class ) ) ).thenThrow(
            NodeNotFoundException.class );

        assertThrows( ContentNotFoundException.class, () -> createInstance().execute() );

        verify( nodeService, times( 1 ) ).
            getByPathAndVersionId( any( NodePath.class ), any( NodeVersionId.class ) );
        verifyNoMoreInteractions( nodeService );
    }

    @Test
    public void testExecute_ContentNotFound()
    {
        when( nodeService.getByPathAndVersionId( any( NodePath.class ), any( NodeVersionId.class ) ) ).thenReturn( node );
        when( translator.fromNode( any( Node.class ), anyBoolean() ) ).thenReturn( null );

        assertThrows( ContentNotFoundException.class, () -> createInstance().execute() );

        verify( nodeService, times( 1 ) ).
            getByPathAndVersionId( any( NodePath.class ), any( NodeVersionId.class ) );
        verify( translator, times( 1 ) ).
            fromNode( any( Node.class ), anyBoolean() );
        verifyNoMoreInteractions( nodeService, translator );
    }

    private GetContentByPathAndVersionIdCommand createInstance()
    {
        return GetContentByPathAndVersionIdCommand.create().
            contentPath( contentPath ).
            versionId( versionId ).
            nodeService( nodeService ).
            translator( translator ).
            eventPublisher( eventPublisher ).
            contentTypeService( contentTypeService ).
            build();
    }
}
