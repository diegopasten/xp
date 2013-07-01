module app {

    export class ContentAppBar extends api_app.AppBar {

        constructor() {
            super("Content Manager", new ContentAppBarTabMenu(),
                {
                    showAppLauncherAction: ContentAppBarActions.SHOW_APP_LAUNCHER,
                    showAppBrowsePanelAction: ContentAppBarActions.SHOW_APP_BROWSER_PANEL
                });
        }

    }

}