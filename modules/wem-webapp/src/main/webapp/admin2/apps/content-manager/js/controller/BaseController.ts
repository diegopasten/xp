/**
 * Base controller for admin
 */
Ext.define('Admin.controller.BaseController', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    /*requires: [
     'Admin.lib.UriHelper',
     'Admin.lib.RemoteService'
     ],*/

    init: function () {
    },


    /*  Getters */

    getCmsTabPanel: function () {
        return Ext.ComponentQuery.query('cmsTabPanel')[0];
    },

    getTopBar: function () {
        return Ext.ComponentQuery.query('topBar')[0];
    },

    getMainViewport: function () {
        var parent = window.parent || window;
        //return parent.Ext.ComponentQuery.query('#mainViewport')[0];
    }

});