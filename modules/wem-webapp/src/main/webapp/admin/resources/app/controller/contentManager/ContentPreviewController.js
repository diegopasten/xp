Ext.define( 'Admin.controller.contentManager.ContentPreviewController', {
    extend: 'Admin.controller.contentManager.ContentController',

    /*      Controller for handling Content Preview UI events       */

    stores: [],
    models: [],
    views: [],

    init: function()
    {

        this.control( {
            'contentDetailToolbar *[action=duplicateContent]': {
                click: function( el, e )
                {
                    this.duplicateContent( this.getContentPreviewPanel().data );
                }
            },
            'contentDetailToolbar *[action=deleteContent]': {
                click: function( el, e )
                {
                    this.deleteContent( this.getContentPreviewPanel().data );
                }
            }
        } );
    },


    /*      Getters     */

    getContentPreviewTab: function()
    {
        return this.getCmsTabPanel().getActiveTab();
    },

    getContentPreviewPanel: function()
    {
        return this.getContentPreviewTab();
    }

} );

