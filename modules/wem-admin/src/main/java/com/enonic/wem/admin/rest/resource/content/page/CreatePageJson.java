package com.enonic.wem.admin.rest.resource.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.api.command.content.page.CreatePage;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.RootDataSet;

public class CreatePageJson
{
    private final CreatePage createPage;

    @JsonCreator
    public CreatePageJson( @JsonProperty("contentId") String contentId,
                           @JsonProperty("pageTemplateKey") String pageTemplateKey,
                           @JsonProperty("config") List<DataJson> config )
    {
        this.createPage = new CreatePage().
            content( ContentId.from( contentId ) ).
            pageTemplate( PageTemplateKey.from( pageTemplateKey ) ).
            config( parseData( config ) );
    }

    @JsonIgnore
    public CreatePage getCreatePage()
    {
        return createPage;
    }

    private static RootDataSet parseData( final List<DataJson> dataJsonList )
    {
        final RootDataSet data = new RootDataSet();
        for ( DataJson dataJson : dataJsonList )
        {
            data.add( dataJson.getData() );
        }
        return data;
    }
}
