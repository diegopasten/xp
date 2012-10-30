package com.enonic.wem.api.content.type.formitem.comptype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

public class WholeNumber
    extends BaseInputType
{
    public WholeNumber()
    {
        super( "wholeNumber" );
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTypes.WHOLE_NUMBER.checkValidity( data );
    }

    @Override
    public void ensureType( final Data data )
    {
        DataTypes.WHOLE_NUMBER.ensureType( data );
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) data.getValue();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }
}
