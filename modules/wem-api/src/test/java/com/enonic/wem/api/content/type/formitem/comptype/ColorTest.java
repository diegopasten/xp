package com.enonic.wem.api.content.type.formitem.comptype;


import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.formitem.InvalidDataException;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

import static org.junit.Assert.*;

public class ColorTest
{
    @Test
    public void construct()
    {
        new Color();
    }

    @Test
    public void given_data_that_validates_checkValidity_throws_nothing()
        throws InvalidValueTypeException, InvalidValueException
    {
        Content content = new Content();
        content.setData( "myColor.red", 40l );
        content.setData( "myColor.green", 40l );
        content.setData( "myColor.blue", 40l );

        Data myColor = content.getData( "myColor" );

        InputTypes.COLOR.checkValidity( myColor );
    }

    @Test
    public void given_data_missing_red_checkValidity_throws_InvalidDataException()
    {
        Content content = new Content();
        content.setData( "myColor.green", 40l );
        content.setData( "myColor.blue", 40l );

        Data myColor = content.getData( "myColor" );

        try
        {
            InputTypes.COLOR.checkValidity( myColor );
            fail( "Excpected exception" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( e instanceof InvalidDataException );
            assertEquals(
                "Invalid data [Data{path=myColor, type=DataSet, value=myColor: green, blue}]: data required to have sub data at path: red",
                e.getMessage() );
        }
    }

    @Test
    public void given_data_with_illegal_red_checkValidity_throws_InvalidDataException()
    {
        Content content = new Content();
        content.setData( "myColor.red", 256l );
        content.setData( "myColor.green", 40l );
        content.setData( "myColor.blue", 40l );

        Data myColor = content.getData( "myColor" );

        try
        {
            InputTypes.COLOR.checkValidity( myColor );
            fail( "Excpected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof InvalidValueException );
            assertEquals(
                "Invalid value in [Data{path=myColor.red, type=WholeNumber, value=256}]: Value not within range from 0 to 255: 256",
                e.getMessage() );
        }
    }
}
