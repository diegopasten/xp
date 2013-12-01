module api_schema_mixin {

    export class GetMixinByQualifiedNameRequest extends MixinResourceRequest<api_schema_mixin_json.MixinJson> {

        private name:MixinName;

        constructor(name:MixinName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams():Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}