package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlDateTimeProperty;
import com.enonic.wem.export.internal.xml.util.DateTimeConverter;

class LocalDateTimePropertyMapper
{
    static JAXBElement<XmlDateTimeProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlDateTimeProperty prop = new XmlDateTimeProperty();
        prop.setName( property.getName() );
        prop.setValue( DateTimeConverter.toXMLGregorianCalendar( property.getLocalDateTime() ) );

        return objectFactory.createXmlPropertyTreeDateTime( prop );
    }

}
