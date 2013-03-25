package com.enonic.wem.core.content;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetContentTree;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class GetContentTreeHandler
    extends CommandHandler<GetContentTree>
{

    private ContentDao contentDao;

    public GetContentTreeHandler()
    {
        super( GetContentTree.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentTree command )
        throws Exception
    {
        if ( command.getContentSelectors() != null )
        {
            command.setResult( contentDao.getContentTree( context.getJcrSession(), command.getContentSelectors() ) );
        }
        else
        {
            command.setResult( contentDao.getContentTree( context.getJcrSession() ) );
        }
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
