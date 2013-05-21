Ext.define('Admin.view.account.wizard.user.WizardStepMembershipPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardStepMembershipPanel',

    requires: [ 'Admin.plugin.BoxSelect' ],
    border: false,

    initComponent: function () {
        var groupKeys = [];
        if (this.groups) {
            Ext.Array.each(this.groups, function (group) {
                Ext.Array.include(groupKeys, group.key);
            });
        }
        this.items = [
            {
                xtype: 'fieldset',
                title: 'Member of',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        allowBlank: true,
                        minChars: 1,
                        forceSelection: true,
                        triggerOnClick: false,
                        typeAhead: true,
                        resizable: false,
                        xtype: 'boxselect',
                        cls: 'admin-groups-boxselect',
                        name: 'memberships',
                        store: 'Admin.store.account.GroupStore',
                        mode: 'local',
                        value: groupKeys,
                        displayField: 'name',
                        itemClassResolver: function (values) {
                            if (values.type === 'role') {
                                return 'admin-role-item';
                            } else {
                                return 'admin-group-item';
                            }
                        },
                        listConfig: {
                            getInnerTpl: function () {
                                var Templates_common_groupList =
                                		'<div class="clearfix">' +
                                		    '<div class="admin-left">' +
                                		        '<span class="{[values.type==="user" && !values.builtIn ? "icon-user" : values.type==="role" || values.builtIn ? "icon-role" : "icon-group"]} admin-list-item"></span>' +
                                		    '</div>' +
                                		    '<div class="admin-left">' +
                                		        '<span>' +
                                		            '<tpl if="type==\'user\'"> {name} ({qualifiedName})</tpl>' +
                                		            '<tpl if="type!=\'user\'">{name} ({userStore})</tpl>' +
                                		        '</span>' +
                                		    '</div>' +
                                		'</div>';


                                return Templates_common_groupList;
                            }

                        },
                        valueField: 'key',
                        growMin: 75,
                        hideTrigger: true,
                        pinList: false,
                        labelTpl: '{name} ({userStore})',
                        listeners: {
                            afterrender: function (component, eOpts) {
                                // Fix for BoxSelect's missing "focus on click" behaviour.
                                // The element that looks like a text area is not actually a text area but a DIV element containing a borderless textfield for input. Hence the extending of the Combo box.
                                // In order to focus on the component we have to add a click listener to the element for the component and set focus on the buried text field in the callback.
                                // TODO: Make a feature request.
                                var element = Ext.get(component.getEl());
                                element.on('click', function () {
                                    element.child('* input', true).focus();
                                }, this, {capture: true});
                            },
                            scope: this
                        }
                    },
                    {
                        xtype: 'button',
                        action: 'newGroup',
                        iconCls: 'icon-group-add-24',
                        iconAlign: 'left',
                        scale: 'medium',
                        width: 'auto',
                        text: 'New'
                    }
                ]
            }
        ];

        this.callParent(arguments);

    },

    getData: function () {
        var selectBox = this.down('comboboxselect');
        var values = selectBox.valueModels;
        var groupsSelected = [];
        Ext.Array.each(values, function (group) {
            var groupMap = group.raw.key;
            groupsSelected.push(groupMap);
        });
        var userData = { groups: groupsSelected };
        return userData;
    }

});
