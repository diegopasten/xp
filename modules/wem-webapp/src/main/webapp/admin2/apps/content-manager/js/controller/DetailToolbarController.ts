Ext.define('Admin.controller.DetailToolbarController', {
    extend: 'Admin.controller.Controller',

    stores: [
    ],

    models: [
    ],
    /*    views: [
     'Admin.view.contentManager.DetailToolbar'
     ],*/

    init: function () {

        this.control({
            'contentDetailToolbar *[action=newContent]': {
                click: function (el, e) {
                    this.getNewContentWindow().doShow();
                }
            },
            'contentDetailToolbar *[action=editContent]': {
                click: function (el, e) {
                    this.editContent();
                }
            },
            'contentDetailToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent();
                }
            },
            'contentDetailToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    this.duplicateContent();
                }
            },
            'contentDetailToolbar *[action=moveContent]': {
                click: function (el, e) {

                }
            },
            'contentDetailToolbar *[action=relations]': {
                click: function (el, e) {

                }
            },
            'contentDetailToolbar *[action=closeContent]': {
                click: function (el, e) {
                    this.getCmsTabPanel().getActiveTab().close();
                }
            },
            'contentDetailToolbar *[action=toggleLive]': {
                change: function (slider, state) {
                    slider.up().down('#deviceCycle').setDisabled(!state);
                }
            },
            'contentDetailToolbar #deviceCycle': {
                change: function (cycle, item) {
                    this.application.fireEvent('toggleDeviceContext', item.device);
                }
            }
        });
    }

});
