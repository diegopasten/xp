module api.liveedit {

    import Content = api.content.Content;
    import Site = api.content.site.Site;

    export class PageItemType extends ItemType {

        private static INSTANCE = new PageItemType();

        static get(): PageItemType {
            return PageItemType.INSTANCE;
        }

        private content: Content;

        private site: Site;

        constructor() {
            super("page", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=page]',
                draggable: false,
                cursor: 'pointer',
                iconCls: 'live-edit-font-icon-page',
                highlighterStyle: {
                    stroke: 'rgba(20, 20, 20, 1)',
                    strokeDasharray: '',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['reset']
            });
        }

        setContent(content: Content) {
            this.content = content;
        }

        setSite(site: Site) {
            this.site = site;
        }

        getContent(): Content {
            return this.content;
        }

        getSite(): Site {
            return this.site;
        }

        createView(config: CreateItemViewConfig<any,any>): PageView {
            throw new Error("Not supported");
        }
    }

    PageItemType.get();
}