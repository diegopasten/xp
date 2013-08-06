module api_app_wizard {

    export interface WizardPanelParams {

        formIcon:FormIcon;

        toolbar:api_ui_toolbar.Toolbar;

        saveAction:api_ui.Action;
    }

    export class WizardPanel extends api_ui.Panel implements api_ui.Closeable {

        private persistedItem:api_remote.Item;

        private header:WizardPanelHeader;

        private steps:WizardStep[] = [];

        private stepNavigator:WizardStepNavigator;

        private stepPanels:api_app_wizard.WizardStepDeckPanel;

        // TODO: @alb - Value is set to 'changed' by default to see SaveChangesBeforeCloseDialog behavior.
        private isChanged:bool = true;

        private previous:WizardStepNavigationArrow;

        private next:WizardStepNavigationArrow;

        private closingEventListeners:Function[] = [];

        constructor(params:WizardPanelParams) {
            super("WizardPanel");

            this.getEl().addClass("wizard-panel");

            this.appendChild(params.toolbar);
            this.appendChild(params.formIcon);

            this.header = new WizardPanelHeader(this);
            this.appendChild(this.header);

            this.stepPanels = new api_app_wizard.WizardStepDeckPanel();
            this.stepNavigator = new WizardStepNavigator(this.stepPanels);
            this.appendChild(this.stepNavigator);
            this.appendChild(this.stepPanels);

            this.previous = new WizardStepNavigationArrow(WizardStepNavigationArrow.PREVIOUS, this.stepNavigator);
            this.next = new WizardStepNavigationArrow(WizardStepNavigationArrow.NEXT, this.stepNavigator);
            this.appendChild(this.previous);
            this.appendChild(this.next);

            params.saveAction.addExecutionListener(() => {
                this.saveChanges();
            });
        }

        afterRender() {
            super.afterRender();
            this.stepPanels.afterRender();
        }

        addClosingEventListener(listener:(wizardPanel:WizardPanel) => void) {
            this.closingEventListeners.push(listener);
        }

        setPersistedItem(item:api_remote.Item) {
            this.persistedItem = item;
        }

        isItemPersisted():bool {
            return this.persistedItem != null;
        }

        getIconUrl():string {
            return null; // TODO:
        }

        getDisplayName():string {
            return this.header.getDisplayName();
        }

        setDisplayName(value:string) {
            this.header.setDisplayName(value);
        }

        setName(value:string) {
            this.header.setName(value);
        }

        getName():string {
            return this.header.getName();
        }

        isAutogenerateDisplayName():bool {
            return this.header.isAutogenerateDisplayName();
        }

        setAutogenerateDisplayName(value:bool) {
            this.header.setAutogenerateDisplayName(value);
        }

        isAutogenerateName():bool {
            return this.header.isAutogenerateName();
        }

        setAutogenerateName(value:bool) {
            this.header.setAutogenerateName(value);
        }

        generateName(value:string):string {
            return this.header.generateName(value);
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            this.stepNavigator.addStep(step);
        }

        close(checkCanClose?:bool = false) {

            if (checkCanClose) {
                if (this.canClose()) {
                    this.closing();
                }
            }
            else {
                this.closing();
            }
        }

        canClose():bool {

            if (this.hasUnsavedChanges()) {
                this.askUserForSaveChangesBeforeClosing();
                return false;
            }
            else {
                return true;
            }
        }

        closing() {
            this.fireClosingEvent();
        }

        private fireClosingEvent() {
            this.closingEventListeners.forEach((listener:(wizardPanelClosing:WizardPanel) => void) => {
                listener(this);
            });
        }

        /*
         * Override this method in specific wizard to do proper check.
         */
        hasUnsavedChanges():bool {
            return this.isChanged;
        }

        askUserForSaveChangesBeforeClosing() {
            new api_app_wizard.SaveBeforeCloseDialog(this).open();
        }

        saveChanges(successCallback?:() => void) {

            if (this.isItemPersisted()) {
                this.updatePersistedItem(successCallback);
            }
            else {
                this.persistNewItem(successCallback);
            }

            this.isChanged = false;
        }

        /*
         * Override this method in specific wizard to do actual persisting of new item.
         */
        persistNewItem(successCallback?:() => void) {

        }

        /*
         * Override this method in specific wizard to do actual update of item.
         */
        updatePersistedItem(successCallback?:() => void) {

        }
    }
}