package com.enonic.xp.event;

import com.google.common.annotations.Beta;

@Beta
public interface EventListener
{
    void onEvent( Event event );
}
