Ext.define('Admin.view.contentManager.wizard.form.input.TextLine', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextLine',


    initComponent: function () {

        this.items = [
            {
                xtype: 'textfield',
                displayNameSource: true,   // property to select components taking part in auto generation
                name: this.name,
                value: this.value,
                enableKeyEvents: true
            }
        ];

        this.callParent(arguments);
    },

    setValue: function (value) {
        this.down('textfield').setValue(value);
    }
});
