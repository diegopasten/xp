package com.enonic.xp.web.session.impl;

public @interface WebSessionConfig
{
    String storeMode() default "non-persistent";

    int savePeriodSeconds() default 10;
}
