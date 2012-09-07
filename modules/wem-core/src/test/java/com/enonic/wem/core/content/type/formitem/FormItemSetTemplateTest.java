package com.enonic.wem.core.content.type.formitem;

import org.junit.Test;

import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.formitem.Component.newComponent;
import static com.enonic.wem.core.content.type.formitem.ComponentTemplateBuilder.newComponentTemplate;
import static com.enonic.wem.core.content.type.formitem.FormItemSetTemplateBuilder.newFormItemSetTemplate;
import static com.enonic.wem.core.content.type.formitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class FormItemSetTemplateTest
{

    @Test
    public void adding_a_fieldSetTemplate_to_another_fieldSetTemplate_throws_exception()
    {
        Module module = newModule().name( "myModule" ).build();

        ComponentTemplate ageTemplate =
            newComponentTemplate().module( module ).component( newComponent().name( "age" ).type( FieldTypes.TEXT_LINE ).build() ).build();

        FormItemSetTemplate personTemplate = newFormItemSetTemplate().module( module ).formItemSet(
            FormItemSet.newFormItemTest().name( "person" ).add( newComponent().name( "name" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newTemplateReference( ageTemplate ).name( "age" ).build() ).build() ).build();

        FormItemSetTemplate addressTemplate = newFormItemSetTemplate().module( module ).formItemSet(
            FormItemSet.newFormItemTest().name( "address" ).add( newComponent().type( FieldTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newComponent().type( FieldTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newComponent().type( FieldTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personTemplate.addFormItem( newTemplateReference( addressTemplate ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A template cannot reference other templates unless it is of type COMPONENT: FORM_ITEM_SET", e.getMessage() );
        }
    }

}
