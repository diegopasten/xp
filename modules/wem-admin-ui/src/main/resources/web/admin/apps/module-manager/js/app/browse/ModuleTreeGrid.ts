module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ModuleSummary = api.module.ModuleSummary;
    import ModuleSummaryViewer = api.module.ModuleSummaryViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;

    export class ModuleTreeGrid extends TreeGrid<ModuleSummary> {

        constructor() {
            super(new TreeGridBuilder<ModuleSummary>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<ModuleSummary>>().
                            setName("Name").
                            setId("displayName").
                            setField("displayName").
                            setFormatter(this.nameFormatter).
                            setMinWidth(250).
                            build(),

                        new GridColumnBuilder<TreeNode<ModuleSummary>>().
                            setName("Version").
                            setId("version").
                            setField("version").
                            setCssClass("version").
                            setMinWidth(50).
                            setMaxWidth(70).
                            build(),

                        new GridColumnBuilder<TreeNode<ModuleSummary>>().
                            setName("State").
                            setId("state").
                            setField("state").
                            setCssClass("state").
                            setMinWidth(80).
                            setMaxWidth(100).
                            build(),

                        new GridColumnBuilder<TreeNode<ModuleSummary>>().
                            setName("ModifiedTime").
                            setId("modifiedTime").
                            setField("modifiedTime").
                            setCssClass("modified").
                            setMinWidth(150).
                            setMaxWidth(170).
                            setFormatter(DateTimeFormatter.format).
                            build()

                    ]).prependClasses("module-grid")
            );
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ModuleSummary>) {
            var viewer = new ModuleSummaryViewer();
            viewer.setObject(node.getData());
            return viewer.toString();
        }

        getDataId(data: ModuleSummary): string {
            return data.getId();
        }

        fetchRoot(): wemQ.Promise<ModuleSummary[]> {
            return new api.module.ListModulesRequest().sendAndParse();
        }

        fetch(node: TreeNode<ModuleSummary>): wemQ.Promise<api.module.Module> {
            return this .fetchByKey(node.getData().getModuleKey());
        }

        fetchByKey(moduleKey: api.module.ModuleKey): wemQ.Promise<api.module.Module> {
            var deferred = wemQ.defer<api.module.Module>();
            new api.module.GetModuleRequest(moduleKey, true).sendAndParse().then((modulee: api.module.Module)=> {
                deferred.resolve(modulee);
            });

            return deferred.promise;
        }

        updateModuleNode(moduleKey: api.module.ModuleKey) {
            var root = this.getRoot();
            root.getChildren().forEach((child: TreeNode<ModuleSummary>) => {
                var moduleSummary: ModuleSummary = child.getData();
                if (moduleSummary.getModuleKey().toString() == moduleKey.toString()) {
                    this.updateNode(moduleSummary);
                }
            });
        }

        deleteModuleNode(moduleKey: api.module.ModuleKey) {
            var root = this.getRoot();
            root.getChildren().forEach((child: TreeNode<ModuleSummary>) => {
                var moduleSummary: ModuleSummary = child.getData();
                if (moduleSummary.getModuleKey().toString() == moduleKey.toString()) {
                    this.deleteNode(moduleSummary);
                }
            });
        }

        appendModuleNode(moduleKey: api.module.ModuleKey) {
            var root = this.getRoot();
            this.fetchByKey(moduleKey)
                .then((data: api.module.Module) => {
               this.appendNode(data);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            });
        }

    }
}
