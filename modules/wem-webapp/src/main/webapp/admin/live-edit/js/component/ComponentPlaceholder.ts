module LiveEdit.component {
    export class ComponentPlaceholder extends Component {
        constructor() {
            super();
            this.addClass("live-edit-empty-component");
            this.getEl().setData('live-edit-empty-component', "true");

            $liveEdit(this.getHTMLElement()).on('componentSelect.liveEdit', (event, name?)=> {
                this.onSelect();
            });

            $liveEdit(window).on('componentDeselect.liveEdit', (event, name?)=> {
                this.onDeselect();
            });
        }

        static fromComponent(type: LiveEdit.component.Type): ComponentPlaceholder {
            var placeholder: ComponentPlaceholder;
            if (type === Type.IMAGE) {
                placeholder = new LiveEdit.component.ImagePlaceholder();
            } else if (type == Type.PART) {
                placeholder = new LiveEdit.component.PartPlaceholder();
            } else if (type == Type.LAYOUT) {
                placeholder = new LiveEdit.component.LayoutPlaceholder();
            } else {
                var emptyComponentIcon = new api.dom.DivEl();
                emptyComponentIcon.addClass('live-edit-empty-component-icon');
                placeholder = new ComponentPlaceholder();
                placeholder.appendChild(emptyComponentIcon);
            }
            return placeholder;
        }

        onSelect() {

        }

        onDeselect() {

        }

    }
}