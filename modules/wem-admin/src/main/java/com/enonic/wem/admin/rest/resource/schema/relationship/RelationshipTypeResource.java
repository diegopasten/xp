package com.enonic.wem.admin.rest.resource.schema.relationship;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeConfigJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.json.CreateOrUpdateSchemaJsonResult;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.admin.rest.resource.schema.relationship.json.RelationshipTypeCreateOrUpdateJson;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.UploadedIconFetcher;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.api.schema.relationship.editor.SetRelationshipTypeEditor;
import com.enonic.wem.core.schema.relationship.RelationshipTypeXmlSerializer;

@Path("schema/relationship")
@Produces(MediaType.APPLICATION_JSON)
public class RelationshipTypeResource
    extends AbstractResource
{
    private UploadService uploadService;

    @GET
    public RelationshipTypeJson get( @QueryParam("name") final String name )
    {
        final RelationshipTypeName relationshipTypeName = RelationshipTypeName.from( name );
        final RelationshipType relationshipType = fetchRelationshipType( relationshipTypeName );

        if ( relationshipType == null )
        {
            String message = String.format( "RelationshipType [%s] was not found.", relationshipTypeName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new RelationshipTypeJson( relationshipType );
    }

    @GET
    @Path("config")
    public RelationshipTypeConfigJson getConfig( @QueryParam("name") final String name )
    {
        final RelationshipTypeName relationshipTypeName = RelationshipTypeName.from( name );
        final RelationshipType relationshipType = fetchRelationshipType( relationshipTypeName );

        if ( relationshipType == null )
        {
            String message = String.format( "RelationshipType [%s] was not found.", relationshipTypeName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new RelationshipTypeConfigJson( relationshipType );

    }

    public RelationshipType fetchRelationshipType( final RelationshipTypeName name )
    {
        final GetRelationshipTypes command = Commands.relationshipType().get().names( RelationshipTypeNames.from( name ) );
        final RelationshipTypes relationshipTypes = client.execute( command );
        return relationshipTypes.isEmpty() ? null : relationshipTypes.first();
    }

    @GET
    @Path("list")
    public RelationshipTypeListJson list()
    {
        final RelationshipTypes relationshipTypes = client.execute( Commands.relationshipType().get().all() );

        return new RelationshipTypeListJson( relationshipTypes );
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public SchemaDeleteJson delete( SchemaDeleteParams param )
    {
        final RelationshipTypeNames relationshipTypeNames =
            RelationshipTypeNames.from( param.getNames().toArray( new String[param.getNames().size()] ) );

        final SchemaDeleteJson deletionResult = new SchemaDeleteJson();
        for ( RelationshipTypeName relationshipTypeName : relationshipTypeNames )
        {
            final DeleteRelationshipType deleteCommand = Commands.relationshipType().delete().name( relationshipTypeName );
            final DeleteRelationshipTypeResult result = client.execute( deleteCommand );

            switch ( result )
            {
                case SUCCESS:
                    deletionResult.success( relationshipTypeName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( relationshipTypeName,
                                            String.format( "Mixin [%s] was not found", relationshipTypeName.toString() ) );
                    break;

            }
        }

        return deletionResult;
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateOrUpdateSchemaJsonResult create( RelationshipTypeCreateOrUpdateJson param )
    {
        try
        {
            final RelationshipType relationshipType = new RelationshipTypeXmlSerializer().toRelationshipType( param.getConfig() );

            final Icon icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( param.getIconReference() );
            createRelationshipType( relationshipType, icon );

            return CreateOrUpdateSchemaJsonResult.result( new RelationshipTypeJson( relationshipType ) );
        }
        catch ( Exception e )
        {
            return CreateOrUpdateSchemaJsonResult.error( e.getMessage() );
        }


    }

    private void createRelationshipType( final RelationshipType relationshipType, final Icon icon )
    {
        final CreateRelationshipType createCommand = Commands.relationshipType().create();
        createCommand.
            name( relationshipType.getName() ).
            displayName( relationshipType.getDisplayName() ).
            fromSemantic( relationshipType.getFromSemantic() ).
            toSemantic( relationshipType.getToSemantic() ).
            allowedFromTypes( relationshipType.getAllowedFromTypes() ).
            allowedToTypes( relationshipType.getAllowedToTypes() ).
            icon( icon );

        try
        {
            client.execute( createCommand );
        }
        catch ( BaseException e )
        {
            throw new WebApplicationException( e );
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateOrUpdateSchemaJsonResult update( RelationshipTypeCreateOrUpdateJson param )
    {
        try
        {
            final RelationshipType relationshipType = new RelationshipTypeXmlSerializer().toRelationshipType( param.getConfig() );

            final Icon icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( param.getIconReference() );
            updateRelationshipType( relationshipType, icon );

            return CreateOrUpdateSchemaJsonResult.result( new RelationshipTypeJson( relationshipType ) );
        }
        catch ( Exception e )
        {
            return CreateOrUpdateSchemaJsonResult.error( e.getMessage() );
        }


    }

    private void updateRelationshipType( final RelationshipType relationshipType, final Icon icon )
    {
        final UpdateRelationshipType updateCommand = Commands.relationshipType().update();
        updateCommand.name( relationshipType.getContentTypeName() );
        updateCommand.editor( SetRelationshipTypeEditor.newSetRelationshipTypeEditor().
            displayName( relationshipType.getDisplayName() ).
            fromSemantic( relationshipType.getFromSemantic() ).
            toSemantic( relationshipType.getToSemantic() ).
            allowedFromTypes( relationshipType.getAllowedFromTypes() ).
            allowedToTypes( relationshipType.getAllowedToTypes() ).
            icon( icon ).
            build() );

        try
        {
            client.execute( updateCommand );
        }
        catch ( BaseException e )
        {
            throw new WebApplicationException( e );
        }
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
